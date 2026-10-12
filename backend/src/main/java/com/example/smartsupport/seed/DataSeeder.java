package com.example.smartsupport.seed;
import com.example.smartsupport.order.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import java.time.LocalDate;
@Configuration
public class DataSeeder {
    @Bean CommandLineRunner seedOrders(OrderRepository repository) {
        return args -> {
            if(repository.count()==0) {
                repository.save(new OrderEntity("ORD-1001",1L,"SHIPPED",new BigDecimal("1299.00"),LocalDate.now().plusDays(3)));
                repository.save(new OrderEntity("ORD-1002",1L,"DELIVERED",new BigDecimal("799.00"),LocalDate.now().minusDays(4)));
                repository.save(new OrderEntity("ORD-2001",2L,"PROCESSING",new BigDecimal("2499.00"),LocalDate.now().plusDays(5)));
            }
        };
    }
}
