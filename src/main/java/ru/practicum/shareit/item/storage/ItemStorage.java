package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item create(Item item);

    Item save(Item item);

    Optional<Item> findById(Long id);

    Collection<Item> findAllByOwnerId(Long ownerId);

    Collection<Item> searchAvailableItems(String text);
}