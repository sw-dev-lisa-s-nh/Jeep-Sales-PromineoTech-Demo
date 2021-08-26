package com.promineotech.jeep.entity;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Color {
  private Long colorPK;
  private String colorId;
  private String color;
  private BigDecimal price;
  private boolean isExterior;
  
  @JsonIgnore
  public Long getColorPK() {
    return colorPK;
  }
}
