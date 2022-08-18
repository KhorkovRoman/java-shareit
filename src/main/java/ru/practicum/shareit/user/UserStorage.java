package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {

    Set<String> getSetEmail();

    User createUser(User user);

    User getUserById(Integer userId);

    Collection<User> getAllUsers();

    User updateUser(User user);

    void deleteUser(Integer userId);
}
