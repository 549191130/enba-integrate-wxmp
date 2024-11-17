package com.enba.integrate.wxmp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: enba
 * @description: 基础消息能力
 */
@RestController
@RequestMapping("/enba-wxmp")
@Slf4j
public class WxMpMsgController {

  // 接收普通消息!!!!
  /*当普通微信用户向公众账号发消息时，微信服务器将POST消息的XML数据包到开发者填写的URL上。
  请注意：
  关于重试的消息排重，推荐使用msgid排重。
  微信服务器在五秒内收不到响应会断掉连接，并且重新发起请求，总共重试三次。假如服务器无法保证在五秒内处理并回复，可以直接回复空串，微信服务器不会对此作任何处理，并且不会发起重试。详情请见“发送消息-被动回复消息”。
  如果开发者需要对用户消息在5秒内立即做出回应，即使用“发送消息-被动回复消息”接口向用户被动回复消息时，可以在
  公众平台官网的开发者中心处设置消息加密。开启加密后，用户发来的消息和开发者回复的消息都会被加密（但开发者通过客服接口等API调用形式向用户发送消息，则不受影响）。关于消息加解密的详细说明，请见“发送消息-被动回复消息加解密说明”。 各消息类型的推送XML数据包结构如下：
  */

  // 接收事件推送!!!!
  /*在微信用户和公众号产生交互的过程中，用户的某些操作会使得微信服务器通过事件推送的形式通知到开发者在开发者中心处设置的服务器地址，
  从而开发者可以获取到该信息。其中，某些事件推送在发生后，是允许开发者回复用户的，某些则不允许，详细内容如下：
  目录
  1 关注/取消关注事件
  2 扫描带参数二维码事件
  3 上报地理位置事件
  4 自定义菜单事件
  5 点击菜单拉取消息时的事件推送
  6 点击菜单跳转链接时的事件推送
  */

  /**
   * 接收普通消息&接收事件推送 均走此接口
   *
   * @param requestBody 请求体
   * @param signature 微信加密签名
   * @param timestamp 时间戳
   * @param nonce 随机数
   * @return 响应消息 (被动回复用户消息)
   */
  @PostMapping("/")
  public String handleWeChatMessage(
      @RequestBody String requestBody,
      @RequestParam String signature,
      @RequestParam String timestamp,
      @RequestParam String nonce) {
    log.info(
        "Received request with body: {}, signature: {}, timestamp: {}, nonce: {}",
        requestBody,
        signature,
        timestamp,
        nonce);

    // FIXME 这里只是简单地返回一个固定回复消息，可以根据实际情况进行修改和扩展

    // 这里需要解析requestBody中的XML数据
    // 并根据不同的消息类型进行处理
    // 然后构建相应的响应消息
    // 示例中简单地返回一个文本消息
    return "<xml><ToUserName><![CDATA[oEFbN6Kch-_VScWaQ7VIjPmKYQGU]]></ToUserName><FromUserName><![CDATA[gh_a5432ef8b4e2]]></FromUserName><CreateTime>123456789</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[Hello, world!]]></Content></xml>";
  }
}
