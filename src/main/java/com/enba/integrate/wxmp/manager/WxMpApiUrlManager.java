package com.enba.integrate.wxmp.manager;

import com.alibaba.fastjson.JSONObject;
import com.enba.integrate.wxmp.dto.AccessTokenDTO;
import com.enba.integrate.wxmp.enums.WxMpApiUrl.CustomMenuEnum;
import com.enba.integrate.wxmp.enums.WxMpApiUrl.GetTokenEnum;
import com.enba.integrate.wxmp.enums.WxMpApiUrl.KfMsgEnum;
import com.enba.integrate.wxmp.enums.WxMpApiUrl.TemplateMsgEnum;
import com.enba.integrate.wxmp.properties.EnbaMpProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class WxMpApiUrlManager {

  private final RestTemplate restTemplate;

  private final EnbaMpProperties enbaMpProperties;

  public WxMpApiUrlManager(RestTemplate restTemplate, EnbaMpProperties enbaMpProperties) {
    this.restTemplate = restTemplate;
    this.enbaMpProperties = enbaMpProperties;
  }

  /**
   * 获取 Access token
   *
   * <p>access_token是公众号的全局唯一接口调用凭据，公众号调用各接口时都需使用access_token。开发者需要进行妥善保存。access_token的存储至少要保留512个字符空间。access_token的有效期目前为2个小时，需定时刷新，重复获取将导致上次获取的access_token失效。
   *
   * <p>公众平台的API调用所需的access_token的使用及生成方式说明：
   *
   * <p>1、建议公众号开发者使用中控服务器统一获取和刷新access_token，其他业务逻辑服务器所使用的access_token均来自于该中控服务器，不应该各自去刷新，否则容易造成冲突，导致access_token覆盖而影响业务；
   *
   * <p>2、目前access_token的有效期通过返回的expires_in来传达，目前是7200秒之内的值。中控服务器需要根据这个有效时间提前去刷新新access_token。在刷新过程中，中控服务器可对外继续输出的老access_token，此时公众平台后台会保证在5分钟内，新老access_token都可用，这保证了第三方业务的平滑过渡；
   *
   * <p>3、access_token的有效时间可能会在未来有调整，所以中控服务器不仅需要内部定时主动刷新，还需要提供被动刷新access_token的接口，这样便于业务服务器在API调用获知access_token已超时的情况下，可以触发access_token的刷新流程。
   *
   * <p>4、对于可能存在风险的调用，在开发者进行获取 access_token调用时进入风险调用确认流程，需要用户管理员确认后才可以成功获取。具体流程为：
   *
   * <p>开发者通过某IP发起调用->平台返回错误码[89503]并同时下发模板消息给公众号管理员->公众号管理员确认该IP可以调用->开发者使用该IP再次发起调用->调用成功。
   *
   * <p>如公众号管理员第一次拒绝该IP调用，用户在1个小时内将无法使用该IP再次发起调用，如公众号管理员多次拒绝该IP调用，该IP将可能长期无法发起调用。平台建议开发者在发起调用前主动与管理员沟通确认调用需求，或请求管理员开启IP白名单功能并将该IP加入IP白名单列表。
   *
   * <p>公众号和小程序均可以使用AppID和AppSecret调用本接口来获取access_token。AppID和AppSecret可在“微信公众平台-设置与开发--基本配置”页中获得（需要已经成为开发者，且账号没有异常状态）。**调用接口时，请登录“微信公众平台-开发-基本配置”提前将服务器IP地址添加到IP白名单中，点击查看设置方法，否则将无法调用成功。**小程序无需配置IP白名单。
   *
   * <p>如长期无AppSecret的使用需求，开发者可以使用管理员账号登录公众平台，在“设置与开发-基本配置”中对AppSeceret进行冻结，提高账号的安全性。AppSecret冻结后，开发者无法使用AppSecret获取Access
   * token（接口返回错误码40243），不影响账号基本功能的正常使用，不影响通过第三方授权调用后台接口，不影响云开发调用后台接口。开发者可以随时使用管理员账号登录公众平台，在“设置与开发-基本配置”中对AppSecret进行解冻。
   *
   * @return 返回获取 Access token
   */
  public AccessTokenDTO getAccessToken() {
    String url = GetTokenEnum.GET_ACCESS_TOKEN.getUrl();

    String accessTokenUrl =
        String.format(url, enbaMpProperties.getAppId(), enbaMpProperties.getAppSecret());
    log.info("请求地址accessTokenUrl:{}", accessTokenUrl);

    String ret = restTemplate.getForObject(accessTokenUrl, String.class);
    log.info("接收响应ret:{}", ret);
    // {"access_token":"84_KOZta4Dunr25RHxRbFLR8DlVUEuonpn8qd2YQUZ33pFxNayB1Fn5BEmBBNZ5CbG0wqIbCKblBU74cT0twAa4lyXl6jFOSLD_DbXkQQmfUvVmKX4WEb-bg3QHCTAPNJbAFAFDZ","expires_in":7200}

    AccessTokenDTO accessTokenDTO = JSONObject.parseObject(ret, AccessTokenDTO.class);

    /*
        FIXME 待完善，根据官方文档可知，获取access_token接口每天有频率限制，所以每次获取到token之后应该进行缓存，如redis中，并定期刷新。
    */

    return accessTokenDTO;
  }

  // 发送模版消息
  public void sendTemplateMsg() {
    // FIXME 获取token 为了演示这里简单写死，正常从缓存中获取或者从微信侧获取
    String token =
        "84_n8ytYl6DGReu08ySf4i98mj-3XRdbmCiAJIdf5EWRrEZ7jYDI_Ue7h_C0azDN8b5LMg6TqqBrOViCtPZkzyreJdPqJrKvvm0DeQUaPxQ00VHhipeYojdjfz5MwMLRLdACAHNH";

    String url = TemplateMsgEnum.MESSAGE_TEMPLATE_SEND.getUrl();
    String formatUrl = String.format(url, token);

    // FIXME 这里为了演示写死，可以结合自己的业务从数据库获取 为了省事，正常需要封装成对象
    String requestBody =
        " {\n"
            + "           \"touser\":\"oEFbN6Kch-_VScWaQ7VIjPmKYQGU\",\n"
            + "           \"template_id\":\"4vw1OaXoSZvwhNL3FuhWXwkdOgbTvKjt2pLLUutfhvg\",\n"
            + "           \"data\":{\n"
            + "                   \"name01\":{\n"
            + "                       \"value\":\"恩爸编程\"\n"
            + "                   },\n"
            + "                   \"amount01\": {\n"
            + "                       \"value\":\"100.99元\"\n"
            + "                   },\n"
            + "                   \"thing01\": {\n"
            + "                       \"value\":\"行程：北京-上海\"\n"
            + "                   },\n"
            + "                   \"date01\": {\n"
            + "                       \"value\":\"2024年9月28日\"\n"
            + "                   }\n"
            + "           }\n"
            + "       }";

    String s = restTemplate.postForObject(formatUrl, requestBody, String.class);
    log.info("发送模版消息:{}", s);
  }

  // 发送客服消息
  public void sendKfMsg() {
    // FIXME 获取token 为了演示这里简单写死，正常从缓存中获取或者从微信侧获取
    String token =
        "84_n8ytYl6DGReu08ySf4i98mj-3XRdbmCiAJIdf5EWRrEZ7jYDI_Ue7h_C0azDN8b5LMg6TqqBrOViCtPZkzyreJdPqJrKvvm0DeQUaPxQ00VHhipeYojdjfz5MwMLRLdACAHNH";

    String url = KfMsgEnum.MESSAGE_CUSTOM_SEND.getUrl();
    String formatUrl = String.format(url, token);

    // FIXME 这里为了演示写死，可以结合自己的业务从数据库获取 为了省事，正常需要封装成对象
    String requestBody =
        "{\n"
            + "    \"touser\":\"oEFbN6Kch-_VScWaQ7VIjPmKYQGU\",\n"
            + "    \"msgtype\":\"text\",\n"
            + "    \"text\":\n"
            + "    {\n"
            + "         \"content\":\"Hello World\"\n"
            + "    }\n"
            + "}";
    String s = restTemplate.postForObject(formatUrl, requestBody, String.class);
    log.info("发送客服消息:{}", s);
  }

  /**
   * 自定义菜单
   */
  @Data
  public class SelfMenu {
    // 创建接口

    public void createMenu() {
      // TODO 正常是从缓存中获取，缓存中没有在请求微信获取token
      String accessToken = "";

      String menuJson = "{\n"
          + "     \"button\":[\n"
          + "     {\t\n"
          + "          \"type\":\"click\",\n"
          + "          \"name\":\"今日歌曲\",\n"
          + "          \"key\":\"V1001_TODAY_MUSIC\"\n"
          + "      },\n"
          + "      {\n"
          + "           \"name\":\"菜单\",\n"
          + "           \"sub_button\":[\n"
          + "           {\t\n"
          + "               \"type\":\"view\",\n"
          + "               \"name\":\"搜索\",\n"
          + "               \"url\":\"http://www.soso.com/\"\n"
          + "            },\n"
          + "            {\n"
          + "                 \"type\":\"miniprogram\",\n"
          + "                 \"name\":\"wxa\",\n"
          + "                 \"url\":\"http://mp.weixin.qq.com\",\n"
          + "                 \"appid\":\"wx286b93c14bbf93aa\",\n"
          + "                 \"pagepath\":\"pages/lunar/index\"\n"
          + "             },\n"
          + "            {\n"
          + "               \"type\":\"click\",\n"
          + "               \"name\":\"赞一下我们\",\n"
          + "               \"key\":\"V1001_GOOD\"\n"
          + "            }]\n"
          + "       }]\n"
          + " }";

      String url = CustomMenuEnum.CREATE_MENU.getUrl();
      String format = String.format(url, accessToken);

      String s = restTemplate.postForObject(format, menuJson, String.class);

      log.info("自定义菜单创建结果：{}", s);
    }

  }
}
