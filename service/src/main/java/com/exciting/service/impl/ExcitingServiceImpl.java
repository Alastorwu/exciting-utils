package com.exciting.service.impl;

import com.exciting.common.entity.vo.ExcitingVo;
import com.exciting.common.util.DateUtils;
import com.exciting.dao.entity.po.Exciting;
import com.exciting.dao.mapper.ExcitingMapper;
import com.exciting.service.ExcitingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        return excitingMapper.insertSelective(exciting)>0;
    }

    @Override
    public ExcitingVo get(int id) {
        Exciting exciting = excitingMapper.selectByPrimaryKey(id);
        if(exciting!=null){
            ExcitingVo excitingVo = new ExcitingVo();
            LocalDateTime localDateTime = DateUtils.dateToLocalDateTime(exciting.getCreateTime());
            String dateToString = DateUtils.localDateTimeToString(localDateTime, DateUtils.format_ymdhms);
            excitingVo.setCreateTime(dateToString);
            BeanUtils.copyProperties(exciting,excitingVo);
            return excitingVo;
        }
        return null;
    }

    @Override
    public PageInfo<ExcitingVo> page(ExcitingVo excitingVo, int pageNum, int pageSize, String orderBy) {

        Exciting search = new Exciting();
        BeanUtils.copyProperties(excitingVo,search);
        PageHelper.startPage(pageNum, pageSize,orderBy);
        List<Exciting> excitingList = excitingMapper.listByExciting(search);
        PageInfo page = new PageInfo<>(excitingList);
        List<ExcitingVo> vos = new ArrayList<>();
        excitingList.forEach(e->{
            ExcitingVo vo = new ExcitingVo();
            LocalDateTime localDateTime = DateUtils.dateToLocalDateTime(e.getCreateTime());
            String dateToString = DateUtils.localDateTimeToString(localDateTime, DateUtils.format_ymdhms);
            vo.setCreateTime(dateToString);
            BeanUtils.copyProperties(e,vo);
            vos.add(vo);
        });
        page.setList(vos);
        return page;
    }
}
