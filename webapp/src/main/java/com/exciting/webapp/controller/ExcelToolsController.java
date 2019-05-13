package com.exciting.webapp.controller;

import com.alibaba.fastjson.JSONObject;
import com.exciting.common.entity.ResponseEntity;
import com.exciting.common.util.PoiExcelUtil;
import com.exciting.webapp.entity.TestVo;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/excel/tool")
public class ExcelToolsController {


    @RequestMapping(value = "/testUp",method = RequestMethod.POST)
    public ResponseEntity<List<TestVo>> testUp(
            @ApiParam(required = true,value = "上传文件")@RequestParam() MultipartFile file
            ,@ApiParam(required = true,value = "{Excel对应标题:Object对应字段}example={'姓名':'name','年龄':'age'}")
             @RequestParam()String titles
            ) {
        try {
            String fileName = file.getOriginalFilename();
            log.info("上传的文件名为：" + fileName);
            if(StringUtils.isBlank(fileName)){
                return ResponseEntity.forbidden("文件名获取错误!");
            }
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            log.info("文件的后缀名为：" + suffixName);
            if(StringUtils.isBlank(suffixName)){
                return ResponseEntity.forbidden("文件格式获取失败!");
            }
            suffixName = suffixName.toLowerCase();
            if ( !".xls".equals(suffixName)
                 && !".xlsx".equals(suffixName)) {
                return ResponseEntity.forbidden("文件格式必须为xls、xlsx!");
            }

            InputStream inputStream = file.getInputStream();
            Map<String,String> map = new HashMap(JSONObject.parseObject(titles));
            //List<Map<String,Object>> maps = PoiExcelUtil.readExcelToMap(inputStream, fileName, null, map);
            List<TestVo> maps = PoiExcelUtil.readExcelToObjectList(inputStream, fileName, null, map, TestVo.class);
            return ResponseEntity.ok(maps);
        } catch (IOException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
            return ResponseEntity.serverError("文件读取错误!");
        }
    }

    @RequestMapping(value = "/testDown",method = RequestMethod.GET)
    public ResponseEntity<String> testDown() {

        return ResponseEntity.ok("");
    }

}
