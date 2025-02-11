package com.soat.hackathon.video_processor.domain.ports;

import com.soat.hackathon.video_processor.domain.exception.GettingExternalResourceException;

import java.io.InputStream;
import java.io.OutputStream;

public interface VideoSource {

  InputStream downloadVideoAsStream(String videoKey) throws GettingExternalResourceException;

  OutputStream createResourceOutputStream(String zipKey) throws GettingExternalResourceException;
}
