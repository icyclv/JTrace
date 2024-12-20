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
if not exist "%USERPROFILE%\.jtrace\lib" (
    mkdir "%USERPROFILE%\.jtrace\lib"
)

:: 复制并重命名文件
echo Copying files to .jtrace/lib...

:: 复制 boot jar
copy /Y "boot\target\jtrace-boot-jar-with-dependencies.jar" "%USERPROFILE%\.jtrace\lib\jtrace-boot.jar"

:: 复制 agent jar
copy /Y "agent\target\jtrace-agent-jar-with-dependencies.jar" "%USERPROFILE%\.jtrace\lib\jtrace-agent.jar"

:: 复制 client jar
copy /Y "client\target\jtrace-client-shade.jar" "%USERPROFILE%\.jtrace\lib\jtrace-client.jar"

:: 复制 server jar
copy /Y "server\target\jtrace-server.jar" "%USERPROFILE%\.jtrace\lib\jtrace-server.jar"

:: 复制 spy jar
copy /Y "spy\target\jtrace-spy.jar" "%USERPROFILE%\.jtrace\lib\jtrace-spy.jar"

echo Build and deployment completed successfully.