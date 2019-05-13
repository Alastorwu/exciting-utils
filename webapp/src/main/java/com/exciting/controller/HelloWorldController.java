package com.exciting.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/test")
public class HelloWorldController {


    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public String helloWorld(HttpServletResponse response){
        return "Hello World!";
    }


}
