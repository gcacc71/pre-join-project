package com.ttn.practice.repository;


import com.ttn.practice.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {

    @Query("SELECT lr FROM LeaveRequest lr " +
            "WHERE lr.user.id = :userId " +
            "AND lr.status NOT IN ('REJECTED', 'CANCELLED')" +
            "AND lr.endDate >= :startDate " +
            "AND lr.startDate <= :endDate")
    List<LeaveRequest> countConflictLeaveRequest(int userId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("SELECT lr FROM LeaveRequest lr " +
            "JOIN FETCH lr.user " +
            "WHERE lr.status = 'PENDING' " +
            "AND lr.user.department.id = :departmentId")
    List<LeaveRequest> getAllPendingLeaveRequestByDepartmentID(int departmentId);

}
