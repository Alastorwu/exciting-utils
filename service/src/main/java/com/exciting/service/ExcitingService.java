package com.exciting.service;

import com.exciting.common.entity.vo.ExcitingVo;
import com.github.pagehelper.PageInfo;

public interface ExcitingService {
    boolean insert(ExcitingVo excitingVo);

    ExcitingVo get(int id);

    PageInfo<ExcitingVo> page(ExcitingVo excitingVo, int pageNum, int pageSize, String orderBy);

    boolean update(ExcitingVo excitingVo);
}
