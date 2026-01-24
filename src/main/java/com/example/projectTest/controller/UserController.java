package com.example.projectTest.controller;

import com.example.projectTest.dto.CreateUserDto;
import com.example.projectTest.dto.UpdateUserDto;
import com.example.projectTest.dto.UserDto;
import com.example.projectTest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/users")
@Tag(name = "User Management", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех зарегистрированных пользователей с HATEOAS ссылками",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список пользователей успешно получен",
                            content = @Content(schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователи не найдены")
            }
    )
    public CollectionModel<UserDto> getUsers() {
        List<UserDto> users = userService.findAll();
        return CollectionModel.of(users);
    }

    @GetMapping("{id}")
    @Operation(summary = "Получить пользователя по ID")
    public EntityModel<UserDto> getUser(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long id) {
        UserDto user = userService.findById(id);
        return EntityModel.of(user);
    }

    @GetMapping("/findByEmail")
    @Operation(summary = "Найти пользователя по email")
    public EntityModel<UserDto> getUserByEmail(
            @Parameter(description = "Email пользователя", example = "user@example.com")
            @RequestParam String email) {
        UserDto user = userService.findByEmail(email);
        return EntityModel.of(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать нового пользователя")
    public EntityModel<UserDto> createUser(@RequestBody @Valid CreateUserDto createUserDto) {
        UserDto createdUser = userService.create(createUserDto);
        return EntityModel.of(createdUser);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить пользователя по ID")
    public void deleteUser(
            @Parameter(description = "ID пользователя для удаления", example = "1")
            @PathVariable Long id) {
        userService.delete(id);
    }

    @PutMapping("{id}")
    @Operation(summary = "Обновить данные пользователя")
    public EntityModel<UserDto> updateUser(
            @Parameter(description = "ID пользователя для обновления", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserDto updateUserDto) {
        UserDto updatedUser = userService.update(id, updateUserDto);
        return EntityModel.of(updatedUser);
    }
}