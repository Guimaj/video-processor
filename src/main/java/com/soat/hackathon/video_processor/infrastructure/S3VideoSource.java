package com.soat.hackathon.video_processor.infrastructure;

import com.soat.hackathon.video_processor.domain.exception.GettingExternalResourceException;
import com.soat.hackathon.video_processor.domain.ports.VideoSource;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Component
public class S3VideoSource implements VideoSource {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final S3Template s3Template;
  private final String bucketName;

  public S3VideoSource(@Autowired S3Template s3Template, @Value("${s3.bucket}") String bucketName) {
    this.s3Template = s3Template;
    this.bucketName = bucketName;
  }

  @Override
  public InputStream downloadVideoAsStream(final String videoKey) throws GettingExternalResourceException {
    try {
      logger.debug("Getting video resource: " + videoKey);
      S3Resource s3Resource = s3Template.download(bucketName, videoKey);
      return s3Resource.getInputStream();
    } catch (IOException e) {
      throw new GettingExternalResourceException(e);
    }
  }

  @Override
  public OutputStream createResourceOutputStream(final String zipKey) throws GettingExternalResourceException {
    try {
      logger.debug("Generate Zip resource for video " + zipKey);
      var resource = s3Template.createResource(bucketName, zipKey);
      return resource.getOutputStream();
    } catch (IOException e) {
      throw new GettingExternalResourceException(e);
    }
  }
}
