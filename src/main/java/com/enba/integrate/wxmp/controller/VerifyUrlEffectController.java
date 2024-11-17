package com.enba.integrate.wxmp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: enba
 * @description: 验证url有效性
 */
@RestController
@RequestMapping("/enba-wxmp")
@Slf4j
public class VerifyUrlEffectController {

  /**
   * 验证URL有效性成功后即接入生效，成为开发者
   *
   * @param signature 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
   * @param timestamp 时间戳
   * @param nonce     随机数
   * @param echostr   随机字符串
   * @return 结果返回
   */
  @GetMapping(value = "/")
  public String verifyUrlEffect(
      //文档地址：https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Access_Overview.html
      @RequestParam(name = "signature", required = false) String signature,
      @RequestParam(name = "timestamp", required = false) String timestamp,
      @RequestParam(name = "nonce", required = false) String nonce,
      @RequestParam(name = "echostr", required = false) String echostr) {

    //FIXME 为了快速演示，忽略验证逻辑

    log.info("signature=[{}],timestamp=[{}],nonce=[{}],echostr=[{}]", signature, timestamp, nonce,
        echostr);

    return echostr;
  }

}
