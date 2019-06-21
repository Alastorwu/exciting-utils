package com.exciting.webapp.controller;

import com.alibaba.fastjson.JSONObject;
import com.exciting.common.entity.ResEntity;
import com.exciting.common.util.excel.PoiExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags ={"excel操作"})
@Slf4j
@RestController
@RequestMapping("/excel/tool")
public class ExcelToolsController {


    @RequestMapping(value = "/testUp",method = RequestMethod.POST)
    public ResEntity<List<Map>> testUp(
            @ApiParam(required = true,value = "上传文件")@RequestParam() MultipartFile file
            ,@ApiParam(required = true,value = "{Excel对应标题:Object对应字段}example={'姓名':'name','年龄':'age'}")
             @RequestParam()String titles
            ) {
        try {
            String fileName = file.getOriginalFilename();
            log.info("上传的文件名为：" + fileName);
            if(StringUtils.isBlank(fileName)){
                return ResEntity.forbidden("文件名获取错误!");
            }
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            log.info("文件的后缀名为：" + suffixName);
            if(StringUtils.isBlank(suffixName)){
                return ResEntity.forbidden("文件格式获取失败!");
            }
            suffixName = suffixName.toLowerCase();
            if ( !".xls".equals(suffixName)
                 && !".xlsx".equals(suffixName)) {
                return ResEntity.forbidden("文件格式必须为xls、xlsx!");
            }

            InputStream inputStream = file.getInputStream();
            Map<String,String> titleMap = new HashMap(JSONObject.parseObject(titles));
            //List<Map<String,Object>> mapList = PoiExcelUtil.readExcelToMap(inputStream, fileName, null, titleMap);
            List<Map> mapList = PoiExcelUtil.readExcelToList(inputStream, fileName, null, titleMap, Map.class);
            return ResEntity.ok(mapList);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
            return ResEntity.serverError("文件解析错误!");
        }
    }

    @RequestMapping(value = "/testDown",method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> testDown() throws IOException {
        //File file = new File("D://pic.png");
        String fileName = "pic.png";
        FileSystemResource file = new FileSystemResource("D://pic.png");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        InputStream inputStream = file.getInputStream();
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(inputStreamResource);

    }


}
