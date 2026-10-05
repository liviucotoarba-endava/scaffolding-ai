package com.scaffolding.userserviceapi.services;

import com.scaffolding.userserviceapi.dto.UserRequestDto;
import com.scaffolding.userserviceapi.dto.UserResponseDto;
import com.scaffolding.userserviceapi.entities.User;
import com.scaffolding.userserviceapi.exceptions.UserNotFoundException;
import com.scaffolding.userserviceapi.mappers.UserMapper;
import com.scaffolding.userserviceapi.repos.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final UserRequestDto REQUEST = new UserRequestDto("Ada Lovelace", "ada@example.com");
    private static final UserResponseDto RESPONSE = new UserResponseDto(1L, "Ada Lovelace", "ada@example.com");

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper);
    }

    @Test
    void findAllReturnsMappedUsers() {
        User user = user(1L);
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(RESPONSE);

        assertThat(userService.findAll()).containsExactly(RESPONSE);
    }

    @Test
    void findAllReturnsEmptyListWhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertThat(userService.findAll()).isEmpty();
        verifyNoInteractions(userMapper);
    }

    @Test
    void findByIdReturnsMappedUser() {
        User user = user(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(RESPONSE);

        assertThat(userService.findById(1L)).isEqualTo(RESPONSE);
    }

    @Test
    void findByIdThrowsWhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.findById(99L));

        assertThat(exception).hasMessage("User with id 99 was not found");
        verifyNoInteractions(userMapper);
    }

    @Test
    void createMapsRequestAndReturnsSavedUser() {
        User newUser = user(null);
        User savedUser = user(1L);
        when(userMapper.toEntity(REQUEST)).thenReturn(newUser);
        when(userRepository.save(newUser)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(RESPONSE);

        assertThat(userService.create(REQUEST)).isEqualTo(RESPONSE);
        verify(userRepository).save(newUser);
    }

    @Test
    void updateMapsChangesBeforeSavingExistingUser() {
        User user = user(1L);
        UserRequestDto request = new UserRequestDto("Grace Hopper", "grace@example.com");
        UserResponseDto response = new UserResponseDto(1L, "Grace Hopper", "grace@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(response);

        assertThat(userService.update(1L, request)).isEqualTo(response);

        var order = inOrder(userRepository, userMapper);
        order.verify(userRepository).findById(1L);
        order.verify(userMapper).updateEntity(request, user);
        order.verify(userRepository).save(user);
        order.verify(userMapper).toDto(user);
    }

    @Test
    void updateThrowsWithoutSavingWhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.update(99L, REQUEST));

        verify(userRepository).findById(99L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void deleteRemovesExistingUser() {
        User user = user(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteThrowsWithoutDeletingWhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.delete(99L));

        verify(userRepository).findById(99L);
        verifyNoMoreInteractions(userRepository);
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setName(REQUEST.name());
        user.setEmail(REQUEST.email());
        return user;
    }
}
