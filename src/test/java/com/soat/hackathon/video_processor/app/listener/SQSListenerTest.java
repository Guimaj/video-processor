package com.soat.hackathon.video_processor.app.listener;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soat.hackathon.video_processor.app.dto.ReceivedMessageDto;
import com.soat.hackathon.video_processor.domain.exception.GettingExternalResourceException;
import com.soat.hackathon.video_processor.domain.usecases.GetFramesFromVideoUseCase;
import com.soat.hackathon.video_processor.infrastructure.SendMessageProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SQSListenerTest {

  private SQSListener listener;
  private ObjectMapper mapper;
  private GetFramesFromVideoUseCase getFramesUseCase;
  private SendMessageProvider sendMessageProvider;

  @BeforeEach
  void setUp() {
    mapper = Mockito.mock(ObjectMapper.class);
    getFramesUseCase = Mockito.mock(GetFramesFromVideoUseCase.class);
    sendMessageProvider = Mockito.mock(SendMessageProvider.class);
    listener = new SQSListener(mapper, getFramesUseCase, sendMessageProvider);
  }

  @Test
  void testListenMessageSucessfully() throws IOException, GettingExternalResourceException {
    var message = "{\"id\":\"id\", \"keyVideo\" : \"videos/test.mp4\", \"keyZip\" : \"images/test.zip\", \"intervalInSeconds\" : 20}";

    when(mapper.readValue(message, ReceivedMessageDto.class))
      .thenReturn(new ReceivedMessageDto("id", "videos/test1.mp4", 20, "images/test.zip",0));

    listener.listen(message);

    verify(getFramesUseCase).extractImagesFromVideoAndUploadZip(any());
    verify(sendMessageProvider).sendSucessMessage("id");
  }

  @Test
  void testdonothingwhenparsemessagefailed() throws IOException, GettingExternalResourceException {
    var message = "{\"videoKey\" : \"videos/test1.mp4\", \"intervalInSeconds\" : 20}";

    when(mapper.readValue(message, ReceivedMessageDto.class)).thenThrow(new JsonParseException(""));

    listener.listen(message);

    verify(getFramesUseCase, never()).extractImagesFromVideoAndUploadZip(any());
    verify(sendMessageProvider,never()).sendSucessMessage(any());
    verify(sendMessageProvider,never()).sendMessageToReprocess(any());
    verify(sendMessageProvider,never()).sendErrorStatusMessage(any(), any());
  }

  @Test
  void testSendMessageToReprocess() throws IOException, GettingExternalResourceException {
    var message = "{\"videoKey\" : \"videos/test1.mp4\", \"intervalInSeconds\" : 20}";

    when(mapper.readValue(message, ReceivedMessageDto.class))
      .thenReturn(new ReceivedMessageDto("id", "videos/test1.mp4", 20, "images/test.zip",0));

    Mockito.doThrow(new GettingExternalResourceException(new RuntimeException()))
      .when(getFramesUseCase).extractImagesFromVideoAndUploadZip(any());

    listener.listen(message);

    verify(sendMessageProvider,never()).sendSucessMessage(any());
    verify(sendMessageProvider).sendMessageToReprocess(any());
    verify(sendMessageProvider,never()).sendErrorStatusMessage(any(), any());
  }

  @Test
  void testSendErrorMessage() throws IOException, GettingExternalResourceException {
    var message = "{\"videoKey\" : \"videos/test1.mp4\", \"intervalInSeconds\" : 20}";

    when(mapper.readValue(message, ReceivedMessageDto.class))
      .thenReturn(new ReceivedMessageDto("id", "videos/test1.mp4", 20, "images/test.zip",0));

    Mockito.doThrow(new RuntimeException())
      .when(getFramesUseCase).extractImagesFromVideoAndUploadZip(any());

    listener.listen(message);

    verify(sendMessageProvider,never()).sendSucessMessage(any());
    verify(sendMessageProvider, never()).sendMessageToReprocess(any());
    verify(sendMessageProvider).sendErrorStatusMessage(any(), any());
  }
}