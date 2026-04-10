# TA Recruitment System
> A Maven-managed Java Servlet/JSP web application that supports the full recruitment workflow for three user roles: TA Applicant, MO (Module Organiser), and System Admin.

## 📋 Project Overview
This repository contains a fully functional TA recruitment portal system, designed with a **MVC layered architecture**:
- **Backend**: Java Servlets for request processing, business logic implementation, and data persistence
- **Frontend**: JSP template rendering, unified CSS styling, and JavaScript interactive logic
- **Data Storage**: CSV text file based, no database dependency
- **Deployment**: WAR package deployment to a local Tomcat server

### ✨ Core Features
| Feature | Description |
|---------|-------------|
| **Three Role Support** | TA Applicant, MO (Job Publisher), and System Admin |
| **Session-Based Authentication** | Secure login and logout access control |
| **Job Management** | MOs create and publish positions; TAs browse and apply for jobs |
| **Approval Workflow** | MOs accept/reject applications; Admins view workload statistics |
| **Responsive Design** | Unified and user-friendly interface style |

---

## 👥 Contributors
| Name | GitHub |
|------|--------|
| Chen Taiyu | `@ccc-7` |
| Zhu Siyuan | `@woruqingshan` |
| Liu Zetang | `@yongyuandez` |
| Miao Runxi | `@Miao200506` |
| Yang Gang | `@SystemName-e6lq` |
| Xin Jiongche | `@jiongche110` |

---

## 🎯 Implemented Features
### 1️⃣ TA Applicant
- ✅ TA account registration
- ✅ System login
- ✅ Dashboard access (application overview)
- ✅ Browse open job listings
- ✅ Submit job applications
- ✅ Check application status

### 2️⃣ Module Organiser (MO)
- ✅ System login
- ✅ MO Dashboard access
- ✅ Create and publish new job positions
- ✅ View all application records
- ✅ Accept/reject applications

### 3️⃣ System Admin
- ✅ System login
- ✅ View TA workload statistics

### 4️⃣ System Common Capabilities
- ✅ Session-based role login control
- ✅ CSV-based data read and write operations
- ✅ Service-layer encapsulated business logic
- ✅ Unified portal style based on JSP + CSS

---

## 📁 Project Structure
```
ta-webapp/
├── README.md
├── pom.xml                           # Maven build configuration
│
├── src/main/java/edu/bupt/ta/
│   ├── controller/                   # HTTP Request Handling Layer
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
│   ├── model/                        # Data Models and Enums
│   │   ├── User.java
│   │   ├── UserRole.java
│   │   ├── Job.java
│   │   ├── JobStatus.java
│   │   ├── Application.java
│   │   └── ApplicationStatus.java
│   │
│   ├── service/                      # Business Logic Layer
│   │   ├── UserService.java
│   │   ├── JobService.java
│   │   ├── ApplicationService.java
│   │   ├── DashboardService.java
│   │   └── AdminService.java
│   │
│   └── storage/                      # Data Persistence Layer
│       └── FileStorageUtil.java
│
├── src/main/webapp/                  # Web Root Directory
│   ├── index.jsp                     # Root Path Entry
│   ├── assets/
│   │   ├── css/
│   │   │   └── style.css             # Global Unified Styles
│   │   └── js/
│   │       └── app.js                # Page Interaction Scripts
│   │
│   └── WEB-INF/
│       ├── web.xml                   # Servlet Deployment Descriptor
│       └── jsp/
│           ├── home.jsp              # Login Home Page
│           ├── register.jsp          # TA Registration Page
│           │
│           ├── common/               # Common Page Fragments
│           │   ├── header.jspf
│           │   ├── footer.jspf
│           │   └── flash.jspf        # Message Notification
│           │
│           ├── ta/                   # TA Related Pages
│           │   ├── dashboard.jsp
│           │   ├── jobs.jsp
│           │   └── applications.jsp
│           │
│           ├── mo/                   # MO Related Pages
│           │   ├── dashboard.jsp
│           │   ├── new-job.jsp
│           │   └── applications.jsp
│           │
│           └── admin/                # Admin Related Pages
│               └── dashboard.jsp
│
├── data/                             # Data File Directory
│   ├── ta_users.csv                  # User Data
│   ├── jobs.csv                      # Job Position Data
│   └── applications.csv              # Application Records
│
├── docs/                             # Project Documentation
│   ├── project-plan.md
│   ├── requirements.md
│   └── architecture.md
│
└── target/                           # Build Output (Not Committed)
    └── ta-webapp.war
```

---

## 🔐 Default Test Accounts
Quick login for system testing:
| Role | Username | Password |
|------|----------|----------|
| **TA** | `seele` | `123456` |
| **MO** | `mo1` | `123456` |
| **Admin** | `admin` | `123456` |

---

## 💻 Environment Configuration (JDK 17 + Tomcat 10 + Maven)
### Prerequisites
- **Windows** Operating System
- **JDK 17**
- **Apache Tomcat 10**
- **Apache Maven 3.9+**
- **Git for Windows**

### Step-by-Step Installation
#### 1️⃣ Install VS Code and Java Extensions
```bash
# Search and install in VS Code Extensions:
- Extension Pack for Java
- Community Server Connectors
```

#### 2️⃣ Configure JDK 17
1. **Download and Install JDK 17**
   - Installation Directory (Example): `D:\Java\jdk-17.0.x`

2. **Configure System Environment Variables**
   - Create a new system variable `JAVA_HOME` with value: `D:\Java\jdk-17.0.x`
   - Modify the `Path` variable: add `%JAVA_HOME%\bin`

3. **Verify Installation**
   ```powershell
   java -version
   # Expected output: java version "17.x.x"
   ```

#### 3️⃣ Install Apache Tomcat 10
1. **Download and Extract**
   - Tomcat 10: `https://tomcat.apache.org/`
   - Extract to: `D:\apache-tomcat-10.1.52`

2. **Configure System Environment Variables**
   - Create a new system variable `CATALINA_HOME` with value: `D:\apache-tomcat-10.1.52`
   - Modify the `Path` variable: add `%CATALINA_HOME%\bin`

3. **Start Tomcat**
   ```powershell
   cd D:\apache-tomcat-10.1.52\bin
   .\startup.bat
   ```

4. **Verify Running Status**
   - Visit `http://localhost:8080` in your browser
   - Installation is successful if the Tomcat welcome page is displayed

#### 4️⃣ Install Apache Maven
1. **Download and Extract**
   - Maven 3.9+: `https://maven.apache.org/download.cgi`
   - Extract to: `D:\apache-maven-3.9.x`

2. **Configure System Environment Variables**
   - Create a new system variable `MAVEN_HOME` with value: `D:\apache-maven-3.9.x`
   - Modify the `Path` variable: add `%MAVEN_HOME%\bin`

3. **Verify Installation**
   ```powershell
   mvn -v
   # Expected output: Maven version and Java 17 version information
   ```

#### 5️⃣ Install Git for Windows
```powershell
# Download and install Git for Windows (x64)
# Verify installation
git --version
```

---

## 🚀 Quick Start
### Step 1: Build the Project
Execute the following command in the project root directory `ta-webapp/`:
```powershell
mvn clean package
```
✅ On success, `BUILD SUCCESS` will be printed, and **`ta-webapp.war`** will be generated in the `target/` directory.

### Step 2: Deploy to Tomcat
```powershell
# Copy the WAR file to the Tomcat webapps directory
copy target\ta-webapp.war "D:\apache-tomcat-10.1.52\webapps\"
```

### Step 3: Start Tomcat
```powershell
cd D:\apache-tomcat-10.1.52\bin
.\startup.bat
```

If you need to write runtime data to an external directory `D:\apache-tomcat-10.1.52\ta-data` outside Tomcat, and synchronize it to the local project directory `ta-webapp\data`, **do NOT directly input `-Dta.data.dir=...` alone in PowerShell**.
The correct approach is to set `CATALINA_OPTS` first, then start Tomcat.

Temporary Method: Only effective for the current PowerShell window
```powershell
# Modify to your own apache-tomcat-10.1.52\ta-data path and ta-webapp\data path
$env:CATALINA_OPTS='-Dta.data.dir=D:\apache-tomcat-10.1.52\ta-data -Dta.data.mirror.dir=C:\Users\siyuen\Desktop\all code\JavaIDEA\TA_system\ta-webapp\data'
cd D:\apache-tomcat-10.1.52\bin
.\startup.bat
```

Recommended Persistent Method: Add the following content to `D:\apache-tomcat-10.1.52\bin\setenv.bat`
```bat
@echo off
set "CATALINA_OPTS=%CATALINA_OPTS% -Dta.data.dir=D:\apache-tomcat-10.1.52\ta-data -Dta.data.mirror.dir=C:\Users\siyuen\Desktop\all code\JavaIDEA\TA_system\ta-webapp\data"
```

Then start Tomcat normally:
```powershell
cd D:\apache-tomcat-10.1.52\bin
.\startup.bat
```

If you need to reinitialize the external data directory, you can delete the files first:
```powershell
Remove-Item "D:\apache-tomcat-10.1.52\ta-data\ta_users.csv" -Force -ErrorAction SilentlyContinue
Remove-Item "D:\apache-tomcat-10.1.52\ta-data\jobs.csv" -Force -ErrorAction SilentlyContinue
Remove-Item "D:\apache-tomcat-10.1.52\ta-data\applications.csv" -Force -ErrorAction SilentlyContinue
```

*If Tomcat is already running, it will automatically detect the new WAR file and reload the application*

### Step 4: Access the Application
Open the following link in your browser:
- Home Page: `http://localhost:8080/ta-webapp/`
- Alternative: `http://localhost:8080/ta-webapp/home`

---

## 📝 Development Workflow
### Daily Development Workflow (Local Validation After Changes)
```
Modify Code
    ↓
mvn clean package    (Compile and Build)
    ↓
Copy WAR to Tomcat    (Deploy Application)
    ↓
Tomcat Auto Reload      (Or manual startup.bat)
    ↓
Browser F5 Refresh to Validate   (Check Results)
```

### Detailed Steps
1. **Modify Code**: Edit JSP, Java, CSS or JS files in VSCode
2. **Local Build**:
   ```powershell
   mvn clean package
   ```
3. **Deploy to Tomcat**:
   ```powershell
   copy target\ta-webapp.war "D:\apache-tomcat-10.1.52\webapps\"
   ```
4. **Start or Reload** (if Tomcat is not running):
   ```powershell
   cd D:\apache-tomcat-10.1.52\bin
   .\startup.bat
   ```
5. **Check in Browser**:
   - Open `http://localhost:8080/ta-webapp/`
   - Press `F5` to refresh and view changes

---

## 🔄 Git Version Control (Contributor Branch Workflow)
This project adopts a **contributor branch** workflow to ensure each developer has an independent branch and complete commit history.

### Workflow Principles
| Branch | Rules |
|--------|-------|
| **`master`** | Only stores stable, demonstrable versions; direct commits are prohibited; only merged via Pull Request (PR) |
| **Contributor Branches** | One branch per developer, with tasks assigned by the team lead; submit PR to master after passing self-test |

### Step 1: Team Lead Initializes Contributor Branches (One Time Only)
```powershell
git checkout master
git pull origin master

# Create branch for each contributor
git checkout -b zhangsan
git push -u origin zhangsan

git checkout -b lisi
git push -u origin lisi

# ... Repeat for other contributors

git checkout master
```

### Step 2: Daily Development for Contributors
```powershell
# 1. Sync master and switch to your own branch
git checkout master
git pull origin master
git checkout zhangsan

# 2. (Optional) Merge the latest master
git merge master

# 3. Modify code (follow the "Development Workflow" above)
# - Edit code
# - mvn clean package
# - Deploy to Tomcat
# - Validate in browser

# 4. Commit and push
git add .
git commit -m "feat: add feature description"
git push origin zhangsan

# 5. Create a Pull Request on GitHub
#    From zhangsan → master
```

### Step 3: Team Lead Review and Merge
```powershell
# 1. View "Files changed" of the PR on GitHub
# 2. Confirm the changes are consistent with the assigned task

# 3. (Optional) Local validation
git fetch origin zhangsan
git checkout zhangsan
mvn clean package
# Deploy to Tomcat for functional inspection

# 4. Click "Merge pull request" on GitHub
```

### Step 4: Sync the Latest Master
After a PR is merged, other contributors should immediately pull and merge the latest master branch:
```powershell
git checkout master
git pull origin master
git checkout zhangsan
git merge master
```

---

## 🤝 Resolving Merge Conflicts
### Scenario: Two Developers Modified the Same File
**Developer A's branch has been merged into master, now merging Developer B's branch**

1. **Developer B resolves the conflict locally**:
   ```powershell
   git checkout master
   git pull origin master
   git checkout member-b
   git merge master
   ```

2. **Manually Resolve Conflicts**:
   - Open the conflicted file
   - Remove conflict markers: `<<<<<<<`, `=======`, `>>>>>>>`
   - Keep or merge the required code changes

3. **Commit Conflict Resolution**:
   ```powershell
   git add .
   git commit -m "chore: resolve merge conflict with master"
   git push origin member-b
   ```

4. **Team Lead Merges the PR Again on GitHub**

### 💡 Best Practices
- **Team Lead**: Assign tasks to different contributors for separate files or modules to minimize conflicts
- **Contributors**: Sync the master branch regularly to avoid falling too far behind
- **PR Order**: Merge PRs one by one according to submission time, and resolve conflicts immediately when they occur

---

## 📚 Detailed File Function Description
### Controller Layer (HTTP Request Handling)
| Servlet | Function |
|---------|----------|
| `HomeServlet` | Handles `/home` requests, redirects to the login home page |
| `LoginServlet` | User login authentication, role judgment, and Session creation |
| `LogoutServlet` | User logout, clears Session data |
| `RegisterServlet` | New TA account registration and automatic login |
| `TaDashboardServlet` | Loads statistical data for the TA Dashboard |
| `JobListServlet` | Displays the list of available job positions |
| `ApplyServlet` | Handles TA job application submission |
| `ApplicationStatusServlet` | Displays TA's application history and status |
| `MODashboardServlet` | Displays statistical information for the MO Dashboard |
| `MOJobServlet` | Handles job position creation and publishing by MOs |
| `MOApplicationServlet` | Handles application viewing and review by MOs |
| `AdminDashboardServlet` | Displays system workload statistics for admins |

### Service Layer (Business Logic)
| Service | Responsibility |
|---------|----------------|
| `UserService` | User authentication, registration, and information query |
| `JobService` | Job position query, creation, and matching degree calculation |
| `ApplicationService` | Application submission, query, and status update |
| `DashboardService` | Aggregates data for Dashboard display |
| `AdminService` | System workload statistics calculation |

### Storage Layer (Data Persistence)
| File | Description |
|------|-------------|
| `FileStorageUtil.java` | CSV file loading and saving, runtime directory resolution |

### Model Layer (Data Models)
| Class | Description |
|-------|-------------|
| `User` | User entity |
| `UserRole` | User role enum: TA, MO, ADMIN |
| `Job` | Job position entity |
| `JobStatus` | Job status enum |
| `Application` | Application entity |
| `ApplicationStatus` | Application status enum |

---

## 📊 Data File Description
### `ta_users.csv` (User Data)
```csv
Username, Password, Full Name, Role, Description
seele,123456,Seele,TA,TA Applicant Account
mo1,123456,Module Organiser,MO,Module Organiser Account
admin,123456,Administrator,ADMIN,System Administrator Account
```

### `jobs.csv` (Job Position Data)
```csv
Job ID, Title, Course Code, Lecturer, Working Hours, Skills Required, Status
job001,Python TA,CS101,Dr. Smith,50,Python/Teaching,OPEN
```

### `applications.csv` (Application Records)
```csv
Application ID, Applicant, Job ID, Status, Submission Time, Remarks
app001,seele,job001,PENDING,2024-03-20 10:30:00,
```

---

## 🛑 Troubleshooting Common Issues
### Q1: `mvn clean package` Fails
**Possible Causes**:
- ❌ JDK version is not 17, switch to JDK 17 and retry
- ❌ Cannot access Maven Central Repository, check your network connection
- ❌ Project file encoding issue, check `<project.build.sourceEncoding>` in `pom.xml`

**Solutions**:
```powershell
# Check Java version
java -version

# Clear Maven local cache and retry
mvn clean package -U
```

### Q2: Browser Returns 404 After Deployment
**Possible Causes**:
- ❌ Tomcat is not running
- ❌ WAR file is not extracted by Tomcat (you should see a `ta-webapp` folder under `webapps/` normally)
- ❌ Incorrect URL path

**Solutions**:
```powershell
# Ensure Tomcat is running
cd D:\apache-tomcat-10.1.52\bin
.\startup.bat

# Access the correct URL
http://localhost:8080/ta-webapp/

# Check Tomcat logs (if there are errors)
D:\apache-tomcat-10.1.52\logs\catalina.out
```

### Q3: PR Shows Conflicts After Git Commit
**Cause**: Your branch is behind the master branch, and two developers have modified the same file.

**Solution**: Follow the steps in the "Resolving Merge Conflicts" section above.

---

## 📖 Related Documentation
- [📋 Project Plan](sslocal://flow/file_open?url=docs%2Fproject-plan.md&flow_extra=eyJsaW5rX3R5cGUiOiJjb2RlX2ludGVycHJldGVyIn0=)
- [📝 Requirements Analysis](sslocal://flow/file_open?url=docs%2Frequirements.md&flow_extra=eyJsaW5rX3R5cGUiOiJjb2RlX2ludGVycHJldGVyIn0=)
- [🏗️ Architecture Description](sslocal://flow/file_open?url=docs%2Farchitecture.md&flow_extra=eyJsaW5rX3R5cGUiOiJjb2RlX2ludGVycHJldGVyIn0=)

---

## 🎓 Tech Stack
| Component | Version |
|-----------|---------|
| **Java** | 17 |
| **Servlet/JSP** | Jakarta 10 (Tomcat 10) |
| **Maven** | 3.9+ |
| **JSTL** | 2.0+ |
| **CSS3** | Modern |
| **JavaScript** | ES6+ |

---

**Project Status**: ✅ Core features completed, under continuous iteration...

---
### Notes for Git Submission
This file uses standard UTF-8 encoding and GitHub-flavored Markdown syntax, which will not cause garbled code when submitted to Git. All CSV headers remain consistent with your actual project code to avoid parsing errors.