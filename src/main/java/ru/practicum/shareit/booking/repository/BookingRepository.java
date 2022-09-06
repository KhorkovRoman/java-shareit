package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 or b.item.owner.id = ?1 " +
            "group by b.id " +
            "order by b.end desc")
    Collection<Booking> getAllBookingsByUser(Long userId);

    @Query("select b " +
            "from Booking b " +
            "where (b.booker.id = ?1 or b.item.owner.id = ?1) and (?2 > b.start and ?2 < b.end)" +
            "group by b.id " +
            "order by b.end desc")
    Collection<Booking> getCurrentBookingsByUser(Long userId, LocalDateTime dateTimeNow);

    @Query("select b " +
            "from Booking b " +
            "where (b.booker.id = ?1 or b.item.owner.id = ?1)  and (?2 > b.end)" +
            "group by b.id " +
            "order by b.end desc")
    Collection<Booking> getPastBookingsByUser(Long userId, LocalDateTime dateTimeNow);

    @Query("select b " +
            "from Booking b " +
            "where (b.booker.id = ?1 or b.item.owner.id = ?1) and (?2 < b.start)" +
            "group by b.id " +
            "order by b.start desc")
    Collection<Booking> getFutureBookingsByUser(Long userId, LocalDateTime dateTimeNow);

    @Query("select b " +
            "from Booking b " +
            "where (b.booker.id = ?1 or b.item.owner.id = ?1) and (b.status = ?2)" +
            "group by b.id " +
            "order by b.end desc")
    Collection<Booking> getWaitingRejectedBookingsByUser(Long userId, String textStatus);
}
