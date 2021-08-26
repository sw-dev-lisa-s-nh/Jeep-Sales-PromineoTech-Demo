package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doThrow;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import com.promineotech.jeep.Constants;
import com.promineotech.jeep.JeepSales;
import com.promineotech.jeep.controller.support.FetchJeepTestSupport;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.service.JeepSalesService;




class FetchJeepTest extends  FetchJeepTestSupport {
  
  @Nested
  @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {JeepSales.class})
  @ActiveProfiles("test")
  @Sql(scripts = {"classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
      "classpath:flyway/migrations/V1.1__Jeep_Data.sql"},
      config = @SqlConfig(encoding = "utf-8"))
  class TestsThatDoNotPolluteTheApplicationContext extends  FetchJeepTestSupport{
    @Test
    void testThatJeepsAreReturnedWhenAValidModelAndTrimAreSupplied() {
      //  Given:  a valid model, trim and URI
      JeepModel model = JeepModel.WRANGLER;
      String trim = "Sport";
      String uri = String.format("%s?model=%s&trim=%s", getBaseUriForJeeps(), model, trim);
         
      //  When:  a connection is made to the URI      
      ResponseEntity<List<Jeep>> response = getRestTemplate().exchange(uri, HttpMethod.GET, null, 
          new ParameterizedTypeReference<List<Jeep>>() {});
      //  Then:  a success (OK - 200) status code is returned  (future:  a list of jeeps is returned)
      
      System.out.println("Response Body=" + response.getBody());
      
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      
      // And:  the actual list returned is the same as the expected list
      List<Jeep> actual = response.getBody();
      List<Jeep> expected = buildExpected();   
      // One way to fix this is to set the data PK to null before comparison!
      // actual.forEach(jeep -> jeep.setModelPK(null));
      // System.out.println(expected);      
      assertThat(actual).isEqualTo(expected);
    }

    
    /**
     *  
     */
    @Test
    void testThatAnErrorMessageIsReturnedWhenAnUnknownTrimIsSupplied() {
      //  Given:  a valid model, trim and URI
      JeepModel model = JeepModel.WRANGLER;
      String trim = "Unknown Value";
      String uri = String.format("%s?model=%s&trim=%s", getBaseUriForJeeps(), model, trim);
          
      //  When:  a connection is made to the URI
      
      ResponseEntity<Map<String,Object>> response = getRestTemplate().exchange(uri, HttpMethod.GET, null, 
          new ParameterizedTypeReference<Map<String,Object>>() {});
      
      //  Then:  a not found (404) status code is returned
      System.out.println("Response Body=" + response.getBody());

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      
      // And:  an error message is returned
      Map<String, Object> error = response.getBody();
      
      assertErrorMessageValid(error, HttpStatus.NOT_FOUND);
    }
    
    /**
     * 
     * @param model
     * @param trim
     * @param reason
     */
    @ParameterizedTest
    @MethodSource("com.promineotech.jeep.controller.FetchJeepTest#parametersForInvalidInput")
    void testThatAnErrorMessageIsReturnedWhenAnInvalidTrimIsSupplied(
        String model, String trim, String reason) {
      //  Given:  a valid model, trim and URI
      String uri = String.format("%s?model=%s&trim=%s", getBaseUriForJeeps(), model, trim);
          
      //  When:  a connection is made to the URI    
      ResponseEntity<Map<String,Object>> response = getRestTemplate().exchange(uri, HttpMethod.GET, null, 
          new ParameterizedTypeReference<Map<String,Object>>() {});
      
      //  Then:  a not found (404) status code is returned
      
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
      
      // And:  an error message is returned
      Map<String, Object> error = response.getBody();
      
      assertErrorMessageValid(error, HttpStatus.BAD_REQUEST);
    
    }   
  }
  
  static Stream<Arguments> parametersForInvalidInput() {
    String repeated = new String(new char[Constants.TRIM_MAX_LENGTH + 1]).replace("\0", "C");

    repeated.equals("abcabcabc");
    // @formatter:off
    return Stream.of(
        arguments("WRANGLER","@#$*", "Trim contains non-alphanumeric characters"),
        arguments("WRANGLER", repeated, "Trim length too long"),
        arguments("INVALID", "Sport", "Model is not enum value")
        );
    // @formatter:on
  }
  
  @Nested
  @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {JeepSales.class})
  @ActiveProfiles("test")
  @Sql(scripts = {"classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
      "classpath:flyway/migrations/V1.1__Jeep_Data.sql"},
      config = @SqlConfig(encoding = "utf-8"))
  class TestsThatPolluteTheApplicationContext extends  FetchJeepTestSupport{
    @MockBean
    private JeepSalesService jeepSalesService;
    
    
    /**
     *  
     */
    @Test
    void testThatAnUnplannedErrorResultsInA500Status() {
      //  Given:  a valid model, trim and URI
      JeepModel model = JeepModel.WRANGLER;
      String trim = "Invalid";
      String uri = String.format("%s?model=%s&trim=%s", getBaseUriForJeeps(), model, trim);
      
      // Programming a mock object -- to throw an exception
      doThrow(new RuntimeException("Ouch!")).when(jeepSalesService)
           .fetchJeeps(model,trim);
          
      //  When:  a connection is made to the URI
      
      ResponseEntity<Map<String,Object>> response = getRestTemplate().exchange(uri, HttpMethod.GET, null, 
          new ParameterizedTypeReference<Map<String,Object>>() {});
      
      System.out.println(response.getBody());
      //  Then:  an internal server error (500) status is returned.
      
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
      
      // And:  an error message is returned
      Map<String, Object> error = response.getBody();
      
      assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  
  
    
}
