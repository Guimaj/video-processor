package com.soat.hackathon.video_processor.app.listener;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soat.hackathon.video_processor.app.dto.ReceivedMessageDto;
import com.soat.hackathon.video_processor.domain.entity.VideoData;
import com.soat.hackathon.video_processor.domain.exception.GettingExternalResourceException;
import com.soat.hackathon.video_processor.domain.usecases.GetFramesFromVideoUseCase;
import com.soat.hackathon.video_processor.infrastructure.SendMessageProvider;
import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SQSListener {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final ObjectMapper mapper;

  private final GetFramesFromVideoUseCase getFramesFromVideoUseCase;
  private final SendMessageProvider sendMessageProvider;

  public SQSListener(ObjectMapper mapper, GetFramesFromVideoUseCase getFramesFromVideoUseCase, SendMessageProvider sendMessageProvider) {
    this.mapper = mapper;
    this.getFramesFromVideoUseCase = getFramesFromVideoUseCase;
    this.sendMessageProvider = sendMessageProvider;
  }

  @SqsListener("${messaging.processing-queue}")
  public void listen(String message) throws IOException {
    logger.debug("Processing {}", message);

    try {
      var dto = mapper.readValue(message, ReceivedMessageDto.class);

      VideoData videoData = new VideoData(dto.keyVideo(), dto.keyZip(), dto.intervalInSeconds());

      getFramesFromVideoUseCase.extractImagesFromVideoAndUploadZip(videoData);

      logger.info("Video {} sucessfully processed.", dto.keyVideo());
      sendMessageProvider.sendSucessMessage(dto.id());

    } catch (JsonParseException e){
      logger.error("Unable to parse message: {}", message, e);
    } catch (S3Exception | GettingExternalResourceException e){
      logger.error(e.getMessage(), e);
      sendMessageProvider.sendMessageToReprocess(message);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      sendMessageProvider.sendErrorStatusMessage(message, e);
    }
  }


}
