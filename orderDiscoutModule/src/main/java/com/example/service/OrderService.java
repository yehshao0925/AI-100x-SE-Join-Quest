package com.example.service;

import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.model.OrderSummary;
import com.example.service.promotion.Promotion;

import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final List<Promotion> promotions;

    public OrderService(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public OrderSummary calculatePrice(Order order) {
        int originalAmount = order.getItems().stream()
                                .mapToInt(item -> item.getSubtotal())
                                .sum();

        OrderSummary summary = new OrderSummary(originalAmount, 0, originalAmount, new ArrayList<>(order.getItems()));

        for (Promotion promotion : promotions) {
            promotion.apply(order, summary);
        }

        return summary;
    }
}
