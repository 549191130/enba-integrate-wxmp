package com.enba.integrate.wxmp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/enba-wxmp")
public class EnbaWxMpApplication {

  public static void main(String[] args) {
    SpringApplication.run(EnbaWxMpApplication.class, args);
  }

  @RequestMapping("/hello")
  public String index() {
    return "hello world";
  }
}
