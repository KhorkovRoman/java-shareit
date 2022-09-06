package ru.practicum.shareit.comment;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class Comment {
    private int id;
    private String text;
    private Item item;
    private User author;
    private LocalDateTime created;
}
