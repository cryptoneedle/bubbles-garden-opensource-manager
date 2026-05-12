package com.bubbles.app.opensource.controller;

import com.bubbles.app.opensource.entity.Repo;
import com.bubbles.app.opensource.enums.AbilityEnum;
import com.bubbles.app.opensource.service.RepoService;
import com.bubbles.common.core.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    
    @PostMapping("/add")
    public Result<Repo> addRepo(@RequestParam("content") String content) {
        Repo repo = repoService.addRepo(content);
        repoService.asyncFetchRepo(repo);
        return Result.success(repo);
    }
    
    @PostMapping("/list")
    public Result<List<Repo>> listRepos() {
        return Result.success(repoService.listRepos());
    }
    
    @PostMapping("/sync")
    public Result<?> syncLocalRepo() {
        repoService.syncLocalRepo();
        return Result.success();
    }
    
    @PostMapping("/{id}/fetch")
    public Result<?> pull(@PathVariable("id") Long id) {
        Repo repo = repoService.checkRepoId(id);
        repoService.pullRepo(repo);
        return Result.success();
    }
    
    @PutMapping("/{id}/ability")
    public Result<?> updateAbility(@PathVariable Long id, @RequestParam("ability") AbilityEnum ability) {
        Repo repo = repoService.checkRepoId(id);
        repoService.updateAbility(repo, ability);
        return Result.success();
    }
    
    @PostMapping("/{id}/tag/rely")
    public Result<?> relyTags(@PathVariable Long id, @RequestBody List<Long> tagIds) {
        Repo repo = repoService.checkRepoId(id);
        repoService.relyTags(repo, tagIds);
        return Result.success();
    }
}