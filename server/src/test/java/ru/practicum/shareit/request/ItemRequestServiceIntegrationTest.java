package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    void addRequest_shouldSaveRequest() {
        User requester = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        assertThat(requester.getId()).isNotNull();

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Looking for a drill")
                .requesterId(requester.getId())
                .created(LocalDateTime.now())
                .build();

        ItemRequestDto savedRequestDto = itemRequestService.addRequest(requestDto);

        assertThat(savedRequestDto.getId()).isNotNull();
        assertThat(savedRequestDto.getDescription()).isEqualTo("Looking for a drill");
        assertThat(savedRequestDto.getRequesterId()).isEqualTo(requester.getId());
    }

    @Test
    void addRequest_shouldThrowNotFoundException_whenUserNotFound() {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Looking for a drill")
                .requesterId(999L)
                .created(LocalDateTime.now())
                .build();

        assertThatThrownBy(() -> itemRequestService.addRequest(requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id=999 не найден.");
    }

    @Test
    void getUserRequests_shouldReturnUserRequests() {
        User requester = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        requestRepository.save(ItemRequest.builder()
                .description("Looking for a drill")
                .requester(requester)
                .timestamp(LocalDateTime.now())
                .build());

        requestRepository.save(ItemRequest.builder()
                .description("Looking for a hammer")
                .requester(requester)
                .timestamp(LocalDateTime.now().minusDays(1))
                .build());

        List<ItemRequestDto> requests = itemRequestService.getUserRequests(requester.getId());

        assertThat(requests.size()).isEqualTo(2);
        assertThat(requests.get(0).getDescription()).isEqualTo("Looking for a drill");
        assertThat(requests.get(1).getDescription()).isEqualTo("Looking for a hammer");
    }

    @Test
    void getUserRequests_shouldThrowNotFoundException_whenUserNotFound() {
        assertThatThrownBy(() -> itemRequestService.getUserRequests(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id=999 не найден.");
    }

    @Test
    void getAllRequests_shouldReturnRequestsForOtherUsers() {
        User requester1 = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        User requester2 = userRepository.save(User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build());

        requestRepository.save(ItemRequest.builder()
                .description("Looking for a drill")
                .requester(requester1)
                .timestamp(LocalDateTime.now())
                .build());

        requestRepository.save(ItemRequest.builder()
                .description("Looking for a hammer")
                .requester(requester2)
                .timestamp(LocalDateTime.now().minusDays(1))
                .build());

        List<ItemRequestDto> requests = itemRequestService.getAllRequests(requester1.getId());

        assertThat(requests.size()).isEqualTo(1);
        assertThat(requests.getFirst().getDescription()).isEqualTo("Looking for a hammer");
    }

    @Test
    void getRequestById_shouldReturnRequest() {
        User requester = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        ItemRequest request = requestRepository.save(ItemRequest.builder()
                .description("Looking for a drill")
                .requester(requester)
                .timestamp(LocalDateTime.now())
                .build());

        ItemRequestDto requestDto = itemRequestService.getRequestById(requester.getId(), request.getId());

        assertThat(requestDto).isNotNull();
        assertThat(requestDto.getDescription()).isEqualTo("Looking for a drill");
        assertThat(requestDto.getRequesterId()).isEqualTo(requester.getId());
    }

    @Test
    void getRequestById_shouldThrowNotFoundException_whenRequestNotFound() {
        User requester = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        assertThatThrownBy(() -> itemRequestService.getRequestById(requester.getId(), 999L))  // Non-existent request ID
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос с id=999 не найден.");
    }

}
