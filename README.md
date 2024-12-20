<div style="text-align: center;">
  <img src="static/logo.png" width="20%"  />
</div>

# JTrace： A simple  Java agent for monitoring JVM

## Introduction
**JTrace** is a Java performance analysis tool inspired by Arthas [1]. This project adopts a client-server (C-S) architecture based on [2] and includes additional features such as I/O, memory, and CPU sampling tools. The collected data can be exported directly to InfluxDB for visualization with tools like Grafana. For enhanced usability, the project also provides a web-based interface.

**Warning: This is solely a learning project and currently lacks thorough testing. Please do not deploy it in a production environment.**

## Features
- **Sampling Profilers**: Supports CPU, memory, stack trace, and I/O sampling.
- **JVM Information**: Displays comprehensive JVM details, including memory, threads, classes, and garbage collection information.
- **Thread Information**: Offers insights into thread stacks, states, and locks.
- **Instrumentation-based Commands**: Includes watch, trace, and stack commands for monitoring method invocations, execution times, and arguments.
- **Decompile**: Enables decompilation of classes and methods.
- **Logger Tools**: Displays logger details and allows dynamic updates to logger levels.
- **Spring Tools**: Provides information on Spring beans and supports updates to Spring bean configurations.

## Quick Start
### 1. Build
Windows:
```cmd
./build.cmd
```

```shell
bash ./build.sh
```
The build script will generate jar files at ```$HOME/.jtrace/lib```

### 2. Start Server
```shell
cd ~/.jtrace/lib
java -jar jtrace-server.jar
```
It will start a web server at port 8088 and a netty server at port 4090.

You can configure parameters such as InfluxDB in the ```server/src/main/resources/application.yml``` file.

### 3. Start Client
```shell
cd ~/.jtrace/lib
java -jar jtrace-boot.jar
```
This command lists all Java processes running on the host. Select the **PID** of the target process to attach.


### 4. Check with Web
Open your browser and go to http://localhost:8088, you will see the JTrace web page.

## Note
- Due to the introduction of modules in JDK 9, some features (such as the logger) require access permissions in higher versions of the JDK. Please add the following JVM option when starting the target JVM:
```
--add-opens java.base/java.lang=ALL-UNNAMED
```
- Due to time constraints, there are still many areas in this project that need improvement, including but not limited to the lack of user access restrictions on the web end and the absence of any login handling on the backend.

## Structure
|Module|Description|
|---|---|
|jtrace-agent|Attaches the agent to the target JVM and starts the client.|
|jtrace-boot|Provides the bootstrap mechanism for jtrace-agent.|
|jtrace-agent|Implements the Netty client.|
|jtrace-command|Contains the command module and utility functions.|
|jtrace-server|Serves as the web server (for users, port 8080) and Netty server (for agents, port 4090).|
|jtrace-spy|Define the Spy class functionality.|
|web-ui|Provides the Web UI for JTrace (port 8080). If packaged with the server, the port changes to 8088.|

## Reference
[1] [Arthas](https://github.com/alibaba/arthas) 

[2] [JVMEye](https://github.com/gy4j/JvmEye)

[3] [JVM-Profiler](https://github.com/uber-common/jvm-profiler)

Special thanks to these projects for providing inspiration, code references, and integration opportunities.





