package com.bubbles.app.opensource.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Getter
@AllArgsConstructor
public enum RemoteStatusEnum {
    
    ACTIVE("活跃"),
    ARCHIVED("归档"),
    NOT_FOUND("未找到"),
    ;
    
    private final String value;
}