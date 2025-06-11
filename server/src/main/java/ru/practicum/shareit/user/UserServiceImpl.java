package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = repository.findAll();
        return UserMapper.mapToUserDto(users);
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDto userDto) {
        User user = repository.save(UserMapper.mapToNewUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public String getUserNameById(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return user.getName();
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(long userId, UserDto userDto) {
        User updatedUser = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(updatedUser.getEmail())) {
            if (repository.existsByEmail(userDto.getEmail())) {
                throw new ConflictException("User with email '" + userDto.getEmail() + "' already exists.");
            }
            updatedUser.setEmail(userDto.getEmail());
        }

        repository.updateUser(userId, updatedUser.getName(), updatedUser.getEmail());
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUserById(long userId) {
        repository.deleteUserById(userId);
    }
}
