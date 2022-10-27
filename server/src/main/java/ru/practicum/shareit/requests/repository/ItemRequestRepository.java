package ru.practicum.shareit.requests.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.Collection;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select ir " +
            "from ItemRequest ir " +
            "where ir.requester.id = ?1 " +
            "group by ir.id " +
            "order by count(distinct ir.id) desc")
    Collection<ItemRequest> getAllItemRequestsByRequester(Long userId);

    @Query("select ir " +
            "from ItemRequest ir " +
            "where ir.requester.id <> ?1 " +
            "group by ir.id " +
            "order by count(distinct ir.id) desc")
    Page<ItemRequest> findAllItemRequests(Long userId, Pageable pageable);
}
