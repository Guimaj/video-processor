package com.soat.hackathon.video_processor.domain.entity;

public record VideoData(
  String name,
  String extension,
  String location,
  Integer intervalInSeconds
) {
}
