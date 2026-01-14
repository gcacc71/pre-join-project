package com.ttn.practice.service.impl;

import com.ttn.practice.dto.LeaveRequestRequestDto;
import com.ttn.practice.dto.LeaveRequestResponseDto;
import com.ttn.practice.model.LeaveRequest;
import com.ttn.practice.model.User;
import com.ttn.practice.repository.LeaveRequestRepository;
import com.ttn.practice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 1. Dùng Mockito để giả lập (không cần @SpringBootTest)
public class LeaveRequestServiceImplTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository; // Giả lập Repository

    @Mock
    private UserService userService; // Giả lập UserService

    @InjectMocks
    private LeaveRequestServiceImpl leaveRequestService; // Inject 2 cái giả ở trên vào Service thật này

    // --- TEST CASE 1: TẠO THÀNH CÔNG (HAPPY PATH) ---
    @Test
    public void createLeaveRequest_WhenValidInfo_ShouldSuccess() {
        // --- GIVEN ---
        // 1. Giả lập dữ liệu đầu vào
        LeaveRequestRequestDto requestDto = new LeaveRequestRequestDto();
        requestDto.setStartDate(LocalDate.now().plusDays(10));
        requestDto.setEndDate(LocalDate.now().plusDays(12));
        requestDto.setReason("Nghỉ phép năm");

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setFullName("Nhan Test");
        mockUser.setUsername("tonhanlk1@gmail.com");
        mockUser.setPassword("asdasdasd23213@");

        // 2. Dạy các Mock object cách cư xử
        // Khi gọi lấy user -> trả về mockUser
        when(userService.getCurrentUser()).thenReturn(mockUser);

        // Khi check conflict -> trả về List rỗng (Nghĩa là KHÔNG TRÙNG)
        when(leaveRequestRepository.countConflictLeaveRequest(anyInt(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Khi save -> Trả về chính entity đó (giả vờ đã lưu xong)
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenAnswer(invocation -> {
            LeaveRequest savedArg = invocation.getArgument(0);
            savedArg.setId(999); // Giả vờ DB đã sinh ID
            return savedArg;
        });

        // --- WHEN ---
        LeaveRequestResponseDto result = leaveRequestService.createLeaveRequest(requestDto);

        // --- THEN ---
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("PENDING");
        // Kiểm tra xem hàm save có thực sự được gọi 1 lần không
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
    }

    // --- TEST CASE 2: LỖI NGÀY KHÔNG HỢP LỆ (Start > End) ---
    @Test
    public void createLeaveRequest_WhenStartDateAfterEndDate_ShouldThrowException() {
        // --- GIVEN ---
        LeaveRequestRequestDto requestDto = new LeaveRequestRequestDto();
        requestDto.setStartDate(LocalDate.now().plusDays(10));
        requestDto.setEndDate(LocalDate.now().plusDays(5)); // Lỗi: Kết thúc trước khi bắt đầu

        // Mock user (vì dòng lấy user chạy trước dòng check date)
        when(userService.getCurrentUser()).thenReturn(new User());

        // --- WHEN & THEN ---
        // Mong đợi ném ra RuntimeException với message cụ thể
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            leaveRequestService.createLeaveRequest(requestDto);
        });

        assertThat(exception.getMessage()).isEqualTo("Start or end date is not valid!");

        // Đảm bảo không bao giờ gọi xuống DB save
        verify(leaveRequestRepository, never()).save(any());
    }

    // --- TEST CASE 3: LỖI BỊ TRÙNG LỊCH (CONFLICT) ---
    @Test
    public void createLeaveRequest_WhenConflict_ShouldThrowException() {
        // --- GIVEN ---
        LeaveRequestRequestDto requestDto = new LeaveRequestRequestDto();

        // SỬA: Dùng ngày tương lai để vượt qua hàm checkValidDate
        LocalDate futureStart = LocalDate.now().plusDays(10);
        LocalDate futureEnd = LocalDate.now().plusDays(12);

        requestDto.setStartDate(futureStart);
        requestDto.setEndDate(futureEnd);

        User mockUser = new User();
        mockUser.setId(1);

        when(userService.getCurrentUser()).thenReturn(mockUser);

        // Mock Conflict: Khi check trùng thì trả về List có dữ liệu
        // Lưu ý: Dùng any() như mình đã hướng dẫn ở bài trước
        when(leaveRequestRepository.countConflictLeaveRequest(anyInt(), any(), any(), any()))
                .thenReturn(List.of(new LeaveRequest()));

        // --- WHEN & THEN ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            leaveRequestService.createLeaveRequest(requestDto);
        });

        // Bây giờ nó sẽ vượt qua checkValidDate và bị chặn ở checkConflict
        assertThat(exception.getMessage()).isEqualTo("Start or end date is conflict!");

        verify(leaveRequestRepository, never()).save(any());
    }
}
