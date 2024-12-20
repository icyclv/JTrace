<template>
  <div>
    <el-row>
      <el-col style="text-align: left;">
        <el-button type="primary" @click="showVmOptions()">刷新数据</el-button>
      </el-col>
    </el-row>
    <el-table :data="vmOptions" :height="dynamicHeight" border stripe style="margin-top:10px;">
      <el-table-column label="类型" prop="name" width="350"></el-table-column>
      <el-table-column label="值" prop="value" width="350"></el-table-column>
      <el-table-column label="是否可改" prop="writeable">
        <template slot-scope="scope">
          {{ scope.row.writeable ? '是' : '否' }}
        </template>
      </el-table-column>
      <el-table-column label="源类型" prop="origin"></el-table-column>
    </el-table>
  </div>
</template>
<script>
import Vue from "vue";

export default {
  name: 'VmOptionPanel',
  props: {
    clientId: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      vmOptions: [],
    }
  },
  created() {
    this.showVmOptions();
  },
  methods: {
    showVmOptions() {
      Vue.axios.post('/api/jvm/vmoption?clientId=' + this.clientId
          , {}).then((response) => {
        if (response.data.success) {
          this.vmOptions = response.data.data.vmOptions;
        } else {
          this.$message.error(response.data.errorMsg);
        }
      });
    },
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