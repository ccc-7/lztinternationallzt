# EBU6304 Group Project - TA Recruitment System

## Development Environment Setup Guide

This project is implemented as a lightweight Java Servlet/JSP Web application.
The recommended local development environment is based on VS Code, JDK 17, Apache Maven, Apache Tomcat 10.1, and Git for Windows. This setup is sufficient for developing, running, and testing the project locally. VS Code’s Java Web guidance explicitly treats JDK, Maven, and Java extensions as the key prerequisites for this workflow.

Please note that this project uses Tomcat 10.1. Because Tomcat 10.1 is on the Jakarta EE 10 line, all Servlet-related imports in the project should use the `jakarta.servlet` namespace rather than the older `javax.servlet` namespace.

## Installation Order

To avoid configuration problems, install the required tools in the following order:

1. JDK 17
2. VS Code
3. VS Code Java extensions
4. Apache Maven
5. Apache Tomcat 10.1
6. Git for Windows

This order matters because the Java extensions, Maven, and Tomcat all rely on Java being installed first. VS Code’s official Java Web workflow also assumes that Java and Maven are already available before building the web application.

## Step 1: Install JDK 17

Download JDK 17 for Windows x64. Java 17 is a long-term support release and is suitable for this project environment.

A recommended installation path is: C:\Java\jdk-17

After installation, open Command Prompt and run the Java version check commands to confirm that both the Java runtime and compiler are using version 17. If both commands show version 17.x, then the installation is successful.

If you previously installed JDK 21, make sure your environment variables are updated so that the default Java points to JDK 17 instead of JDK 21. In practice, this means setting `JAVA_HOME` to the JDK 17 directory and ensuring that the `Path` variable points to the JDK 17 `bin` directory before any JDK 21 paths.

## Step 2: Install VS Code

Download the Windows User Installer x64 version of VS Code. The User Installer is the recommended option on Windows because it is simple to install and does not require administrator privileges in most cases.

Install VS Code with the default settings. After installation, open it once to make sure it starts correctly.

## Step 3: Install Java Extensions in VS Code

Open VS Code and go to the Extensions view.

First, install “Extension Pack for Java”. This extension pack provides the basic Java development experience, including code completion, debugging, testing, and Java project support. VS Code officially recommends this extension pack for Java development.

Second, install “Community Server Connectors”.
Do not install the old “Tomcat for Java” extension, because it has been deprecated. The recommended replacement is Community Server Connectors.

Optionally, you may also install “Maven for Java” and “Language Support for Java(TM) by Red Hat”, although most core Java capabilities are already included through the Extension Pack for Java.

## Step 4: Install Apache Maven

Download the current binary zip package of Apache Maven. Maven is strongly recommended because VS Code’s Java Web workflow uses it as the standard build and dependency management tool, and Maven 3.9.x works with JDK 17 without issue.

A recommended extraction path is: D:\tools\apache-maven-3.9.13

After extracting Maven, add its `bin` directory to the system `Path` environment variable.
The directory you should add is: D:\tools\apache-maven-3.9.13\bin

Then open a new Command Prompt window and check the Maven version. If the command shows Maven version information together with Java 17 runtime information, Maven is installed correctly.

## Step 5: Install Apache Tomcat 10.1

Download the Apache Tomcat 10.1 Core zip package. Tomcat 10.1 is the recommended server for this project because it supports the Jakarta EE 10 servlet and JSP standards used by modern Servlet/JSP web applications.

A recommended extraction path is: D:\tools\apache-tomcat-10.1

After extracting Tomcat, go into its `bin` folder and run `startup.bat`. Then open your browser and visit `http://localhost:8080`. If the Tomcat welcome page appears, the server is running correctly. Tomcat’s official documentation supports this standard local setup workflow.

Again, because Tomcat 10.1 uses Jakarta EE, the project must use `jakarta.servlet.*` and `jakarta.servlet.http.*` imports rather than the older Java EE `javax.*` imports.

## Step 6: Install Git for Windows

Download and install Git for Windows x64.
Install it with the default settings unless the team has a specific reason to use a different configuration.

After installation, open Command Prompt and check the Git version. If a version number is displayed, Git is installed correctly.

## Step 7: Configure VS Code to Use JDK 17

After installing the Java extensions, VS Code usually detects installed JDK versions automatically. If it does not, open the Command Palette in VS Code and use the Java runtime configuration command to manually point VS Code to your JDK 17 installation directory. VS Code’s Java tooling supports configuring the runtime explicitly in this way.

The recommended JDK path is: C:\Java\jdk-17

This step is important, especially if your system previously used JDK 21 by default.

## Step 8: Set Up the Project Workspace

Do not create project files directly on the desktop.
Instead, create a dedicated workspace folder for the project.

A recommended path is: D:\workspace\ta-recruitment-system

Open this folder in VS Code using the normal “Open Folder” workflow. This makes project management, Maven usage, and Git version control much cleaner.

## Final Expected Local Environment

After all setup steps are complete, each team member’s machine should contain the following tools:

VS Code
JDK 17
Extension Pack for Java
Community Server Connectors
Apache Maven
Apache Tomcat 10.1
Git for Windows

This environment is sufficient for building and running a lightweight Java Servlet/JSP Web application for the group project. VS Code’s official Java Web documentation and Tomcat’s current version guidance support this toolchain.

## Verification Checklist

Each team member should verify the following before starting development:

First, Java is correctly installed and the default version is 17.
Second, Maven is correctly installed and uses Java 17.
Third, Git is correctly installed.
Fourth, Tomcat can be started locally and the Tomcat welcome page opens at `http://localhost:8080`.
Fifth, VS Code can detect JDK 17 and the Java extensions are active.

If all of these checks pass, the environment setup is complete.

## Important Notes for This Project

This project uses a lightweight Java Servlet/JSP architecture rather than Spring Boot or a front-end framework. The purpose is to keep the system simple, aligned with the coursework requirements, and easy for all team members to understand and maintain.

Because the project uses Tomcat 10.1, all team members must consistently use the Jakarta namespace in servlet code. Mixing `javax.servlet` and `jakarta.servlet` across different team members will cause unnecessary errors and confusion. Tomcat 10.1’s official compatibility guidance makes this distinction explicit.

The team should also ensure that everyone uses the same major versions of JDK, Maven, and Tomcat to avoid environment mismatch problems.

## Next Step

Once the environment is ready, the next step is to create the first Maven Web project in VS Code and run a minimal working example that includes an `index.jsp` page and a basic `HelloServlet`. This will confirm that the complete JDK, Maven, Tomcat, and VS Code workflow is functioning correctly. VS Code’s Java Web guidance is built around exactly this kind of project bootstrap flow.

