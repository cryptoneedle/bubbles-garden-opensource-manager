package com.bubbles.app.opensource.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>description: 标签 </p>
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
@Table(name = "tag", comment = "标签")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(comment = "主键")
    private Long id;

    @Column(comment = "标签名")
    private String name;
    
    @Column(comment = "排序")
    private Integer sort;
    
    @Column(comment = "颜色")
    private String color;

    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    private Set<Repo> repos = new HashSet<>();
}