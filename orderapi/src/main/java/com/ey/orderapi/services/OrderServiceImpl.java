package com.ey.orderapi.services;

import com.ey.orderapi.exceptions.OrderIdException;
import com.ey.orderapi.models.Order;
import com.ey.orderapi.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;
    @Override
    public Order addOrder(Order order) {

        if(order.getOrderId()>0) {
           return orderRepository.save(order);
        }else
            throw new OrderIdException("OrderId should be > 0");
    }
}
