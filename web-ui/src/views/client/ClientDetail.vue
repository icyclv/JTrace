<template>
  <div>
    <el-container>
      <el-header style="padding: 0px">
        <Header :client-host="clientInfo.host+'('+clientInfo.ip+')'" :client-name="clientInfo.clientName+'('+clientInfo.clientId+')'"
                :currentPath="currentPath"/>
      </el-header>
      <el-container>
        <el-main style="background: #f8f8f9;padding: 10px;margin-top: 3px">
          <el-tabs v-model="tabName" class="tabs" style="height:calc(100vh - 100px)" tab-position="left">
            <el-tab-pane label="采样管理" name="sampling">
              <SamplingPanel :client-id="clientInfo.clientId"></SamplingPanel>
            </el-tab-pane>
            <el-tab-pane label="JVM信息" name="jvm">
              <JvmPanel :client-id="clientInfo.clientId"></JvmPanel>
            </el-tab-pane>
            <el-tab-pane label="JVM参数" name="vmoption">
              <VmOptionPanel :client-id="clientInfo.clientId"></VmOptionPanel>
            </el-tab-pane>
            <el-tab-pane label="环境变量" name="sysenv">
              <SysEnvPanel :client-id="clientInfo.clientId"></SysEnvPanel>
            </el-tab-pane>
            <el-tab-pane label="系统属性" name="sysprop">
              <SysPropPanel :client-id="clientInfo.clientId"></SysPropPanel>
            </el-tab-pane>
            <el-tab-pane label="内存分析" name="memory">
              <MemoryPanel :client-id="clientInfo.clientId"/>
            </el-tab-pane>
            <el-tab-pane label="线程分析" name="thread">
              <ThreadPanel :client-id="clientInfo.clientId"/>
            </el-tab-pane>
            <el-tab-pane label="类加载器分析" name="classloader">
              <ClassLoaderPanel :client-id="clientInfo.clientId"></ClassLoaderPanel>
            </el-tab-pane>
            <el-tab-pane label="类分析" name="class">
              <el-row>
                <el-col style="text-align: left">
                  <el-input v-model="searchClassName" clearable placeholder="请输入类名"
                            style="width: 20%;margin-right: 10px"></el-input>
                  <el-button type="primary" @click="searchClass">搜索</el-button>
                </el-col>
              </el-row>
              <el-table :data="classList" :height="dynamicHeight" border stripe style="margin-top: 10px">
                <el-table-column label="类名" min-width="25%" prop="className"></el-table-column>
                <el-table-column label="类加载器" min-width="20%" prop="classloader"></el-table-column>
                <el-table-column label="类加载器Hash" min-width="10%" prop="classLoaderHash"></el-table-column>
                <el-table-column label="类加载来源" min-width="25%" prop="codeSource"></el-table-column>
                <el-table-column fixed="right" label="操作" min-width="15%">
                  <template slot-scope="scope">
                    <el-button size="small" type="text" @click="showClassSource(scope.row)">源码</el-button>
                    <el-button size="small" type="text" @click="showClassMethods(scope.row)">方法列表</el-button>
                    <el-button size="small" type="text" @click="showLoggerLevel(scope.row)">日志级别</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="方法分析" name="method">
              <el-row>
                <el-col style="text-align: left">
                  <el-input v-model="searchClassName" clearable placeholder="请输入类名"
                            style="width: 20%;margin-right: 10px"></el-input>
                  <el-input v-model="searchMethodName" clearable placeholder="请输入方法名"
                            style="width: 20%;margin-right: 10px"></el-input>
                  <!-- <el-input v-model="searchClassLoaderHash" placeholder="请输入类加载器Hash" clearable
                            style="width: 20%;margin-right: 30px"></el-input> -->
                  <el-button style="margin-right: 10px" type="primary"
                             @click="searchClassMethod">搜索
                  </el-button>
                  <el-button type="info" @click="showEnhanceDialog">
                    打开方法监测页面
                  </el-button>
                </el-col>
              </el-row>
              <el-table :data="methodList" :height="dynamicHeight" border stripe style="margin-top: 10px">
                <el-table-column label="方法名" min-width="15%" prop="methodName"></el-table-column>
                <el-table-column label="参数类型" min-width="20%" prop="parameters">
                  <template slot-scope="scope">
                    <span>{{ scope.row.parameters.join() }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="类名" min-width="20%" prop="className"></el-table-column>
                <el-table-column label="类加载器" min-width="15%" prop="classloader"></el-table-column>
                <el-table-column label="类加载器Hash" min-width="10%" prop="classLoaderHash"></el-table-column>
                <el-table-column label="操作" min-width="20%">
                  <template slot-scope="scope">
                    <el-button size="small" type="text" @click="showMethodSource(scope.row)">源码</el-button>
                    <el-button size="small" type="text" @click="watch(scope.row)">watch</el-button>
                    <el-button size="small" type="text" @click="trace(scope.row)">trace</el-button>
                    <el-button size="small" type="text" @click="stack(scope.row)">stack</el-button>
                    <el-button size="small" type="text" @click="call(scope.row)">调用</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="Spring相关" name="spring">
              <SpringPanel :client-id="clientInfo.clientId"></SpringPanel>
            </el-tab-pane>
          </el-tabs>
        </el-main>
      </el-container>
    </el-container>
    <el-dialog :visible.sync="dialogSourceCodeVisible" title="Source code" width="80%">
      <el-row v-for="(value, key) in sourceDialogInfo" :key="key" style="text-align: left">
        <el-col :span="24">
          <span>{{ key }}: {{ value }}</span>
        </el-col>
      </el-row>
      <MonacoEditor
          :key="randomKey"
          :code="code"
          :editorOptions="options"
          :height="dynamicHeight"
          language="java" style="text-align: left">
      </MonacoEditor>
    </el-dialog>
    <el-dialog :visible.sync="dialogLoggerLevelVisible">
      <LoggerDetail :client-id="clientInfo.clientId" :logger-info="loggerInfo" @closeLoggerDialog="closeLoggerDialog"/>
    </el-dialog>
    <el-dialog :visible.sync="dialogEnhanceVisible" width="90%">
      <Enhance :client-ids="clientIds" :method-info="methodInfo"/>
    </el-dialog>
    <el-dialog :fullscreen="true" :visible.sync="dialogCallVisible">
      <MethodCall :client-id="clientInfo.clientId" :method-info="methodInfo"/>
    </el-dialog>
  </div>
</template>
<script>
import Header from "@/components/common/Header";
import Vue from "vue";
import JvmPanel from "@/components/jvm/JvmPanel";
import SpringPanel from "@/components/spring/SpringPanel";
import ThreadPanel from "@/components/jvm/ThreadPanel";
import MemoryPanel from "@/components/jvm/MemoryPanel";
import VmOptionPanel from "@/components/jvm/VmOptionPanel";
import ClassLoaderPanel from "@/components/jvm/ClassLoaderPanel";
import SysEnvPanel from "@/components/jvm/SysEnvPanel";
import SysPropPanel from "@/components/jvm/SysPropPanel";
import MonacoEditor from 'vue-monaco-editor';
import GroupLabelValue from "@/components/common/GroupLabelValue";
import LoggerDetail from "@/components/logger/LoggerDetail";
import Enhance from "@/components/enhance/Enhance";
import MethodCall from "@/components/method/MethodCall";
import SamplingPanel from "@/components/sampling/SamplingPanel";

export default {
  name: 'ClientDetail',

  components: {
    Header,
    MonacoEditor,
    JvmPanel,
    GroupLabelValue,
    LoggerDetail,
    Enhance,
    ThreadPanel,
    MemoryPanel,
    VmOptionPanel,
    ClassLoaderPanel,
    SysEnvPanel,
    SysPropPanel,
    SpringPanel,
    MethodCall,
    SamplingPanel
  },

  data() {
    return {
      currentPath: "detail",
      clientInfo: {},
      tabName: "sampling",
      searchClassName: "",
      searchMethodName: "",
      searchClassLoaderHash: "",
      classList: [],
      methodList: [],
      randomKey: 123456,
      dialogSourceCodeVisible: false,
      dialogLoggerLevelVisible: false,
      dialogEnhanceVisible: false,
      dialogCallVisible: false,
      code: "",
      options: {
        selectOnLineNumbers: true,
        roundedSelection: false,
        readOnly: false,
        cursorStyle: 'line',
        automaticLayout: false,
        glyphMargin: true
      },
      loggerInfo: {},
      methodInfo: {},
      sourceDialogInfo: {}
    };
  },

  created() {
    let query = this.$route.query;
    if (query && query.clientId) {
      this.clientInfo = query;
    } else {
      // 先去选择客户端
      this.$router.push({name: 'ClientList'});
    }
  },

  methods: {
    changeClient() {
      this.$router.push({name: 'ClientList'});
    },
    searchClass() {
      Vue.axios.post('/api/class/list?clientId=' + this.clientInfo.clientId
          , {"className": this.searchClassName}).then((response) => {
        if (response.data.success) {
          this.classList = response.data.data.classInfos;
        } else {
          this.$message.error(response.data.errorMsg);
        }
      });
    },
    searchClassMethod() {
      Vue.axios.post('/api/class/method?clientId=' + this.clientInfo.clientId
          , {
            "className": this.searchClassName,
            "methodName": this.searchMethodName,
          }).then((response) => {
        if (response.data.success) {
          this.methodList = response.data.data.methodInfos;
        } else {
          this.$message.error(response.data.errorMsg);
        }
      });
    },
    showClassSource(classInfo) {
      Vue.axios.post('/api/class/jad?clientId=' + this.clientInfo.clientId
          , {
            "className": classInfo.className
            , "classLoaderHash": classInfo.classLoaderHash
          }).then((response) => {
        if (response.data.success) {
          this.randomKey = Math.floor(Math.random() * 100000);
          this.code = response.data.data.sourceInfo.source;
          this.sourceDialogInfo = {
            "className": classInfo.className,
            "classLoaderHash": classInfo.classLoaderHash
          }
          this.dialogSourceCodeVisible = true;
        } else {
          this.$message.error(response.data.errorMsg);
        }
      });
    },
    showMethodSource(methodInfo) {
      Vue.axios.post('/api/class/jad?clientId=' + this.clientInfo.clientId
          , {
            "className": methodInfo.className,
            "methodName": methodInfo.methodName
            , "classLoaderHash": methodInfo.classLoaderHash
          }).then((response) => {
        if (response.data.success) {
          this.randomKey = Math.floor(Math.random() * 100000);
          this.code = response.data.data.sourceInfo.source;
          this.sourceDialogInfo = {
            "className": methodInfo.className,
            "methodName": methodInfo.methodName,
            "classLoaderHash": methodInfo.classLoaderHash
          }
          this.dialogSourceCodeVisible = true;
        } else {
          this.$message.error(response.data.errorMsg);
        }
      });
    },
    showClassMethods(classInfo) {
      this.searchClassName = classInfo.className;
      this.searchClassLoaderHash = classInfo.classLoaderHash;
      this.searchClassMethod();
      this.tabName = "method";
    },
    showLoggerLevel(classInfo) {
      Vue.axios.post('/api/logger/info?clientId=' + this.clientInfo.clientId
          , {
            "name": classInfo.className, "classLoaderHash": classInfo.classLoaderHash,
            "includeNoAppender": true
          }).then((response) => {
        if (response.data.success) {
          console.log(response.data.data);
          if (response.data.data && response.data.data.loggerInfos.length > 0) {
            this.loggerInfo = response.data.data.loggerInfos[0];
            this.loggerInfo.loggerName = classInfo.className;
            this.dialogLoggerLevelVisible = true;
          }
        } else {
          this.$message.error(response.data.errorMsg);
        }
      });
    },
    closeLoggerDialog() {
      this.dialogLoggerLevelVisible = false;
    },
    stack(methodInfo) {
      this.methodInfo = this.simpleMethodInfo("stack", methodInfo);
      this.dialogEnhanceVisible = true;
    },
    watch(methodInfo) {
      this.methodInfo = this.simpleMethodInfo("watch", methodInfo);
      this.dialogEnhanceVisible = true;
    },
    trace(methodInfo) {
      this.methodInfo = this.simpleMethodInfo("trace", methodInfo);
      this.dialogEnhanceVisible = true;
    },
    simpleMethodInfo(enhanceType, methodInfo) {
      return {
        "enhanceType": enhanceType
        , "className": methodInfo.className
        , "methodName": methodInfo.methodName
        , "classLoaderHash": methodInfo.classLoaderHash
        , "parameters": this.generateParameters(methodInfo.parameters)
      }
    },
    showEnhanceDialog() {
      if (!this.methodInfo.enhanceType) {
        this.methodInfo = this.simpleMethodInfo("stack", {});
      }
      this.dialogEnhanceVisible = true;
    },
    generateParameters(parameters) {
      let arr = [];
      for (let idx in parameters) {
        if (parameters[idx].trim() == 'int' || parameters[idx].trim() == 'java.lang.Integer'
            || parameters[idx].trim() == 'long' || parameters[idx].trim() == 'java.lang.Long'
            || parameters[idx].trim() == 'byte' || parameters[idx].trim() == 'java.lang.Byte'
            || parameters[idx].trim() == 'short' || parameters[idx].trim() == 'java.lang.Short'
            || parameters[idx].trim() == 'char' || parameters[idx].trim() == 'java.lang.Char'
            || parameters[idx].trim() == 'float' || parameters[idx].trim() == 'java.lang.Float'
            || parameters[idx].trim() == 'double' || parameters[idx].trim() == 'java.lang.Double') {
          arr.push(0);
        } else if (parameters[idx].trim() == 'java.util.List') {
          arr.push("new java.util.ArrayList()");
        } else if (parameters[idx].trim() == 'java.util.Map') {
          arr.push("new java.util.HashMap()");
        } else {
          arr.push("new " + parameters[idx] + "()");
        }
      }
      return arr.join(",");
    },
    call(methodInfo) {
      this.methodInfo = this.simpleMethodInfo("call", methodInfo);
      this.dialogCallVisible = true;
    }
  },
  computed: {
    dynamicHeight: function () {
      return (window.innerHeight - 270);
    },
    clientIds: function () {
      return [this.clientInfo.clientId];
    }
  }
};
</script>
<style>
.tabs {
  margin-top: 10px;
  padding: 10px;
  background-color: white;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1)
}
</style>