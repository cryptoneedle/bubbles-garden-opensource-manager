package com.bubbles.app.opensource.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

/**
 * <p>description: 来源枚举 </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Getter
@AllArgsConstructor
public enum PlatformEnum {
    
    GITHUB("Github"),
    GITEE("Gitee"),
    GITLAB("Gitlab"),
    GITCODE("Gitcode"),
    LOCAL("Local"),
    AI("AI"),
    ;
    
    private final String value;
}