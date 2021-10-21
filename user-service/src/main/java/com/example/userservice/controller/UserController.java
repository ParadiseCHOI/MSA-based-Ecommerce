package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.repository.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
@Slf4j
public class UserController {

    Environment env;
    Greeting greeting;
    UserService userService;

    public UserController(Environment env, Greeting greeting, UserService userService) {
        this.env = env;
        this.greeting = greeting;
        this.userService = userService;
    }

    @GetMapping("/health_check")
    @Timed(value = "users.status", longTask = true)
    public String status() {
        // return "It's Working in User Service";
        return String.format("It's Working in User Service" +
                ", PORT(local.server.port)=%s" +
                ", PORT(server.port)=%s" +
                ", with token secret=%s" +
                ", with token time=%s",
                env.getProperty("local.server.port"), env.getProperty("server.port"),
                env.getProperty("token.secret"), env.getProperty("token.expiration_time"));
    }

    @GetMapping("/welcome")
    @Timed(value = "users.welcome", longTask = true)
    public String welcome() {
        return greeting.getMessage();
        // return env.getProperty("greeting.message");
    }


    /* ************************************
    회원 가입
    POST /user-service/users
    ************************************ */
    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@Valid @RequestBody RequestUser user) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);

        ResponseUser responseUser = mapper.map(userService.createUser(userDto), ResponseUser.class);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userDto.getUserId())
                .toUri();
        return ResponseEntity.created(location).body(responseUser);
    }

    /* ************************************
     * 전체 사용자 목록 조회
     * GET /user-service/users
    ************************************ */
    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getAllUsers() {
        Iterable<UserEntity> userList = userService.getAllUsers();

        List<ResponseUser> result = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        userList.forEach(user -> result.add(mapper.map(user, ResponseUser.class)));

        return ResponseEntity.ok(result);
    }
    /* ************************************
     * 사용자 상세 정보 조회
     * GET /user-service/users/{userId}
     ************************************ */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUserByUserId(@PathVariable("userId") String userId) {
        UserDto user = userService.getUserByUserId(userId);

        ResponseUser result = new ModelMapper().map(user, ResponseUser.class);

        return ResponseEntity.ok(result);
    }
}
