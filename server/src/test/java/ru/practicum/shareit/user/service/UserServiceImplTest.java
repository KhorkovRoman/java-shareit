package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validation.ValidationUser;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Slf4j
@WebMvcTest(controllers = UserServiceImpl.class)
@AutoConfigureMockMvc
class UserServiceImplTest {

    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ValidationUser validationUser;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private User user1;
    private User user2;
    private UserDto userDto;
    private UserDto userDto2;

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(userRepository, validationUser);
        user1 = new User(1L, "user1", "user1@user.com");
        user2 = new User(2L, "user2", "user2@user.com");
        userDto = new UserDto(1L, "user", "user@user.com");
        userDto2 = new UserDto(2L, "user", "user@user.com");
        userRepository.save(user2);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser() {
        when(userService.createUser(userDto))
                .thenReturn(user1);

        assertNotNull(user1);
        assertEquals(1, user1.getId());
    }

    @Test
    void testUpdateUser() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        userService.updateUser(user2.getId(), userDto2);
        Mockito.verify(userRepository, Mockito.times(1))
                .save(user2);
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));

        User user = userService.getUserById(user1.getId());

        assertNotNull(user);
        assertEquals(1, user.getId());
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userRepository.findAll())
                .thenReturn(List.of(user1));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        userService.deleteUser(2L);
        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(anyLong());
    }
}