package ru.practicum.shareit.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
@Component
@Getter
@Setter
public class UserStorageInMemory implements UserStorage {

    private final Map<Integer, User> userMap = new HashMap<>();

    private final Set<String> setEmail = new HashSet<>();

    @Override
    public Set<String> getSetEmail() {
        return setEmail;
    }

    @Override
    public User createUser(User user) {
        userMap.put(user.getId(), user);
        setEmail.add(user.getEmail());
        return user;
    }

    @Override
    public User updateUser(User user) {
        userMap.put(user.getId(), user);
        setEmail.remove(user.getEmail());
        setEmail.add(user.getEmail());
        return user;
    }

    @Override
    public User getUserById(Integer userId) {
        return userMap.get(userId);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userMap.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = userMap.get(userId);
        setEmail.remove(user.getEmail());
        userMap.remove(userId);
    }
}
