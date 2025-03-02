package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByOwnerId(Long ownerId);

    Collection<Item> findByNameContainingIgnoreCaseAndAvailableTrueOrDescriptionContainingIgnoreCaseAndAvailableTrue(
            String name, String description);
}