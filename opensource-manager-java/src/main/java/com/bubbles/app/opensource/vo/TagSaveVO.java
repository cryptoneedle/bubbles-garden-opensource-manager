package com.bubbles.app.opensource.vo;

import lombok.Data;

/**
 * <p>description: 保存标签 </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Data
public class TagSaveVO {
    
    private String name;
    private String color;
    private Integer sort;
}