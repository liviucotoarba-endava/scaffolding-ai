package com.scaffolding.userserviceapi.mappers;

import com.scaffolding.userserviceapi.dto.UserRequestDto;
import com.scaffolding.userserviceapi.dto.UserResponseDto;
import com.scaffolding.userserviceapi.entities.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toDtoCopiesIdNameAndEmail() {
        User user = new User();
        user.setId(1L);
        user.setName("Ada Lovelace");
        user.setEmail("ada@example.com");

        assertThat(userMapper.toDto(user)).isEqualTo(new UserResponseDto(1L, "Ada Lovelace", "ada@example.com"));
    }

    @Test
    void toEntityCopiesRequestFieldsWithoutAssigningId() {
        UserRequestDto request = new UserRequestDto("Ada Lovelace", "ada@example.com");

        User user = userMapper.toEntity(request);

        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isEqualTo(request.name());
        assertThat(user.getEmail()).isEqualTo(request.email());
    }

    @Test
    void updateEntityReplacesFieldsAndPreservesId() {
        User user = new User();
        user.setId(1L);
        user.setName("Ada Lovelace");
        user.setEmail("ada@example.com");
        UserRequestDto request = new UserRequestDto("Grace Hopper", "grace@example.com");

        userMapper.updateEntity(request, user);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo(request.name());
        assertThat(user.getEmail()).isEqualTo(request.email());
    }
}
