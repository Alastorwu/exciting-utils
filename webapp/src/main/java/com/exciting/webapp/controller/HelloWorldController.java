package com.exciting.webapp.controller;

import com.exciting.common.entity.ResEntity;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Api(tags ={"hello world"})
@RestController
@RequestMapping("/test")
public class HelloWorldController {

    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public ResEntity<String> helloWorld(HttpServletResponse response){
        return ResEntity.ok("Hello World!");
    }


    public static void main(String[] args) {
        Byte b = new Byte("1");
        Integer i = new Integer(1);
        System.out.println((b.intValue()==i.intValue())+"");
    }
}
