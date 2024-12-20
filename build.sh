#!/bin/bash

# Exit on any error
set -e

# 运行 Maven clean package
echo "Running Maven clean package..."
mvn clean package
BUILD_RESULT=$?
echo "Maven build completed with exit code=${BUILD_RESULT}"

if [ $BUILD_RESULT -ne 0 ]; then
    echo "Maven build failed!"
    exit 1
fi

# 创建 .jtrace 目录结构
echo "Creating .jtrace directory structure..."
JTRACE_DIR="$HOME/.jtrace/jars"
mkdir -p "$JTRACE_DIR"

# 复制并重命名文件
echo "Copying files to .jtrace/jars..."

# 复制 boot jar
cp -f "boot/target/jtrace-boot-jar-with-dependencies.jar" "$JTRACE_DIR/jtrace-boot.jar"

# 复制 agent jar
cp -f "agent/target/jtrace-agent-jar-with-dependencies.jar" "$JTRACE_DIR/jtrace-agent.jar"

# 复制 client jar
cp -f "client/target/jtrace-client-shade.jar" "$JTRACE_DIR/jtrace-client.jar"

# 复制 server jar
cp -f "server/target/jtrace-server.jar" "$JTRACE_DIR/jtrace-server.jar"

# 复制 spy jar
cp -f "spy/target/jtrace-spy.jar" "$JTRACE_DIR/jtrace-spy.jar"

echo "Build and deployment completed successfully."