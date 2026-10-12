package com.example.smartsupport.order;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController @RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orders;
    public OrderController(OrderService orders){this.orders=orders;}
    @GetMapping public List<OrderService.OrderView> list(){return orders.list();}
    @GetMapping("/{orderNumber}") public OrderService.OrderView get(@PathVariable String orderNumber) { return orders.getOrder(orderNumber).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Order not found for demo customer")); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public OrderService.OrderView create(@RequestBody OrderService.OrderInput input){return orders.create(input);}
    @PutMapping("/{orderNumber}") public OrderService.OrderView update(@PathVariable String orderNumber,@RequestBody OrderService.OrderInput input){return orders.update(orderNumber,input);}
}
