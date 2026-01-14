package com.ttn.practice.service.impl;

import com.ttn.practice.dto.LeaveRequestRequestDto;
import com.ttn.practice.dto.LeaveRequestResponseDto;
import com.ttn.practice.model.LeaveRequest;
import com.ttn.practice.model.User;
import com.ttn.practice.repository.LeaveRequestRepository;
import com.ttn.practice.service.LeaveRequestService;
import com.ttn.practice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private UserService userService;

    @Override
    public LeaveRequestResponseDto createLeaveRequest(LeaveRequestRequestDto leaveRequestRequestDto) {
        User currentUser = userService.getCurrentUser();

        boolean isValidDate = checkValidDate(leaveRequestRequestDto.getStartDate(), leaveRequestRequestDto.getEndDate());

        if(!isValidDate){
            throw new RuntimeException("Start or end date is not valid!");
        }

        boolean hasConflict = !leaveRequestRepository.countConflictLeaveRequest(currentUser.getId(), leaveRequestRequestDto.getStartDate(),
                leaveRequestRequestDto.getEndDate(), PageRequest.of(0, 1)).isEmpty();

        if(hasConflict){
            throw new RuntimeException("Start or end date is conflict!");
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setUser(currentUser);
        leaveRequest.setCreatedAt(Instant.now());
        leaveRequest.setReason(leaveRequestRequestDto.getReason());
        leaveRequest.setStatus("PENDING");
        leaveRequest.setStartDate(leaveRequestRequestDto.getStartDate());
        leaveRequest.setEndDate(leaveRequestRequestDto.getEndDate());
        leaveRequest.setManagerComment("No comment yet!");

        return convertToDto(leaveRequestRepository.save(leaveRequest));
    }

    @Override
    public List<LeaveRequestResponseDto> getLeaveRequestByDepartmentID(int departmentId) {
        return leaveRequestRepository.getAllPendingLeaveRequestByDepartmentID(departmentId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveRequestResponseDto processLeaveRequest(int leaveRequestId, String status, String managerComment) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Not found leave request by id: " + leaveRequestId));

        leaveRequest.setStatus(status);
        leaveRequest.setManagerComment(managerComment);
        leaveRequestRepository.save(leaveRequest);
        return LeaveRequestResponseDto.builder()
                .status(leaveRequest.getStatus())
                .startDate(leaveRequest.getStartDate())
                .endDate(leaveRequest.getEndDate())
                .reason(leaveRequest.getReason())
                .createdAt(leaveRequest.getCreatedAt())
                .build();
    }

    private boolean checkValidDate(LocalDate startDate, LocalDate endDate) {
        boolean isNotPast = !startDate.isBefore(LocalDate.now());
        boolean isValidRange = !startDate.isAfter(endDate);
        return isNotPast && isValidRange;
    }

    private LeaveRequestResponseDto convertToDto(LeaveRequest leaveRequest){
        return LeaveRequestResponseDto.builder()
                .startDate(leaveRequest.getStartDate())
                .endDate(leaveRequest.getEndDate())
                .reason(leaveRequest.getReason())
                .status(leaveRequest.getStatus())
                .managerComment(leaveRequest.getManagerComment())
                .createdAt(leaveRequest.getCreatedAt())
                .build();
    }
}
