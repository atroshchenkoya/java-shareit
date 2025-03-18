package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.util.exception.ConditionsNotMetException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.UnauthorizedException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User owner = userService.findById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        if (Objects.isNull(itemDto.getRequestId())) {
            return ItemMapper.toItemDto(itemRepository.save(item));
        }
        ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + itemDto.getId() + " не найден"));
        item.setRequest(itemRequest);
        ItemDto itemDtoForRs = ItemMapper.toItemDto(itemRepository.save(item));
        itemDtoForRs.setRequestId(itemRequest.getId());
        return itemDtoForRs;
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemDto.getId() + " не найдена."));
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Пользователь с id=" + userId + " не является владельцем вещи.");
        }
        if (Objects.nonNull(itemDto.getName())) existingItem.setName(itemDto.getName());
        if (Objects.nonNull(itemDto.getDescription())) existingItem.setDescription(itemDto.getDescription());
        if (Objects.nonNull(itemDto.getAvailable())) existingItem.setAvailable(itemDto.getAvailable());

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        itemDto.setComments(comments);
        return itemDto;
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text.isBlank()) return List.of();
        return itemRepository.findAvailableItemsByNameOrDescription(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getUserItems(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> lastBookings = bookingRepository.findLastBookingsByItemIds(itemIds, LocalDateTime.now());
        List<Booking> nextBookings = bookingRepository.findNextBookingsByItemIds(itemIds, LocalDateTime.now());

        return items.stream()
                .map(item -> {
                    ItemDto itemDto = ItemMapper.toItemDto(item);
                    setBookingDates(itemDto, lastBookings, nextBookings);
                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    private void setBookingDates(ItemDto itemDto, List<Booking> lastBookings, List<Booking> nextBookings) {
        Booking lastBooking = lastBookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemDto.getId()))
                .findFirst()
                .orElse(null);
        Booking nextBooking = nextBookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemDto.getId()))
                .findFirst()
                .orElse(null);
        itemDto.setLastBooking(lastBooking != null ? lastBooking.getStart() : null);
        itemDto.setNextBooking(nextBooking != null ? nextBooking.getStart() : null);
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
        User user = userService.findById(userId);
        bookingRepository.findTopByItemIdAndBookerIdAndEndBeforeOrderByEndDesc(itemId, userId, LocalDateTime.now())
                .orElseThrow(() -> new ConditionsNotMetException("Пользователь c id=" + userId + " не брал вещь c id=" + itemId + " в аренду или аренда еще не завершена"));
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
