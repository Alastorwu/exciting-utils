package com.exciting.webapp.controller;

import com.exciting.common.entity.ResEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/test")
public class HelloWorldController {

    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public ResEntity<String> helloWorld(HttpServletResponse response){
        return ResEntity.ok("Hello World!");
    }

}
