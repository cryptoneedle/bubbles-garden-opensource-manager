package com.bubbles.app.opensource.properties;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-05-11
 */
@Component
@ConfigurationProperties(prefix = "repo")
@Data
@Slf4j
public class RepoProperties {
    
    private String root;
    
    @PostConstruct
    public void init() {
        log.info("RepoRoot value is: {}", this.root);
    }
}