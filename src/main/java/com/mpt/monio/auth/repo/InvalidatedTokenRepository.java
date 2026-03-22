package com.mpt.monio.auth.repo;

import com.mpt.monio.auth.entity.InvalidatedToken;
import org.springframework.data.repository.CrudRepository;

public interface InvalidatedTokenRepository extends CrudRepository<InvalidatedToken, String> {
}
