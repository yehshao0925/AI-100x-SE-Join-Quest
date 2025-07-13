package com.example.service.promotion;

import com.example.model.Order;
import com.example.model.OrderSummary;

public class ThresholdDiscountPromotion implements Promotion {
    private final int threshold;
    private final int discount;

    public ThresholdDiscountPromotion(int threshold, int discount) {
        this.threshold = threshold;
        this.discount = discount;
    }

    @Override
    public void apply(Order order, OrderSummary summary) {
        if (summary.getOriginalAmount() >= threshold) {
            summary.setDiscount(summary.getDiscount() + discount);
        }
    }
}
