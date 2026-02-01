package com.example.projectTest.service;

import com.example.projectTest.dto.CreateUserDto;
import com.example.projectTest.dto.UpdateUserDto;
import com.example.projectTest.dto.UserDto;
import com.example.projectTest.entity.User;
import com.example.projectTest.exception.DuplicateEmailException;
import com.example.projectTest.exception.EmptyListException;
import com.example.projectTest.exception.UserNotFoundException;
import com.example.projectTest.kafka.UserEventProducer;
import com.example.projectTest.mapper.UserMapper;
import com.example.projectTest.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final UserEventProducer userEventProducer;


    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackFindAll")
    public List<UserDto> findAll() {
        log.info("Запустился метод получения всех пользователей (findAll) в UserService");
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            log.error("Метод findAll вернул пустой список");
            throw new EmptyListException();
        }
        return users.stream().map(userMapper :: toUserDto).toList();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackFindById")
    public UserDto findById(Long id) {
        log.info("Запустился метод поиска пользователя по Id (findById) в UserService");
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            log.error("Пользователь с id = {} не найден", id);
            throw new UserNotFoundException();
        }
        return userMapper.toUserDto(user.get());
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackFindByEmail")
    public UserDto findByEmail(String email) {
        log.info("Запустился метод поиска пользователя по email (findByEmail) в UserService");
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            log.error("Пользователь с email = {} не найден", email);
            throw new UserNotFoundException();
        }
        return userMapper.toUserDto(optionalUser.get());
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackCreate")
    public UserDto create(CreateUserDto createUserDto) {
        log.info("Запустился метод создания нового пользователя (create) в UserService");
        try {
            User newUser = userMapper.toEntity(createUserDto);
            userRepository.save(newUser);
            log.info("Пользователь {} успешно создан.", newUser);
            userEventProducer.send("CREATED", newUser.getEmail());
            return userMapper.toUserDto(newUser);
        } catch (DataIntegrityViolationException ex) {
            if (Objects.requireNonNull(ex.getRootCause()).getMessage().contains("uk6dotkott2kjsp8vw4d0m25fb7")) {
                throw new DuplicateEmailException();
            }
            log.error("Ошибка при создании пользователя: ", ex);
            throw ex;
        }
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackDelete")
    public void delete(Long id) {
        log.info("Запустился метод удаления пользователя (delete) в UserService");
        User deleteUser = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        userRepository.deleteById(id);
        userEventProducer.send("DELETED", deleteUser.getEmail());
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackUpdate")
    @Transactional
    public UserDto update(Long id, UpdateUserDto updateUserDto) {
        log.info("Запустился метод обновления данных пользователя (update) в UserService");

        User updateUser = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (updateUserDto.getEmail() != null) updateUser.setEmail(updateUserDto.getEmail());
        if (updateUserDto.getName() != null) updateUser.setName(updateUserDto.getName());
        if (updateUserDto.getAge() != null) updateUser.setAge(updateUserDto.getAge());
        try {
            userRepository.save(updateUser);
            userRepository.flush();
            log.info("Данные пользователя успешно обновленны.");
            return userMapper.toUserDto(updateUser);
        } catch (DataIntegrityViolationException ex) {
            if (Objects.requireNonNull(ex.getRootCause()).getMessage().contains("uk6dotkott2kjsp8vw4d0m25fb7")) {
                throw new DuplicateEmailException();
            }
            log.error("Ошибка при обновлении данных пользователя: ", ex);
            throw ex;
        }
    }

    // === FALLBACK-МЕТОДЫ ===

    private List<UserDto> fallbackFindAll(Exception e) {
        log.warn("[Circuit Breaker] findAll fallback вызван. Причина: {}", e.getMessage(), e);
        return List.of();
    }

    private UserDto fallbackFindById(Long id, Exception e) {
        log.warn("[Circuit Breaker] findById fallback вызван для id={}. Причина: {}", id, e.getMessage(), e);
        return UserDto.builder()
                .id(id)
                .name("Недоступно")
                .email("unknown@example.com")
                .age(0)
                .build();
    }

    private UserDto fallbackFindByEmail(String email, Exception e) {
        log.warn("[Circuit Breaker] findByEmail fallback вызван для email={}. Причина: {}", email, e.getMessage(), e);
        return UserDto.builder()
                .name("Недоступно")
                .email(email)
                .age(0)
                .build();
    }

    private UserDto fallbackCreate(CreateUserDto createUserDto, Exception e) {
        log.error("[Circuit Breaker] create fallback вызван. Данные пользователя: {}. Причина: {}", createUserDto, e.getMessage(), e);
        return null;
    }

    private void fallbackDelete(Long id, Exception e) {
        log.error("[Circuit Breaker] delete fallback вызван для id={}. Причина: {}", id, e.getMessage(), e);
    }

    private UserDto fallbackUpdate(Long id, UpdateUserDto updateUserDto, Exception e) {
        log.error("[Circuit Breaker] update fallback вызван для id={}. Данные обновления: {}. Причина: {}",
                id, updateUserDto, e.getMessage(), e);
        return null;
    }
}