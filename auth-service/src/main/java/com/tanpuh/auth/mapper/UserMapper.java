package com.tanpuh.auth.mapper;

import com.tanpuh.auth.dto.SignUpRequest;
import com.tanpuh.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    User toEntity(SignUpRequest signUpRequest);
}
