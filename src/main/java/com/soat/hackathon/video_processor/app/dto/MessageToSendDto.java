package com.soat.hackathon.video_processor.app.dto;

public record MessageToSendDto(
  String videoKey,
  String error
) {
}
