package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequesterIdOrderByTimestampDesc(Long requesterId);

    List<ItemRequest> findByRequesterIdNotOrderByTimestampDesc(Long requesterId);
}