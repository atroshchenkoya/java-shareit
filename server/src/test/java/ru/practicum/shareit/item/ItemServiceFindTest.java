package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.service.ItemServiceImpl;


import java.util.List;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

class ItemServiceFindTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void searchItems_shouldReturnItems() {
        Item item1 = Item.builder()
                .name("Item One")
                .description("Description One")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .name("Item Two")
                .description("Description Two")
                .available(true)
                .build();

        when(itemRepository.findAvailableItemsByNameOrDescription("Item"))
                .thenReturn(List.of(item1, item2));

        var items = itemService.searchItems("Item");

        assertThat(items).hasSize(2);
    }
}
