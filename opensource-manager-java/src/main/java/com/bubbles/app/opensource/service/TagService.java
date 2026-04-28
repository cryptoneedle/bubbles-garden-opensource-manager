package com.bubbles.app.opensource.service;

import com.bubbles.app.opensource.entity.Tag;
import com.bubbles.app.opensource.repository.TagRepository;
import com.bubbles.app.opensource.vo.TagAddVO;
import com.bubbles.app.opensource.vo.TagColorVO;
import com.bubbles.app.opensource.vo.TagSaveVO;
import com.bubbles.app.opensource.vo.TagSelectVO;
import com.bubbles.app.opensource.vo.TagSortVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 标签服务 </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Service
public class TagService {
    
    @Autowired
    private TagRepository tagRepository;
    
    public void addTag(TagAddVO vo) {
        Integer maxSort = tagRepository.findMaxSort();
        int sort = (maxSort != null) ? maxSort + 1 : 0;
        
        Tag tag = Tag.builder()
                     .name(vo.getName())
                     .color(vo.getColor())
                     .sort(sort)
                     .build();
        tagRepository.save(tag);
    }
    
    public void saveTag(Long id, TagSaveVO vo) {
        Tag tag = tagRepository.findById(id)
                               .orElseThrow(() -> new IllegalArgumentException("标签不存在: " + id));
        tag.setName(vo.getName());
        tag.setSort(vo.getSort());
        tag.setColor(vo.getColor());
        tagRepository.save(tag);
    }
    
    /**
     * 删除标签（同时清理与仓库的关联）
     */
    @Transactional
    public void deleteRag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new IllegalArgumentException("标签不存在: " + id);
        }
        tagRepository.deleteTagAssociations(id);
        tagRepository.deleteById(id);
    }
    
    public List<TagSelectVO> list() {
        return tagRepository.findAllTagSelectVO();
    }
}