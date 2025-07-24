package com.funding.backend.domain.project.service;

import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ProjectViewCountService {

    // 명확히 어떤 RedisTemplate 주입할지 @Qualifier 사용
    private final RedisTemplate<String, String> redisTemplate;
    private final ProjectRepository projectRepository;

    private static final String VIEW_KEY_PREFIX = "project:view:";

    // 생성자에서 @Qualifier 붙이기 (롬복 사용 시 직접 작성하거나 필드에 붙이기)
    public ProjectViewCountService(@Qualifier("customStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
        ProjectRepository projectRepository) {
        this.redisTemplate = redisTemplate;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Long viewCountProject(Long projectId) {
        addViewCount(projectId);
        return projectRepository.findViewCountByProjectId(projectId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND));
    }

    public void addViewCount(Long projectId) {
        String redisKey = VIEW_KEY_PREFIX + projectId;
        Long afterIncrement = redisTemplate.opsForValue().increment(redisKey, 1L);
        log.info("Incremented view count for project {}: new value={}", projectId, afterIncrement);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void flushViewCountsToDb() {
        Set<String> keys = redisTemplate.keys(VIEW_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) continue;

            Long projectId = Long.parseLong(key.split(":")[2]);
            Long views = Long.parseLong(value);

            projectRepository.findById(projectId).ifPresent(project -> {
                Long newViewCount = project.getViewCount() + views;
                project.setViewCount(newViewCount);
                projectRepository.save(project);
            });
            redisTemplate.delete(key);
        }
    }
}

