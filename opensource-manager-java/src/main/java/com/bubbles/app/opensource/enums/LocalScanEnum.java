package com.bubbles.app.opensource.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description: 本地扫描状态 </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Getter
@AllArgsConstructor
public enum LocalScanEnum {
    
    EXISTS("匹配"),
    NOT_EXISTS("不存在"),
    ;
    
    private final String value;
}