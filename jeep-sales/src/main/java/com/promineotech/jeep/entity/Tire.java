package com.promineotech.jeep.entity;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tire {
  private Long tirePK;
  private String tireId;
  private String tireSize;
  private String manufacturer;
  private BigDecimal price;
  private int warrantyMiles;
  
  @JsonIgnore
  public Long getTirePK() {
    return tirePK;
  }
  
}
