package com.castlebird.blog.image.dto.result;

public record StorageResult(
    String path,
    String contentType,
    long sizeBytes
) {

}
