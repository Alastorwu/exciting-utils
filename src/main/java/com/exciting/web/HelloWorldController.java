package com.exciting.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/test")
public class HelloWorldController {

    private Logger logger = LoggerFactory.getLogger(HelloWorldController.class);


    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public String helloWorld(HttpServletResponse response){
        return "Hello World!";
    }


}
