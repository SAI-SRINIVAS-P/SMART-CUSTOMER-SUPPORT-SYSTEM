package com.example.smartsupport.order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
@Service
public class OrderService {
    private final OrderRepository repository; private final long demoCustomerId;
    private static final Pattern ORDER_PATTERN=Pattern.compile("[A-Z0-9-]{3,50}");
    public OrderService(OrderRepository repository, @Value("${app.demo-customer-id:1}") long demoCustomerId) { this.repository=repository; this.demoCustomerId=demoCustomerId; }
    public Optional<OrderView> getOrder(String orderNumber) {
        if(orderNumber==null || !ORDER_PATTERN.matcher(orderNumber.toUpperCase(Locale.ROOT)).matches()) return Optional.empty();
        return repository.findByOrderNumberAndCustomerId(orderNumber.toUpperCase(Locale.ROOT), demoCustomerId).map(this::view);
    }
    public List<OrderView> list(){return repository.findAllByCustomerIdOrderByIdDesc(demoCustomerId).stream().map(this::view).toList();}
    public OrderView create(OrderInput input){
        String number=input.orderNumber().trim().toUpperCase(Locale.ROOT);
        if(!ORDER_PATTERN.matcher(number).matches()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Order number must contain 3-50 letters, numbers or hyphens");
        if(repository.existsByOrderNumber(number)) throw new ResponseStatusException(HttpStatus.CONFLICT,"That order number already exists");
        validate(input);
        return view(repository.save(new OrderEntity(number,demoCustomerId,input.status().toUpperCase(Locale.ROOT),input.totalAmount(),input.estimatedDelivery())));
    }
    public OrderView update(String number, OrderInput input){
        validate(input);
        OrderEntity entity=repository.findByOrderNumberAndCustomerId(number.toUpperCase(Locale.ROOT),demoCustomerId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Order not found"));
        entity.update(input.status().toUpperCase(Locale.ROOT),input.totalAmount(),input.estimatedDelivery()); return view(repository.save(entity));
    }
    private void validate(OrderInput input){
        if(input==null||input.status()==null||!Set.of("PROCESSING","SHIPPED","DELIVERED","CANCELLED","RETURN_REQUESTED").contains(input.status().toUpperCase(Locale.ROOT))) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Choose a supported order status");
        if(input.totalAmount()==null||input.totalAmount().compareTo(BigDecimal.ZERO)<0||input.totalAmount().compareTo(new BigDecimal("99999999.99"))>0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Total must be a non-negative amount");
    }
    private OrderView view(OrderEntity o){return new OrderView(o.getOrderNumber(),o.getStatus(),o.getTotalAmount().toPlainString(),o.getEstimatedDelivery()==null?"Not available":o.getEstimatedDelivery().toString());}
    public record OrderView(String orderNumber,String status,String totalAmount,String estimatedDelivery){}
    public record OrderInput(String orderNumber,String status,BigDecimal totalAmount,LocalDate estimatedDelivery){}
}
