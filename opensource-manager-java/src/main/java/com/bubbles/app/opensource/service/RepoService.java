package com.bubbles.app.opensource.service;

import cn.hutool.v7.core.io.file.FileUtil;
import com.bubbles.app.opensource.entity.Repo;
import com.bubbles.app.opensource.enums.LocalScanEnum;
import com.bubbles.app.opensource.enums.SourceEnum;
import com.bubbles.app.opensource.repository.RepoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Service
public class RepoService {
    
    @Autowired
    private RepoRepository repoRepository;
    
    @Value("${repo.root}")
    private String repoRoot;
    
    public void scanLocalRepo() {
        List<Repo> repoList = repoRepository.findAll();
        for (Repo repo : repoList) {
            // 获取本地仓库路径
            String repoPath = repo.genLocalPath(repoRoot);
            // 判断本地仓库是否存在
            if (FileUtil.exists(repoPath)) {
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
                                            .filter(repo -> repo.getScanStatus().equals(LocalScanEnum.NOT_EXISTS))
                                            .filter(repo -> !repo.isRemote())
                                            .toList();
        for (Repo repo : repoList) {
            // 克隆远程仓库
            cloneRepo(repo);
        }
        
        // 创建本地文件夹
        String localPath = repoRoot + "/" + SourceEnum.LOCAL.getValue();
        String tempPath = repoRoot + "/" + SourceEnum.TEMP.getValue();
        String aiPath = repoRoot + "/" + SourceEnum.AI.getValue();
        String[] filePaths = {localPath, tempPath, aiPath};
        for (String filePath : filePaths) {
            if (!FileUtil.exists(filePath)) {
                FileUtil.mkdir(filePath);
            }
        }
    }
    
    public void addRepo(String content) {
        // todo 保存 Repo
    }
    
    public void cloneRepo(Repo repo) {
        // todo 克隆 Repo
        String sshCloneCmd = repo.genSshClone();
        
    }
    
    public void cloneRepo(Long id) {
        // todo 克隆 Repo
    }
    
    public void pullRepo(Long id) {
        // todo 拉取 Repo
    }
    
    public void updateAbility(Long id, String ability) {
        // todo 更新能力
    }
}