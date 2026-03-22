package com.mpt.monio.auth.mapper;

import com.mpt.monio.auth.dto.SignUpRequest;
import com.mpt.monio.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    User toEntity(SignUpRequest signUpRequest);
}
