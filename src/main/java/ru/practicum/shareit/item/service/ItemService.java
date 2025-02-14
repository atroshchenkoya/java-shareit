package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getItemById(Long itemId);

    Collection<ItemDto> getUserItems(Long userId);

    Collection<ItemDto> searchItems(String text);
}