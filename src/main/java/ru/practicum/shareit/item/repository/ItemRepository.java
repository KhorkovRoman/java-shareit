package ru.practicum.shareit.item.repository;

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
    Collection<Item> getAllItemsByUser(Long userId);

}
