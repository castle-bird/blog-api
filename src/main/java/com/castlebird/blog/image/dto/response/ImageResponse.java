package com.castlebird.blog.image.dto.response;

public record ImageResponse(
    Long id,
    String url
) {

  public static ImageResponse of(Long id, String url) {
    return new ImageResponse(id, url);
  }
}
