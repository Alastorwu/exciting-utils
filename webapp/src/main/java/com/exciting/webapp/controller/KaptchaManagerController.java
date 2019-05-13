package com.exciting.webapp.controller;

import com.alibaba.fastjson.JSONObject;
import com.exciting.webapp.component.FakeSessionComponent;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import io.swagger.annotations.*;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;

/**
 * Created by wujiaxing on 2017/5/9.
 */
@Api(description = "图片验证码")
@Slf4j
@RestController
@RequestMapping("/kaptcha")
public class KaptchaManagerController {


    @Resource
    private Producer captchaProducer;

    @Resource
    private FakeSessionComponent fakeSessionComponent;

    @ApiOperation(value = "验证码获取"/*, httpMethod = "GET"*/)
    @RequestMapping(value = "/image/kaptcha.jpg",method = {RequestMethod.GET,RequestMethod.POST})
    public ModelAndView handleRequest(HttpServletResponse response,
                                      @ApiParam(value = "时间戳")@RequestParam("time")Long time) throws Exception{
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");

        // return a jpeg
        response.setContentType("image/jpeg");

        // create the text for the image
        String capText = captchaProducer.createText();

        // store the text in the session
        //request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
        fakeSessionComponent.setAttribute(Constants.KAPTCHA_SESSION_KEY+time,capText,300L);
        log.info(Constants.KAPTCHA_SESSION_KEY+time+";code:"+capText);

        // create the image with the text
        BufferedImage bi = captchaProducer.createImage(capText);

        @Cleanup ServletOutputStream out = response.getOutputStream();

        // write the data out
        ImageIO.write(bi, "jpg", out);
        out.flush();
        return null;
    }

    @ApiOperation(value = "验证码验证")
    @PostMapping("/check")
    public String loginCheck(HttpServletRequest request,
                             @ApiParam(value = "验证码") @RequestParam(value = "code") String code,
                             @ApiParam(value = "时间戳") @RequestParam("time")Long time){
        log.info("验证码验证,参数为：{}", "{code="+code+";}");
        JSONObject ret = new JSONObject();
        ret.put("code","0");
        ret.put("message","SUCCESS");
        //用户输入的验证码的值
        String kaptchaExpected = fakeSessionComponent.getAttribute(Constants.KAPTCHA_SESSION_KEY+time);
        //校验验证码是否正确
        if (code == null || !code.equals(kaptchaExpected) ) {
            ret.put("code","500");
            ret.put("message","FAILD");
        }
        ret.put("currentDate",System.currentTimeMillis());
        log.info("验证码验证结束：{}", ret.toJSONString());
        return ret.toJSONString();
    }



}
