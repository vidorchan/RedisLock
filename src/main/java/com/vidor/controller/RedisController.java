package com.vidor.controller;

import com.vidor.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

@RestController
public class RedisController {

    @Autowired
    private RedisService redisService;

    @GetMapping("/deduct_product")
    public void deduct() {
        redisService.deduct();
    }

    @GetMapping("/deduct_product1")
    public void deduct1() {
        redisService.deduct1();
    }

    @GetMapping("/deduct_product2")
    public void deduct2() {
        redisService.deduct2();
    }
}
