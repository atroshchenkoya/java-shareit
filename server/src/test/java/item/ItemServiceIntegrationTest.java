package item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest(classes = ShareItServer.class)
//@Transactional
//@Rollback
class ItemServiceIntegrationTest {

//    @Autowired
//    private ItemRepository itemRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ItemService itemService;
//
//    private User user;
//    private Item item;
//
//    @BeforeEach
//    void setUp() {
//        user = new User();
//        user.setName("John Doe");
//        user.setEmail("john.doe@example.com");
//        user = userRepository.save(user);
//
//        item = new Item();
//        item.setName("Test Item");
//        item.setDescription("A sample item for testing");
//        item.setAvailable(true);
//        item.setOwner(user);
//        item = itemRepository.save(item);
//    }
//
//    @Test
//    void getUserItems_shouldReturnItemsForUser() {
//        List<ItemDto> items = (List<ItemDto>) itemService.getUserItems(user.getId());
//
//        assertThat(items).isNotEmpty();
//        assertThat(items).hasSize(1);
//        assertThat(items.getFirst().getId()).isEqualTo(item.getId());
//        assertThat(items.getFirst().getName()).isEqualTo(item.getName());
//    }
}