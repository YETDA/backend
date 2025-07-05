package com.funding.backend.domain.role.repository;
import com.funding.backend.domain.role.entity.Role;
import com.funding.backend.enums.RoleType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByRole(RoleType roleType);
    List<Role> findByRoleIn(List<RoleType> roleTypes);
}

