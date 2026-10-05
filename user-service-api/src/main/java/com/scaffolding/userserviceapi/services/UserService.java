package com.scaffolding.userserviceapi.services;

import com.scaffolding.userserviceapi.dto.UserRequestDto;
import com.scaffolding.userserviceapi.dto.UserResponseDto;
import com.scaffolding.userserviceapi.entities.User;
import com.scaffolding.userserviceapi.exceptions.UserNotFoundException;
import com.scaffolding.userserviceapi.mappers.UserMapper;
import com.scaffolding.userserviceapi.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserResponseDto findById(Long id) {
        return userMapper.toDto(findUser(id));
    }

    @Transactional
    public UserResponseDto create(UserRequestDto request) {
        User user = userMapper.toEntity(request);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserResponseDto update(Long id, UserRequestDto request) {
        User user = findUser(id);
        userMapper.updateEntity(request, user);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(findUser(id));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
