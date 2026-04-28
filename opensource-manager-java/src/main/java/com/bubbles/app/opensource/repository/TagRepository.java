package com.bubbles.app.opensource.repository;

import com.bubbles.app.opensource.entity.Tag;
import com.bubbles.app.opensource.vo.TagSelectVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 标签仓库 </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {

    /**
     * 查询所有标签及其关联的仓库（避免 N+1 问题）
     */
    @Query("SELECT t FROM Tag t LEFT JOIN FETCH t.repos")
    List<Tag> findAllWithRepos();

    /**
     * 删除标签与仓库的关联记录
     */
    @Modifying
    @Query(value = "DELETE FROM repo_tag WHERE tag_id = :tagId", nativeQuery = true)
    void deleteTagAssociations(@Param("tagId") Long tagId);

    /**
     * 查询所有标签列表（含关联仓库数量，按排序字段升序）
     */
    @Query("SELECT new com.bubbles.app.opensource.vo.TagSelectVO(t.id, t.name, t.sort, t.color, SIZE(t.repos)) " +
           "FROM Tag t ORDER BY t.sort ASC")
    List<TagSelectVO> findAllTagSelectVO();

    /**
     * 查询当前最大的排序值
     */
    @Query("SELECT MAX(t.sort) FROM Tag t")
    Integer findMaxSort();
}