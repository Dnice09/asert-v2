package tz.go.mnrt.asert.modules.user.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.configs.NoAuthorization;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.errors.DeleteException;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.passwordresettoken.dtos.PasswordResetDto;
import tz.go.mnrt.asert.modules.user.dtos.ChangePasswordDto;
import tz.go.mnrt.asert.modules.user.dtos.ExternalUserDto;
import tz.go.mnrt.asert.modules.user.dtos.ForgotPasswordDto;
import tz.go.mnrt.asert.modules.user.dtos.UserDto;
import tz.go.mnrt.asert.modules.user.entity.User;
import tz.go.mnrt.asert.modules.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(Constant.API_V1 + "/users")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                userService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(@Valid @RequestBody UserDto userDto) {
        if (userDto.getId() != null || userDto.getUuid() != null) {
            throw new ValidationException("New User cannot contain id or uuid");
        }
        return CustomApiResponse.created("User create successfully", userService.save(userDto));
    }

    @PostMapping("/change-password")
    public CustomApiResponse changePassword(@Valid @RequestBody ChangePasswordDto dto) {
        userService.changePassword(dto);
        return CustomApiResponse.ok("Password updated successfully");
    }

    @PostMapping("/{uuid}/reset-password")
    public CustomApiResponse resetPassword(@PathVariable UUID uuid) {
        userService.resetPassword(uuid);
        return CustomApiResponse.ok("Password reset successfully");
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(@Valid @RequestBody UserDto userDto, @PathVariable UUID uuid) {
        if (userDto.getUuid() == null || !Objects.equals(userDto.getUuid(), uuid)) {
            throw new ValidationException("User uuid must be present and equals to path uuid ");
        }
        userService.save(userDto);
        return CustomApiResponse.ok("User updated successfully");
    }

    @PutMapping("/{uuid}/approve")
    @Transactional
    public CustomApiResponse approve(@Valid @RequestBody UserDto userDto, @PathVariable UUID uuid) {
        if (userDto.getUuid() == null || !Objects.equals(userDto.getUuid(), uuid)) {
            throw new ValidationException("User uuid must be present and equals to path uuid ");
        }
        userService.approve(uuid);
        return CustomApiResponse.ok("User approved successfully");
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(userService.findById(uuid));
    }

    @GetMapping("/{id}/user")
    public CustomApiResponse findByUserId(@PathVariable("id") Long id) {
        return CustomApiResponse.ok(userService.findByUserId(id));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        try {
            userService.delete(uuid);
        } catch (Exception e) {
            throw new DeleteException(User.class.getName());
        }
        return CustomApiResponse.noContent("User deleted successfully");
    }

    @NoAuthorization
    @PostMapping("/register-user")
    @Transactional
    public ResponseEntity<?> registerUser(@Valid @RequestBody ExternalUserDto userDto) {
        if (userDto.getId() != null || userDto.getUuid() != null) {
            return new ResponseEntity<>("New User cannot contain id or uuid", HttpStatus.BAD_REQUEST);
        }
        CustomApiResponse response;
        if (userDto.getRegistrationType().equals("ASSESSOR")) {
            response = CustomApiResponse.created("User create successfully", userService.registerAssessor(userDto));
        } else {
            response = CustomApiResponse.created("User create successfully",
                    userService.registerFacilityServiceOwner(userDto));
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @NoAuthorization
    @PostMapping("/register-assessor")
    @Transactional
    public CustomApiResponse registerAssessor(@Valid @RequestBody ExternalUserDto userDto) {
        if (userDto.getId() != null || userDto.getUuid() != null) {
            throw new ValidationException("New User cannot contain id or uuid");
        }
        return CustomApiResponse.created("User create successfully", userService.registerAssessor(userDto));

    }

    @NoAuthorization
    @PostMapping("/register-facility-service-owner")
    @Transactional
    public CustomApiResponse registerFacilityServiceOwner(@Valid @RequestBody ExternalUserDto userDto) {
        if (userDto.getId() != null || userDto.getUuid() != null) {
            throw new ValidationException("New User cannot contain id or uuid");
        }
        return CustomApiResponse.created("User create successfully",
                userService.registerFacilityServiceOwner(userDto));

    }

    @NoAuthorization
    @PostMapping("/forgot-password")
    public CustomApiResponse forgotPassword(@Valid @RequestBody ForgotPasswordDto dto, HttpServletRequest request) {
        userService.forgotPassword(dto,request);
        return CustomApiResponse.ok("Password reset link sent successfully");
    }

    @NoAuthorization
    @PostMapping("/reset-password")
    public CustomApiResponse resetPassword(@Valid @RequestBody PasswordResetDto dto) {
        userService.resetUserPassword(dto);
        return CustomApiResponse.ok("Password changed successfully");
    }
}
