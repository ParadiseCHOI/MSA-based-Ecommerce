package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.repository.UserEntity;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;
    // RestTemplate restTemplate;
    Environment env;
    OrderServiceClient orderServiceClient;
    CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);

        if (userEntity.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }

        return new User(userEntity.get().getEmail(), userEntity.get().getEncryptedPassword(),
                true, true, true, true,
                new ArrayList<>());
    }

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                           OrderServiceClient orderServiceClient, Environment env,
                           CircuitBreakerFactory circuitBreakerFactory) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.orderServiceClient = orderServiceClient;
        this.env = env;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        userDto.setEncryptedPassword(passwordEncoder.encode(userDto.getPassword()));

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userRepository.save(userEntity);

        userDto = mapper.map(userEntity, UserDto.class);
        return userDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        Optional<UserEntity> userEntity = userRepository.findByUserId(userId);

        if (userEntity.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        UserDto userDto = new ModelMapper().map(userEntity.get(), UserDto.class);
        /* ***********************************************
        * Using rest template
        * order_service.url
        * http://127.0.0.1:8000/order-service/%s/orders
        ************************************************ */
        // String orderUrl = String.format(env.getProperty("order_service.url"), userId);
        // ResponseEntity<List<ResponseOrder>> orderListResponse =
        //         restTemplate.exchange(orderUrl, HttpMethod.GET, null,
        //                 new ParameterizedTypeReference<List<ResponseOrder>>() {
        //                 });

        // 주문 정보
        // List<ResponseOrder> orderList = orderListResponse.getBody();

        /* **********************************************
        * Using Feign Client
        ************************************************ */

        /* **********************************************
        *************************************************
        * FeignException handling ***********************
        * ***********************************************
        * List<ResponseOrder> orderList = null;
        * try {
        *     orderList = orderServiceClient.getOrders(userId);
        * } catch (FeignException e) {
        *     log.error(e.getMessage());
        * }
        ********************************************** */

        /* ErrorDecoder */
        // List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);

        /* Circuit Breaker*/
        // 필요로하는 Trace ID, Span ID 파악용
        log.info("Before call Order Microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseOrder> orderList = circuitBreaker.run(()
                        -> orderServiceClient.getOrders(userId),
                throwable -> new ArrayList<>());
        log.info("After called Order Microservice");

        userDto.setOrders(orderList);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);

        if (userEntity.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }

        return new ModelMapper().map(userEntity.get(), UserDto.class);
    }
}
