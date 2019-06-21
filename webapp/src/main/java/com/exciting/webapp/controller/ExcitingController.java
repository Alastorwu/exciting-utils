package com.exciting.webapp.controller;

import com.exciting.common.entity.ResEntity;
import com.exciting.common.entity.vo.ExcitingVo;
import com.exciting.service.ExcitingService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags ={"赛艇操作"})
@Slf4j
@RestController
public class ExcitingController {

    @Resource
    private ExcitingService excitingService;

    @RequestMapping(value = "/insert",method = {RequestMethod.POST})
    public ResEntity<Boolean> insert(@RequestBody ExcitingVo excitingVo){
        boolean flag = excitingService.insert(excitingVo);
        if(flag){
            return ResEntity.ok(flag);
        }else {
            return ResEntity.forbidden("插入失败");
        }
    }


    @RequestMapping(value = "/get",method = {RequestMethod.GET,RequestMethod.POST})
    public ResEntity<ExcitingVo> get(@ApiParam("id") @RequestParam int id){
        return ResEntity.ok(excitingService.get(id));
    }


    @RequestMapping(value = "/page",method = {RequestMethod.GET,RequestMethod.POST})
    public ResEntity<PageInfo<ExcitingVo>> page(@RequestBody ExcitingVo excitingVo
            ,@ApiParam("页数") @RequestParam(defaultValue = "1") int pageNum
            ,@ApiParam("分页大小") @RequestParam(defaultValue = "10") int pageSize
            ,@ApiParam("排序") @RequestParam(defaultValue = "id desc") String orderBy){
        PageInfo<ExcitingVo> page = excitingService.page(excitingVo,pageNum,pageSize,orderBy);
        return ResEntity.ok(page);
    }


}
