package com.enba.integrate.wxmp.controller;

import com.alibaba.fastjson.JSON;
import com.enba.integrate.wxmp.dto.AccessTokenDTO;
import com.enba.integrate.wxmp.dto.GetPermanentMediaListDTO;
import com.enba.integrate.wxmp.dto.MediaUploadDTO;
import com.enba.integrate.wxmp.enums.WxMpApiUrl.MediaEnum;
import com.enba.integrate.wxmp.manager.WxMpApiUrlManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: enba
 * @description: 素材管理
 */
@RestController
@RequestMapping("/enba-wxmp")
@Slf4j
public class MediaController {

  private final RestTemplate restTemplate;

  private final WxMpApiUrlManager wxMpApiUrlManager;

  public MediaController(RestTemplate restTemplate, WxMpApiUrlManager wxMpApiUrlManager) {
    this.restTemplate = restTemplate;
    this.wxMpApiUrlManager = wxMpApiUrlManager;
  }

  /**
   * 新增临时素材
   *
   * @param file 文件
   * @param mediaType 媒体类型
   * @return r
   * @throws IOException e
   */
  @PostMapping(value = "/upload-media")
  public MediaUploadDTO uploadMedia(MultipartFile file, String mediaType) throws IOException {
    // 获取accessToken
    AccessTokenDTO accessTokenDTO = wxMpApiUrlManager.getAccessToken();

    // 构建请求 URL
    String url = MediaEnum.MEDIA_UPLOAD.formatUrl(accessTokenDTO.getAccess_token(), mediaType);

    // 构建请求头
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    // 构建请求体
    MultiValueMap<String, Object> reqBody = new LinkedMultiValueMap<>();
    reqBody.add(
        "media",
        new ByteArrayResource(file.getBytes()) {
          @Override
          public String getFilename() {
            return file.getOriginalFilename();
          }
        });

    // 创建请求实体
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(reqBody, headers);

    // 发送请求 TODO 简单演示，可以记录相关信息到数据库，如：media_id，type
    ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
    log.info("响应内容: {}", JSON.toJSONString(response));
    /*
       {
        "type": "image",
        "media_id": "DbwKwuBL0f2F2wSv8l2yUG8vh_sbDoRbb3VicuKGFclO_0KU94fLNXhRVGUHZvxF",
        "created_at": "1727172902",
        "item": []
       }
    */

    // 解析响应
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(response.getBody(), MediaUploadDTO.class);
  }

  /**
   * 获取临时素材
   *
   * @param mediaId 媒体文件ID
   * @return r
   */
  @GetMapping(value = "/get-media")
  public ResponseEntity<?> getMedia(String mediaId) {
    // 获取accessToken
    AccessTokenDTO accessTokenDTO = wxMpApiUrlManager.getAccessToken();

    // 构建请求 URL
    String url = MediaEnum.MEDIA_GET.formatUrl(accessTokenDTO.getAccess_token(), mediaId);

    // 发送请求
    ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
    if (response.getStatusCode().is2xxSuccessful()) {
      byte[] imageBytes = response.getBody();

      // 保存到本地文件
      try (FileOutputStream fos = new FileOutputStream("临时素材.jpg")) {
        assert imageBytes != null;
        fos.write(imageBytes);
      } catch (IOException e) {
        log.error("保存图片失败", e);
      }

      // 或者直接返回给前端显示，FIXME 这里写死了，可以根据文件类型动态设置contentType
      return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }

    return null;
  }

  /**
   * 上传图文消息内的图片获取URL
   *
   * @param file 文件
   * @return r
   * @throws IOException e
   */
  @PostMapping(value = "/upload-news-image")
  public ResponseEntity<?> uploadNewsImage(MultipartFile file) throws IOException {
    // 获取accessToken
    AccessTokenDTO accessTokenDTO = wxMpApiUrlManager.getAccessToken();

    // 构建请求 URL
    String url = MediaEnum.MEDIA_UPLOADIMG.formatUrl(accessTokenDTO.getAccess_token());

    // 构建请求头
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    // 构建请求体
    MultiValueMap<String, Object> reqBody = new LinkedMultiValueMap<>();
    reqBody.add(
        "media",
        new ByteArrayResource(file.getBytes()) {
          @Override
          public String getFilename() {
            return file.getOriginalFilename();
          }
        });
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(reqBody, headers);
    ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
    log.info("响应内容: {}", JSON.toJSONString(response));

    return response;
  }

  /**
   * 新增其他类型永久素材
   *
   * @param file 文件
   * @param mediaType 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
   * @param title 新增永久视频素材时必传，视频素材的标题
   * @param introduction 新增永久视频素材时必传，视频素材的描述
   * @return r
   * @throws IOException e
   */
  @PostMapping(value = "/upload-permanent-media")
  public ResponseEntity<?> uploadPermanentMedia(
      MultipartFile file,
      String mediaType,
      @RequestParam(value = "title", required = false) String title,
      @RequestParam(value = "introduction", required = false) String introduction)
      throws IOException {
    // 获取accessToken
    AccessTokenDTO accessTokenDTO = wxMpApiUrlManager.getAccessToken();

    // 构建请求 URL
    String url =
        MediaEnum.MATERIAL_ADD_MATERIAL.formatUrl(accessTokenDTO.getAccess_token(), mediaType);

    // 构建请求头
    HttpHeaders headers = new HttpHeaders();

    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    // 构建请求体
    MultiValueMap<String, Object> reqBody = new LinkedMultiValueMap<>();
    reqBody.add(
        "media",
        new ByteArrayResource(file.getBytes()) {
          @Override
          public String getFilename() {
            return file.getOriginalFilename();
          }
        });
    if (mediaType.equals("video")) {
      VideoDescription videoDescription = new VideoDescription();
      videoDescription.setTitle(title);
      videoDescription.setIntroduction(introduction);

      reqBody.add("description", JSON.toJSONString(videoDescription));
    }

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(reqBody, headers);
    ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
    log.info("响应内容: {}", JSON.toJSONString(response));

    /*
    {"body":"{\"media_id\":\"iRlB_80Z8xByujJNpSciD0H7LtfULTGqsCfILBrjg4spilTMb-J4aAf4Sv4RXoS2\",\"item\":[]}",
    "headers":{"Connection":["keep-alive"],"Content-Type":["text/plain"],
    "Date":["Tue, 24 Sep 2024 14:50:42 GMT"],"Content-Length":["89"]},"statusCode":"OK","statusCodeValue":200}
    */
    return response;
  }

  /** 上传永久素材为视频时使用 */
  @Data
  public static class VideoDescription {

    private String title;

    private String introduction;
  }

  /**
   * 获取永久素材
   *
   * @param mediaId 要获取的素材的media_id
   * @return r
   */
  @GetMapping(value = "/get-permanent-media")
  public ResponseEntity<?> getPermanentMedia(String mediaId) {
    // 获取accessToken
    AccessTokenDTO accessTokenDTO = wxMpApiUrlManager.getAccessToken();

    // 构建请求 URL
    String url = MediaEnum.MATERIAL_GET_MATERIAL.formatUrl(accessTokenDTO.getAccess_token());
    Map<String, String> params = new HashMap<>();
    params.put("media_id", mediaId);

    ResponseEntity<String> resp =
        restTemplate.postForEntity(url, JSON.toJSONString(params), String.class);
    log.info("响应内容: {}", JSON.toJSONString(resp));

    return resp;
  }

  // 删除永久素材
  @GetMapping(value = "/delete-permanent-media")
  public ResponseEntity<?> deletePermanentMedia(String mediaId) {
    // 获取accessToken
    AccessTokenDTO accessTokenDTO = wxMpApiUrlManager.getAccessToken();

    // 构建请求 URL
    String url = MediaEnum.MATERIAL_DEL_MATERIAL.formatUrl(accessTokenDTO.getAccess_token());
    Map<String, String> params = new HashMap<>();
    params.put("media_id", mediaId);

    ResponseEntity<String> resp =
        restTemplate.postForEntity(url, JSON.toJSONString(params), String.class);
    log.info("响应内容: {}", JSON.toJSONString(resp));

    return resp;
  }

  /**
   * 获取素材总数
   *
   * @return r
   */
  @GetMapping(value = "/get-permanent-media-count")
  public ResponseEntity<?> getPermanentMediaCount() {
    // 获取accessToken
    AccessTokenDTO accessTokenDTO = wxMpApiUrlManager.getAccessToken();

    // 构建请求 URL
    String url = MediaEnum.MATERIAL_GET_MATERIALCOUNT.formatUrl(accessTokenDTO.getAccess_token());

    ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
    log.info("响应内容: {}", JSON.toJSONString(resp));
    /*
     * {"voice_count":0,"video_count":1,"image_count":1,"news_count":0}
     *
     * voice_count	语音总数量
     * video_count	视频总数量
     * image_count	图片总数量
     * news_count	图文总数量
     * */

    return resp;
  }

  /**
   * 获取素材列表
   *
   * @param type 素材的类型，图片（image）、视频（video）、语音 （voice）、图文（news）
   * @param offset 从全部素材的该偏移位置开始返回，0表示从第一个素材 返回
   * @param count 返回素材的数量，取值在1到20之间
   * @return
   */
  @GetMapping(value = "/get-permanent-media-list")
  public ResponseEntity<?> getPermanentMediaList(String type, Integer offset, Integer count) {
    // 获取accessToken
    AccessTokenDTO accessTokenDTO = wxMpApiUrlManager.getAccessToken();
    String url = MediaEnum.MATERIAL_BATCHGET_MATERIAL.formatUrl(accessTokenDTO.getAccess_token());

    GetPermanentMediaListDTO dto = new GetPermanentMediaListDTO();
    dto.setType(type);
    dto.setOffset(offset);
    dto.setCount(count);

    ResponseEntity<String> resp =
        restTemplate.postForEntity(url, JSON.toJSONString(dto), String.class);
    log.info("响应内容: {}", JSON.toJSONString(resp));

    return resp;
  }
}
