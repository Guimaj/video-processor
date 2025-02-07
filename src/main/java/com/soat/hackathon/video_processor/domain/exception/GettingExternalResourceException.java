package com.soat.hackathon.video_processor.domain.exception;

public class GettingExternalResourceException extends Exception{
  public GettingExternalResourceException(Exception e) {
    super(e);
  }
}
