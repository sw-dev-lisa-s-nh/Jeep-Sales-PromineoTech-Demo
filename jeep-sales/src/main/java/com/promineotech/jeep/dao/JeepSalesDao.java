package com.promineotech.jeep.dao;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import com.promineotech.jeep.entity.Image;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;

public interface JeepSalesDao {

  List<Jeep> fetchJeeps(JeepModel model, String trim);
  
  List<Jeep> fetchAllJeeps();

  void saveImage(Image image);

  Optional<Image> retrieveImage(String imageId);

  Jeep createJeep(@Valid Jeep newJeep);

  void deleteJeep(int deleteId);

  
  
}
