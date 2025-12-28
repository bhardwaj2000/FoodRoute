package com.foodroute.foodroute.dummy_service.conttroller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/dummy")
@RestController
public class DummyController {

    @GetMapping("/hello")
    public String dummy() {
        return "Hello from food route dummy service!";
    }

}
