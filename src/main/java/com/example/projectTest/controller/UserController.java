package com.example.projectTest.controller;

import com.example.projectTest.dto.CreateUserDto;
import com.example.projectTest.dto.UpdateUserDto;
import com.example.projectTest.dto.UserDto;
import com.example.projectTest.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.findAll();
    }

    @GetMapping("{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/findByEmail")
    public UserDto getUserByEmail(@RequestParam String email) {
        return userService.findByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid CreateUserDto createUserDto) {
        return userService.create(createUserDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }

    @PutMapping("{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody @Valid UpdateUserDto updateUserDto) {
        return userService.update(id, updateUserDto);
    }
}