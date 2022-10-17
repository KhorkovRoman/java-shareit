package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i " +
            "from Item i " +
            "where i.owner.id = ?1 " +
            "group by i.id " +
            "order by count(distinct i.id) desc")
    Page<Item> getAllItemsByUser(Long userId, PageRequest pageRequest);

    @Query("select i " +
            "from Item i " +
            "where i.itemRequest.id = ?1 " +
            "group by i.id " +
            "order by count(distinct i.id) desc")
    Collection<Item> getAllItemsByRequest(Long requestId);
}
