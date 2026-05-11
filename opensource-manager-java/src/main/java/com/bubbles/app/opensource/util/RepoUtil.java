package com.bubbles.app.opensource.util;

import com.bubbles.app.opensource.enums.PlatformEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.io.File;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-05-11
 */
public class RepoUtil {
    
    public static String getLocalPath(String repoRoot) {
        return repoRoot + File.separator + PlatformEnum.LOCAL.getValue();
    }
    
    public static String getAIPath(String repoRoot) {
        return repoRoot + File.separator + PlatformEnum.AI.getValue();
    }
    
    public static PlatformEnum analyzePlatform(String platform) {
        if (StringUtils.isEmpty(platform)) {
            throw new RuntimeException("platform is empty");
        }
        String lower = platform.toLowerCase();
        if (lower.contains("github")) return PlatformEnum.GITHUB;
        if (lower.contains("gitcode")) return PlatformEnum.GITCODE;  // gitcode 优先于 gitee，因为 gitcode 排前面
        if (lower.contains("gitee")) return PlatformEnum.GITEE;
        return PlatformEnum.GITLAB;
    }
}