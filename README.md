## TA Recruitment System – 环境与基础配置说明

当前阶段，本仓库的 `README` 只说明**本地开发环境的配置步骤（JDK 17 + Tomcat 10 + Maven）**。  
后续会在文档中逐步补充需求、设计和分支规则等内容。

---

### 一、系统环境配置（JDK 17 + Tomcat 10 + Maven）

#### 1. 安装并配置 JDK 17

1. 安装 JDK 17（例如解压到：`D:\Java\jdk-17.0.x`）。  
2. 配置系统环境变量（以 Windows 为例）：
   - 新建/修改系统变量 **`JAVA_HOME`**：
     - 值：`D:\Java\jdk-17.0.x`  
     - 注意：**不要在值后面加 `\bin`**。
   - 在系统变量 **`Path`** 中添加：
     - `%JAVA_HOME%\bin`
   - 如系统中已有其他 JDK 版本的路径（例如 JDK 21/22），可以将其移到下面或删除，确保 **JDK 17 的路径优先**。
3. 打开新的 PowerShell，验证：

```powershell
java -version
```

应看到输出类似：`java version "17.x.x"`。

---

#### 2. 安装并配置 Apache Tomcat 10

1. 下载 Tomcat 10：
   - 打开 `https://tomcat.apache.org/`，进入 **Tomcat 10** 下载页面；
   - 在 **Binary Distributions → Core** 选择 `64-bit Windows zip` 包。
2. 解压到固定目录，例如：
   - `D:\apache-tomcat-10.1.52`
3. 配置系统环境变量：
   - 新建系统变量 **`CATALINA_HOME`**：
     - 值：`D:\apache-tomcat-10.1.52`
   - 在系统变量 **`Path`** 中添加：
     - `%CATALINA_HOME%\bin`
4. 启动 Tomcat 并验证：

```powershell
cd D:\apache-tomcat-10.1.52\bin
startup.bat
```

在浏览器访问：

```text
http://localhost:8080
```

如果看到 Tomcat 欢迎页面，则说明 Tomcat 配置成功，并且正在使用当前系统的 JDK（建议为 17）。

---

#### 3. 安装并配置 Maven

1. 下载 Maven：
   - 打开 `https://maven.apache.org/download.cgi`
   - 下载 **Binary zip archive**（例如 `apache-maven-3.9.x-bin.zip`）。
2. 解压到固定目录，例如：
   - `D:\apache-maven-3.9.x`
3. 配置系统环境变量：
   - 新建系统变量 **`MAVEN_HOME`**：
     - 值：`D:\apache-maven-3.9.x`
   - 在系统变量 **`Path`** 中添加：
     - `%MAVEN_HOME%\bin`
4. 打开新的 PowerShell，验证：

```powershell
mvn -v
```

应看到 Maven 版本信息，并且 Java 版本为 17。

---

