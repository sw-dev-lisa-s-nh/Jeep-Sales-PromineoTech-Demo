package com.promineotech.jeep.service;

import java.util.List;
import com.promineotech.jeep.entity.Order;
import com.promineotech.jeep.entity.OrderRequest;

public interface JeepOrderService {

  
  /*
   * function: createOrder
   * parameters:
   *        model
   *        trim
   */
   Order createOrder(OrderRequest orderRequest);

  List<Order> fetchAllOrders();

}
