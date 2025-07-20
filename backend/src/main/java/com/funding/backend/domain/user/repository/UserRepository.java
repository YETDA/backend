package com.funding.backend.domain.user.repository;

import com.funding.backend.domain.user.entity.User;
import com.funding.backend.enums.RoleType;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialIdAndSsoProvider(String socialId, String ssoProvider);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role.role = :role")
    List<User> findUsersByRole(@Param("role") RoleType role);


}