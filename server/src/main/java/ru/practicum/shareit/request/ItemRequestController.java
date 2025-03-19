package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(HEADER_USER_ID) Long userId,
                                     @RequestBody ItemRequestDto requestDto) {
        requestDto.setId(userId);
        return requestService.addRequest(requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(HEADER_USER_ID) Long userId) {
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(HEADER_USER_ID) Long userId) {
        return requestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(HEADER_USER_ID) Long userId,
                                         @PathVariable("requestId") Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}
