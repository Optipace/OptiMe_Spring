package com.employee.AuthService.service.impl;

import com.employee.AuthService.dto.request.AuthIdentityRequest;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.enums.RegisterEnum;
import com.employee.AuthService.enums.UserStatusEnum;
import com.employee.AuthService.exception.CustomException;
import com.employee.AuthService.model.User;
import com.employee.AuthService.repository.UserRepository;
import com.employee.AuthService.service.InternalService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class InternalServiceImpl implements InternalService {

    private final UserRepository userRepository;
    @Override
    public ApiResponse<?> createIdentity(AuthIdentityRequest request) {

        User user = userRepository.findByEmployeeId(request.getCreatedBy())
                .orElseThrow(() -> new CustomException("Admin ID not found", HttpStatus.NOT_FOUND));


        if(userRepository.findByEmployeeId(request.getEmployeeId()).isPresent()){
            throw new CustomException("User identity already exists", HttpStatus.BAD_REQUEST);
        }

        User newUser = new User();
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
    public ApiResponse<?> deleteIdentity(String employeeId) {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException("Deletion not possible", HttpStatus.BAD_REQUEST));
        userRepository.delete(user);
        return new ApiResponse<>(
                true,
                "User entity deleted "+user.getEmployeeId(),
                null,
                LocalDateTime.now(),
                200
        );
    }
}
