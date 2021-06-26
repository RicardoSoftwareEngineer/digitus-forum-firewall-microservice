package com.digitusforum.firewall.model.repository;

import com.digitusforum.firewall.model.entity.EmailVerification;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends CrudRepository<EmailVerification, String> {
    Optional<EmailVerification> findByEmailVerificationId(String resetPasswordId);
    Optional<EmailVerification> findByReadableNumber(Integer readableId);
    EmailVerification findByEmailAndReadableNumber(String userId, Integer readableId);
    EmailVerification findByEmail(String email);
}
