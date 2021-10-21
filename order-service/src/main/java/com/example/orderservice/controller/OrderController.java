package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.repository.OrderEntity;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-service")
@Slf4j
public class OrderController {
    Environment env;
    OrderService orderService;
    KafkaProducer kafkaProducer;
    OrderProducer orderProducer;

    public OrderController(OrderService orderService, Environment env,
                           KafkaProducer kafkaProducer, OrderProducer orderProducer) {
        this.orderService = orderService;
        this.env = env;
        this.kafkaProducer = kafkaProducer;
        this.orderProducer = orderProducer;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on PORT %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {

        log.info("Before add order data");

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        /* jpa */
        OrderDto createDto = orderService.createOrder(orderDto);
        ResponseOrder returnValue = mapper.map(createDto, ResponseOrder.class);

        /* kafka */
        // orderDto.setOrderId(UUID.randomUUID().toString());
        // orderDto.setTotalPrice(orderDetails.getQuantity() * orderDetails.getUnitPrice());

        /* Send this order to the Kafka */
        kafkaProducer.send("example-order-topic", orderDto);
        // orderProducer.send("orders", orderDto);

        // ResponseOrder returnValue = mapper.map(orderDto, ResponseOrder.class);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{orderId}")
                .buildAndExpand(returnValue.getOrderId())
                .toUri();
        log.info("After added orders data");


        return ResponseEntity.created(location).body(returnValue);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrders(@PathVariable("userId") String userId) {
        log.info("Before retrieve orders data");
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        ModelMapper mapper = new ModelMapper();

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(order -> result.add(mapper.map(order, ResponseOrder.class)));

        /* Resilience4J Test*/
        // try {
        //     Thread.sleep(1000);
        //     throw new Exception("장애 발생")
        // } catch (InterruptedException e) {
        //     log.warn(e.getMessage())
        // }

        log.info("After retrieved orders data");

        return ResponseEntity.ok(result);
    }

}
