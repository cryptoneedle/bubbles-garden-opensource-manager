package com.bubbles.app.opensource.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.rmi.Remote;

/**
 * <p>description: 本地状态 </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Getter
@AllArgsConstructor
public enum LocalStatusEnum {
    
    INIT("初始化"),
    SUCCESS("成功"),
    FAIL("失败"),
    CONFLICT("冲突"),
    ;
    
    private final String value;
}