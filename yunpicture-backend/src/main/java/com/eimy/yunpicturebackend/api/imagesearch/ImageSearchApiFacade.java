package com.eimy.yunpicturebackend.api.imagesearch;

import com.eimy.yunpicturebackend.api.imagesearch.model.ImageSearchResult;
import com.eimy.yunpicturebackend.api.imagesearch.sub.GetImageFirstUrlApi;
import com.eimy.yunpicturebackend.api.imagesearch.sub.GetImageListApi;
import com.eimy.yunpicturebackend.api.imagesearch.sub.GetImagePageUrlApi;
import com.eimy.yunpicturebackend.model.entity.Picture;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ImageSearchApiFacade {

    /**
     * 搜索图片
     *
     * @param imageUrl
     * @return
     */
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl);
        return imageList;
    }

    public static void main(Picture picture) {
        String imageUrl = picture.getUrl();
        List<ImageSearchResult> resultList = searchImage(imageUrl);
        System.out.println("结果列表" + resultList);
    }
}

