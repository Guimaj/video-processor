package com.soat.hackathon.video_processor.app.config;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class SQSTestConfig {

  @Bean
  public SqsTemplate sqsTemplate() {
    return Mockito.mock(SqsTemplate.class);
  }
}
