package com.soat.hackathon.video_processor.domain.ports;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ImagesExtractor {

  void extractFramesToImageZipInOutputStream(InputStream filePath, OutputStream out,
                                             Integer intervalInSeconds) throws IOException;
}
