package com.enba.integrate.wxmp.controller;

import com.alibaba.fastjson.JSONObject;
import com.enba.integrate.wxmp.dto.AccessTokenDTO;
import com.enba.integrate.wxmp.dto.QrCodeTicketDTO;
import com.enba.integrate.wxmp.enums.WxMpApiUrl.QrCodeEnum;
import com.enba.integrate.wxmp.manager.WxMpApiUrlManager;
import java.io.FileOutputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 为了满足用户渠道推广分析和用户账号绑定等场景的需要，公众平台提供了生成带参数二维码的接口。使用该接口可以获得多个带不同场景值的二维码，用户扫描后，公众号可以接收到事件推送。使用接口过程中有任何问题，可以前往微信开放社区
 * #公众号 专区发帖交流。
 *
 * <p>目前有2种类型的二维码：
 *
 * <p>1、临时二维码，是有过期时间的，最长可以设置为在二维码生成后的30天（即2592000秒）后过期，但能够生成较多数量。临时二维码主要用于账号绑定等不要求二维码永久保存的业务场景
 * 2、永久二维码，是无过期时间的，但数量较少（目前为最多10万个）。永久二维码主要用于适用于账号绑定、用户来源统计等场景。
 *
 * <p>用户扫描带场景值二维码时，可能推送以下两种事件：
 *
 * <p>如果用户还未关注公众号，则用户可以关注公众号，关注后微信会将带场景值关注事件推送给开发者。
 *
 * <p>如果用户已经关注公众号，在用户扫描后会自动进入会话，微信也会将带场景值扫描事件推送给开发者。
 *
 * <p>获取带参数的二维码的过程包括两步，首先创建二维码ticket，然后凭借ticket到指定URL换取二维码。
 *
 * @author: enba
 * @description: 生成带参数的二维码
 */
@RestController
@RequestMapping("/enba-wxmp")
public class QrCodeController {
  private static final Logger log = LoggerFactory.getLogger(QrCodeController.class);

  private final RestTemplate restTemplate;

  private final WxMpApiUrlManager wxMpApiUrlManager;

  public QrCodeController(RestTemplate restTemplate, WxMpApiUrlManager wxMpApiUrlManager) {
    this.restTemplate = restTemplate;
    this.wxMpApiUrlManager = wxMpApiUrlManager;
  }

  /**
   * 使用场景：微信用户扫码生成带参数的二维码，这个参数会在微信用户扫码时，
   *
   * <p>触发微信服务器推送事件到开发侧服务器，开发者可以在服务器端根据场景值进行后续的业务处理。
   *
   * @return 临时二维码
   */
  @GetMapping("/temp-qr-code")
  public ResponseEntity<?> createQrCodeTicket() {
    // 获取accessToken
    AccessTokenDTO accessTokenDTO = wxMpApiUrlManager.getAccessToken();

    // 地址
    String url = QrCodeEnum.QR_SCENE.getUrl();
    String format = String.format(url, accessTokenDTO.getAccess_token());

    // 请求体 为了省事，正常需要封装成对象
    String requestBody =
        "{\"expire_seconds\": 604800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": 123}}}";

    // 发送请求获取 ticket
    String ticket = restTemplate.postForObject(format, requestBody, String.class);
    QrCodeTicketDTO qrCodeTicketDTO = JSONObject.parseObject(ticket, QrCodeTicketDTO.class);
    log.info("创建二维码ticket返回结果：{}", qrCodeTicketDTO);
    /*
    * {"ticket":"gQH47joAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL2taZ2Z3TVRtNzJXV1Brb3ZhYmJJAAIEZ23sUwMEmm
        3sUw==","expire_seconds":60,"url":"http://weixin.qq.com/q/kZgfwMTm72WWPkovabbI"}
    */

    // 通过ticket换取二维码
    String showQrCodeUrl = QrCodeEnum.SHOW_QR_CODE.formatUrl(qrCodeTicketDTO.getTicket());
    ResponseEntity<byte[]> response = restTemplate.getForEntity(showQrCodeUrl, byte[].class);
    if (response.getStatusCode().is2xxSuccessful()) {
      byte[] imageBytes = response.getBody();

      // 保存到本地文件
      try (FileOutputStream fos = new FileOutputStream("qr_code.jpg")) {
        fos.write(imageBytes);
      } catch (IOException e) {
        log.error("保存图片失败", e);
      }

      // 或者直接返回给前端显示
      return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }

    return null;
  }

  // 创建永久二维码
  @GetMapping("/permanent-qr-code")
  public ResponseEntity<?> createPermanentQrCodeTicket() {

    // 获取accessToken
    AccessTokenDTO accessTokenDTO = wxMpApiUrlManager.getAccessToken();

    // 地址
    String url = QrCodeEnum.QR_SCENE.getUrl();
    String format = String.format(url, accessTokenDTO.getAccess_token());

    // 请求体 为了省事，正常需要封装成对象
    String requestBody =
        "{\"expire_seconds\": 604800, \"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": 123}}}";

    // 发送请求获取 ticket
    String ticket = restTemplate.postForObject(format, requestBody, String.class);
    QrCodeTicketDTO qrCodeTicketDTO = JSONObject.parseObject(ticket, QrCodeTicketDTO.class);
    log.info("创建二维码ticket返回结果：{}", qrCodeTicketDTO);
    /*
    * {"ticket":"gQH47joAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL2taZ2Z3TVRtNzJXV1Brb3ZhYmJJAAIEZ23sUwMEmm
        3sUw==","expire_seconds":60,"url":"http://weixin.qq.com/q/kZgfwMTm72WWPkovabbI"}
    */

    // 通过ticket换取二维码
    String showQrCodeUrl = QrCodeEnum.SHOW_QR_CODE.formatUrl(qrCodeTicketDTO.getTicket());
    ResponseEntity<byte[]> response = restTemplate.getForEntity(showQrCodeUrl, byte[].class);
    if (response.getStatusCode().is2xxSuccessful()) {
      byte[] imageBytes = response.getBody();

      // 保存到本地文件
      try (FileOutputStream fos = new FileOutputStream("qr_code.jpg")) {
        fos.write(imageBytes);
      } catch (IOException e) {
        log.error("保存图片失败", e);
      }

      // 或者直接返回给前端显示
      return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }

    return null;
  }
}
