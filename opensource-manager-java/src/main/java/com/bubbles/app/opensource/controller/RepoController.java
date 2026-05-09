package com.bubbles.app.opensource.controller;

import com.bubbles.app.opensource.service.RepoService;
import com.bubbles.common.core.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@RestController
@RequestMapping("/repo")
public class RepoController {
    
    @Autowired
    private RepoService repoService;
    
    @PostMapping("/scan")
    public Result<?> scanLocalRepo() {
        repoService.scanLocalRepo();
        return Result.success();
    }
    
    @PostMapping("/sync")
    public Result<?> syncLocalRepo() {
        repoService.syncLocalRepo();
        return Result.success();
    }
    
    @PostMapping("/add")
    public void addRepo(@RequestBody String content) {
        repoService.addRepo(content);
    }
    
    @PostMapping("/{id}/clone")
    public void clone(@PathVariable("id") Long id) {
        repoService.cloneRepo(id);
    }
    
    @PostMapping("/{id}/pull")
    public void pull(@PathVariable("id") Long id) {
        repoService.pullRepo(id);
    }
    
    @PutMapping("/{id}/ability")
    public void updateAbility(@PathVariable Long id, @RequestParam("ability") String ability) {
        repoService.updateAbility(id, ability);
    }
}