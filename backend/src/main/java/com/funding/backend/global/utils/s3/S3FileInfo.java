package com.funding.backend.global.utils.s3;

public record S3FileInfo(
    String fileUrl,
    String originalFileName,
    Long fileSize,
    String fileType
) {}
