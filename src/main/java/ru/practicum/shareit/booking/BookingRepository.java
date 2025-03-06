package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    Optional<Booking> findTopByItemIdAndBookerIdAndEndBeforeOrderByEndDesc(Long itemId, Long bookerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long userId, BookingStatus bookingStatus);

    @Query(value = """
            SELECT b.*
            FROM bookings b
            WHERE b.item_id IN :itemIds
            AND b.start_date = (
                SELECT MAX(b2.start_date)
                FROM bookings b2
                WHERE b2.item_id = b.item_id
                AND b2.start_date < :now
            )
            """, nativeQuery = true)
    List<Booking> findLastBookingsByItemIds(@Param("itemIds") List<Long> itemIds, @Param("now") LocalDateTime now);

    @Query(value = """
            SELECT b.*
            FROM bookings b
            WHERE b.item_id IN :itemIds
            AND b.start_date = (
                SELECT MIN(b2.start_date)
                FROM bookings b2
                WHERE b2.item_id = b.item_id
                AND b2.start_date > :now
            )
            """, nativeQuery = true)
    List<Booking> findNextBookingsByItemIds(@Param("itemIds") List<Long> itemIds, @Param("now") LocalDateTime now);
}
