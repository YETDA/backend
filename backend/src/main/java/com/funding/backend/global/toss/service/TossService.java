package com.funding.backend.global.toss.service;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TossService {

    @Value("${toss.secret-key}")
    private  String tossSecretKey;


}
