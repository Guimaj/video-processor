package com.soat.hackathon.video_processor.domain.entity;

public record VideoData(
  String videoKey,
  String zipKey,
  Integer intervalInSeconds
) {
}
