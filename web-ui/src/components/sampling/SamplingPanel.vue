<template>
  <div>
    <el-row>
      <el-col style="text-align: left;">
        <el-button type="primary" @click="showProfilerInfo()">刷新数据</el-button>
        <el-button type="primary" @click="createProfilerInfo()">新建采样</el-button>

      </el-col>
      <el-col style="text-align: right;">
      </el-col>

    </el-row>
    <el-table :data="profilerInfos" :height="dynamicHeight" border stripe style="margin-top:10px;">
      <el-table-column label="profiler名称" prop="profilerName" width="350"></el-table-column>
      <el-table-column label="采样/报告间隔" prop="intervalMillis" width="350"></el-table-column>
      <el-table-column label="操作" width="350">
        <template slot-scope="scope">
          <el-button size="small" type="text" @click="editProfilerInfo(scope.row)">修改间隔</el-button>
          <el-button size="small" type="text" @click="disableProfiler(scope.row)">关闭</el-button>
        </template>

      </el-table-column>

    </el-table>
    <el-dialog :title="profiler.profilerName.length > 0 ? '修改采样间隔' : '新建采样'" :visible.sync="editDialogVisible"
               width="50%">
      <el-form ref="form" :model="profiler" label-width="80px">
        <el-form-item label="采样类型">
          <el-select v-model="profiler.profilerName" :disabled="editing" placeholder="请选择采样类型">
            <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>


        <el-form-item label="采样间隔">
          <el-input v-model="profiler.samplingInterval" :disabled="profiler.profilerName != 'Stacktrace'"
                    min="100" placeholder="采样间隔(ms,需大于100)" type="number"></el-input>
        </el-form-item>


        <el-form-item label="报告间隔">
          <el-input v-model="profiler.reportInterval" min="100" placeholder="报告间隔(ms,需大于100)"
                    type="number"></el-input>
        </el-form-item>


        <el-button type="primary" @click="saveProfilerInfo()">保存</el-button>

      </el-form>
    </el-dialog>
  </div>
</template>
<script>
import Vue from "vue";

export default {
  name: 'SamplingPanel',
  props: {
    clientId: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      profilerInfos: [],
      options: [{
        value: 'Stacktrace',
        label: 'Stacktrace'
      }, {
        value: 'IO',
        label: 'IO'
      }, {
        value: 'CpuAndMemory',
        label: 'CpuAndMemory'
      }, {
        value: 'ThreadInfo',
        label: 'ThreadInfo'
      }],
      profiler: {
        profilerName: "",
        samplingInterval: 0,
        reportInterval: 0,
      },
      editDialogVisible: false,
      editing: false,
    }
  },
  created() {
    this.showProfilerInfo();
  },
  methods: {
    showProfilerInfo() {
      Vue.axios.post('/api/sampling/info?clientId=' + this.clientId
          , {}).then((response) => {
        if (response.data.success) {
          this.profilerInfos = response.data.data.profilerInfos;
        } else {
          this.$message.error(response.data.errorMsg);
        }
      });
    },

    disableProfiler(row) {
      Vue.axios.post('/api/sampling/disable?clientId=' + this.clientId,
          {"profilerName": row.profilerName}).then((response) => {
        if (response.data.success) {
          this.showProfilerInfo();
        } else {
          this.$message.error(response.data.errorMsg);
        }
      });
    },
    createProfilerInfo() {
      this.editing = false;
      this.profiler.profilerName = "";
      this.profiler.samplingInterval = 100;
      this.profiler.reportInterval = 100;
      this.editDialogVisible = true;
    },
    editProfilerInfo(row) {
      this.editing = true;
      this.profiler.profilerName = row.profilerName;
      this.profiler.reportInterval = row.intervalMillis;
      if (row.profilerName.includes("Stacktrace")) {
        this.profiler.profilerName = "Stacktrace";
        for (let profilerInfo of this.profilerInfos) {
          if (profilerInfo.profilerName.includes("StacktraceReporter")) {
            this.profiler.reportInterval = profilerInfo.intervalMillis;
          } else if (profilerInfo.profilerName.includes("StacktraceCollector")) {
            this.profiler.samplingInterval = profilerInfo.intervalMillis;
          }
        }
      }
      this.editDialogVisible = true;
    },
    saveProfilerInfo() {
      if (this.profiler.samplingInterval < 100 || this.profiler.reportInterval < 100) {
        this.$message.error("采样间隔和报告间隔需大于100ms");
        return;
      }
      if (this.profiler.profilerName == "") {
        this.$message.error("请选择采样类型");
        return;
      }
      Vue.axios.post('/api/sampling/enable?clientId=' + this.clientId,
          {
            "profilerName": this.profiler.profilerName,
            "samplingInterval": this.profiler.samplingInterval,
            "reportInterval": this.profiler.reportInterval
          }).then((response) => {
        if (response.data.success) {
          this.showProfilerInfo();
          this.profiler.profilerName = "";
          this.profiler.samplingInterval = 100;
          this.profiler.reportInterval = 100;
          this.$message.success("保存成功");
          this.editing = false;
          this.editDialogVisible = false;
        } else {
          this.$message.error(response.data.errorMsg);
        }
      });
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