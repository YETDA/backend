package com.funding.backend.domain.user.repository;

import com.funding.backend.domain.user.entity.User;
import com.funding.backend.enums.RoleType;
import com.funding.backend.enums.UserActive;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialIdAndSsoProvider(String socialId, String ssoProvider);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole_Role(RoleType role);

    long countByRole_RoleAndUserActive(RoleType role, UserActive userActive);

}