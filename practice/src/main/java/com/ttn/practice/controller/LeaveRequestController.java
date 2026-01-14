package com.ttn.practice.controller;

import com.ttn.practice.dto.LeaveRequestRequestDto;
import com.ttn.practice.dto.LeaveRequestResponseDto;
import com.ttn.practice.service.EmailService;
import com.ttn.practice.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/leave-request")
public class LeaveRequestController {
    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/create")
    public ResponseEntity<LeaveRequestResponseDto> create(@RequestBody LeaveRequestRequestDto leaveRequestRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveRequestService.createLeaveRequest(leaveRequestRequestDto));
    }

    @GetMapping("/get-pending-list-by-department-id/{departmentId}")
    public ResponseEntity<List<LeaveRequestResponseDto>> getListByDepartmentID(@PathVariable int departmentId){
        return ResponseEntity.status(HttpStatus.OK).body(leaveRequestService.getLeaveRequestByDepartmentID(departmentId));
    }

    @PutMapping("/process-status/{leaveRequestId}/")
    public ResponseEntity<LeaveRequestResponseDto> processLeaveRequest(@PathVariable int leaveRequestId,
                                                                       @RequestBody LeaveRequestRequestDto processDto) {
        LeaveRequestResponseDto leaveRequestResponseDto = leaveRequestService.processLeaveRequest(leaveRequestId,
                processDto.getStatus(), processDto.getManagerComment());
        emailService.sendSimpleMessage("tonhanlk113@gmail.com", "test async", "async...");
        return ResponseEntity.status(HttpStatus.OK).body(leaveRequestResponseDto);
    }
}
