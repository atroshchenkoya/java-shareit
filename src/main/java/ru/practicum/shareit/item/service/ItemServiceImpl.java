package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.UnauthorizedException;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User owner = userService.findById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemStorage.create(item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена."));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Пользователь не является владельцем вещи.");
        }

        if (Objects.nonNull(itemDto.getName())) existingItem.setName(itemDto.getName());
        if (Objects.nonNull(itemDto.getDescription())) existingItem.setDescription(itemDto.getDescription());
        if (Objects.nonNull(itemDto.getAvailable())) existingItem.setAvailable(itemDto.getAvailable());

        return ItemMapper.toItemDto(itemStorage.save(existingItem));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemStorage.findById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена."));
    }

    @Override
    public Collection<ItemDto> getUserItems(Long userId) {
        return itemStorage.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text.isBlank()) return Collections.emptyList();
        return itemStorage.searchAvailableItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
