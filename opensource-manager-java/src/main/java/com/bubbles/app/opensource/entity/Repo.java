package com.bubbles.app.opensource.entity;

import com.bubbles.app.opensource.enums.AbilityEnum;
import com.bubbles.app.opensource.enums.LocalStatusEnum;
import com.bubbles.app.opensource.enums.RemoteStatusEnum;
import com.bubbles.app.opensource.enums.PlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>description: 代码库 </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "repo", comment = "代码库")
@Schema(description = "代码库")
public class Repo {
    
    @Id
    @Column(comment = "主键")
    @Schema(description = "主键")
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(comment = "平台")
    @Schema(description = "平台")
    private PlatformEnum platform;
    
    @Column(comment = "平台路径")
    @Schema(description = "平台路径")
    private String platformPath;
    
    @Column(comment = "仓库路径")
    @Schema(description = "仓库路径")
    private String repoPath;
    
    @Column(comment = "所属空间")
    @Schema(description = "所属空间")
    private String namespace;
    
    @Column(comment = "仓库名称")
    @Schema(description = "仓库名称")
    private String repoName;

    @Enumerated(EnumType.STRING)
    @Column(comment = "本地仓库状态")
    @Schema(description = "本地仓库状态")
    private LocalStatusEnum localStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(comment = "远程仓库状态")
    @Schema(description = "远程仓库状态")
    private RemoteStatusEnum remoteStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(comment = "代码理解能力")
    @Schema(description = "代码理解能力")
    private AbilityEnum ability;

    // ==================== 远程元数据 ====================

    @Column(comment = "Star数")
    @Schema(description = "Star数")
    private Integer stars;

    @Column(comment = "Fork数")
    @Schema(description = "Fork数")
    private Integer forks;

    @Column(comment = "Watch数")
    @Schema(description = "Watch数")
    private Integer watchers;

    @Column(comment = "主要语言")
    @Schema(description = "主要语言")
    private String language;

    @Column(columnDefinition = "TEXT", comment = "项目描述")
    @Schema(description = "项目描述")
    private String description;

    @Column(comment = "最后提交时间")
    @Schema(description = "最后提交时间")
    private LocalDateTime lastCommitTime;

    @Column(comment = "默认分支")
    @Schema(description = "默认分支")
    private String defaultBranch;

    @ManyToMany
    @JoinTable(name = "repo_tag", joinColumns = @JoinColumn(name = "repo_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();
    
    public boolean hasRemote() {
        return switch (platform) {
            case GITHUB, GITEE, GITCODE, GITLAB -> true;
            case LOCAL, AI -> false;
            default -> throw new RuntimeException("未定义仓库来源: " + platform);
        };
    }
    
    public String genAbsolutePath(String repoRoot) {
        // 使用标准化的通用路径，并转为当前系统格式
        String commonPath = genRelativePath();
        return Paths.get(repoRoot, commonPath.split("/")).toString();
    }
    
    public String genRelativePath() {
        return switch (platform) {
            case GITHUB, GITEE, GITCODE ->
                    String.format("%s/%s/%s", platform.getValue(), sanitize(namespace), sanitize(repoName));
            case GITLAB -> "gitlab/" + Arrays.stream(repoPath.split("/"))
                                             .map(this::sanitize)
                                             .collect(Collectors.joining("/"));
            case LOCAL, AI ->
                    String.format("%s/%s/%s", platform.name().toLowerCase(), sanitize(namespace), sanitize(repoName));
            default -> throw new RuntimeException("未定义仓库来源: " + platform);
        };
    }
    
    public String genHttpsUrl() {
        if (!hasRemote()) return null;
        return String.format("https://%s/%s.git", platformPath, repoPath);
    }
    
    public String genSshUrl() {
        if (!hasRemote()) return null;
        return String.format("git@%s:%s.git", platformPath, repoPath);
    }
    
    /**
     * 清洗非法字符并统一小写 (防止 Windows 路径错误，防止 macOS 大小写冲突)
     */
    private String sanitize(String input) {
        if (input == null || input.isBlank()) {
            throw new RuntimeException("输入为空");
            // todo 在拼接路径时可能会出现 bug
            //return null;
        }
        // 1. 转小写
        // 2. 替换 Windows 不允许的特殊字符为下划线
        return input.toLowerCase().replaceAll("[<>:\"\\\\/|?*]", "_");
    }
}
