package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByOwnerId(Long ownerId);

    @Query(value = """
        SELECT * FROM items i
        WHERE (i.name ILIKE :text
        OR i.description ILIKE :text)
        AND i.available = TRUE
    """, nativeQuery = true)
    Collection<Item> findAvailableItemsByNameOrDescription(@Param("text") String text);
}