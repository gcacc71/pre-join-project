package com.ttn.practice.service;

import com.ttn.practice.dto.LeaveRequestRequestDto;
import com.ttn.practice.dto.LeaveRequestResponseDto;

import java.util.List;

public interface LeaveRequestService {
    LeaveRequestResponseDto createLeaveRequest(LeaveRequestRequestDto leaveRequestRequestDto);
    List<LeaveRequestResponseDto> getLeaveRequestByDepartmentID(int departmentId);
    LeaveRequestResponseDto processLeaveRequest(int leaveRequestId, String status, String managerComment);
}
