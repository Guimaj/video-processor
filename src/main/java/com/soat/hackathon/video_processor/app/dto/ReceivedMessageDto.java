package com.soat.hackathon.video_processor.app.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "keyVideo", "keyZip", "intervalInSeconds","attemptCounter"})
public record ReceivedMessageDto(
  String id,
  String keyVideo,
  Integer intervalInSeconds,
  String keyZip,
  int attemptCounter
) {
}
