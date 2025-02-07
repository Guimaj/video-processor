package com.soat.hackathon.video_processor.domain.usecases;

import com.soat.hackathon.video_processor.domain.entity.VideoData;
import com.soat.hackathon.video_processor.domain.exception.GettingExternalResourceException;
import com.soat.hackathon.video_processor.domain.ports.ImagesExtractor;
import com.soat.hackathon.video_processor.domain.ports.VideoSource;

import java.io.IOException;

public class GetFramesFromVideoUseCase {

  private final VideoSource videoSource;
  private final ImagesExtractor imagesExtractor;

  public GetFramesFromVideoUseCase(VideoSource videoSource, ImagesExtractor imagesExtractor) {
    this.videoSource = videoSource;
    this.imagesExtractor = imagesExtractor;
  }

  public void extractImagesFromVideoAndUploadZip(VideoData data) throws GettingExternalResourceException {

    try {
      imagesExtractor.extractFramesToImageZipInOutputStream(
        videoSource.downloadVideoAsStream(data.location()),
        videoSource.createResourceOutputStream(data.name()),
        data.intervalInSeconds()
      );

    } catch (IOException e) {
      throw new GettingExternalResourceException(e);
    }

  }
}
