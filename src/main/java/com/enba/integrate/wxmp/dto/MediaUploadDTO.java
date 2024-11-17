package com.enba.integrate.wxmp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MediaUploadDTO {
  /** 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb，主要用于视频与音乐格式的缩略图） */
  private String type;

  /** 媒体文件上传后，获取标识 */
  private String media_id;

  /** 媒体文件上传时间戳 */
  private String created_at;

  private List<?> item;
}
