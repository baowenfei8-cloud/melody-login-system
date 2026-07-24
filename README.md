# 🎵 MelodyLogin - 音乐主题登录系统

一个精美、完整的音乐主题登录系统，基于 Spring Boot + JPA + MySQL + Thymeleaf 构建。

## ✨ 功能特性

| 功能 | 说明 |
|------|------|
| 🏠 **品牌主页** | 黑胶唱片视觉风格的品牌落地页，右上角登录/注册按钮 |
| 🔑 **用户登录** | 支持「记住此账号」功能（Cookie 7天有效期） |
| 📝 **用户注册** | 密码强度检测、实时表单验证 |
| 🔐 **找回密码** | 通过邮箱发送密码（支持 QQ/163/Gmail/Outlook 等任意邮箱） |
| 📊 **Dashboard** | 数据展示卡片、数字滚动动画、最近活动列表 |
| 👤 **个人中心** | 资料编辑、密码修改（AJAX 实时保存） |
| 🎵 **音乐主题UI** | 浮动音符粒子、声波动画、黑胶唱片旋转特效 |

## 🛠️ 技术栈

- **后端**: Java 21, Spring Boot 3.3.12, Spring Data JPA, Hibernate
- **前端**: Thymeleaf, HTML5, CSS3, JavaScript (原生)
- **数据库**: MySQL 8.0
- **构建**: Maven

## 🚀 快速开始

### 前置要求
- JDK 21+
- MySQL 8.0+
- Maven (或使用项目自带的 mvnw)

### 1. 创建数据库
```sql
CREATE DATABASE music_login_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 修改数据库配置
打开 `src/main/resources/application.properties`，修改 MySQL 连接信息：
```properties
spring.datasource.username=你的数据库用户名
spring.datasource.password=你的数据库密码
```

### 3. （可选）配置邮箱找回密码
在 `application.properties` 中取消对应邮箱方案的注释并填入真实信息。

### 4. 运行项目
```bash
# 方式一：Maven
mvn spring-boot:run

# 方式二：项目自带 Maven Wrapper
./mvnw spring-boot:run
```

### 5. 访问
浏览器打开 [http://localhost:8081](http://localhost:8081)

## 📁 项目结构

```
src/main/java/com/musiclogin/
├── MusicLoginApplication.java     # 入口类
├── controller/AuthController.java # 路由控制
├── service/
│   ├── UserService.java           # 业务逻辑
│   └── EmailService.java          # 邮件发送
├── dao/UserRepository.java        # 数据库操作
├── entity/User.java               # 用户实体
└── dto/
    ├── LoginRequest.java          # 登录数据
    └── RegisterRequest.java       # 注册数据

src/main/resources/
├── application.properties         # 配置文件
├── templates/                     # 前端页面
│   ├── index.html                 # 主界面
│   ├── login.html                 # 登录页
│   ├── register.html              # 注册页
│   ├── forgot.html                # 找回密码
│   ├── dashboard.html             # 用户主页
│   └── profile.html               # 个人中心
└── static/
    ├── css/style.css              # 样式
    └── js/app.js                  # 交互脚本
```

## 📄 License

MIT
