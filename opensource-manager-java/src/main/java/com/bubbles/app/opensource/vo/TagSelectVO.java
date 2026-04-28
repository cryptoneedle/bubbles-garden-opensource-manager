package com.bubbles.app.opensource.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>description: 查询标签 </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagSelectVO {
    
    private Long id;
    private String name;
    private Integer sort;
    private String color;
    private Integer repoCount;
}