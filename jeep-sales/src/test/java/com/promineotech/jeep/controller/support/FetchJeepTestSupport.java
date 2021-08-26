package com.promineotech.jeep.controller.support;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;

public class FetchJeepTestSupport extends BaseTest {
  protected List<Jeep> buildExpected() {
    List<Jeep> list = new LinkedList<Jeep>();
    
    //@formatter:off    
    list.add(Jeep.builder()
        .modelId(JeepModel.WRANGLER)
        .trimLevel("Sport")
        .numDoors(4)
        .wheelSize(17)
        .basePrice(new BigDecimal("31975.00"))
        .build());
    
    list.add(Jeep.builder()
        .modelId(JeepModel.WRANGLER)
        .trimLevel("Sport")
        .numDoors(2)
        .wheelSize(17)
        .basePrice(new BigDecimal("28475.00"))
        .build());    
    // @formatter:on
       
    Collections.sort(list);
    return list;
  }
  
  /**
   * 
   * @param error
   * @param status
   */
  protected void assertErrorMessageValid(Map<String, Object> error, 
      HttpStatus status) {
    // @formatter:off
    assertThat(error)
      .containsKey("message")
      .containsEntry("status code", status.value())
      // The reason that this doesn't work, is that it currently is set to http://localhost:port/jeeps"
      //    and the port is randomly set -- not 8080
      // Change the GlobalErrorHandler to set "uri" to a substring of the entire uri if it's a ServeletWebRequest
      .containsEntry("uri", "/jeeps")
      //.containsKey("uri")
      .containsKey("timestamp")
      .containsEntry("reason", status.getReasonPhrase());     
    // @formatter:on
  }

}
