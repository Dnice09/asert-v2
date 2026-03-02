package tz.go.mnrt.asert.modules.user.service;

import java.security.Principal;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto;
import tz.go.mnrt.asert.modules.user.entity.User;
import tz.go.mnrt.asert.modules.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrentUserServiceImpl extends SimpleSearchService<User> implements CurrentUserService {

    private final UserRepository userRepository;

    @Override
    public Optional<LoggedInUserDto> getLoggedInUser(Principal principal) {
        return userRepository.findLoggedIn(principal.getName());
    }

    @SuppressWarnings("unused")
    @Override
    public Optional<LoggedInUserDto> getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info(auth.getName());
        if (auth == null) {
            return Optional.empty();
        } else {
            return userRepository.findLoggedIn(auth.getName());
        }
    }
}
