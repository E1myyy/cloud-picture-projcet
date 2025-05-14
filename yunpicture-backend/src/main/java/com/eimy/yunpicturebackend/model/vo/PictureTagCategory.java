package com.eimy.yunpicturebackend.model.vo;

import lombok.Data;

import java.util.List;


/**
 * 图片标签列表分类视图
 */
@Data
public class PictureTagCategory {
    /**
     * 标签列表
     */
    private List<String> tagList;


    /**
     * 分类列表
     */
    private List<String> categoryList;
}
