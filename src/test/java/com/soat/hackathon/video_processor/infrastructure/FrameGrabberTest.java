package com.soat.hackathon.video_processor.infrastructure;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FrameGrabberTest {

  @Test
  void testCalculateFrameInterval() {
    var grabber = mock(FFmpegFrameGrabber.class);
    when(grabber.getVideoFrameRate()).thenReturn(29.9999);

    var result = new FrameGrabber().calculateFrameInterval(20, grabber);

    assertEquals(600, result);
  }

  @Test
  void testGetImageByteArray() throws IOException {
    var grabber = mock(FFmpegFrameGrabber.class);
    when(grabber.grabFrame()).thenReturn(new Frame(1000,1000,32,1));

    var frameConverter = mock(Java2DFrameConverter.class);
    when(frameConverter.convert(any(Frame.class)))
      .thenReturn(new BufferedImage(1000,1000,1));

    var result = new FrameGrabber().getImageByteArray(frameConverter, grabber);

    assertInstanceOf(byte[].class, result);
  }

  @Test
  void testAddImageToZip() throws IOException {
    var zip = Mockito.mock(ZipOutputStream.class);

    new FrameGrabber().addImageToZip("name", zip, new byte[0]);
    verify(zip).putNextEntry(any());
    verify(zip).write(any());
  }

  @Test
  void testProcess() throws IOException {
    var grabber = mock(FFmpegFrameGrabber.class);
    when(grabber.grabFrame()).thenReturn(new Frame(1000,1000,32,1));
    when(grabber.getVideoFrameRate()).thenReturn(29.9999);
    when(grabber.getLengthInVideoFrames()).thenReturn(500);

    var frameConverter = mock(Java2DFrameConverter.class);
    when(frameConverter.convert(any(Frame.class)))
      .thenReturn(new BufferedImage(1000,1000,1));

    var zip = Mockito.mock(ZipOutputStream.class);

    new FrameGrabber().process(20, grabber, frameConverter,zip);

    verify(grabber).start();
    verify(zip).putNextEntry(any());
    verify(zip).write(any());
  }
}