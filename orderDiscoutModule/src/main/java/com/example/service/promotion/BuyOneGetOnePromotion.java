package com.example.service.promotion;

import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.model.OrderSummary;

import java.util.ArrayList;
import java.util.List;

public class BuyOneGetOnePromotion implements Promotion {
    @Override
    public void apply(Order order, OrderSummary summary) {
        List<OrderItem> newItems = new ArrayList<>();
        for (OrderItem item : summary.getReceivedItems()) {
            if ("cosmetics".equals(item.getProduct().getCategory())) {
                // For buy one get one, if quantity is N, customer receives N + 1
                newItems.add(new OrderItem(item.getProduct(), item.getQuantity() + 1));
            } else {
                newItems.add(item);
            }
        }
        summary.setReceivedItems(newItems);
    }
}
