package com.funding.backend.domain.role.initializer;

import com.funding.backend.domain.role.entity.Role;
import com.funding.backend.domain.role.repository.RoleRepository;
import com.funding.backend.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {  // 앱 시작 시 동작하는 인터페이스

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        insertIfNotExists(RoleType.USER);  // "USER" role 자동으로 추가
    }

    private void insertIfNotExists(RoleType roleType) {
        if (roleRepository.findByRole(roleType).isEmpty()) {
            Role role = Role.builder()
                    .role(roleType)
                    .build();
            roleRepository.save(role);
        }
    }
}