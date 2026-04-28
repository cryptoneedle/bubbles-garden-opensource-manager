package com.bubbles.app.opensource.repository;

import com.bubbles.app.opensource.entity.Repo;
import com.bubbles.app.opensource.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Repository
public interface RepoRepository extends JpaRepository<Repo, Long>, JpaSpecificationExecutor<Repo> {

}