package com.ttn.practice.repository;

import com.ttn.practice.model.LeaveRequest;
import com.ttn.practice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // 1. Chỉ khởi động JPA và Database H2, không khởi động cả server
public class LeaveRequestRepositoryTest {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private TestEntityManager entityManager; // Dùng để insert dữ liệu mẫu

    private User testUser;

    @BeforeEach
    void setUp() {
        // Tạo User mẫu trước mỗi bài test
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");
        testUser.setPassword("password123@");
        testUser.setRole("EMPLOYEE");
        // Lưu vào H2
        testUser = entityManager.persistAndFlush(testUser);
    }

    @Test
    public void findConflict_WhenDatesOverlap_ShouldReturnData() {
        // --- GIVEN: Đã có 1 đơn từ ngày 10 đến 15 ---
        LeaveRequest oldRequest = new LeaveRequest();
        oldRequest.setUser(testUser);
        oldRequest.setStartDate(LocalDate.of(2025, 1, 10));
        oldRequest.setEndDate(LocalDate.of(2025, 1, 15));
        oldRequest.setStatus("APPROVED");
        entityManager.persistAndFlush(oldRequest);

        // --- WHEN: Tìm conflict cho ngày 14 đến 16 (Bị trùng ngày 14, 15) ---
        List<LeaveRequest> result = leaveRequestRepository.countConflictLeaveRequest(
                (Integer) testUser.getId(),
                LocalDate.of(2025, 1, 14), // Start mới
                LocalDate.of(2025, 1, 16), // End mới
                PageRequest.of(0, 1)
        );

        // --- THEN: Phải tìm thấy ---
        assertThat(result).hasSize(1);
    }

    @Test
    public void findConflict_WhenDatesSeparate_ShouldReturnEmpty() {
        // --- GIVEN: Đã có 1 đơn từ ngày 10 đến 15 ---
        LeaveRequest oldRequest = new LeaveRequest();
        oldRequest.setUser(testUser);
        oldRequest.setStartDate(LocalDate.of(2025, 1, 10));
        oldRequest.setEndDate(LocalDate.of(2025, 1, 15));
        oldRequest.setStatus("APPROVED");
        entityManager.persistAndFlush(oldRequest);

        // --- WHEN: Xin nghỉ ngày 20 đến 25 (Không trùng) ---
        List<LeaveRequest> result = leaveRequestRepository.countConflictLeaveRequest(
                testUser.getId(),
                LocalDate.of(2025, 1, 20),
                LocalDate.of(2025, 1, 25),
                PageRequest.of(0, 1)
        );

        // --- THEN: Danh sách rỗng ---
        assertThat(result).isEmpty();
    }
}