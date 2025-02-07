package com.soat.hackathon.video_processor.infrastructure;

import com.soat.hackathon.video_processor.domain.exception.GettingExternalResourceException;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class S3VideoSourceTest {

  private static final String BUCKET = "bucket";
  private static final String VIDEO = "video";

  private S3Template s3Template;
  private S3VideoSource s3VideoSource;

  @BeforeEach
  void setUp() {
    s3Template = mock(S3Template.class);
    s3VideoSource = new S3VideoSource(s3Template, BUCKET);
  }

  @Test
  void testdownloadVideoAsStream() throws IOException, GettingExternalResourceException {
    var resource = mock(S3Resource.class);
    when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

    when(s3Template.download(BUCKET, VIDEO)).thenReturn(resource);

    var input =s3VideoSource.downloadVideoAsStream(VIDEO);

    assertInstanceOf(InputStream.class, input);
  }

  @Test
  void testdownloadVideoAsStreamThrowException() throws IOException, GettingExternalResourceException {
    var resource = mock(S3Resource.class);
    doThrow(new IOException()).when(resource).getInputStream();

    when(s3Template.download(BUCKET, VIDEO)).thenReturn(resource);

    assertThrows(GettingExternalResourceException.class,
      () -> s3VideoSource.downloadVideoAsStream(VIDEO));
  }

  @Test
  void testcreateResourceOutputStream() throws IOException, GettingExternalResourceException {
    var resource = mock(S3Resource.class);
    when(resource.getOutputStream()).thenReturn(new ByteArrayOutputStream());

    when(s3Template.createResource(eq(BUCKET), contains(VIDEO))).thenReturn(resource);

    var out = s3VideoSource.createResourceOutputStream(VIDEO);
    assertInstanceOf(OutputStream.class, out);
  }

  @Test
  void testcreateResourceOutputStreamThrowException() throws IOException, GettingExternalResourceException {
    var resource = mock(S3Resource.class);
    doThrow(new IOException()).when(resource).getOutputStream();

    when(s3Template.createResource(eq(BUCKET), contains(VIDEO))).thenReturn(resource);

    assertThrows(GettingExternalResourceException.class,
      () -> s3VideoSource.createResourceOutputStream(VIDEO));
  }
}