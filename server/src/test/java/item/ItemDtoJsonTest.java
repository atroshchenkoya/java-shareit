package item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = ShareItServer.class)
@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testItemDtoSerialization() throws Exception {
        User user = User.builder().id(1L).name("John").email("john.doe@example.com").build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .owner(user)
                .lastBooking(LocalDateTime.now())
                .nextBooking(LocalDateTime.now().plusDays(1))
                .comments(Collections.emptyList())
                .build();

        String json = objectMapper.writeValueAsString(itemDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Item Name\"");
        assertThat(json).contains("\"description\":\"Item Description\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"owner\":{\"id\":1");
    }

    @Test
    void testItemDtoDeserialization() throws Exception {
        String json = """
                {
                  "id": 1,
                  "name": "Item Name",
                  "description": "Item Description",
                  "available": true,
                  "owner": {"id": 1, "name": "John", "email": "john.doe@example.com"},
                  "lastBooking": "2025-03-19T10:00:00",
                  "nextBooking": "2025-03-20T10:00:00",
                  "comments": []
                }""";

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Item Name");
        assertThat(itemDto.getDescription()).isEqualTo("Item Description");
        assertThat(itemDto.getAvailable()).isEqualTo(true);
        assertThat(itemDto.getOwner().getId()).isEqualTo(1L);
        assertThat(itemDto.getOwner().getName()).isEqualTo("John");
        assertThat(itemDto.getOwner().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(itemDto.getComments()).isEmpty();
    }
}
