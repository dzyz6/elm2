# ele 饿了吧 V1.0

## 1. 内容
- elmclient 前端代码，基于 VUE 2
- elmboot 后端代码，基于 Spring Boot
- elm.sql 数据库初始化脚本

## 2. 数据库设置

- 首先，需要有一个 MySQL Server，为了协同开发方便调试，建议租用一台云服务器来安装 MySQL 。
- 将数据库初始化脚本 elm.sql 导入到数据库中，并设置好访问的用户和权限。

## 3. 后端项目 elmboot 部署运行 
- 首先需要下载安装项目管理工具 maven [ https://maven.apache.org/ ]
- 修改 src/main/resources/application.properties 文件，填写连接数据库需要的正确的地址、用户名和密码。
- 运行打包命令 `mvn package` ，生成 target/elmboot-0.0.1-SNAPSHOT.jar 。这是”胖jar包“，一个jar包就包含后端项目运行所需的全部文件，可以拷贝到任何一台仅安装了JDK的机器上运行。
- 运行命令 `java -jar elmboot-0.0.1-SNAPSHOT.jar` ，启动后端项目，保持字符窗口不被关闭即可提供服务。
- 在浏览器上输入 `http://localhost:8080/elm/UserController/getUserByIdByPass?userId=11111111111&password=123`，如果看到该用户的详细信息，说明后端项目运行正常。（多么不安全的服务）

## 4. 测试后端项目 
- 建议使用 Postman [ https://www.postman.com/ ]、 Apifox [ https://apifox.com/ ] 之类的工具，模拟 HTTP 请求，对后端进行测试。

