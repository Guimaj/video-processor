package com.soat.hackathon.video_processor.app.dto;

public record ReceivedMessageDto(
  String videoKey,
  Integer intervalInSeconds,
  int attemptCounter
) {
}
