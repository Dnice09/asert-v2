package tz.go.mnrt.asert.modules.user.service;

import java.security.Principal;
import java.util.Optional;

import tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto;

public interface CurrentUserService {
    Optional<LoggedInUserDto> getLoggedInUser(Principal principal);

    Optional<LoggedInUserDto> getLoggedInUser();
}
