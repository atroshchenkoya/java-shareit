package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
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

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end);

    Optional<Booking> findTopByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime start);

    Optional<Booking> findTopByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime start);

    Optional<Booking> findTopByItemIdAndBookerIdAndEndBeforeOrderByEndDesc(Long itemId, Long bookerId, LocalDateTime now);
}
