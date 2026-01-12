package com.example.projectTest.controller;

import com.example.projectTest.dto.CreateUserDto;
import com.example.projectTest.dto.UpdateUserDto;
import com.example.projectTest.dto.UserDto;
import com.example.projectTest.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;


    @Test
    @DisplayName("Возврат списка пользователей")
    void getAllUsers() throws Exception {
        List<UserDto> users = List.of(UserDto.builder().id(1L).email("test@mail.ru").name("Test").age(25).build());

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("test@mail.ru"))
                .andExpect(jsonPath("$[0].name").value("Test"));
    }

    @Test
    @DisplayName("Возврат пользователя по Id")
    void getUserById() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("test@mail.ru")
                .name("Test")
                .age(25)
                .build();

        when(userService.findById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.age").value(userDto.getAge()));
    }

    @Test
    @DisplayName("Возврат пользователя по Email")
    void getUserByEmail() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("test@mail.ru")
                .name("Test")
                .age(25)
                .build();

        when(userService.findByEmail("test@mail.ru")).thenReturn(userDto);

        mockMvc.perform(get("/api/users/findByEmail").param("email", "test@mail.ru"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.age").value(userDto.getAge()));
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    void createUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("test@mail.ru")
                .name("Test")
                .age(25)
                .build();
        CreateUserDto createUserDto = CreateUserDto.builder()
                .email("test@mail.ru")
                .name("Test")
                .age(25)
                .build();

        when(userService.create(createUserDto)).thenReturn(userDto);

        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createUserDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(userDto.getId()));
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Обновление данных пользователя")
    void updateUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("test@mail.ru")
                .name("Test")
                .age(25)
                .build();
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .email("test@mail.ru")
                .name("Test")
                .age(25)
                .build();

        when(userService.update(1L, updateUserDto)).thenReturn(userDto);

        mockMvc.perform(put("/api/users/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.age").value(userDto.getAge()));
    }
}