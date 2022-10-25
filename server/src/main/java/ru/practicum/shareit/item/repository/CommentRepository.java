package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

@SuppressWarnings("checkstyle:Regexp")
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c " +
            "from Comment c " +
            "where (c.item.id = ?1) " +
            "group by c.id " +
            "order by c.id desc")
    Collection<Comment> getAllCommentsByItem(Long itemId);

}
