<template>
  <div>
    <el-row>
      <el-col style="text-align: left;">
        <el-button round type="primary" @click="showJvmInfo()">刷新数据</el-button>
        <el-button round type="info" @click="dumpHeap()">导出堆内存</el-button>
      </el-col>
    </el-row>
    <el-row style="margin-top:10px">
      <el-col>
        <el-tabs v-model="activeName">
          <el-tab-pane label="运行信息" name="runtimeInfo">
            <GroupLabelValue :group-obj="jvmInfo.runtimeInfo" :style="{height:dynamicHeight}"
                             group-name="运行信息"
                             style="overflow-y: scroll;"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label=" 编译器信息
            " name="compilationInfo">
            <GroupLabelValue :group-obj="jvmInfo.compilationInfo" :style="{height:dynamicHeight}"
                             group-name="编译器信息"
                             style="overflow-y: scroll;"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label="内存管理信息" name="memoryManagerInfo">
            <GroupLabelValue :group-obj="jvmInfo.memoryManagerInfo" :style="{height:dynamicHeight}"
                             group-name="内存管理信息"
                             style="overflow-y: scroll;"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label="内存信息" name="memoryInfo">
            <GroupLabelValue :group-obj="jvmInfo.memoryInfo" :style="{height:dynamicHeight}"
                             group-name="内存信息"
                             style="overflow-y: scroll;"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label="垃圾回收信息" name="garbageCollectorInfo">
            <GroupLabelValue :group-obj="jvmInfo.garbageCollectorInfo" :style="{height:dynamicHeight}"
                             group-name="垃圾回收信息"
                             style="overflow-y: scroll;"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label="线程信息" name="threadInfoVO">
            <GroupLabelValue :group-obj="jvmInfo.threadInfoVO" :style="{height:dynamicHeight}"
                             group-name="线程信息"
                             style="overflow-y: scroll;"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label="类加载信息" name="classLoadingInfo">
            <GroupLabelValue :group-obj="jvmInfo.classLoadingInfo" :style="{height:dynamicHeight}"
                             group-name="类加载信息"
                             style="overflow-y: scroll;"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label="操作系统信息" name="operatingSystemInfo">
            <GroupLabelValue :group-obj="jvmInfo.operatingSystemInfo" :style="{height:dynamicHeight}"
                             group-name="操作系统信息"
                             style="overflow-y: scroll;"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label="文件描述信息" name="fileDescriptorInfo">
            <GroupLabelValue :group-obj="jvmInfo.fileDescriptorInfo" :style="{height:dynamicHeight}"
                             group-name="文件描述信息"
                             style="overflow-y: scroll;"></GroupLabelValue>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import Vue from "vue";
import {MessageBox} from "element-ui";
import GroupLabelValue from "@/components/common/GroupLabelValue";

export default {
  name: 'JvmPanel',
  components: {GroupLabelValue},
  props: {
    clientId: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      jvmInfo: {},
      activeName: "runtimeInfo"
    }
  },
  created() {
    this.showJvmInfo();
  },
  methods: {
    showJvmInfo() {
      Vue.axios.post('/api/jvm/info?clientId=' + this.clientId
          , {}).then((response) => {
        if (response.data.success) {
          this.jvmInfo = response.data.data.jvmInfo;
          console.log(this.jvmInfo)
        } else {
          this.$message.error(response.data.errorMsg);
        }
      });
    },
    dumpHeap() {
      Vue.axios.post('/api/jvm/heapdump?clientId=' + this.clientId
          , {}).then((response) => {
        if (response.data.success) {
          MessageBox.confirm(response.data.msg + " filePath:" + response.data.dumpFile);
        } else {
          this.$message.error(response.data.message);
        }
      });
    },
  },
  computed: {
    dynamicHeight: function () {
      return (window.innerHeight - 150) + "px";
    }
  }
}
</script>
<style>
</style>