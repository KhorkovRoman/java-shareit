package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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

    private Long generateBookingId() {
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
        if (!item.getAvailable()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Предмет не доступен.");
        }
        Long ownerId = item.getOwner().getId();
        if (Objects.equals(ownerId, userId)) {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "Владелец предмета не может его бронировать.");
        }
        bookingDtoIn.setStatus(BookingStatus.WAITING);

        Booking booking = BookingMapper.toBooking(generateBookingId(), booker, item, bookingDtoIn);

        return bookingRepository.save(booking);
    }

    public Booking approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND,
                        "Не найдено бронирование с id " + bookingId));
        Item item = booking.getItem();
        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "Пользователь с id " + ownerId + " не является хозяином вещи.");
        }

        if (approved) {
            if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                throw new ValidationException(HttpStatus.BAD_REQUEST,
                        "Бронирование с id " + bookingId + " уже подтверждено.");
            }
            booking.setStatus(BookingStatus.APPROVED);
        }
        if (!approved) {
            if (booking.getStatus().equals(BookingStatus.REJECTED)) {
                throw new ValidationException(HttpStatus.BAD_REQUEST,
                        "Бронирование с id " + bookingId + " уже отменено.");
            }
            booking.setStatus(BookingStatus.REJECTED);
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
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "Пользователь с id " + userId + " не является хозяином или бронирующим вещи.");
        }
        return booking;
    }

    public Collection<Booking> getBookingsByUser(Long userId, State state, PageRequest pageRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND,
                        "Не найден пользователь с id " + userId));
        LocalDateTime dateTimeNow = LocalDateTime.now();
        Page<Booking> bookingCollection = null;

        switch (state) {
            case ALL: bookingCollection = bookingRepository.getAllBookingsByUser(userId, pageRequest);
                break;
            case CURRENT: bookingCollection = bookingRepository.getCurrentBookingsByUser(userId, dateTimeNow, pageRequest);
                break;
            case PAST: bookingCollection = bookingRepository.getPastBookingsByUser(userId, dateTimeNow, pageRequest);
                break;
            case FUTURE: bookingCollection = bookingRepository.getFutureBookingsByUser(userId, dateTimeNow, pageRequest);
                break;
            case WAITING:
                bookingCollection = bookingRepository.getWaitingRejectedBookingsByBooker(userId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookingCollection = bookingRepository.getWaitingRejectedBookingsByBooker(userId, BookingStatus.REJECTED, pageRequest);
                break;
        }
        return bookingCollection.getContent();
    }

    public Collection<Booking> getBookingsByOwner(Long ownerId, State state, PageRequest pageRequest) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND,
                        "Не найден пользователь с id " + ownerId));
        LocalDateTime dateTimeNow = LocalDateTime.now();
        Page<Booking> bookingCollection = null;

        switch (state) {
            case ALL: bookingCollection = bookingRepository.getAllBookingsByOwner(ownerId, pageRequest);
                break;
            case CURRENT: bookingCollection = bookingRepository.getCurrentBookingsByOwner(ownerId, dateTimeNow, pageRequest);
                break;
            case PAST: bookingCollection = bookingRepository.getPastBookingsByOwner(ownerId, dateTimeNow, pageRequest);
                break;
            case FUTURE: bookingCollection = bookingRepository.getFutureBookingsByOwner(ownerId, dateTimeNow, pageRequest);
                break;
            case WAITING:
                bookingCollection = bookingRepository.getWaitingRejectedBookingsByOwner(ownerId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookingCollection = bookingRepository.getWaitingRejectedBookingsByOwner(ownerId, BookingStatus.REJECTED, pageRequest);
                break;
        }
        return bookingCollection.getContent();
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
}
