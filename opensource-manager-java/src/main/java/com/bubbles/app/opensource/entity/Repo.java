package com.bubbles.app.opensource.entity;

import com.bubbles.app.opensource.enums.AbilityEnum;
import com.bubbles.app.opensource.enums.RemoteStatusEnum;
import com.bubbles.app.opensource.enums.SourceEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

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
public class Repo {

    @Id
    @Column(comment = "主键")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(comment = "来源")
    private SourceEnum source;

    @Column(comment = "地址")
    private String address;

    @Column(comment = "用户")
    private String userName;

    @Column(comment = "仓库名")
    private String repoName;

    @Enumerated(EnumType.STRING)
    @Column(comment = "远程仓库状态")
    private RemoteStatusEnum remoteStatus;

    @Enumerated(EnumType.STRING)
    @Column(comment = "能力")
    private AbilityEnum ability;

    @ManyToMany
    @JoinTable(
            name = "repo_tag",
            joinColumns = @JoinColumn(name = "repo_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();
}
