package com.soat.hackathon.video_processor.domain.usecases;

import com.soat.hackathon.video_processor.domain.entity.VideoData;
import com.soat.hackathon.video_processor.domain.exception.GettingExternalResourceException;
import com.soat.hackathon.video_processor.domain.ports.ImagesExtractor;
import com.soat.hackathon.video_processor.domain.ports.VideoSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

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
    var video = new VideoData("name", "ext", "videos/name.ext", 20);

    when(videoSource.downloadVideoAsStream(video.location()))
      .thenReturn(mock(InputStream.class));

    when(videoSource.createResourceOutputStream(video.name()))
      .thenReturn(mock(OutputStream.class));

    getFramesFromVideoUseCase.extractImagesFromVideoAndUploadZip(video);

    verify(videoSource).downloadVideoAsStream(video.location());
    verify(videoSource).createResourceOutputStream(video.name());
    verify(imagesExtractor).extractFramesToImageZipInOutputStream(any(), any(), eq(video.intervalInSeconds()));
  }

  @Test
  void testThrowGettingExternalResourceException() throws GettingExternalResourceException, IOException {
    var video = new VideoData("name", "ext", "videos/name.ext", 20);

    when(videoSource.downloadVideoAsStream(video.location()))
      .thenReturn(mock(InputStream.class));

    when(videoSource.createResourceOutputStream(video.name()))
      .thenReturn(mock(OutputStream.class));

    doThrow(new IOException()).when(imagesExtractor).extractFramesToImageZipInOutputStream(any(), any(), any());

    assertThrows(GettingExternalResourceException.class,
      () -> getFramesFromVideoUseCase.extractImagesFromVideoAndUploadZip(video));
  }
}