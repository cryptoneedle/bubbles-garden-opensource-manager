package com.bubbles.app.opensource.service;

import cn.hutool.v7.core.data.id.IdUtil;
import cn.hutool.v7.core.io.file.FileUtil;
import com.bubbles.app.opensource.entity.Repo;
import com.bubbles.app.opensource.enums.LocalScanEnum;
import com.bubbles.app.opensource.enums.RemoteStatusEnum;
import com.bubbles.app.opensource.enums.SourceEnum;
import com.bubbles.app.opensource.repository.RepoRepository;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
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
    
    @Autowired
    private RepoRepository repoRepository;
    
    @Value("${repo.root}")
    private String repoRoot;
    
    public void scanLocalRepo() {
        List<Repo> repoList = repoRepository.findAll();
        for (Repo repo : repoList) {
            String repoFile = repoRoot + File.separator + repo.getRelativePath();
            if (FileUtil.exists(repoFile)) {
                repo.setScanStatus(LocalScanEnum.EXISTS);
            } else {
                repo.setScanStatus(LocalScanEnum.NOT_EXISTS);
            }
            repoRepository.save(repo);
        }
    }
    
    public void syncLocalRepo() {
        scanLocalRepo();
        List<Repo> repoList = repoRepository.findAll()
                                            .stream()
                                            .filter(repo -> !repo.hasRemote())
                                            .filter(repo -> repo.getScanStatus().equals(LocalScanEnum.NOT_EXISTS))
                                            .toList();
        
        // 创建本地文件夹
        FileUtil.mkdir(repoRoot + File.separator + SourceEnum.LOCAL.getValue());
        FileUtil.mkdir(repoRoot + File.separator + SourceEnum.AI.getValue());
        
        for (Repo repo : repoList) {
            // 克隆远程仓库
            cloneRepo(repo);
        }
    }
    
    public void cloneRepo(Long id) {
        Repo repo = repoRepository.findById(id).orElseThrow(() -> new RuntimeException("仓库不存在"));
        cloneRepo(repo);
    }
    
    public void cloneRepo(Repo repo) {
        if (!repo.hasRemote()) {
            throw new RuntimeException("不是远程仓库，无法克隆");
        }
        
        String repoPath = repoRoot + File.separator + repo.getRelativePath();
        if (FileUtil.exists(repoPath)) {
            return;
        }
        
        File repoFile = FileUtil.mkdir(repoPath);
        
        try {
            Git.cloneRepository()
               .setURI(repo.getRemoteAddress())
               .setDirectory(repoFile)
               .setProgressMonitor(new TextProgressMonitor())
               .call();
        } catch (Exception e) {
            log.error("克隆远程仓库失败: {}", repo.getRemoteAddress(), e);
        }
    }
    
    public void addRepo(String content) {
        // 1. 清理输入：去除 "git clone " 前缀（不区分大小写）
        String cleaned = content.trim().replaceFirst("(?i)^git\\s+clone\\s+", "");
        
        // 2. 解析 Git URL 提取 host/user/repo
        Pattern httpsPattern = Pattern.compile("https://([^/]+)/([^/]+)/([^/.]+?)(?:\\.git)?$");
        Pattern sshPattern = Pattern.compile("git@([^:]+):([^/]+)/([^/.]+?)(?:\\.git)?$");
        
        Matcher httpsMatcher = httpsPattern.matcher(cleaned);
        Matcher sshMatcher = sshPattern.matcher(cleaned);
        
        String host, userName, repoName;
        if (httpsMatcher.matches()) {
            host = httpsMatcher.group(1);
            userName = httpsMatcher.group(2);
            repoName = httpsMatcher.group(3);
        } else if (sshMatcher.matches()) {
            host = sshMatcher.group(1);
            userName = sshMatcher.group(2);
            repoName = sshMatcher.group(3);
        } else {
            throw new IllegalArgumentException("无法解析的 Git 仓库地址: " + content);
        }
        
        // 3. 根据 host 判断仓库来源
        SourceEnum source = switch (host.toLowerCase()) {
            case "github.com" -> SourceEnum.GITHUB;
            case "gitee.com" -> SourceEnum.GITEE;
            case "gitcode.com" -> SourceEnum.GITCODE;
            default -> SourceEnum.GITLAB;
        };

        // 4. 计算 relativePath
        String suffix = null;
        String relativePath;
        if (source == SourceEnum.GITLAB) {
            suffix = host;
            relativePath = source.getValue() + "_" + host + "/" + userName + "/" + repoName;
        } else {
            relativePath = source.getValue() + "/" + userName + "/" + repoName;
        }

        // 5. 校验仓库唯一性（以 relativePath 为准，兼容同仓库 HTTPS/SSH 双地址）
        if (repoRepository.existsByRelativePath(relativePath)) {
            throw new IllegalArgumentException("仓库已存在: " + relativePath);
        }
        
        // 6. 构建并保存 Repo 实体
        Repo repo = new Repo();
        repo.setId(IdUtil.getSnowflakeNextId());
        repo.setSource(source);
        repo.setRemoteAddress(cleaned);
        repo.setUserName(userName);
        repo.setRepoName(repoName);
        repo.setScanStatus(LocalScanEnum.NOT_EXISTS);
        repo.setRemoteStatus(RemoteStatusEnum.ACTIVE);
        repo.setRelativePath(relativePath);
        if (suffix != null) {
            repo.setSuffix(suffix);
        }
        
        repoRepository.save(repo);
        log.info("添加仓库成功: {} [{}/{}]", source.getValue(), userName, repoName);
        
        cloneRepo(repo);
    }
    
    public void pullRepo(Long id) {
        Repo repo = repoRepository.findById(id).orElseThrow(() -> new RuntimeException("仓库不存在"));
        
        if (!repo.hasRemote()) {
            throw new RuntimeException("不是远程仓库，无法拉取");
        }
        
        String repoPath = repoRoot + File.separator + repo.getRelativePath();
        if (!FileUtil.exists(repoPath)) {
            throw new RuntimeException("本地仓库不存在，请先克隆: " + repo.getRemoteAddress());
        }
        
        try (Git git = Git.open(new File(repoPath))) {
            PullResult result = git.pull().call();
            
            if (!result.isSuccessful()) {
                log.warn("拉取仓库失败: {} [{}/{}]", repo.getSource()
                                                         .getValue(), repo.getUserName(), repo.getRepoName());
                return;
            }
            
            MergeResult mergeResult = result.getMergeResult();
            if (mergeResult != null && mergeResult.getConflicts() != null && !mergeResult.getConflicts().isEmpty()) {
                repo.setRemoteStatus(RemoteStatusEnum.CONFLICT);
                log.warn("拉取存在冲突: {} [{}/{}]", repo.getSource()
                                                         .getValue(), repo.getUserName(), repo.getRepoName());
            } else {
                repo.setRemoteStatus(RemoteStatusEnum.ACTIVE);
                log.info("拉取成功: {} [{}/{}]", repo.getSource().getValue(), repo.getUserName(), repo.getRepoName());
            }
            
            repoRepository.save(repo);
        } catch (Exception e) {
            log.error("拉取仓库异常: {}", repo.getRemoteAddress(), e);
        }
    }
    
    public void updateAbility(Long id, String ability) {
        // todo 更新能力
    }
}