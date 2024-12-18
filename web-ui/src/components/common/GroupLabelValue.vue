<template>
  <div v-if="groupObj!=null && Object.keys(groupObj).length!=0">
    <el-table :data="processedTableData" border style="width: 100%" stripe>
    <el-table-column prop="field" label="字段名" width="150"></el-table-column>
    <el-table-column prop="value" label="值"></el-table-column>
  </el-table>
    
  </div>
</template>
<script>
export default {
  name: 'GroupLabelValue',
  props: {
    groupObj: {
      type: Object,
      default: null,
    },
    tableData: [],
    groupName: {
      type: String,
      default: null,
    },
  },
  components: {},
  data() {
    return {}
  },
  computed: {
    processedTableData() {
      // 转换对象为数组
      return Object.entries(this.groupObj).map(([key, value]) => ({
        field: key,
        value: value,
      }));
    },
  },
  methods: {}
}
</script>
<style>

.group-content {
  font-size: 12px;
  word-break: break-all;
  line-height: 14px;
}
.bg-purple-light {
    background: #e5e9f2;
  }
.group-label {
  font-weight: bold;
  text-align: right;
  padding-right: 10px;
}
.grid-content {
    border-radius: 4px;
    min-height: 36px;
  }
  .bg-purple {
    background: #d3dce6;
  }
.group-value {
  text-align: left;
  white-space: pre-wrap;
}
</style>