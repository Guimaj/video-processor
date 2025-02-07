package com.soat.hackathon.video_processor.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soat.hackathon.video_processor.app.dto.MessageToSendDto;
import com.soat.hackathon.video_processor.app.dto.ReceivedMessageDto;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SendMessageProvider {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final ObjectMapper mapper;

  private final SqsTemplate sqsTemplate;
  private final String taskStatusQueue;
  private final String processQueue;

  public SendMessageProvider(
    ObjectMapper mapper, SqsTemplate sqsTemplate,
    @Value("${messaging.task-status-queue}") String taskStatusQueue,
    @Value("${messaging.processing-queue}") String processQueue) {
    this.mapper = mapper;
    this.sqsTemplate = sqsTemplate;
    this.taskStatusQueue = taskStatusQueue;
    this.processQueue = processQueue;
  }

  public void sendSucessMessage(String videoKey) throws JsonProcessingException {
    logger.debug("Sending sucess message to queue {}", taskStatusQueue);
    sqsTemplate.sendAsync(taskStatusQueue,
      mapper.writeValueAsString(new MessageToSendDto(videoKey, null)));
  }

  public void sendErrorStatusMessage(String message, Exception e) throws JsonProcessingException {
    logger.debug("Sending error status message to queue {}", taskStatusQueue);
    var dto = mapper.readValue(message, ReceivedMessageDto.class);
    sqsTemplate.sendAsync(taskStatusQueue,
      mapper.writeValueAsString(new MessageToSendDto(dto.videoKey(), e.getMessage())));
  }

  public void sendMessageToReprocess(String message) throws JsonProcessingException {
    var dto = mapper.readValue(message, ReceivedMessageDto.class);
    if (dto.attemptCounter()<3) {
    logger.info("Sending message to reprocess: {}",message);
      sqsTemplate.sendAsync(
        processQueue,
        mapper.writeValueAsString(new ReceivedMessageDto(dto.videoKey(), dto.intervalInSeconds(), dto.attemptCounter() + 1))
      );
    }
  }
}
