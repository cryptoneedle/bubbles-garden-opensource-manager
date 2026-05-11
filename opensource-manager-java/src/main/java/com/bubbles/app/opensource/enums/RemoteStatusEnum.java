package com.bubbles.app.opensource.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description: 远程仓库状态 </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Getter
@AllArgsConstructor
public enum RemoteStatusEnum {
    
    UNKNOWN("未知"),
    CONFLICT("冲突"),
    ACTIVE("活跃"),
    ARCHIVED("归档"),
    NOT_FOUND("未找到"),
    ;
    
    private final String value;
}