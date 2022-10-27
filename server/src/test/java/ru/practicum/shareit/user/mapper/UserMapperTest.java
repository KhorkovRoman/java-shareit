package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserMapperTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    private List<User> userCollection = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "User 1 name", "user1@email.ru"));
        user2 = userRepository.save(new User(2L, "User 2 name", "user2@email.ru"));
        userCollection.add(user1);
        userCollection.add(user2);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        userCollection.clear();
    }

    @Test
    void testToUserDtoCollection() {
        List<UserDto> userDtoCollection = UserMapper.toUserDtoCollection(userCollection);
        assertNotNull(userDtoCollection);
        assertEquals(2, userDtoCollection.size());
        assertEquals(1, userDtoCollection.stream().findFirst().get().getId());
    }

    @Test
    void testToUserDto() {
        UserDto userDto = UserMapper.toUserDto(user1);
        assertNotNull(userDto);
        assertEquals(1, userDto.getId());

    }

    @Test
    void testToUser() {
        UserDto userDto = UserMapper.toUserDto(user1);
        User user = UserMapper.toUser(userDto);
        assertNotNull(user);
        assertEquals(1, user.getId());
    }
}