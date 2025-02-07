package com.soat.hackathon.video_processor.app.config;

import com.soat.hackathon.video_processor.domain.ports.ImagesExtractor;
import com.soat.hackathon.video_processor.domain.ports.VideoSource;
import com.soat.hackathon.video_processor.domain.usecases.GetFramesFromVideoUseCase;
import com.soat.hackathon.video_processor.infrastructure.FrameGrabber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

  @Bean
  public ImagesExtractor imagesExtractor() {
    return new FrameGrabber();
  }

  @Bean
  public GetFramesFromVideoUseCase getFramesFromVideoUseCase(ImagesExtractor imagesExtractor, VideoSource videoSource) {
    return new GetFramesFromVideoUseCase(videoSource, imagesExtractor);
  }
}
