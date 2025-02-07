package com.soat.hackathon.video_processor.infrastructure;

import com.soat.hackathon.video_processor.domain.ports.ImagesExtractor;
import lombok.NoArgsConstructor;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@NoArgsConstructor
public class FrameGrabber implements ImagesExtractor {

  @Override
  public void extractFramesToImageZipInOutputStream(InputStream filePath, OutputStream out, Integer intervalInSeconds) throws IOException {
    FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath);
    var frameConverter = new Java2DFrameConverter();
    ZipOutputStream imagesZip = new ZipOutputStream(out);

    process(intervalInSeconds, grabber, frameConverter, imagesZip);

    imagesZip.close();
    out.close();
    grabber.close();
  }

  public void process(Integer intervalInSeconds, FFmpegFrameGrabber grabber, Java2DFrameConverter frameConverter, ZipOutputStream imagesZip) throws IOException {
    grabber.start();
    long frameInterval = calculateFrameInterval(intervalInSeconds, grabber);
    for (int i = 0; i < grabber.getLengthInVideoFrames(); i+= (int) frameInterval) {
      grabber.setVideoFrameNumber(i);

      byte[] imageByteArray = getImageByteArray(frameConverter, grabber);
      String imageName = "frame_at_" + Math.ceil(i/ grabber.getFrameRate()) + "s.jpg";
      addImageToZip(imageName, imagesZip, imageByteArray);
    }
  }

  public void addImageToZip(String imageName, ZipOutputStream imagesZip, byte[] imageByteArray) throws IOException {
    var zipEntry = new ZipEntry(imageName);
    imagesZip.putNextEntry(zipEntry);
    imagesZip.write(imageByteArray);
  }

  public byte[] getImageByteArray(Java2DFrameConverter frameConverter, FFmpegFrameGrabber grabber) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ImageIO.write(
      frameConverter.convert(grabber.grabFrame()),
      "jpg",
      output
    );
    return output.toByteArray();
  }

  public long calculateFrameInterval(Integer intervalInSeconds, FFmpegFrameGrabber grabber) {
    return Math.round(grabber.getVideoFrameRate() * intervalInSeconds);
  }
}
