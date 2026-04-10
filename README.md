# TA 招聘系统（TA Recruitment System）

> 一个use Maven 管理的 Java Servlet/JSP Web 应用，支持 TA、MO（Module Organiser）、Admin 三类角色的岗位招聘流程。

## 📋 项目简介

本仓库是一个功能完整的 TA 招聘门户系统，采用 **MVC 分层结构** 设计：

- **后端**：Java Servlet 处理请求、业务逻辑、数据持久化
- **前端**：JSP 模板渲染、CSS 样式统一、JavaScript 交互
- **数据存储**：基于 CSV 文本文件，无数据库依赖
- **部署方式**：WAR 包部署到本地 Tomcat

### ✨ 核心特性

| 特性 | 说明 |
|-----|------|
| **三角色支持** | TA 申请者、MO 岗位发布者、Admin 管理员 |
| **基于 Session 的认证** | 安全的登录/登出控制 |
| **岗位管理** | MO 创建发布岗位、TA 浏览申请 |
| **工作流审核** | MO 接受/拒绝申请，Admin 查看工作量统计 |
| **响应式设计** | 统一美观的界面风格 |

---

## 👥 开发成员

| 姓名 | GitHub |
|------|--------|
| 陈泰宇 | `@ccc-7` |
| 朱思远 | `@woruqingshan` |
| 刘泽棠 | `@yongyuandez` |
| 苗润曦 | `@Miao200506` |
| 杨刚 | `@SystemName-e6lq` |
| 辛炯彻 | `@jiongche110` |

---

## 🎯 已实现功能

### 1️⃣ TA Applicant（学生申请者）
- ✅ 注册 TA 账号
- ✅ 登录系统
- ✅ 查看 Dashboard（申请概览）
- ✅ 浏览开放岗位列表
- ✅ 提交岗位申请
- ✅ 查看申请状态

### 2️⃣ Module Organiser（课程负责人）
- ✅ 登录系统
- ✅ 查看 MO Dashboard
- ✅ 创建并发布新岗位
- ✅ 查看所有申请记录
- ✅ 接受/拒绝申请

### 3️⃣ Admin（系统管理员）
- ✅ 登录系统
- ✅ 查看 TA 工作量统计

### 4️⃣ 系统公共能力
- ✅ 基于 Session 的角色登录控制
- ✅ 基于 CSV 的数据读写
- ✅ 基于 Service 的业务逻辑封装
- ✅ 基于 JSP + CSS 的统一门户风格

---

## 📁 项目结构

```
ta-webapp/
├── README.md
├── pom.xml                           # Maven 构建配置
│
├── src/main/java/edu/bupt/ta/
│   ├── controller/                   # HTTP 请求处理层
│   │   ├── HomeServlet.java
│   │   ├── LoginServlet.java
│   │   ├── LogoutServlet.java
│   │   ├── RegisterServlet.java
│   │   ├── TaDashboardServlet.java
│   │   ├── JobListServlet.java
│   │   ├── ApplyServlet.java
│   │   ├── ApplicationStatusServlet.java
│   │   ├── MODashboardServlet.java
│   │   ├── MOJobServlet.java
│   │   ├── MOApplicationServlet.java
│   │   └── AdminDashboardServlet.java
│   │
│   ├── model/                        # 数据模型与枚举
│   │   ├── User.java
│   │   ├── UserRole.java
│   │   ├── Job.java
│   │   ├── JobStatus.java
│   │   ├── Application.java
│   │   └── ApplicationStatus.java
│   │
│   ├── service/                      # 业务逻辑层
│   │   ├── UserService.java
│   │   ├── JobService.java
│   │   ├── ApplicationService.java
│   │   ├── DashboardService.java
│   │   └── AdminService.java
│   │
│   └── storage/                      # 数据持久化层
│       └── FileStorageUtil.java
│
├── src/main/webapp/                  # Web 根目录
│   ├── index.jsp                     # 根路径入口
│   ├── assets/
│   │   ├── css/
│   │   │   └── style.css             # 全站统一样式
│   │   └── js/
│   │       └── app.js                # 页面交互脚本
│   │
│   └── WEB-INF/
│       ├── web.xml                   # Servlet 部署描述
│       └── jsp/
│           ├── home.jsp              # 登录首页
│           ├── register.jsp          # TA 注册页
│           │
│           ├── common/               # 公共页面片段
│           │   ├── header.jspf
│           │   ├── footer.jspf
│           │   └── flash.jspf        # 消息提示
│           │
│           ├── ta/                   # TA 相关页面
│           │   ├── dashboard.jsp
│           │   ├── jobs.jsp
│           │   └── applications.jsp
│           │
│           ├── mo/                   # MO 相关页面
│           │   ├── dashboard.jsp
│           │   ├── new-job.jsp
│           │   └── applications.jsp
│           │
│           └── admin/                # Admin 相关页面
│               └── dashboard.jsp
│
├── data/                             # 数据文件目录
│   ├── ta_users.csv                  # 用户数据
│   ├── jobs.csv                      # 岗位数据
│   └── applications.csv              # 申请记录
│
├── docs/                             # 项目文档
│   ├── project-plan.md
│   ├── requirements.md
│   └── architecture.md
│
└── target/                           # 构建输出（不提交）
    └── ta-webapp.war
```

---

## 🔐 默认测试账号

快速登录测试系统：

| 角色 | 用户名 | 密码 |
|------|---------|------|
| **TA** | `seele` | `123456` |
| **MO** | `mo1` | `123456` |
| **Admin** | `admin` | `123456` |

---

## 💻 环境配置（JDK 17 + Tomcat 10 + Maven）

### 前置要求

- **Windows** 操作系统
- **JDK 17**
- **Apache Tomcat 10**
- **Apache Maven 3.9+**
- **Git for Windows**

### 一步步安装

#### 1️⃣ 安装 VS Code 和 Java 扩展

```bash
# 在 VS Code Extensions 中搜索并安装：
- Extension Pack for Java
- Community Server Connectors
```

#### 2️⃣ 配置 JDK 17

1. **下载并安装 JDK 17**
   - 安装目录（示例）：`D:\Java\jdk-17.0.x`

2. **配置系统环境变量**
   - 新建 `JAVA_HOME`：`D:\Java\jdk-17.0.x`
   - 修改 `Path`：添加 `%JAVA_HOME%\bin`

3. **验证安装**
   ```powershell
   java -version
   # 应输出：java version "17.x.x"
   ```

#### 3️⃣ 安装 Apache Tomcat 10

1. **下载并解压**
   - Tomcat 10：`https://tomcat.apache.org/`
   - 解压到：`D:\apache-tomcat-10.1.52`

2. **配置系统环境变量**
   - 新建 `CATALINA_HOME`：`D:\apache-tomcat-10.1.52`
   - 修改 `Path`：添加 `%CATALINA_HOME%\bin`

3. **启动 Tomcat**
   ```powershell
   cd D:\apache-tomcat-10.1.52\bin
   .\startup.bat
   ```

4. **验证运行**
   - 访问 `http://localhost:8080`
   - 看到 Tomcat 欢迎页面说明成功

#### 4️⃣ 安装 Apache Maven

1. **下载并解压**
   - Maven 3.9+：`https://maven.apache.org/download.cgi`
   - 解压到：`D:\apache-maven-3.9.x`

2. **配置系统环境变量**
   - 新建 `MAVEN_HOME`：`D:\apache-maven-3.9.x`
   - 修改 `Path`：添加 `%MAVEN_HOME%\bin`

3. **验证安装**
   ```powershell
   mvn -v
   # 应输出 Maven 版本和 Java 17 版本信息
   ```

#### 5️⃣ 安装 Git for Windows

```powershell
# 下载并安装 Git for Windows (x64)
# 验证安装
git --version
```

---

## 🚀 快速开始

### 第一步：构建项目

在项目根目录 `ta-webapp/` 下执行：

```powershell
mvn clean package
```

✅ 成功时输出 `BUILD SUCCESS`，在 `target/` 生成 **`ta-webapp.war`**

### 第二步：部署到 Tomcat

```powershell
# 复制 WAR 文件到 Tomcat webapps 目录
copy target\ta-webapp.war "D:\apache-tomcat-10.1.52\webapps\"
```

### 第三步：启动 Tomcat

```powershell
cd D:\apache-tomcat-10.1.52\bin
.\startup.bat
```

如果需要让运行数据写入 Tomcat 外部目录 `D:\apache-tomcat-10.1.52\ta-data`，并同步镜像到本地项目 `ta-webapp\data`，不要直接在 PowerShell 里单独输入 `-Dta.data.dir=...`。  
正确方式是先设置 `CATALINA_OPTS`，再启动 Tomcat。

临时方式：仅对当前 PowerShell 窗口生效

```powershell
对应修改为自己的apache-tomcat-10.1.52\ta-data路径和ta-webapp\data（可能是lztinternationallzt/data）
$env:CATALINA_OPTS='-Dta.data.dir=D:\apache-tomcat-10.1.52\ta-data -Dta.data.mirror.dir=C:\Users\siyuen\Desktop\all code\JavaIDEA\TA_system\ta-webapp\data'
cd D:\apache-tomcat-10.1.52\bin
.\startup.bat
```

更推荐的持久方式：在 `D:\apache-tomcat-10.1.52\bin\setenv.bat` 中加入

```bat
@echo off
set "CATALINA_OPTS=%CATALINA_OPTS% -Dta.data.dir=D:\apache-tomcat-10.1.52\ta-data -Dta.data.mirror.dir=C:\Users\siyuen\Desktop\all code\JavaIDEA\TA_system\ta-webapp\data"
```

然后正常启动：

```powershell
cd D:\apache-tomcat-10.1.52\bin
.\startup.bat
```

如果需要重新初始化外部数据目录，可以先删除：

```powershell
Remove-Item "D:\apache-tomcat-10.1.52\ta-data\ta_users.csv" -Force -ErrorAction SilentlyContinue
Remove-Item "D:\apache-tomcat-10.1.52\ta-data\jobs.csv" -Force -ErrorAction SilentlyContinue
Remove-Item "D:\apache-tomcat-10.1.52\ta-data\applications.csv" -Force -ErrorAction SilentlyContinue
```

*若 Tomcat 已运行，会自动检测新 WAR 并重新加载应用*

### 第四步：访问应用

在浏览器打开：
- 首页：`http://localhost:8080/ta-webapp/`
- 或：`http://localhost:8080/ta-webapp/home`

---

## 📝 开发工作流

### 日常开发流程（修改后本地验证）

```
修改代码
    ↓
mvn clean package    （编译构建）
    ↓
copy WAR到 Tomcat    （部署应用）
    ↓
Tomcat 自动重载      （或手动 startup.bat）
    ↓
浏览器 F5 刷新验证   （查看效果）
```

### 详细步骤

1. **修改代码**：在 VSCode 中编辑 JSP、Java、CSS 或 JS 文件

2. **本地构建**：
   ```powershell
   mvn clean package
   ```

3. **部署到 Tomcat**：
   ```powershell
   copy target\ta-webapp.war "D:\apache-tomcat-10.1.52\webapps\"
   ```

4. **启动或重载**（若未启动）：
   ```powershell
   cd D:\apache-tomcat-10.1.52\bin
   .\startup.bat
   ```

5. **浏览器检查**：
   - 打开 `http://localhost:8080/ta-webapp/`
   - 按 `F5` 刷新查看效果

---

## 🔄 Git 版本控制（成员分支工作流）

本项目采用 **成员分支** 工作流，确保每人有独立分支与提交记录。

### 工作原则

| 分支 | 规则 |
|------|------|
| **`master`** | 仅存放稳定、可演示的版本；禁止直接提交；只通过 PR 合并 |
| **成员分支** | 每人一条，任务由组长分配；自测通过后提 PR 到 master |

### 步骤一：组长初始化成员分支（仅一次）

```powershell
git checkout master
git pull origin master

# 为每个成员创建分支
git checkout -b zhangsan
git push -u origin zhangsan

git checkout -b lisi
git push -u origin lisi

# ... 其他成员类似

git checkout master
```

### 步骤二：成员日常开发

```powershell
# 1. 同步 master 并切到自己的分支
git checkout master
git pull origin master
git checkout zhangsan

# 2. （可选）合并最新 master
git merge master

# 3. 修改代码（按上面"开发工作流"操作）
# - 编辑代码
# - mvn clean package
# - 部署到 Tomcat
# - 浏览器验证

# 4. 提交并推送
git add .
git commit -m "feat: add feature description"
git push origin zhangsan

# 5. 在 GitHub 上发起 Pull Request
#    从 zhangsan → master
```

### 步骤三：组长 Review 与合并

```powershell
# 1. 在 GitHub 上查看 PR 的 "Files changed"
# 2. 确认修改与任务一致

# 3. （可选）本地验证
git fetch origin zhangsan
git checkout zhangsan
mvn clean package
# 部署到 Tomcat 检查

# 4. 在 GitHub 上点击 "Merge pull request"
```

### 步骤四：同步最新 master

合并后其他成员应立即拉取并合并最新 master：

```powershell
git checkout master
git pull origin master
git checkout zhangsan
git merge master
```

---

## 🤝 处理合并冲突

### 情况：两人改了同一文件

**A 的分支已合并到 master，现在合并 B 的分支**

1. **B 的成员在本地处理冲突**：
   ```powershell
   git checkout master
   git pull origin master
   git checkout member-b
   git merge master
   ```

2. **手动解决冲突**：
   - 打开冲突文件
   - 删除冲突标记：`<<<<<<<`、`=======`、`>>>>>>>`
   - 保留或合并所需的修改

3. **提交冲突解决**：
   ```powershell
   git add .
   git commit -m "chore: resolve merge conflict with master"
   git push origin member-b
   ```

4. **组长在 GitHub 上再次 Merge PR**

### 💡 最佳实践

- **组长**：分配任务时让不同成员负责不同文件或模块，减少冲突
- **成员**：及时同步 master，避免落后太久
- **PR 顺序**：按提交时间逐个合并，冲突立即处理

---

## 📚 文件功能详解

### Controller 层（HTTP 请求处理）

| Servlet | 功能 |
|---------|------|
| `HomeServlet` | 处理 `/home`，跳转到登录首页 |
| `LoginServlet` | 用户登录验证、角色判断、Session 写入 |
| `LogoutServlet` | 注销登录、清除 Session |
| `RegisterServlet` | 新 TA 注册、自动登录 |
| `TaDashboardServlet` | 加载 TA Dashboard 统计数据 |
| `JobListServlet` | 展示可申请岗位列表 |
| `ApplyServlet` | 提交 TA 岗位申请 |
| `ApplicationStatusServlet` | 显示 TA 申请记录 |
| `MODashboardServlet` | MO 统计信息展示 |
| `MOJobServlet` | MO 创建发布岗位 |
| `MOApplicationServlet` | MO 查看并审核申请 |
| `AdminDashboardServlet` | 展示工作量统计 |

### Service 层（业务逻辑）

| Service | 职责 |
|---------|------|
| `UserService` | 用户认证、注册、查询 |
| `JobService` | 岗位查询、创建、匹配度计算 |
| `ApplicationService` | 申请提交、查询、状态更新 |
| `DashboardService` | 聚合 Dashboard 展示数据 |
| `AdminService` | 工作量统计 |

### Storage 层（数据持久化）

| 文件 | 说明 |
|------|------|
| `FileStorageUtil.java` | CSV 文件加载与保存、运行目录解析 |

### Model 层（数据模型）

| 类 | 说明 |
|----|------|
| `User` | 用户实体 |
| `UserRole` | 用户角色枚举：TA、MO、ADMIN |
| `Job` | 岗位实体 |
| `JobStatus` | 岗位状态枚举 |
| `Application` | 申请实体 |
| `ApplicationStatus` | 申请状态枚举 |

---

## 📊 数据文件说明

### `ta_users.csv`（用户数据）

```csv
用户名,密码,姓名,角色,描述
seele,123456,Seele,TA,TA 申请者账号
mo1,123456,Module Organiser,MO,课程负责人账号
admin,123456,Administrator,ADMIN,系统管理员账号
```

### `jobs.csv`（岗位数据）

```csv
岗位ID,标题,课程代码,教师,工时,技能要求,状态
job001,Python 助教,CS101,Dr. Smith,50,Python/Teaching,OPEN
```

### `applications.csv`（申请记录）

```csv
申请ID,申请人,岗位ID,状态,提交时间,备注
app001,seele,job001,PENDING,2024-03-20 10:30:00,
```

---

## 🛑 常见问题排查

### Q1：`mvn clean package` 失败

**原因可能**：
- ❌ JDK 不是 17，改成 17 后重试
- ❌ 网络无法访问 Maven 中央仓库，检查网络连接
- ❌ 项目文件编码问题，检查 `pom.xml` 中的 `<project.build.sourceEncoding>`

**解决**：
```powershell
# 检查 Java 版本
java -version

# 清空 Maven 缓存后重试
mvn clean package -U
```

### Q2：部署后浏览器访问返回 404

**原因可能**：
- ❌ Tomcat 未启动
- ❌ WAR 文件未被 Tomcat 解压（通常在 `webapps/` 下看到 `ta-webapp` 文件夹）
- ❌ URL 路径错误

**解决**：
```powershell
# 确保 Tomcat 运行
cd D:\apache-tomcat-10.1.52\bin
.\startup.bat

# 访问正确的 URL
http://localhost:8080/ta-webapp/

# 查看 Tomcat 日志（若有错）
D:\apache-tomcat-10.1.52\logs\catalina.out
```

### Q3：Git 提交后 PR 显示冲突

**原因**：自己的分支落后于 master，两人改了同一文件。

**解决**：按上文"处理合并冲突"部分操作。

---

## 📖 相关文档

- [📋 项目计划](docs/project-plan.md)
- [📝 需求分析](docs/requirements.md)
- [🏗️ 架构说明](docs/architecture.md)

---

## 🎓 技术栈

| 组件 | 版本 |
|------|------|
| **Java** | 17 |
| **Servlet/JSP** | Jakarta 10 (Tomcat 10) |
| **Maven** | 3.9+ |
| **JSTL** | 2.0+ |
| **CSS3** | Modern |
| **JavaScript** | ES6+ |

---



**项目状态**：✅ 核心功能完成，持续迭代中...
