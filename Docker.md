## 制作Docker镜像

### 制作镜像 
```
docker build -f Dockerfile -t "enba-integrate-wx-mp" . --no-cache
```

### 运行
```
docker run -d -p 80:80 -v /usr/local/project/enba-integrate-wx-mp:/home/logs --name enba-integrate-wx-mp enba-integrate-wx-mp:latest
```

### 查看运行日志
```
docker logs -f enba-integrate-wx-mp
```

### 进入容器
```
docker exec -it enba-integrate-wx-mp sh
```