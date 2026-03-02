package tz.go.mnrt.asert.modules.passwordresettoken.repository;

import org.springframework.stereotype.Repository;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.passwordresettoken.entity.PasswordResetToken;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends BaseRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);
}
