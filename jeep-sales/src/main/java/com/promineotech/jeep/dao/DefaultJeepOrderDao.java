package com.promineotech.jeep.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import com.promineotech.jeep.entity.Color;
import com.promineotech.jeep.entity.Customer;
import com.promineotech.jeep.entity.Engine;
import com.promineotech.jeep.entity.FuelType;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.entity.Option;
import com.promineotech.jeep.entity.OptionType;
import com.promineotech.jeep.entity.Order;
import com.promineotech.jeep.entity.Tire;

/**
 * 
 *
 */
@Component
public class DefaultJeepOrderDao implements JeepOrderDao {
  
  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;
  
  @Override
  public Order saveOrder(Customer customer, Jeep jeep, Color color, Engine engine, Tire tire,
      BigDecimal price, List<Option> options) {
    
    SqlParams params = generateInsertSql(customer, jeep, color, engine, tire, price);
    
    KeyHolder keyHolder = new GeneratedKeyHolder();
    
    jdbcTemplate.update(params.sql, params.source, keyHolder);
     
    Long orderPK = keyHolder.getKey().longValue();
    saveOptions(options, orderPK);
    
    // @formatter:off
    return Order.builder()
        .orderPK(orderPK) 
        .customer(customer)
        .model(jeep)
        .color(color)
        .engine(engine)
        .tire(tire)
        .options(options)
        .price(price)        
        .build();
    // @formatter:on
  } 
  
  private void saveOptions(List<Option> options, Long orderPK) {
    for (Option option : options) {
      SqlParams params = generateInsertSql(option, orderPK);
      jdbcTemplate.update(params.sql, params.source);
    }    
  }

  private SqlParams generateInsertSql(Option option, Long orderPK) {
    SqlParams params = new SqlParams();
 // @formatter:off
    params.sql = ""
        +"INSERT INTO order_options ("
        + "option_fk , order_fk" 
        + ") VALUES ("
        + ":option_fk , :order_fk" 
       + ")";
    // @formatter:on   
    
    params.source.addValue("option_fk", option.getOptionPK());
    params.source.addValue("order_fk", orderPK);
    
    return params;
  }

  /**
   * 
   * @param customer
   * @param jeep
   * @param color
   * @param engine
   * @param tire
   * @param price
   * @return
   */
  private SqlParams generateInsertSql(Customer customer, Jeep jeep, Color color, Engine engine,
      Tire tire, BigDecimal price) {
    // @formatter:off
    String sql = ""
        +"INSERT INTO orders ("
        + "customer_fk , color_fk, engine_fk, tire_fk, model_fk, price" 
        + ") VALUES ("
        + ":customer_fk , :color_fk, :engine_fk, :tire_fk, :model_fk, :price" 
        + ")";
    // @formatter:on   
    
    SqlParams params = new SqlParams();
    params.sql = sql;
    params.source.addValue("customer_fk", customer.getCustomerPK());
    params.source.addValue("color_fk", color.getColorPK());
    params.source.addValue("engine_fk", engine.getEnginePK());
    params.source.addValue("tire_fk", tire.getTirePK());
    params.source.addValue("model_fk", jeep.getModelPK());
    params.source.addValue("price", price);
    return params;
  }

  @Override
  public List<Option> fetchOptions(List<String> optionIds) {
   
   if (optionIds.isEmpty()) {
     return new LinkedList<Option>();
   }
    
    Map<String, Object> params = new HashMap<>();
  
    //@formatter:off
    String sql = ""
          +"SELECT * "
          + "FROM options "
          + "WHERE option_id IN( ";
    // @formatter:on
 
    for (int index = 0; index < optionIds.size(); index++) {
      String key = "option_" + index;
      
      sql += ":" + key + ", "; 
      params.put(key,  optionIds.get(index));
    }
    // Remove the last ", "
    sql = sql.substring(0, sql.length() - 2);
    sql += ")";
   
    
    return jdbcTemplate.query(sql,  params, new RowMapper<Option>() {

      @Override
      public Option mapRow(ResultSet rs, int rowNum) throws SQLException {
        // @formatter:off
        return Option.builder()
            .optionPK(rs.getLong("option_pk"))
            .optionId(rs.getString("option_id"))
            .category(OptionType.valueOf(rs.getString("category")))
            .manufacturer(rs.getString("manufacturer"))
            .name(rs.getString("name"))
            .price(rs.getBigDecimal("price"))
            .build();
        // @formatter:on
      }});
  }
  
  protected List<Option> fetchOptionsByPK (int orderPK) {
     
     Map<String, Object> params = new HashMap<>();
   
     //@formatter:off
     String sql = ""
           +"SELECT *"
           + " FROM options o"
           + " INNER JOIN order_options oo ON oo.order_fk = :order_fk"
           + " WHERE o.option_pk = oo.option_fk";
     // @formatter:on
    
     params.put("order_fk", orderPK);
      
     return jdbcTemplate.query(sql,  params, new RowMapper<Option>() {

       @Override
       public Option mapRow(ResultSet rs, int rowNum) throws SQLException {
         // @formatter:off
         return Option.builder()
             .optionPK(rs.getLong("option_pk"))
             .optionId(rs.getString("option_id"))
             .category(OptionType.valueOf(rs.getString("category")))
             .manufacturer(rs.getString("manufacturer"))
             .name(rs.getString("name"))
             .price(rs.getBigDecimal("price"))
             .build();
         // @formatter:on
       }});
   }
  
  @Override
  public Optional<Customer> fetchCustomer(String customerId) {
  
    //@formatter:off
    String sql = ""
          +"SELECT * "
          + "FROM customers "
          + "WHERE customer_id = :customer_id";
    // @formatter:on
 
    Map<String, Object> params = new HashMap<>();
    params.put("customer_id", customerId);
  
  
    return Optional.ofNullable(
        jdbcTemplate.query(sql,params, new CustomerResultSetExtractor()));
   
  }
  
  protected Customer fetchCustomerByPK(int customerPK) {
    //@formatter:off
    String sql = ""
          +"SELECT * "
          + "FROM customers "
          + "WHERE customer_pk = :customer_pk";
    // @formatter:on
 
    Map<String, Object> params = new HashMap<>();
    params.put("customer_pk", customerPK);
  
    return 
        jdbcTemplate.query(sql,params, new CustomerResultSetExtractor());
  }
 
  
  @Override
  public Optional<Jeep> fetchModel(JeepModel model, String trim, int doors) {
    //@formatter:off
    String sql = ""
          +"SELECT * "
          + "FROM models "
          + "WHERE model_id = :model_id"
          + " AND trim_level = :trim_level"
          + " AND num_doors = :num_doors";
    // @formatter:on
 
    Map<String, Object> params = new HashMap<>();
    params.put("model_id", model.toString());
    params.put("trim_level", trim);
    params.put("num_doors", doors);
  
  
    return Optional.ofNullable( 
        jdbcTemplate.query(sql,params, new ModelResultSetExtractor()));
    
  }
  
  @Override
  public Optional<Color> fetchColor(String colorId) {
    //@formatter:off
    String sql = ""
          +"SELECT * "
          + "FROM colors "
          + "WHERE color_id = :color_id";
    // @formatter:on
 
    Map<String, Object> params = new HashMap<>();
    params.put("color_id", colorId);

    return Optional.ofNullable(
        jdbcTemplate.query(sql,params, new ColorResultSetExtractor()));
    
  }
  

  protected Color fetchColorByPK(int colorPK) {
    //@formatter:off
    String sql = ""
          +"SELECT * "
          + "FROM colors "
          + "WHERE color_pk = :color_pk";
    // @formatter:on
 
    Map<String, Object> params = new HashMap<>();
    params.put("color_pk", colorPK);

    return 
        jdbcTemplate.query(sql,params, new ColorResultSetExtractor());
    
  }
  
  @Override
  public Optional<Engine> fetchEngine(String engineId) {
    //@formatter:off
    String sql = ""
          +"SELECT * "
          + "FROM engines "
          + "WHERE engine_id = :engine_id";
    // @formatter:on
 
    Map<String, Object> params = new HashMap<>();
    params.put("engine_id", engineId);

    return Optional.ofNullable(
        jdbcTemplate.query(sql,params, new EngineResultSetExtractor()));
    
  }
  
  protected Engine fetchEngineByPK(int enginePK) {
    //@formatter:off
    String sql = ""
          +"SELECT * "
          + "FROM engines "
          + "WHERE engine_pk = :engine_pk";
    // @formatter:on
 
    Map<String, Object> params = new HashMap<>();
    params.put("engine_pk", enginePK);

    return 
        jdbcTemplate.query(sql,params, new EngineResultSetExtractor());
  }
  
  
  /**
   * 
   */
  @Override
  public Optional<Tire> fetchTire(String tireId) {
    //@formatter:off
    String sql = ""
          +"SELECT * "
          + "FROM tires "
          + "WHERE tire_id = :tire_id";
    // @formatter:on
 
    Map<String, Object> params = new HashMap<>();
    params.put("tire_id", tireId);

    return Optional.ofNullable(
        jdbcTemplate.query(sql,params, new TireResultSetExtractor()));
    
  }
  
  protected Tire fetchTireByPK(int tirePK) {
    //@formatter:off
    String sql = ""
          +"SELECT * "
          + "FROM tires "
          + "WHERE tire_pk = :tire_pk";
    // @formatter:on
 
    Map<String, Object> params = new HashMap<>();
    params.put("tire_pk", tirePK);

    return 
        jdbcTemplate.query(sql,params, new TireResultSetExtractor());
  }
  
  /**
   * 
   * @author Promineo Tech Videos
   *
   */
  class CustomerResultSetExtractor implements ResultSetExtractor<Customer> {
      @Override
      public Customer extractData(ResultSet rs) 
          throws SQLException, DataAccessException {
        rs.next();
        // @formatter:off
        return Customer.builder()
            .customerId(rs.getString("customer_id"))
            .customerPK(rs.getLong("customer_pk"))
            .firstName(rs.getString("first_name"))
            .lastName(rs.getString("last_name"))
            .phone(rs.getString("phone"))
            .build();
        // @formatter:on
      }
    } 
  
  class ModelResultSetExtractor implements ResultSetExtractor<Jeep> {
    @Override
    public Jeep extractData(ResultSet rs) 
        throws SQLException, DataAccessException {
      rs.next();
      // @formatter:off
      return Jeep.builder()
          .modelPK(rs.getLong("model_pk"))
          .modelId(JeepModel.valueOf(rs.getString("model_id")))
          .trimLevel(rs.getString("trim_level"))
          .numDoors(rs.getInt("num_doors"))
          .wheelSize(rs.getInt("wheel_size"))
          .basePrice(rs.getBigDecimal("base_price"))         
          .build();
      // @formatter:on
    }
  } 
  
  class ColorResultSetExtractor implements ResultSetExtractor<Color> {
    @Override
    public Color extractData(ResultSet rs) 
        throws SQLException, DataAccessException {
      rs.next();
      // @formatter:off
      return Color.builder()
          .colorPK(rs.getLong("color_pk"))
          .colorId(rs.getString("color_id"))
          .color(rs.getString("color"))
          .price(rs.getBigDecimal("price"))
          .isExterior(rs.getBoolean("is_exterior"))
          .build();
      // @formatter:on
    }
  } 
  
  class EngineResultSetExtractor implements ResultSetExtractor<Engine> {
    @Override
    public Engine extractData(ResultSet rs) 
        throws SQLException, DataAccessException {
      rs.next();
      // @formatter:off
      return Engine.builder()
          .enginePK(rs.getLong("engine_pk"))
          .engineId(rs.getString("engine_id"))
          .sizeInLiters(rs.getFloat("size_in_liters"))
          .name(rs.getString("name"))
          .fuelType(FuelType.valueOf(rs.getString("fuel_type")))
          .mpgCity(rs.getFloat("mpg_city"))
          .mpgHwy(rs.getFloat("mpg_hwy"))
          .hasStartStop(rs.getBoolean("has_start_stop"))
          .description(rs.getString("description"))
          .price(rs.getBigDecimal("price"))
          .build();
      // @formatter:on
    }
  } 
  /**
   * 
   * @author Promineo Tech Videos
   *
   */
  class TireResultSetExtractor implements ResultSetExtractor<Tire> {
    @Override
    public Tire extractData(ResultSet rs) 
        throws SQLException, DataAccessException {
      rs.next();
      // @formatter:off
      return Tire.builder()
          .tirePK(rs.getLong("tire_pk"))
          .manufacturer(rs.getString("manufacturer"))
          .price(rs.getBigDecimal("price"))
          .tireId(rs.getString("tire_id"))
          .tireSize(rs.getString("tire_size"))
          .warrantyMiles(rs.getInt("warranty_miles"))
          .build();
      // @formatter:on
    }
  }
   class SqlParams {
     String sql;
     MapSqlParameterSource source = new MapSqlParameterSource();
   }
 
   @Override
   public List<Order> fetchAllOrders() {
     
     //@formatter:off
     String sql = ""
           +"SELECT * "
           + "FROM orders; ";
     // @formatter:on
   
     return jdbcTemplate.query(sql,
         new RowMapper<Order>() {
           @Override
           public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
             // @formatter:off
             
             // first do a query on the Options table with the model_fk....
             //Optional<Option> options = fetchOptions(????);
             
             return Order.builder()
                 .orderPK(rs.getLong("order_pk"))
                 .price(rs.getBigDecimal("price"))  
                 .customer(fetchCustomerByPK(rs.getInt("customer_fk")))
                 .color(fetchColorByPK(rs.getInt("color_fk")))
                 .tire(fetchTireByPK(rs.getInt("tire_fk")))
                 .model(fetchModelByPK(rs.getInt("model_fk")))
                 .engine(fetchEngineByPK(rs.getInt("engine_fk")))
                 .options(fetchOptionsByPK(rs.getInt("order_pk")))
                 .build();
  
             // @formatter:on
           }});
  
    
   }

  @Override
   public Optional<Jeep> fetchModel(JeepModel model) {
     //@formatter:off
     String sql = ""
           +"SELECT * "
           + "FROM models "
           + "WHERE model_id = :model_id;";
     // @formatter:on
  
     Map<String, Object> params = new HashMap<>();
     params.put("model_id", model.toString());
   
  
     return Optional.ofNullable( 
         jdbcTemplate.query(sql,params, new ModelResultSetExtractor()));
     
   }
   
   public Jeep fetchModelByPK(int modelPK) {
     //@formatter:off
     String sql = ""
           +"SELECT * "
           + "FROM models "
           + "WHERE model_pk = :model_pk;";
     // @formatter:on
  
     Map<String, Object> params = new HashMap<>();
     params.put("model_pk", modelPK);
   
  
     return 
         jdbcTemplate.query(sql,params, new ModelResultSetExtractor());
     
   }
   
}
   
 