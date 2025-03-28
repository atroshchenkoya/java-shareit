package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> findByRequestIdIn(List<Long> requestIds);

    @Query(value = """
        SELECT * FROM items i
        WHERE (i.name ILIKE :text
        OR i.description ILIKE :text)
        AND i.available = TRUE
    """, nativeQuery = true)
    Collection<Item> findAvailableItemsByNameOrDescription(@Param("text") String text);
}