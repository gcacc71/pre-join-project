package com.ttn.practice.repository;


import com.ttn.practice.model.Task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public interface TaskRepository extends JpaRepository<Task, Integer> {

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.assignee LEFT JOIN FETCH t.createdBy")
    List<Task> findAllTasks();

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.assignee LEFT JOIN FETCH t.createdBy WHERE t.status = :status")
    List<Task> findTasksByStatus(String status);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.assignee LEFT JOIN FETCH t.createdBy WHERE t.assignee.id = :id")
    List<Task> findTasksByAssigneeID(int id);

    @Query("SELECT u.fullName , COUNT(t) " +
            "FROM Task t " +
            "LEFT JOIN User u ON t.assignee = u " +
            "WHERE t.status != 'DONE' " +
            "GROUP BY u.id, u.fullName")
    List<Objects[]> findUserWorkload();

    @Query("SELECT t.assignee.fullName, t.title, t.description, t.dueDate, lr.startDate, lr.endDate " +
            "FROM Task t " +
            "INNER JOIN LeaveRequest lr ON t.assignee = lr.user " +
            "WHERE lr.status = 'APPROVED' " +
            "AND t.status != 'DONE' " +
            "AND (t.dueDate BETWEEN lr.startDate AND lr.endDate)")
    List<Object[]> findInvalidTask();

}