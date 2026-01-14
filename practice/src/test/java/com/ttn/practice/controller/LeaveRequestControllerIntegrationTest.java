package com.ttn.practice.controller;

import com.ttn.practice.dto.LeaveRequestRequestDto;
import com.ttn.practice.model.LeaveRequest;
import com.ttn.practice.model.User;
import com.ttn.practice.repository.LeaveRequestRepository;
import com.ttn.practice.repository.UserRepository;
import com.ttn.practice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest // 1. Khởi động TOÀN BỘ ứng dụng (Full Context)
@AutoConfigureMockMvc // 2. Cấu hình MockMvc để gọi API
@Transactional // 3. Sau mỗi hàm test, tự rollback dữ liệu để sạch DB
public class LeaveRequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository; // Repo thật để kiểm tra DB

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService; // Mock User Service để bypass Security

    private User savedUser;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Tạo User thật trong DB để làm test data
        User user = new User();
        user.setUsername("integration_user");
        user.setFullName("Integration Test User");
        user.setRole("MANAGER");
        user.setPassword("sdasdsd@123123");
        savedUser = userRepository.save(user);

        // Giả lập: Khi gọi getCurrentUser thì trả về user vừa lưu trong DB
        when(userService.getCurrentUser()).thenReturn(savedUser);
    }

    @Test
    @WithMockUser(username = "nhan_test", roles = {"MANAGER"})
    public void create_ShouldSaveToDatabase_AndReturn201() throws Exception {
        // --- GIVEN ---
        LeaveRequestRequestDto requestDto = new LeaveRequestRequestDto();
        requestDto.setStartDate(LocalDate.now().plusDays(10)); // 10 ngày nữa
        requestDto.setEndDate(LocalDate.now().plusDays(15));   // 15 ngày nữa;
        requestDto.setReason("Test Integration Flow");

        // --- WHEN: Gọi API /create ---
        mockMvc.perform(post("/api/leave-request/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // --- THEN 1: Check HTTP Response ---
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));

        // --- THEN 2: Quan trọng nhất - Check Database H2 ---
        // Query trực tiếp vào DB xem record đã được tạo ra chưa?
        List<LeaveRequest> requestsInDb = leaveRequestRepository.findAll();

        assertThat(requestsInDb).hasSize(1); // Phải có 1 bản ghi
        LeaveRequest savedRequest = requestsInDb.get(0);

        assertThat(savedRequest.getReason()).isEqualTo("Test Integration Flow");
        assertThat(savedRequest.getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @WithMockUser(username = "nhan_test", roles = {"MANAGER"})
    public void create_WhenDateInvalid_ShouldReturnError() throws Exception {
        // --- GIVEN ---
        LeaveRequestRequestDto requestDto = new LeaveRequestRequestDto();
        // Start date (tương lai) > End date (quá khứ 2025) -> Chắc chắn lỗi
        requestDto.setStartDate(LocalDate.now().plusDays(10));
        requestDto.setEndDate(LocalDate.of(2025, 2, 1));

        // --- WHEN ---
        mockMvc.perform(post("/api/leave-request/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // --- THEN ---
                .andExpect(status().isBadRequest()) // <--- SỬA THÀNH isBadRequest (400)
                .andExpect(jsonPath("$.error").value("Start or end date is not valid!")); // Check JSON trả về

        // DB vẫn phải trống
        assertThat(leaveRequestRepository.findAll()).isEmpty();
    }

    @Test
    @WithMockUser(username = "nhan_test", roles = {"MANAGER"})
    public void create_WhenValidInput_ShouldSuccess() throws Exception {
        // --- GIVEN ---
        LeaveRequestRequestDto requestDto = new LeaveRequestRequestDto();

        // SỬA Ở ĐÂY: Luôn lấy ngày hiện tại cộng thêm, đảm bảo luôn là TƯƠNG LAI
        LocalDate futureStart = LocalDate.now().plusDays(10); // 10 ngày nữa
        LocalDate futureEnd = LocalDate.now().plusDays(15);   // 15 ngày nữa

        requestDto.setStartDate(futureStart);
        requestDto.setEndDate(futureEnd);
        requestDto.setReason("Nghỉ phép Happy Path");

        // --- WHEN ---
        mockMvc.perform(post("/api/leave-request/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))

                // --- THEN ---
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));

        // --- Check DB ---
        List<LeaveRequest> requestsInDb = leaveRequestRepository.findAll();
        assertThat(requestsInDb).isNotEmpty();

        LeaveRequest savedRequest = requestsInDb.get(requestsInDb.size() - 1);
        // Assert ngày tháng cũng phải dùng biến động
        assertThat(savedRequest.getStartDate()).isEqualTo(futureStart);
        assertThat(savedRequest.getEndDate()).isEqualTo(futureEnd);
    }
}