package com.promineotech.jeep.service;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import com.promineotech.jeep.entity.Image;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;

public interface JeepSalesService {
  
 
  /*
   * function: fetchJeeps
   * parameters:
   *        model
   *        trim
   */
   List<Jeep> fetchJeeps(JeepModel model, String trim);
   
   List<Jeep> fetchAllJeeps();

   /**
    * 
    * @param image
    * @param jeepPK
    * @return
    */
  String uploadImage(MultipartFile image, Long jeepPK);

  /**
   * 
   * @param imageId
   * @return
   */
  Image retrieveImage(String imageId);

  /**
   * 
   * @param newJeep
   * @return
   */
  Jeep createJeep(@Valid Jeep newJeep);

  void deleteJeep(int deleteId);

}
