package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingRqDto;
import ru.practicum.shareit.booking.dto.BookingRsDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() throws Exception {
        try (AutoCloseable ignored = MockitoAnnotations.openMocks(this)) {
            mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }
    }

    @Test
    void shouldCreateBooking() throws Exception {
        BookingRqDto bookingRqDto = BookingRqDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingRsDto bookingRsDto = BookingRsDto.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.createBooking(anyLong(), any(BookingRqDto.class))).thenReturn(bookingRsDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingRqDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingRsDto.getId()));

        verify(bookingService, times(1)).createBooking(anyLong(), any(BookingRqDto.class));
    }

    @Test
    void shouldApproveBooking() throws Exception {
        BookingRsDto bookingRsDto = BookingRsDto.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingRsDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(bookingRsDto.getStatus().toString()));

        verify(bookingService, times(1)).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void shouldGetBookingById() throws Exception {
        BookingRsDto bookingRsDto = BookingRsDto.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingRsDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingRsDto.getId()));

        verify(bookingService, times(1)).getBooking(anyLong(), anyLong());
    }

    @Test
    void shouldGetUserBookings() throws Exception {
        BookingRsDto bookingRsDto = BookingRsDto.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .build();

        List<BookingRsDto> bookings = Collections.singletonList(bookingRsDto);

        when(bookingService.getUserBookings(anyLong(), any())).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(bookingService, times(1)).getUserBookings(anyLong(), any());
    }

    @Test
    void shouldGetOwnerBookings() throws Exception {
        BookingRsDto bookingRsDto = BookingRsDto.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .build();

        List<BookingRsDto> bookings = Collections.singletonList(bookingRsDto);

        when(bookingService.getOwnerBookings(anyLong(), any())).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(bookingService, times(1)).getOwnerBookings(anyLong(), any());
    }
}
