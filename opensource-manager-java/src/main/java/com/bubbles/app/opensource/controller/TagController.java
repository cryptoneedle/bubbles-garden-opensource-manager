package com.bubbles.app.opensource.controller;

import com.bubbles.app.opensource.service.TagService;
import com.bubbles.app.opensource.vo.TagAddVO;
import com.bubbles.app.opensource.vo.TagColorVO;
import com.bubbles.app.opensource.vo.TagSaveVO;
import com.bubbles.app.opensource.vo.TagSelectVO;
import com.bubbles.app.opensource.vo.TagSortVO;
import com.bubbles.common.core.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>description: 标签控制器 </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@RestController
@RequestMapping("/tag")
public class TagController {
    
    @Autowired
    private TagService tagService;
    
    @PostMapping("/add")
    public Result<Void> addTag(@RequestBody TagAddVO vo) {
        tagService.addTag(vo);
        return Result.success();
    }
    
    @PostMapping("/{id}/save")
    public Result<Void> saveTag(@PathVariable Long id, @RequestBody TagSaveVO vo) {
        tagService.saveTag(id, vo);
        return Result.success();
    }
    
    @DeleteMapping("/{id}/delete")
    public Result<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteRag(id);
        return Result.success();
    }
    
    @GetMapping("/list")
    public Result<List<TagSelectVO>> list() {
        return Result.success(tagService.list());
    }
}