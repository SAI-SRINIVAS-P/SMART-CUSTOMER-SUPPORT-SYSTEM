package com.example.smartsupport.order;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
@Component
public class OrderTools {
    private final OrderService orders;
    public OrderTools(OrderService orders){this.orders=orders;}
    @Tool(description="Look up the status and estimated delivery of an order belonging to the demo customer. Use the exact order number provided. Do not guess order numbers.")
    public OrderService.OrderView getOrderStatus(String orderNumber) {
        return orders.getOrder(orderNumber).orElse(new OrderService.OrderView(orderNumber,"NOT_FOUND","Unavailable","Unavailable"));
    }
    @Tool(description="Return the general return policy. Returns should normally be requested within 30 days after delivery. This is guidance, not approval.")
    public String getReturnPolicy() {
        return "Demo policy: return requests should be made within 30 days after delivery. This tool provides general guidance only and does not approve a return.";
    }
}
