package com.enba.integrate.wxmp.dto;

import lombok.Data;

@Data
public class GetPermanentMediaListDTO {

  /** 素材的类型，图片（image）、视频（video）、语音 （voice）、图文（news） */
  private String type;

  /** 从全部素材的该偏移位置开始返回，0表示从第一个素材 返回 */
  private int offset;

  /** 返回素材的数量，取值在1到20之间 */
  private int count;
}
