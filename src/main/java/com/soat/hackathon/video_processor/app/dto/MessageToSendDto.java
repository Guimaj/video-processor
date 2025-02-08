package com.soat.hackathon.video_processor.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageToSendDto(
  @JsonProperty("x-amz-arquivo-id") String id,
  String status
) {
}
