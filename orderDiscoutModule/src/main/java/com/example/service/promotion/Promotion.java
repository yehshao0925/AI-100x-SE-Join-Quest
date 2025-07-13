package com.example.service.promotion;

import com.example.model.Order;
import com.example.model.OrderSummary;

public interface Promotion {
    void apply(Order order, OrderSummary summary);
}
