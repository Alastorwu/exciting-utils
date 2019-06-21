package com.exciting.common.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "赛艇")
public class ExcitingVo {
    @ApiModelProperty(value = "赛艇id")
    private Integer id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "赛艇值")
    private String value;
    @ApiModelProperty(value = "赛艇类型")
    private Integer type;
    @ApiModelProperty(value = "赛艇价格")
    private BigDecimal price;
    @ApiModelProperty(value = "创建时间")
    private String createTime;

}