package com.bubbles.app.opensource.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description: 来源枚举 </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Getter
@AllArgsConstructor
public enum SourceEnum {
    
    GITHUB("Github"),
    GITEE("Gitee"),
    GITLAB("Gitlab"),
    GITCODE("Gitcode"),
    USER(null);
    
    private final String value;
}