<template>
  <div>
    <el-container>
      <el-header style="padding: 0px">
        <Header :currentPath="currentPath"/>
      </el-header>
      <el-container>
        <el-main style="background: #f8f8f9;margin-top: 3px">
          <el-row>
            <el-col :span="4" style="text-align: left">
              <el-button icon="el-icon-search" round type="primary" @click="initClientInfos">刷新数据</el-button>
            </el-col>
            <el-col :span="20" style="text-align: right">
              <el-autocomplete v-model="clientSearchKey" :fetch-suggestions="querySearchKey"
                               class="inline-input" placeholder="请输入搜索关键词(客户端名称、ID、服务器名)"
                               style="width: 400px">
                <el-button slot="append" icon="el-icon-search" @click="refreshPageClientInfos"></el-button>
              </el-autocomplete>
            </el-col>
          </el-row>
          <el-table :data="pageClientInfos" :height="dynamicHeight" stripe
                    style="width: 100%;margin-top: 20px; margin-bottom: 20px">
            <el-table-column label="客户端名称" min-width="20%" prop="clientName"></el-table-column>
            <el-table-column label="客户端ID" min-width="25%" prop="clientId"></el-table-column>
            <el-table-column label="主机名称" min-width="15%" prop="host"></el-table-column>
            <el-table-column label="IP" min-width="15%" prop="ip"></el-table-column>
            <el-table-column label="版本" min-width="10%" prop="version"></el-table-column>
            <el-table-column fixed="right" label="操作" min-width="15%">
              <template slot-scope="scope">
                <el-button size="small" type="text" @click="showClientDetail(scope.row)">查看详情</el-button>
                <el-button size="small" type="text" @click="stopAgent(scope.row)">关闭Agent</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination :current-page.sync="currentPage"
                         :page-size.sync="pageSize"
                         :page-sizes="[10, 20, 30, 40]"
                         :total="total"
                         layout="total, sizes, prev, pager, next, jumper"
                         @size-change="handleSizeChange"
                         @current-change="handleCurrentChange">
          </el-pagination>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>
<script>
import Vue from 'vue';
import Header from "@/components/common/Header";

export default {
  name: 'ClientList',

  components: {Header},

  data() {
    return {
      currentPath: "list",
      clientSearchKey: "",
      suggestClientNames: [],
      clientInfos: [],
      filteredClientInfos: [],
      pageClientInfos: [],
      currentPage: 1,
      pageSize: 10,
      total: 0
    };
  },

  created() {
    this.initClientInfos();
  },

  methods: {
    showClientDetail(clientInfo) {
      this.$router.push({name: 'ClientDetail', query: clientInfo});
    },
    stopAgent(clientInfo) {
      Vue.axios.post('/api/client/shutdown?clientId=' + clientInfo.clientId, {}).then((response) => {
        if (response.data.success) {
          this.$message.success(response.data.data.message);
        } else {
          this.$message.error(response.data.msg);
        }
      });
    },
    handleSizeChange() {
      this.refreshPageClientInfos();
    },
    handleCurrentChange() {
      this.refreshPageClientInfos();
    },
    querySearchKey(queryString, cb) {
      let results = queryString ? this.suggestClientNames
          .filter(item => item.value.toLowerCase().indexOf(queryString.toLowerCase()) != -1) : this.suggestClientNames;
      cb(results);
    },
    refreshPageClientInfos() {
      let filteredClientInfos = this.filterClientInfos();
      this.filteredClientInfos = filteredClientInfos;
      this.total = filteredClientInfos.length;

      let start = (this.currentPage - 1) * this.pageSize;
      let end = this.currentPage * this.pageSize;
      let arr = [];
      for (let i = start; i < end && i < this.total; i++) {
        arr.push(filteredClientInfos[i])
      }

      this.pageClientInfos = arr;
    },
    filterClientInfos() {
      let filteredClientInfos = [];
      if (this.clientSearchKey) {
        let sk = this.clientSearchKey.toLowerCase();
        for (let index in this.clientInfos) {
          if (this.clientInfos[index].clientName.toLowerCase().indexOf(sk) != -1
              || this.clientInfos[index].clientId.toLowerCase().indexOf(sk) != -1
              || this.clientInfos[index].host.toLowerCase().indexOf(sk) != -1) {
            filteredClientInfos.push(this.clientInfos[index])
          }
        }
      } else {
        filteredClientInfos = this.clientInfos;
      }
      return filteredClientInfos;
    },
    initClientInfos() {
      this.clientInfos = [];
      this.filteredClientInfos = [];
      Vue.axios.get('/api/client/list', {}).then((response) => {
        if (response.data.success) {
          this.clientInfos = response.data.data;
          let arr = [];
          let exists = {};
          for (let index in this.clientInfos) {
            let clientInfo = this.clientInfos[index];
            if (!exists[clientInfo.clientName]) {
              arr.push({value: clientInfo.clientName});
              exists[clientInfo.clientName] = true;
            }
          }
          this.suggestClientNames = arr;
          this.refreshPageClientInfos();
        } else {
          this.$message.error(response.data.errorMsg);
        }
      });
    }
  },
  computed: {
    dynamicHeight: function () {
      return (window.innerHeight - 210);
    }
  }
};
</script>
<style>
</style>