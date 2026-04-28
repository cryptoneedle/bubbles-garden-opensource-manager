package com.bubbles.app.opensource.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description: 能力枚举 </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Getter
@AllArgsConstructor
public enum AbilityEnum {
    
    MEET("遇见"),
    KONW("了解"),
    WIKI("维基"),
    GRASP("理解"),
    COMMAND("掌控"),
    MASTER("精通"),
    ;
    
    private final String value;
}