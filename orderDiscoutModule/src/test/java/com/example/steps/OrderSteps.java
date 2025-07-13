package com.example.steps;

import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.model.Product;
import com.example.model.OrderSummary;
import com.example.service.OrderService;
import com.example.service.promotion.BuyOneGetOnePromotion;
import com.example.service.promotion.Promotion;
import com.example.service.promotion.ThresholdDiscountPromotion;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderSteps {

    private List<Promotion> promotions;
    private OrderService orderService;
    private Order order;
    private OrderSummary orderSummary;

    @Before
    public void setUp() {
        promotions = new ArrayList<>();
    }

    @Given("the threshold discount promotion is configured:")
    public void the_threshold_discount_promotion_is_configured(DataTable dataTable) {
        Map<String, String> promotionConfig = dataTable.asMaps().get(0);
        int threshold = Integer.parseInt(promotionConfig.get("threshold"));
        int discount = Integer.parseInt(promotionConfig.get("discount"));
        promotions.add(new ThresholdDiscountPromotion(threshold, discount));
    }

    @Given("the buy one get one promotion for cosmetics is active")
    public void the_buy_one_get_one_promotion_for_cosmetics_is_active() {
        promotions.add(new BuyOneGetOnePromotion());
    }

    @Given("no promotions are applied")
    public void no_promotions_are_applied() {
        // No promotions added to the list
    }

    @When("a customer places an order with:")
    public void a_customer_places_an_order_with(DataTable dataTable) {
        orderService = new OrderService(promotions);
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map<String, String> columns : rows) {
            Product product = new Product(
                columns.get("productName"),
                columns.get("category"),
                Integer.parseInt(columns.get("unitPrice"))
            );
            orderItems.add(new OrderItem(
                product,
                Integer.parseInt(columns.get("quantity"))
            ));
        }
        order = new Order(orderItems);
        orderSummary = orderService.calculatePrice(order);
    }

    @Then("the order summary should be:")
    public void the_order_summary_should_be(DataTable dataTable) {
        Map<String, String> expectedSummary = dataTable.asMaps().get(0);

        if (expectedSummary.containsKey("originalAmount")) {
            int expectedOriginalAmount = Integer.parseInt(expectedSummary.get("originalAmount"));
            assertEquals(expectedOriginalAmount, orderSummary.getOriginalAmount());
        }
        if (expectedSummary.containsKey("discount")) {
            int expectedDiscount = Integer.parseInt(expectedSummary.get("discount"));
            assertEquals(expectedDiscount, orderSummary.getDiscount());
        }
        int expectedTotalAmount = Integer.parseInt(expectedSummary.get("totalAmount"));
        assertEquals(expectedTotalAmount, orderSummary.getTotalAmount());
    }

    @Then("the customer should receive:")
    public void the_customer_should_receive(DataTable dataTable) {
        List<Map<String, String>> expectedItems = dataTable.asMaps(String.class, String.class);
        List<OrderItem> actualItems = orderSummary.getReceivedItems();

        assertEquals(expectedItems.size(), actualItems.size());

        for (int i = 0; i < expectedItems.size(); i++) {
            Map<String, String> expectedItem = expectedItems.get(i);
            OrderItem actualItem = actualItems.get(i);
            assertEquals(expectedItem.get("productName"), actualItem.getProduct().getName());
            assertEquals(Integer.parseInt(expectedItem.get("quantity")), actualItem.getQuantity());
        }
    }
}
