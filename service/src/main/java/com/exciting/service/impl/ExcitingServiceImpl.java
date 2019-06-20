package com.exciting.service.impl;

import com.exciting.common.entity.vo.ExcitingVo;
import com.exciting.common.util.DateUtils;
import com.exciting.dao.entity.po.Exciting;
import com.exciting.dao.mapper.ExcitingMapper;
import com.exciting.service.ExcitingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class ExcitingServiceImpl implements ExcitingService {

    @Resource
    private ExcitingMapper excitingMapper;

    @Override
    public boolean insert(ExcitingVo excitingVo) {
        Exciting exciting = new Exciting();
        BeanUtils.copyProperties(excitingVo,exciting);
        exciting.setId(null);
        //Date createTime = DateUtils.stringToDate(excitingVo.getCreateTime());
        return excitingMapper.insertSelective(exciting)>0;
    }
}
