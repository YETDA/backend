package com.funding.backend.domain.project.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/project")
@Validated
@AllArgsConstructor
@Tag(name = "유저 관리 컨트롤러")
@Slf4j
public class ProjectController {
}
