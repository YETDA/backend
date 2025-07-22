package com.funding.backend.domain.user.repository;

import com.funding.backend.domain.admin.dto.response.UserListDto;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.enums.RoleType;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import com.funding.backend.enums.UserActive;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    long countByRole_Role(RoleType role);

    long countByRole_RoleAndUserActive(RoleType role, UserActive userActive);

    @Query("""
                SELECT new com.funding.backend.domain.admin.dto.response.UserListDto(
                    u.id,
                    u.name,
                    u.createdAt,
                    u.email,
                    COUNT(p),
                    u.approvedReportCount,
                    u.userActive
                )
                FROM User u
                LEFT JOIN u.projectList p
                WHERE u.role.role = :role
                GROUP BY u
            """)
    Page<UserListDto> findByRoleWithStats(
            @Param("role") RoleType role,
            Pageable pageable
    );

}