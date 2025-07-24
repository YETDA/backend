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

    private final RedisTemplate<String, String> redisTemplate;
    private final ProjectRepository projectRepository;

    private static final String VIEW_KEY_PREFIX = "project:view:";

    public ProjectViewCountService(@Qualifier("customStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
        ProjectRepository projectRepository) {
        this.redisTemplate = redisTemplate;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Long viewCountProject(Long projectId) {
        addViewCount(projectId);

        Long dbViewCount = projectRepository.findViewCountByProjectId(projectId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND));

        String redisKey = VIEW_KEY_PREFIX + projectId;
        String redisCountStr = redisTemplate.opsForValue().get(redisKey);
        Long redisCount = redisCountStr != null ? Long.parseLong(redisCountStr) : 0L;

        return dbViewCount + redisCount;
    }

    public void addViewCount(Long projectId) {
        String redisKey = VIEW_KEY_PREFIX + projectId;
        redisTemplate.opsForValue().increment(redisKey, 1L);
    }

    @Scheduled(fixedRate = 120000)
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

