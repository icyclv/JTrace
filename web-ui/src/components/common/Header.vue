<template>
  <div class="header-container">
    <div class="sub-header">
      <span class="home-icon">
        <img src="/img/logo.png">
      </span>

      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/' }">客户端列表</el-breadcrumb-item>
        <el-breadcrumb-item v-if="currentPath=='detail'">客户端详情</el-breadcrumb-item>
      </el-breadcrumb>

      <div class="client-info" style="position: absolute; left: 50%; transform: translateX(-50%); ">
        <el-row v-if="clientName.length>0">
          <el-col :span="12">
            <el-tag type="primary" :title="clientName" style="width: 200px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">{{ "客户端：" + clientName }}</el-tag>
          </el-col>
        </el-row>
        <el-row v-if="clientHost.length>0">
          <el-col :span="12">
            <el-tag type="primary" style="width: 200px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">{{ "地址：" + clientHost }}</el-tag>
          </el-col>
        </el-row>
      </div>
     
      <!-- 右侧头像 -->
      <div class="avatar-wrapper">
        <img src="/img/user.png" class="user-avatar">
      </div>
    </div>
  </div>
</template>
<script>

export default {
  name: 'Layout',
  props: {
    currentPath: {
      type: String,
      default: "list"
    },
    clientName: {
      type: String,
      default: ""
    },
    clientHost: {
      type: String,
      default: ""

    }
  },

  data() {
    return {};
  },

  created() {

  },

  methods: {

  }
};
</script>
<style>
.header-container {
  width: 100%;
  height: 64px;
  top: 0;
  background: #fff;
  z-index: 999;
}

.header-container .sub-header {
  color: #493e54;
  border-bottom: 0;
  position: relative;
  margin: 0 0 10px;
  line-height: 1;
  font-size: 16px;
  min-height: 62px;
  box-shadow: 0 2px 10px 0 rgb(0 0 0 / 5%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 20px;
}

.header-container .sub-header > span:first-child {
  height: 60px;
  line-height: 60px;
  padding: 15px;
  font-size: 22px;
  font-weight: 700;
  font-family: monospace;
  letter-spacing: -2px;
  cursor: pointer;
  float: left;
  margin-right: 55px;
}

.header-container .sub-header > span:first-child img {
  float: left;
  height: 100%;
}

.header-container .version-box {
  font-size: 12px;
  color: #aaa;
  font-weight: 200;
  position: absolute;
  line-height: 12px;
  margin-top: 14px;
  margin-left: 2px;
}

/* .header-container .sub-header .menu-container {
  float: right;
  height: 60px;
  line-height: 60px;
  font-size: 14px;
  border: none;
} */

.avatar-color-0 {
  background: #387aec;
}

.header-container .avatar-circle {
  display: -ms-flexbox;
  display: flex;
  -ms-flex-pack: center;
  justify-content: center;
  -ms-flex-align: center;
  align-items: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  font-size: 14px;
  color: #fff;
  cursor: pointer;
  margin-top: 14px;
}

.el-menu--horizontal > .el-menu-item.is-active {
  font-weight: 700;
}

.el-header {
  padding: 0px;
}

.el-breadcrumb {
  height: 40px;
  display: flex;
  align-items: center;
  font-size: 18px; /* 增加整体字体大小 */
}
/* 修改菜单样式 */
.header-container .sub-header .left-menu {
  float: left; /* 左侧菜单靠左 */
  height: 60px;
  line-height: 60px;
  font-size: 14px;
  border: none;
}

.header-container .sub-header .right-menu {
  float: right; /* 右侧菜单靠右 */
  height: 60px;
  line-height: 60px;
  font-size: 14px;
  border: none;
}

.sub-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 20px;
}

.home-icon {
  height: 40px;
  display: flex;
  align-items: center;
}

.home-icon img {
  height: 40px;
  width: auto;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  margin-left: auto;
  cursor: pointer;
}

.user-avatar {
  height: 40px;
  width: 40px;
  border-radius: 50%;
  margin-right: 8px;
}

.user-name {
  font-size: 14px;
  color: #333;
}
</style>