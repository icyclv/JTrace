<template>
  <div>
    <el-row>
      <el-col style="text-align: left;">
        <el-button type="primary" @click="showMemoryInfos()">刷新数据</el-button>
      </el-col>
    </el-row>
    <el-table :data="memoryInfos" :height="dynamicHeight" border stripe style="margin-top:10px;">
      <el-table-column label="类型" prop="type" width="150"></el-table-column>
      <el-table-column label="名称" prop="name" width="200"></el-table-column>
      <el-table-column label="已使用" prop="used">
        <template slot-scope="scope">
          <span>{{ computeValue(scope.row.used) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总大小" prop="total">
        <template slot-scope="scope">
          <span>{{ computeValue(scope.row.total) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最大" prop="max">
        <template slot-scope="scope">
          <span>{{ computeValue(scope.row.max) }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import Vue from "vue";

export default {
  name: 'MemoryPanel',
  props: {
    clientId: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      memoryInfos: [],
    }
  },
  created() {
    this.showMemoryInfos();
  },
  methods: {
    showMemoryInfos() {
      Vue.axios.post('/api/jvm/memory?clientId=' + this.clientId
          , {}).then((response) => {
        if (response.data.success) {
          this.memoryInfos = response.data.data.memoryInfos;
        } else {
          this.$message.error(response.data.errorMsg);
        }
      });
    },
    computeValue(value) {
      if (value <= 0) {
        return "0M";
      }
      return (value / 1024 / 1024).toFixed(2) + "M";
    }
  },
  computed: {
    dynamicHeight: function () {
      return (window.innerHeight - 270);
    }
  }
}
</script>
<style>
</style>