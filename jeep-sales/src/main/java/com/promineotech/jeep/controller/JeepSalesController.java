package com.promineotech.jeep.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import com.promineotech.jeep.Constants;
import com.promineotech.jeep.entity.Image;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;

@Validated
@RequestMapping("/jeeps")
@OpenAPIDefinition(info = @Info(title = "Jeep Sales Service"), servers = {
    @Server(url = "http://localhost:8080", description = "Local server.")})
public interface JeepSalesController {

  
  // @formatter:off
  @Operation(
      summary = "Returns all Jeeps",
      description = "Returns all Jeeps",
      responses = {
          @ApiResponse(
              responseCode = "200", 
              description = "All Jeeps are returned in a List",
              content = @Content(
                  mediaType = "application/json", 
                  schema = @Schema(implementation = Jeep.class))),
          @ApiResponse(
              responseCode = "400", 
              description = "Error",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(
              responseCode = "404", 
              description = "No Jeeps were found",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(
              responseCode = "500", 
              description = "An unplanned error occurred.",
              content = @Content(mediaType = "application/json"))
      }
  )
  @GetMapping("/all")
  @ResponseStatus(code = HttpStatus.OK) 
  List<Jeep> fetchAllJeeps();  
  // @formatter:on
  
  
  
  // @formatter:off
  @Operation(
      summary = "Returns a list of Jeeps by specified model & trim",
      description = "Returns a list of Jeeps given an optional model and/or trim",
      responses = {
          @ApiResponse(
              responseCode = "200", 
              description = "A list of Jeeps is returned",
              content = @Content(
                  mediaType = "application/json", 
                  schema = @Schema(implementation = Jeep.class))),
          @ApiResponse(
              responseCode = "400", 
              description = "The request parameters are invalid",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(
              responseCode = "404", 
              description = "No Jeeps were found with the input criteria",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(
              responseCode = "500", 
              description = "An unplanned error occurred.",
              content = @Content(mediaType = "application/json"))
      },
      parameters = {
          @Parameter(
              name = "model", 
              allowEmptyValue = false, 
              required = false, 
              description = "The model name (i.e., 'WRANGLER'"),
          @Parameter(
              name = "trim", 
              allowEmptyValue = false, 
              required = false, 
              description = "The trim level (i.e., 'Sport' ")
          
      }
  )
  @GetMapping
  @ResponseStatus(code = HttpStatus.OK)
  
  List<Jeep> fetchJeeps(
      @RequestParam(required = false) 
            JeepModel model, 
      @Length(max = Constants.TRIM_MAX_LENGTH)
      @Pattern(regexp = "[\\w\\s]*")
      @RequestParam(required = false) 
            String trim);  
  // @formatter:on

 

  /**
   * You should add the OpenAPI documentation!
   * @param image
   * @param jeepPK
   * @return
   */
  
//@formatter:off
 @Operation(
     summary = "An image is stored with a specified Jeep by jeepPK",
     description = "An image is stored with a specified Jeep by jeepPK",
     responses = {
         @ApiResponse(
             responseCode = "201", 
             description = "An image is stored with a Jeep",
             content = @Content(
                 mediaType = "application/json", 
                 schema = @Schema(implementation = Jeep.class))),
         @ApiResponse(
             responseCode = "400", 
             description = "The request parameters are invalid",
             content = @Content(mediaType = "application/json")),
         @ApiResponse(
             responseCode = "500", 
             description = "An unplanned error occurred.",
             content = @Content(mediaType = "application/json"))
     }
  )
  @PostMapping("/{jeepPK}/image")
  @ResponseStatus(code = HttpStatus.CREATED)
 
  String uploadImage(@RequestParam("image") MultipartFile image, 
      @PathVariable Long jeepPK);
 // @formatter:on

  
  /**
   * 
   * @param imageId
   * @return
   */
 
 // @formatter:off
 @Operation(
     summary = "Return an image with an imageId",
     description = "Return an image",
     responses = {
         @ApiResponse(
             responseCode = "200", 
             description = "Image requested is returned", 
             content = @Content(
                 mediaType = "application/json", 
                 schema = @Schema(implementation = Image.class))),
         @ApiResponse(
             responseCode = "400", 
             description = "Error",
             content = @Content(mediaType = "application/json")),
         @ApiResponse(
             responseCode = "404", 
             description = "No image found with the input criteria",
             content = @Content(mediaType = "application/json")),
         @ApiResponse(
             responseCode = "500", 
             description = "An unplanned error occurred.",
             content = @Content(mediaType = "application/json"))
     }
 )
 @GetMapping("/image/{imageId}")
 ResponseEntity<byte[]> retrieveImage(@PathVariable String imageId);
 // @formatter:on
  
  /**
   * 
   * @param newJeep
   * @return
   */
 // @formatter:off
  @Operation(
      summary = "Create a new Jeep",
      description = "Returns the created Jeep ",
      responses = {
          @ApiResponse(
              responseCode = "201", 
              description = "The created Jeep is returned",
              content = @Content(
                  mediaType = "application/json", 
                  schema = @Schema(implementation = Jeep.class))),
          @ApiResponse(
              responseCode = "400", 
              description = "The request parameters are invalid",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(
              responseCode = "404", 
              description = "A Jeep component was not found with the input criteria",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(
              responseCode = "500", 
              description = "An unplanned error occurred.",
              content = @Content(mediaType = "application/json"))
      }
  )
  @PostMapping
  @ResponseStatus(code = HttpStatus.CREATED)
  
  Jeep createJeep(@Valid @RequestBody Jeep newJeep);    
  //@formatter:on
  
  /**
   * 
   * @param deleteId
   * @return
   */
  // @formatter:off
  @Operation(
      summary = "Delete a  Jeep with a specified jeepId",
      description = "No return value ",
      responses = {
          @ApiResponse(
              responseCode = "200", 
              description = "The requested Jeep has been deleted",
              content = @Content(
                  mediaType = "application/json", 
                  schema = @Schema(implementation = Jeep.class))),
          @ApiResponse(
              responseCode = "404", 
              description = "A Jeep was not found with the input criteria",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(
              responseCode = "500", 
              description = "An unplanned error occurred.",
              content = @Content(mediaType = "application/json"))
      }
  )
  @DeleteMapping("/{jeepPK}")
  @ResponseStatus(code = HttpStatus.OK)
  void deleteJeep(@PathVariable int jeepPK); 
  // @formatter:on
}
  
