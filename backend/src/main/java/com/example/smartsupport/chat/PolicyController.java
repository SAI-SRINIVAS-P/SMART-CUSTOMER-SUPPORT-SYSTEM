package com.example.smartsupport.chat;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/policy")
public class PolicyController {
 @GetMapping public Map<String,Object> policy(){return Map.of("title","Returns & refunds","windowDays",30,"summary","Return requests should normally be made within 30 days after delivery. Items may need to be unused and in their original packaging. This demo policy is guidance only; it does not approve or initiate a return.","steps",new String[]{"Locate your order number","Check the delivery date and item eligibility","Contact support to submit a return request"});}
}
