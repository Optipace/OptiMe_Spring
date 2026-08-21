package com.employee.AuthService.service.impl;

import com.employee.AuthService.client.EmployeeClient;
import com.employee.AuthService.dto.request.AuthIdentityRequest;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.dto.response.EmployeeResponse;
import com.employee.AuthService.dto.response.NewUserResponse;
import com.employee.AuthService.dto.response.SingleResponse;
import com.employee.AuthService.enums.CustomStatus;
import com.employee.AuthService.enums.RoleEnum;
import com.employee.AuthService.enums.UserStatusEnum;
import com.employee.AuthService.exception.CustomException;
import com.employee.AuthService.model.User;
import com.employee.AuthService.repository.UserRepository;
import com.employee.AuthService.service.InternalService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class InternalServiceImpl implements InternalService {

    private final UserRepository userRepository;

    private final EmployeeClient employeeClient;

    @Override
    public SingleResponse<NewUserResponse> createIdentity(AuthIdentityRequest request) {

        log.info("The creater Id is {}",request.getCreatedBy());

//        User user = userRepository.findByEmployeeId(request.getCreatedBy())
//                .orElseThrow(() -> new CustomException("Admin ID not found", HttpStatus.NOT_FOUND));
        User user = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new CustomException(null, CustomStatus.ADMIN_NOT_FOUND, 404));

        if(!user.getRole().equals(RoleEnum.ADMIN)){
            throw new CustomException(null, CustomStatus.UNAUTHORISED_ACCESS, 401);
        }
        if(userRepository.findByEmployeeId(request.getEmployeeId()).isPresent()){
            throw new CustomException(null, CustomStatus.EMPLOYEE_ID_ALREADY_EXISTS, 400);
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
        newUser = userRepository.save(newUser);

        NewUserResponse newUserResponse = new NewUserResponse(newUser.getId());

        return new SingleResponse<>(
                newUserResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    @Transactional
    public ApiResponse<?> deleteIdentity(String employeeId) {
        userRepository.findByEmployeeId(employeeId).ifPresent(user -> {
            userRepository.delete(user);
            log.info("Rollback executed: User {} deleted.", employeeId);
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
