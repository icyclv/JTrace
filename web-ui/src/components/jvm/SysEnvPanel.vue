<template>
  <div>
    <el-row>
      <el-col style="text-align: left;">
        <el-button type="primary" @click="showSysEnvs()">刷新数据</el-button>
      </el-col>
    </el-row>
    <GroupLabelValue :group-obj="sysEnvs" :style="{height:dynamicHeight}" group-name="环境变量"
                     style="height: 480px;overflow-y: scroll;margin-top: 10px;"></GroupLabelValue>
  </div>
</template>
<script>
import Vue from "vue";
import GroupLabelValue from "@/components/common/GroupLabelValue";

export default {
  name: 'SysEnvPanel',

  components: {
    GroupLabelValue,
  },

  props: {
    clientId: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      sysEnvs: {},
    }
  },
  created() {
    this.showSysEnvs();
  },
  methods: {
    showSysEnvs() {
      Vue.axios.post('/api/jvm/sysenv?clientId=' + this.clientId
          , {}).then((response) => {
        if (response.data.success) {
          this.sysEnvs = response.data.data.info;
        } else {
          this.$message.error(response.data.errorMsg);
        }
      });
    },
  },
  computed: {
    dynamicHeight: function () {
      return (window.innerHeight - 270) + "px";
    }
  }
}
</script>
<style>
</style>