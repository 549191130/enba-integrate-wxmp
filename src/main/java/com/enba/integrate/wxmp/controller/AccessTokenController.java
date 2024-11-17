package com.enba.integrate.wxmp.controller;

import com.enba.integrate.wxmp.dto.AccessTokenDTO;
import com.enba.integrate.wxmp.manager.WxMpApiUrlManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: enba
 * @description: 获取 Access token
 */
@RestController
@RequestMapping("/enba-wxmp")
@Slf4j
public class AccessTokenController {

  private final WxMpApiUrlManager manager;

  public AccessTokenController(WxMpApiUrlManager manager) {
    this.manager = manager;
  }

  /**
   * 获取 access token
   *
   * @return access token
   */
  @GetMapping("/access-token")
  public AccessTokenDTO getAccessToken() {
    return manager.getAccessToken();
  }

}
