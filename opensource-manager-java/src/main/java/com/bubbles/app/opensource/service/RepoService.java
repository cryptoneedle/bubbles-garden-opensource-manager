package com.bubbles.app.opensource.service;

import cn.hutool.v7.core.data.id.IdUtil;
import cn.hutool.v7.core.io.file.FileUtil;
import com.bubbles.app.opensource.entity.Repo;
import com.bubbles.app.opensource.enums.AbilityEnum;
import com.bubbles.app.opensource.enums.LocalStatusEnum;
import com.bubbles.app.opensource.enums.PlatformEnum;
import com.bubbles.app.opensource.enums.RemoteStatusEnum;
import com.bubbles.app.opensource.properties.RepoProperties;
import com.bubbles.app.opensource.entity.Tag;
import com.bubbles.app.opensource.repository.RepoRepository;
import com.bubbles.app.opensource.repository.TagRepository;
import com.bubbles.app.opensource.util.RepoUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Slf4j
@Service
public class RepoService {
    
    @Lazy
    @Autowired
    private RepoService repoService;
    
    @Lazy
    @Autowired
    private TagService tagService;
    
    @Autowired
    private RepoRepository repoRepository;

    @Autowired
    private TagRepository tagRepository;
    
    @Autowired
    private RepoProperties repoProperties;
    
    private static final Pattern GIT_URL_PATTERN = Pattern.compile("^(?:https://|git@)([^/:]+(?::\\d+)?)[/:](.+?)(?:\\.git)?(?:#.*)?/?$");
    
    @Transactional
    public Repo addRepo(String content) {
        if (StringUtils.isBlank(content)) {
            throw new RuntimeException("仓库地址不能为空");
        }
        // 1. 去除 "git clone " 前缀和首尾空白
        content = content.trim().replaceFirst("^git\\s+clone\\s+", "");
        // 2.路径匹配
        Matcher matcher = GIT_URL_PATTERN.matcher(content);
        if (!matcher.matches()) {
            throw new RuntimeException("无法识别的仓库地址: " + content);
        }
        // 3.信息提取
        String platformPath = matcher.group(1);
        String repoPath = matcher.group(2);
        // 4.信息分析
        PlatformEnum platform = RepoUtil.analyzePlatform(platformPath);
        String[] segments = repoPath.split("/");
        String namespace = (segments.length > 1) ?
                           String.join("/", Arrays.copyOfRange(segments, 0, segments.length - 1)) :
                           null;
        String repoName = segments[segments.length - 1];
        // 5. 唯一性校验
        if (repoRepository.existsByPlatformPathAndRepoPath(platformPath, repoPath)) {
            throw new RuntimeException("仓库已存在: " + platformPath + "/" + repoPath);
        }
        // 6. 构建 Repo 实体
        Repo repo = Repo.builder()
                        .id(IdUtil.getSnowflakeNextId())
                        .platform(platform)
                        .platformPath(platformPath)
                        .repoPath(repoPath)
                        .namespace(namespace)
                        .repoName(repoName)
                        .localStatus(LocalStatusEnum.INIT)
                        .remoteStatus(RemoteStatusEnum.UNKNOWN)
                        .ability(AbilityEnum.MEET)
                        .build();
        // 7. 保存
        repoRepository.save(repo);
        
        log.info("仓库添加成功: {} -> {}", repo.genRelativePath(), platform.getValue());
        
        return repo;
    }
    
    public void syncLocalRepo() {
        // 创建本地文件夹
        FileUtil.mkdir(RepoUtil.getLocalPath(repoProperties.getRoot()));
        FileUtil.mkdir(RepoUtil.getAIPath(repoProperties.getRoot()));
        
        repoRepository.findAll().stream().filter(Repo::hasRemote).forEach(repo -> {
            try {
                fetchRepo(repo);
            } catch (Exception e) {
                log.error("同步仓库失败，跳过: {}", repo.genRelativePath(), e);
            }
        });
    }
    
    @Async("repoExecutor")
    public void asyncFetchRepo(Repo repo) {
        try {
            fetchRepo(repo);
        } catch (Exception e) {
            log.error("异步拉取仓库失败: {}", repo.genRelativePath(), e);
        }
    }
    
    private void fetchRepo(Repo repo) {
        if (!repo.hasRemote()) {
            throw new RuntimeException("不是远程仓库");
        }
        
        File repoFile = FileUtil.mkdir(repo.genAbsolutePath(repoProperties.getRoot()));
        if (FileUtil.isEmpty(repoFile)) {
            // 克隆远程仓库
            repoService.cloneRepo(repo);
        } else {
            // 拉取远程仓库
            repoService.pullRepo(repo);
        }
    }
    
    public void cloneRepo(Repo repo) {
        if (!repo.hasRemote()) {
            throw new RuntimeException("不是远程仓库，无法克隆");
        }
        
        File repoFile = FileUtil.mkdir(repo.genAbsolutePath(repoProperties.getRoot()));
        
        try (Git git = Git.cloneRepository()
                          .setURI(repo.genSshUrl())
                          .setDirectory(repoFile)
                          .setProgressMonitor(new TextProgressMonitor())
                          .call()) {
            repo.setLocalStatus(LocalStatusEnum.SUCCESS);
            log.info("SSH克隆成功: {}", repo.genRelativePath());
        } catch (Exception e) {
            log.warn("SSH克隆失败，尝试HTTPS: {}", repo.genSshUrl(), e);
            // 清理残留
            FileUtil.clean(repoFile);
            try (Git git = Git.cloneRepository()
                              .setURI(repo.genHttpsUrl())
                              .setDirectory(repoFile)
                              .setProgressMonitor(new TextProgressMonitor())
                              .call()) {
                repo.setLocalStatus(LocalStatusEnum.SUCCESS);
                log.info("HTTPS克隆成功: {}", repo.genRelativePath());
            } catch (Exception e1) {
                log.error("HTTPS克隆失败: {}", repo.genHttpsUrl(), e1);
                // 清理残留
                FileUtil.clean(repoFile);
                repo.setLocalStatus(LocalStatusEnum.FAIL);
            }
        }
        
        repoRepository.save(repo);
    }
    
    public void pullRepo(Repo repo) {
        if (!repo.hasRemote()) {
            throw new RuntimeException("不是远程仓库，无法拉取");
        }
        
        File repoFile = FileUtil.file(repo.genAbsolutePath(repoProperties.getRoot()));
        
        try (Git git = Git.open(repoFile)) {
            PullResult pullResult = git.pull().setProgressMonitor(new TextProgressMonitor()).call();
            if (!pullResult.isSuccessful()) {
                log.warn("拉取结果不是最新: {}", repo.genRelativePath());
                repo.setLocalStatus(LocalStatusEnum.FAIL);
            }
            repo.setLocalStatus(LocalStatusEnum.SUCCESS);
        } catch (Exception e) {
            log.error("拉取仓库失败: {}", repo.genRelativePath(), e);
            repo.setLocalStatus(LocalStatusEnum.FAIL);
        }
        
        repoRepository.save(repo);
    }
    
    public void updateAbility(Repo repo, AbilityEnum ability) {
        repo.setAbility(ability);
        repoRepository.save(repo);
    }
    
    public List<Repo> listRepos() {
        return repoRepository.findAll();
    }
    
    @Transactional
    public void relyTags(Repo repo, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            repo.setTags(new HashSet<>());
        } else {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(tagIds));
            repo.setTags(tags);
        }
        repoRepository.save(repo);
    }

    public Repo checkRepoId(Long id) {
        return repoRepository.findById(id).orElseThrow(() -> new RuntimeException("仓库不存在"));
    }
}