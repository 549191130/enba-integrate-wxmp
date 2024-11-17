package com.enba.integrate.wxmp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 微信公众号API接口地址 */
public interface WxMpApiUrl {

  /** 获取 Access token */
  @Getter
  @AllArgsConstructor
  enum GetTokenEnum {
    GET_ACCESS_TOKEN(
        "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Get_access_token.html"),
    ;

    /** 接口地址 */
    private final String url;

    /** 官方文档地址 */
    private final String docAddress;
  }

  /** 模板消息 */
  @Getter
  @AllArgsConstructor
  enum TemplateMsgEnum {

    /** 发送模板消息 */
    MESSAGE_TEMPLATE_SEND(
        "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Template_Message_Interface.html"),
    ;

    /** 接口地址 */
    private final String url;

    /** 官方文档地址 */
    private final String docAddress;
  }

  /** 客服消息 */
  @Getter
  @AllArgsConstructor
  enum KfMsgEnum {

    /** 发送模板消息 */
    MESSAGE_CUSTOM_SEND(
        "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Service_Center_messages.html"),
    ;

    /** 接口地址 */
    private final String url;

    /** 官方文档地址 */
    private final String docAddress;
  }

  /** 生成带参数的二维码 */
  @Getter
  @AllArgsConstructor
  enum QrCodeEnum {

    /** 临时二维码 */
    QR_SCENE(
        "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Account_Management/Generating_a_Parametric_QR_Code.html"),

    /** 永久二维码 */
    QR_LIMIT_SCENE(
        "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Account_Management/Generating_a_Parametric_QR_Code.html"),

    /** 通过ticket换取二维码 */
    SHOW_QR_CODE(
        "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Account_Management/Generating_a_Parametric_QR_Code.html"),
    ;

    /** 接口地址 */
    private final String url;

    /** 官方文档地址 */
    private final String docAddress;

    public String formatUrl(String... params) {
      return String.format(url, params);
    }
  }

  /** 素材管理 */
  @Getter
  @AllArgsConstructor
  enum MediaEnum {

    /** 新增临时素材 */
    MEDIA_UPLOAD(
        "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=%s&type=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/New_temporary_materials.html"),

    /** 获取临时素材 */
    MEDIA_GET(
        "https://api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/Get_temporary_materials.html"),

    /*新增永久素材-上传图文消息内的图片获取URL*/
    MEDIA_UPLOADIMG(
        "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/Adding_Permanent_Assets.html"),

    /*新增其他类型永久素材*/
    MATERIAL_ADD_MATERIAL(
        "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=%s&type=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/Adding_Permanent_Assets.html"),

    /*获取永久素材*/
    MATERIAL_GET_MATERIAL(
        "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/Getting_Permanent_Assets.html"),

    /*删除永久素材*/
    MATERIAL_DEL_MATERIAL(
        "https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/Deleting_Permanent_Assets.html"),

    /*获取素材总数*/
    MATERIAL_GET_MATERIALCOUNT(
        "https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/Get_the_total_of_all_materials.html"),

    /*获取素材列表*/
    MATERIAL_BATCHGET_MATERIAL(
        "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/Get_materials_list.html"),
    ;

    /** 接口地址 */
    private final String url;

    /** 官方文档地址 */
    private final String docAddress;

    public String formatUrl(String... params) {
      return String.format(url, params);
    }
  }

  /** 自定义菜单 */
  @Getter
  @AllArgsConstructor
  enum CustomMenuEnum {

    /** 创建接口 */
    CREATE_MENU(
        "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Creating_Custom-Defined_Menu.html"),

    /** 查询接口 */
    GET_MENU(
        "https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=%s",
        "https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Querying_Custom_Menus.html"),

    /** 删除接口 */
    delete_MENU(
        "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN",
        "https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Deleting_Custom-Defined_Menu.html"),
    ;
    /** 接口地址 */
    private final String url;

    /** 官方文档地址 */
    private final String docAddress;
  }
}
