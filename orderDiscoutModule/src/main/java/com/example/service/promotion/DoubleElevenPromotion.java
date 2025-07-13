package com.example.service.promotion;

import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.model.OrderSummary;
import com.example.model.Product;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DoubleElevenPromotion implements Promotion {
    private final int groupSize;
    private final double discountPercentage;

    public DoubleElevenPromotion(int groupSize, double discountPercentage) {
        this.groupSize = groupSize;
        this.discountPercentage = discountPercentage;
    }

    @Override
    public void apply(Order order, OrderSummary summary) {
        int totalDiscount = 0;

        // Group order items by product to apply discount per identical item group
        Map<Product, List<OrderItem>> itemsByProduct = order.getItems().stream()
                .collect(Collectors.groupingBy(OrderItem::getProduct));

        for (Map.Entry<Product, List<OrderItem>> entry : itemsByProduct.entrySet()) {
            Product product = entry.getKey();
            int totalQuantity = entry.getValue().stream().mapToInt(OrderItem::getQuantity).sum();
            int numberOfGroups = totalQuantity / groupSize;

            if (numberOfGroups > 0) {
                int discountPerGroup = (int) (product.getUnitPrice() * groupSize * (discountPercentage / 100.0));
                totalDiscount += numberOfGroups * discountPerGroup;
            }
        }
        summary.setDiscount(summary.getDiscount() + totalDiscount);
        summary.setTotalAmount(summary.getTotalAmount() - totalDiscount);
    }
}
