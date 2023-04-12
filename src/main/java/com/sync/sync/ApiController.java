package com.sync.sync;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

@RestController

public class ApiController {

    @GetMapping
    @RequestMapping("/api")
    public String getMessage(@RequestParam(value = "name", defaultValue = "YourName") String name) {

        System.out.println("hello, " + name + "!!");
        return "Hello, " + name;
    }
}
