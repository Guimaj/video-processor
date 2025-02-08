package com.soat.hackathon.video_processor.domain.usecases;

import com.soat.hackathon.video_processor.domain.entity.VideoData;
import com.soat.hackathon.video_processor.domain.exception.GettingExternalResourceException;
import com.soat.hackathon.video_processor.domain.ports.ImagesExtractor;
import com.soat.hackathon.video_processor.domain.ports.VideoSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetFramesFromVideoUseCaseTest {
  private VideoSource videoSource;
  private ImagesExtractor imagesExtractor;
  private GetFramesFromVideoUseCase getFramesFromVideoUseCase;

  @BeforeEach
  void setUp() {
    videoSource = mock(VideoSource.class);
    imagesExtractor = mock(ImagesExtractor.class);
    getFramesFromVideoUseCase = new GetFramesFromVideoUseCase(videoSource, imagesExtractor);
  }

  @Test
  void testExtractImagesFromVideoAndUploadZip() throws GettingExternalResourceException, IOException {
    var video = new VideoData("name", "ext",  20);

    when(videoSource.downloadVideoAsStream(video.videoKey()))
      .thenReturn(mock(InputStream.class));

    when(videoSource.createResourceOutputStream(video.zipKey()))
      .thenReturn(mock(OutputStream.class));

    getFramesFromVideoUseCase.extractImagesFromVideoAndUploadZip(video);

    verify(videoSource).downloadVideoAsStream(video.videoKey());
    verify(videoSource).createResourceOutputStream(video.zipKey());
    verify(imagesExtractor).extractFramesToImageZipInOutputStream(any(), any(), eq(video.intervalInSeconds()));
  }

  @Test
  void testThrowGettingExternalResourceException() throws GettingExternalResourceException, IOException {
    var video = new VideoData("name", "ext", 20);

    when(videoSource.downloadVideoAsStream(video.videoKey()))
      .thenReturn(mock(InputStream.class));

    when(videoSource.createResourceOutputStream(video.zipKey()))
      .thenReturn(mock(OutputStream.class));

    doThrow(new IOException()).when(imagesExtractor).extractFramesToImageZipInOutputStream(any(), any(), any());

    assertThrows(GettingExternalResourceException.class,
      () -> getFramesFromVideoUseCase.extractImagesFromVideoAndUploadZip(video));
  }
}