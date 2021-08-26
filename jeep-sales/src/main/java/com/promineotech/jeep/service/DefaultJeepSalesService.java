package com.promineotech.jeep.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.promineotech.jeep.dao.JeepSalesDao;
import com.promineotech.jeep.entity.Image;
import com.promineotech.jeep.entity.ImageMimeType;
import com.promineotech.jeep.entity.Jeep;
import lombok.extern.slf4j.Slf4j;
import com.promineotech.jeep.entity.JeepModel;


@Service
@Slf4j
public class DefaultJeepSalesService implements JeepSalesService {

  
  @Autowired
  private JeepSalesDao jeepSalesDao;

  @Transactional(readOnly = true)
  @Override
  public Image retrieveImage(String imageId) {  
    return jeepSalesDao.retrieveImage(imageId)
        .orElseThrow(() -> new NoSuchElementException(
            "Could not find image with ID=" + imageId));
  }

  @Transactional
  @Override
  public String uploadImage(MultipartFile file, Long modelPK) {
    String imageId = UUID.randomUUID().toString();
    //log.debug("Uploading image with ID={}", imageId);
    try(InputStream inputStream = file.getInputStream()) {
      BufferedImage bufferedImage = ImageIO.read(inputStream);
      // @formatter:off
      Image image = Image.builder()
          .modelFK(modelPK)
          .imageId(imageId)
          .width(bufferedImage.getWidth())
          .height(bufferedImage.getHeight())
          .mimeType(ImageMimeType.IMAGE_JPEG)
          .name(file.getOriginalFilename())
          .data(toByteArray(bufferedImage, "jpeg"))
          .build();
      // @formatter:on
      
      jeepSalesDao.saveImage(image);
      return imageId;
      
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
  
  private byte[] toByteArray(BufferedImage bufferedImage, String renderType) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      if (!ImageIO.write(bufferedImage, renderType, baos)) {
        throw new RuntimeException("Could not writeto image buffer");
      }     
      return baos.toByteArray();
      
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    
  }

  @Transactional(readOnly = true)
  @Override
  public List<Jeep> fetchJeeps(JeepModel model, String trim) {
    log.info("The fetchJeeps method was called with model={} and trim={}",
        model, trim);
    
    List<Jeep> jeeps = jeepSalesDao.fetchJeeps(model,trim);
    
    if (jeeps.isEmpty()) {
      String msg = String.format("No jeeps found with model=%s and trim=%s",
          model,trim);
      throw new NoSuchElementException(msg);
    }
    Collections.sort(jeeps);;
    return jeeps;
  }
  
  
  @Transactional(readOnly = true)
  @Override
  public List<Jeep> fetchAllJeeps() {
    log.info("In DefaultJeepSalesService.fetchAllJeeps()");
    
    List<Jeep> jeeps = jeepSalesDao.fetchAllJeeps();
    
    if (jeeps.isEmpty()) {
      String msg = String.format("No jeeps found");
      throw new NoSuchElementException(msg);
    }
    Collections.sort(jeeps);;
    return jeeps;
  }

  @Transactional
  @Override
  public Jeep createJeep(@Valid Jeep newJeep) {
    log.info("In DefaultJeepSalesService.createJeep()");
    return jeepSalesDao.createJeep(newJeep);
  }

  @Transactional
  @Override
  public void deleteJeep(int deleteId) {
    log.info("In DefaultJeepSalesService.deleteJeep()");
    jeepSalesDao.deleteJeep(deleteId);
  }
 
}
