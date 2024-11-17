package com.enba.integrate.wxmp.controller;

import com.enba.integrate.wxmp.manager.WxMpApiUrlManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: enba
 * @description: 客服消息
 */
@RestController
@RequestMapping("/enba-wxmp")
@Slf4j
public class KfMessageController {

  private final WxMpApiUrlManager manager;

  public KfMessageController(WxMpApiUrlManager manager) {
    this.manager = manager;
  }

  /*当用户和公众号产生特定动作的交互时（具体动作列表请见下方说明），微信将会把消息数据推送给开发者，
  开发者可以在一段时间内（目前为48小时，2023年6月12日后启用新规则，查看公告）调用客服接口，
  通过POST一个JSON数据包来发送消息给普通用户。此接口主要用于客服等有人工消息处理环节的功能，方便开发者为用户提供更加优质的服务。

  目前允许的动作列表如下（公众平台会根据运营情况更新该列表，不同动作触发后，允许的客服接口

  1.用户发送信息
  2.点击自定义菜单（仅有点击推事件、扫码推事件、扫码推事件且弹出“消息接收中”提示框这3种菜单类型是会触发客服接口的）
  3.关注公众号
  4.扫描二维码*/

  /**
   * 发送客服消息
   *
   * @return 演示发送客服消息
   */
  @RequestMapping("/send-kf-msg")
  public String sendKfMsg() {
    manager.sendKfMsg();

    return "演示发送客服消息";
  }
}
