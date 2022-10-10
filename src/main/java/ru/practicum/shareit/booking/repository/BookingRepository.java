package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@SuppressWarnings("checkstyle:Regexp")
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "group by b.id " +
            "order by b.end desc")
    Page<Booking> getAllBookingsByUser(Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where (b.booker.id = ?1) and (?2 > b.start and ?2 < b.end)" +
            "group by b.id " +
            "order by b.end desc")
    Page<Booking> getCurrentBookingsByUser(Long userId, LocalDateTime dateTimeNow, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where (b.booker.id = ?1)  and (?2 > b.end)" +
            "group by b.id " +
            "order by b.end desc")
    Page<Booking> getPastBookingsByUser(Long userId, LocalDateTime dateTimeNow, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where (b.booker.id = ?1) and (?2 < b.start)" +
            "group by b.id " +
            "order by b.start desc")
    Page<Booking> getFutureBookingsByUser(Long userId, LocalDateTime dateTimeNow, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "group by b.id " +
            "order by b.end desc")
    Page<Booking> getAllBookingsByOwner(Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where (b.item.owner.id = ?1) and (?2 > b.start and ?2 < b.end)" +
            "group by b.id " +
            "order by b.end desc")
    Page<Booking> getCurrentBookingsByOwner(Long userId, LocalDateTime dateTimeNow, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where (b.item.owner.id = ?1)  and (?2 > b.end)" +
            "group by b.id " +
            "order by b.end desc")
    Page<Booking> getPastBookingsByOwner(Long userId, LocalDateTime dateTimeNow, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where (b.item.owner.id = ?1) and (?2 < b.start)" +
            "group by b.id " +
            "order by b.start desc")
    Page<Booking> getFutureBookingsByOwner(Long userId, LocalDateTime dateTimeNow, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where (b.booker.id = ?1) and (b.status = ?2)" +
            "group by b.id " +
            "order by b.end desc")
    Page<Booking> getWaitingRejectedBookingsByBooker(Long bookerId, BookingStatus bookingStatus, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where (b.item.owner.id = ?1) and (b.status = ?2)" +
            "group by b.id " +
            "order by b.end desc")
    Page<Booking> getWaitingRejectedBookingsByOwner(Long ownerId, BookingStatus bookingStatus, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where (b.item.id =?1) and (?2 > b.start)" +
            "group by b.id " +
            "order by b.id desc")
    Page<Booking> findLastBookingsByItemId(Long itemId, LocalDateTime dateTimeNow, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where (b.item.id =?1) and (?2 < b.start)" +
            "group by b.id " +
            "order by b.id asc")
    Page<Booking> findNextBookingsByItemId(Long itemId, LocalDateTime dateTimeNow, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where (b.item.id =?1) and (b.booker.id = ?2) and (?3 > b.end)" +
            "group by b.id " +
            "order by b.id desc")
    Booking findBookerByItemId(Long itemId, Long authorId, LocalDateTime dateTimeNow);
}
