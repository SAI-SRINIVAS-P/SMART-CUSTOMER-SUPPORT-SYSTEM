package com.example.smartsupport.order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface OrderRepository extends JpaRepository<OrderEntity,Long> {
    Optional<OrderEntity> findByOrderNumberAndCustomerId(String orderNumber, Long customerId);
    List<OrderEntity> findAllByCustomerIdOrderByIdDesc(Long customerId);
    boolean existsByOrderNumber(String orderNumber);
}
