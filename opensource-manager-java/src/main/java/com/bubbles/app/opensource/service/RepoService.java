package com.bubbles.app.opensource.service;

import com.bubbles.app.opensource.repository.RepoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
    public void scanRepo() {
        // todo 扫描本地 Repo
    }
    
    public void addRepo(String content) {
        // todo 保存 Repo
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