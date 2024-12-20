@echo off
setlocal enabledelayedexpansion

:: 运行 Maven clean package
echo Running Maven clean package...
call mvn clean package
echo Maven build completed with errorlevel=%errorlevel%

if %errorlevel% neq 0 (
    echo Maven build failed!
    exit /b 1
)
:: 创建 .jtrace 目录结构
echo Creating .jtrace directory structure...
if not exist "%USERPROFILE%\.jtrace\jars" (
    mkdir "%USERPROFILE%\.jtrace\jars"
)

:: 复制并重命名文件
echo Copying files to .jtrace/jars...

:: 复制 boot jar
copy /Y "boot\target\jtrace-boot-jar-with-dependencies.jar" "%USERPROFILE%\.jtrace\jars\jtrace-boot.jar"

:: 复制 agent jar
copy /Y "agent\target\jtrace-agent-jar-with-dependencies.jar" "%USERPROFILE%\.jtrace\jars\jtrace-agent.jar"

:: 复制 client jar
copy /Y "client\target\jtrace-client-shade.jar" "%USERPROFILE%\.jtrace\jars\jtrace-client.jar"

:: 复制 server jar
copy /Y "server\target\jtrace-server.jar" "%USERPROFILE%\.jtrace\jars\jtrace-server.jar"

:: 复制 spy jar
copy /Y "spy\target\jtrace-spy.jar" "%USERPROFILE%\.jtrace\jars\jtrace-spy.jar"

echo Build and deployment completed successfully.