package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@SuppressWarnings("checkstyle:Regexp")
@Slf4j
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    private Long bookingId = 0L;

    public Long generateBookingId() {
        return ++bookingId;
    }

    public Booking createBooking(Long userId, BookingDtoIn bookingDtoIn) {
        validateDate(bookingDtoIn);
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND,
                        "Не найден пользователь с id " + userId));
        Long itemId = bookingDtoIn.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND,
                        "Не найден предмет с id " + itemId));
        Long ownerId = item.getOwner().getId();
        validateUser(booker, userId);
        if (Objects.equals(ownerId, userId)) {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "Владелец предмета не может его бронировать.");
        }
        validateItem(ItemMapper.toItemDto(item));
        bookingDtoIn.setStatus(BookingStatus.WAITING);

        Booking booking = BookingMapper.toBooking(generateBookingId(), booker, item, bookingDtoIn);

        return bookingRepository.save(booking);
    }

    public Booking approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND,
                        "Не найдено бронирование с id " + bookingId));
        Item item = booking.getItem();
        if (Objects.equals(item.getOwner().getId(), ownerId)) {
            if (approved) {
                if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                    throw new ValidationException(HttpStatus.BAD_REQUEST,
                            "Бронирование с id " + bookingId + " уже подтверждено.");
                } else {
                    booking.setStatus(BookingStatus.APPROVED);
                }
            } else {
                if (booking.getStatus().equals(BookingStatus.REJECTED)) {
                    throw new ValidationException(HttpStatus.BAD_REQUEST,
                            "Бронирование с id " + bookingId + " уже отменено.");
                } else {
                    booking.setStatus(BookingStatus.REJECTED);
                }
            }
        } else {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                "Пользователь с id " + ownerId + " не является хозяином вещи.");
        }
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND,
                        "Не найдено бронирование с id " + bookingId));
        Item item = booking.getItem();

        boolean ownerOrBooker = item.getOwner().getId().equals(userId) ||
                booking.getBooker().getId().equals(userId);
        if (!ownerOrBooker) {
            log.info("Пользователь с id " + userId + " не является хозяином или бронирующим вещи.");
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "Пользователь с id " + userId + " не является хозяином или бронирующим вещи.");
        }
        return booking;
    }

    public Collection<Booking> getBookingsByUser(Long userId, State state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND,
                        "Не найден пользователь с id " + userId));
        validateUser(user, userId);
        LocalDateTime dateTimeNow = LocalDateTime.now();
        Collection<Booking> bookingCollection = null;

        switch (state) {
            case ALL: bookingCollection = bookingRepository.getAllBookingsByUser(userId);
                break;
            case CURRENT: bookingCollection = bookingRepository.getCurrentBookingsByUser(userId, dateTimeNow);
                break;
            case PAST: bookingCollection = bookingRepository.getPastBookingsByUser(userId, dateTimeNow);
                break;
            case FUTURE: bookingCollection = bookingRepository.getFutureBookingsByUser(userId, dateTimeNow);
                break;
            case WAITING:
                bookingCollection = bookingRepository.getWaitingRejectedBookingsByBooker(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingCollection = bookingRepository.getWaitingRejectedBookingsByBooker(userId, BookingStatus.REJECTED);
                break;
        }
        return bookingCollection;
    }

    public Collection<Booking> getBookingsByOwner(Long ownerId, State state) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND,
                        "Не найден пользователь с id " + ownerId));
        validateUser(user, ownerId);
        LocalDateTime dateTimeNow = LocalDateTime.now();
        Collection<Booking> bookingCollection = null;

        switch (state) {
            case ALL: bookingCollection = bookingRepository.getAllBookingsByUser(ownerId);
                break;
            case CURRENT: bookingCollection = bookingRepository.getCurrentBookingsByUser(ownerId, dateTimeNow);
                break;
            case PAST: bookingCollection = bookingRepository.getPastBookingsByUser(ownerId, dateTimeNow);
                break;
            case FUTURE: bookingCollection = bookingRepository.getFutureBookingsByUser(ownerId, dateTimeNow);
                break;
            case WAITING:
                bookingCollection = bookingRepository.getWaitingRejectedBookingsByOwner(ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingCollection = bookingRepository.getWaitingRejectedBookingsByOwner(ownerId, BookingStatus.REJECTED);
                break;
        }
        return bookingCollection;
    }

    public void validateDate(BookingDtoIn bookingDtoIn) {
        if (bookingDtoIn.getStart().compareTo(bookingDtoIn.getEnd()) > 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Конец брони раньше начала.");
        }
        if (LocalDateTime.now().compareTo(bookingDtoIn.getEnd()) > 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Конец брони раньше текущего времени.");
        }
        if (LocalDateTime.now().compareTo(bookingDtoIn.getStart()) > 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Начало брони раньше текущего времени.");
        }
    }

    public void validateItem(ItemDto itemDto) {
        if (itemDto.getName() == null) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Не указано название.");
        }
        if (itemDto.getDescription() == null) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Не указано описание.");
        }
        if (!itemDto.getAvailable()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Предмет не доступен.");
        }
    }

    public void validateUser(User booker, Long userId) {
        if (userId == null) {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "Id пользователя не указан.");
        }
        if (booker == null) {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "В базе нет пользователя c id " + userId);
        }
    }
}
