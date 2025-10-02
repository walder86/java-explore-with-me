package ru.practicum.base.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.base.enums.StatusComment;
import ru.practicum.base.model.Comment;
import ru.practicum.base.model.Event;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT e FROM Comment e " +
            "WHERE (e.status IN (:statuses) OR (:statuses) IS NULL) " +
            "AND (e.event.id IN (:events) OR (:events) IS NULL)")
    List<Comment> findEventsByParams(
            @Param("statuses") List<StatusComment> statuses,
            @Param("events") List<Long> events,
            Pageable pageable);

    List<Comment> findAllByStatusAndEvent(StatusComment status, Event event, Pageable pageable);
}
