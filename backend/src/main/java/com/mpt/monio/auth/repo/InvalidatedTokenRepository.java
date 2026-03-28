package com.mpt.monio.auth.repo;

import com.mpt.monio.auth.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
}
