package com.eimy.yunpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eimy.yunpicturebackend.model.dto.picture.PictureQueryRequest;
import com.eimy.yunpicturebackend.model.dto.picture.PictureReviewRequest;
import com.eimy.yunpicturebackend.model.dto.picture.PictureUploadByBatchRequest;
import com.eimy.yunpicturebackend.model.dto.picture.PictureUploadRequest;
import com.eimy.yunpicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eimy.yunpicturebackend.model.entity.User;
import com.eimy.yunpicturebackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author 路灯
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-05-04 16:11:46
*/
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param inputSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser);


    /**
     * 将查询请求转换为QueryWrapper请求
     *
     * @param pictureQueryRequest
     * @return
     */
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取单个图片封装
     *
     * @param picture
     * @param request
     * @return
     */
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request);


    /**
     * 分页获取图片封装
     *
     * @param picturePage
     * @param request
     * @return
     */
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);


    /**
     * 图片数据校验，在更新和修改图片时进行判断
     *
     * @param picture
     */
    public void validPicture(Picture picture);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 补充审核参数：图片上传、用户编辑、管理员更新
     *
     * @param picture
     * @param loginUser
     */
    public void fillReviewParams(Picture picture, User loginUser);

    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 成功创建的图片数
     */
    Integer uploadPictureByBatch(
            PictureUploadByBatchRequest pictureUploadByBatchRequest,
            User loginUser
    );
}
