package com.scaffolding.userserviceapi.mappers;

import com.scaffolding.userserviceapi.dto.UserRequestDto;
import com.scaffolding.userserviceapi.dto.UserResponseDto;
import com.scaffolding.userserviceapi.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingConfiguration.class)
public interface UserMapper {

    UserResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequestDto request);

    @Mapping(target = "id", ignore = true)
    void updateEntity(UserRequestDto request, @MappingTarget User user);
}
