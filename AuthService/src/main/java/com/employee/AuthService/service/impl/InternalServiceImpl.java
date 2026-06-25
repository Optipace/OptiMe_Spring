package com.employee.AuthService.service.impl;

import com.employee.AuthService.client.EmployeeClient;
import com.employee.AuthService.dto.request.AuthIdentityRequest;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.dto.response.ListOfOfficeResponse;
import com.employee.AuthService.dto.response.OfficeResponse;
import com.employee.AuthService.enums.RoleEnum;
import com.employee.AuthService.enums.UserStatusEnum;
import com.employee.AuthService.exception.CustomException;
import com.employee.AuthService.model.User;
import com.employee.AuthService.repository.UserRepository;
import com.employee.AuthService.service.InternalService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class InternalServiceImpl implements InternalService {

    private final UserRepository userRepository;

    @Override
    public ApiResponse<?> createIdentity(AuthIdentityRequest request) {

        User user = userRepository.findByEmployeeId(request.getCreatedBy())
                .orElseThrow(() -> new CustomException("Admin ID not found", HttpStatus.NOT_FOUND));


        if(userRepository.findByEmployeeId(request.getEmployeeId()).isPresent()){
            throw new CustomException("Employee ID already exists", HttpStatus.BAD_REQUEST);
        }

        User newUser = new User();
        newUser.setUserName(request.getEmployeeName());
        newUser.setEmployeeId(request.getEmployeeId());
        newUser.setEmailId(request.getEmailId());
        newUser.setContact(request.getContact());
//        newUser.setRegisterStatus(RegisterEnum.N);
        newUser.setRole(request.getRole());
        newUser.setCreatedOn(LocalDateTime.now());
        newUser.setCreatedBy(request.getCreatedBy());
        newUser.setUserStatus(UserStatusEnum.INACTIVE);
        userRepository.save(newUser);

        return new ApiResponse<>(
                true,
                "Identity created successfully",
                null,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    @Transactional
    public ApiResponse<?> deleteIdentity(String employeeId) {
        userRepository.findByEmployeeId(employeeId).ifPresent(user -> {
            userRepository.delete(user);
            System.out.println("Rollback executed: User " + employeeId + " deleted.");
        });

        // We return 200 OK even if the user wasn't found, because the end goal
        // (making sure the user doesn't exist) is achieved either way!
        return new ApiResponse<>(
                true,
                "User identity rollback processed",
                null,
                LocalDateTime.now(),
                200
        );
    }
}
