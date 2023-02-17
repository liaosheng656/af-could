package com.af.common.moudules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 媒体文件列表
 * </p>
 *
 * @author nf-coders
 * @since 2021-12-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SfcMedia对象", description="媒体文件列表")
public class SfcMedia extends Model<SfcMedia> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "文件类型关联表sdc_media_type，暂定有：图片、视频、音频、文档、文件组")
    private Integer typeId;

    @ApiModelProperty(value = "文件组id(关联本表主键)")
    private Integer groupId;

    @ApiModelProperty(value = "所属仓库,0-素材库，1-成品库")
    private Integer storeType;

    @ApiModelProperty(value = "所属目录ID,关联sfc_dir表ID")
    private Integer dirId;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "原文件")
    private String originalFile;

    @ApiModelProperty(value = "预览文件")
    private String previewUrl;

    @ApiModelProperty(value = "关联节目ID,关联sfc_program表ID")
    private Integer programId;

    @ApiModelProperty(value = "关联节目视频ID,关联sfc_program_video表ID")
    private Integer videoId;

    @ApiModelProperty(value = "媒体描述")
    private String description;

    @ApiModelProperty(value = "一级分类ID(单个)")
    private Integer cateId;

    @ApiModelProperty(value = "二级分类ID(至多10个,逗号分割)")
    private String cateIdLeaf;

    @ApiModelProperty(value = "标签信息(逗号分割)")
    private String tags;

    @ApiModelProperty(value = "缩略图URL")
    private String coverUrl;

    @ApiModelProperty(value = "视频编码")
    private String videoCodec;

    @ApiModelProperty(value = "音频编码")
    private String audioCodec;

    @ApiModelProperty(value = "时长(秒)")
    private Integer duration;

    @ApiModelProperty(value = "格式")
    private String format;

    @ApiModelProperty(value = "大小(b)")
    private Long size;

    @ApiModelProperty(value = "视频码率(kbps)")
    private Integer videoBitrate;

    @ApiModelProperty(value = "音频码率(kbps)")
    private Integer audioBitrate;

    @ApiModelProperty(value = "帧率(HZ)")
    private Integer fps;

    @ApiModelProperty(value = "分辨率-宽")
    private Integer width;

    @ApiModelProperty(value = "分辨率-高")
    private Integer height;

    @ApiModelProperty(value = "上传人")
    private String userId;

    @ApiModelProperty(value = "视频字幕文本内容")
    private String videoText;

    @ApiModelProperty(value = "0:Initiated:初始  1:Uplooded:已上传  2:TagsInitiated:已打标签  3:UnPublish:未发布  4Published:已发布")
    private Integer mediaStatus;

    @ApiModelProperty(value = "状态，0-作废；1-正常")
    private Integer status;

    @ApiModelProperty(value = "上传状态 -1:上传失败 1:未上传 2:上传中 3:合并中 4:上传成功 ")
    private Integer uploadStatus;

    @ApiModelProperty(value = "转码状态 -1:转码失败 1:未转码 2:转码中 3:转码成功")
    private Integer decodeStatus;

    @ApiModelProperty(value = "主文件md5")
    private String fileMd5;

    @ApiModelProperty(value = "单位id 冗余字段")
    private String deptId;

    @ApiModelProperty(value = "存储节点id 冗余字段")
    private Integer storageId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
