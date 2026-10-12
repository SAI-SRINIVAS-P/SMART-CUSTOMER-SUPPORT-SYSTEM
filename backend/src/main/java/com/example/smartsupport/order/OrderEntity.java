package com.example.smartsupport.order;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity @Table(name="orders")
public class OrderEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=50) private String orderNumber;
    @Column(nullable=false) private Long customerId;
    @Column(nullable=false, length=30) private String status;
    @Column(nullable=false, precision=10, scale=2) private BigDecimal totalAmount;
    private LocalDate estimatedDelivery;
    protected OrderEntity() {}
    public OrderEntity(String orderNumber, Long customerId, String status, BigDecimal totalAmount, LocalDate estimatedDelivery) { this.orderNumber=orderNumber; this.customerId=customerId; this.status=status; this.totalAmount=totalAmount; this.estimatedDelivery=estimatedDelivery; }
    public Long getId(){return id;} public String getOrderNumber(){return orderNumber;} public Long getCustomerId(){return customerId;}
    public String getStatus(){return status;} public BigDecimal getTotalAmount(){return totalAmount;} public LocalDate getEstimatedDelivery(){return estimatedDelivery;}
    public void update(String status, BigDecimal totalAmount, LocalDate estimatedDelivery){this.status=status;this.totalAmount=totalAmount;this.estimatedDelivery=estimatedDelivery;}
}
