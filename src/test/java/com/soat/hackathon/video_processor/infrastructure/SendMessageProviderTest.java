package com.soat.hackathon.video_processor.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class SendMessageProviderTest {

  private ObjectMapper mapper;
  private SqsTemplate sqsTemplate;
  private SendMessageProvider provider;

  @BeforeEach
  void setUp() {
    mapper = new ObjectMapper();
    sqsTemplate = mock(SqsTemplate.class);
    provider = new SendMessageProvider(mapper, sqsTemplate, "task", "process");
  }

  @Test
  void testSendSucessMessage() throws JsonProcessingException {
    provider.sendSucessMessage("video");
    verify(sqsTemplate).sendAsync(eq("task"), any(String.class));
  }

  @Test
  void testSendErrorStatusMessage() throws JsonProcessingException {
    provider.sendErrorStatusMessage("{\"id\":\"id\", \"keyVideo\" : \"videos/test.mp4\", \"keyZip\" : \"images/test.zip\", \"intervalInSeconds\" : 20}", new RuntimeException("erro"));
    verify(sqsTemplate).sendAsync("task", "{\"x-amz-arquivo-id\":\"id\",\"status\":\"Falha no Processamento\"}");
  }

  @Test
  void testSendMessageToReprocess() throws JsonProcessingException {
    provider.sendMessageToReprocess("{\"id\":\"id\", \"keyVideo\" : \"videos/test.mp4\", \"keyZip\" : \"images/test.zip\", \"intervalInSeconds\" : 20}");
    verify(sqsTemplate).sendAsync("process",
      "{\"id\":\"id\",\"keyVideo\":\"videos/test.mp4\",\"keyZip\":\"images/test.zip\",\"intervalInSeconds\":20,\"attemptCounter\":1}");
  }

  @Test
  void testDoesntSendMessageToReprocess() throws JsonProcessingException {
    provider.sendMessageToReprocess("{\"id\":\"id\", \"keyVideo\" : \"videos/test.mp4\", \"keyZip\" : \"images/test.zip\", \"intervalInSeconds\" : 20,\"attemptCounter\":3}");
    verify(sqsTemplate, never()).sendAsync(any(), any());
  }
}