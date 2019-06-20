package com.exciting.webapp.controller;

import com.exciting.common.entity.ResEntity;
import com.exciting.common.entity.vo.ExcitingVo;
import com.exciting.service.ExcitingService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags ={"赛艇操作"})
@Slf4j
@RestController
public class ExcitingController {

    @Resource
    private ExcitingService excitingService;

    @RequestMapping(value = "/insert",method = {RequestMethod.GET,RequestMethod.POST})
    public ResEntity<Boolean> insert(@RequestBody ExcitingVo excitingVo){
        boolean flag = excitingService.insert(excitingVo);
        if(flag){
            return ResEntity.ok(flag);
        }else {
            return ResEntity.forbidden("插入失败");
        }
    }
}
