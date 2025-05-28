package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.intf.Create;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Received request to save new user");
        return userService.saveUser(userDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        log.info("Received request to get all users");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable("userId") Long userId) {
        log.info("Вывод пользователя под id: {}", userId);
        return userService.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@PathVariable("userId") long userId, @RequestBody UserDto userDto) {
        log.info("Вывод пользователя под id: {}", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteUserById(@PathVariable("userId") long userId) {
        log.info("Удалён пользователь под id: {}", userId);
        userService.deleteUserById(userId);
        return "Пользователь успешно удалён";
    }
}
