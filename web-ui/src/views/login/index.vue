<template>
  <span style="margin-left: 20%;width: 40%;">
<div class="background">
    <img alt="" height="100%" src="/img/background.jpg" width="100%"/>
</div>
    <div class="front">
    <el-form ref="loginForm" :model="loginForm" auto-complete="on" class="login-form" label-position='left'
             style="margin-top: 5%;border: 1px solid #ccc; border-radius: 16px;">
        <div class="title-container">
        <h3 class="title">Welcome to the system</h3>
       </div>
      <el-form-item class="bar" prop="username">

          <el-input v-model="loginForm.username" auto-complete="on" placeholder="Username"/>
      </el-form-item>
      <el-form-item class="bar" prop="password">

          <el-input v-model="loginForm.password" placeholder="Password" show-password></el-input>
      </el-form-item>
      <el-form-item prop="code">
  </el-form-item>
      <div class="bar">
        <el-button :loading="loading" style="width:100%;margin-bottom:30px;" type="primary"
                   @click.native.prevent="handleLogin">Login</el-button>
      </div>
       <div>
        <el-button style="width:100%;margin-bottom:30px;" type="info"
                   @click.native.prevent="register">Register</el-button>
       </div>

       </el-form>
  </div>
  </span>
</template>

<script>
import Vue from 'vue';
import {getSession, setSession} from '../../util/SessionUtil';

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
      },
      loading: false,
      passwordType: 'password',
      redirect: undefined
    }
  },
  created() {
  },
  methods: {
    handleLogin() {
      console.log(Vue.axios)
      // this.loading = true
      Vue.axios.post('/api/user/login', {
        username: this.loginForm.username,
        password: this.loginForm.password
      }).then((response) => {
        this.loading = false
        console.log(response)
        let result = response.data
        console.log("result:", result)
        if (result.success) {
          console.log("result.data:", result.data)
          setSession(result.data)
          var a = getSession()
          console.log("sessionId:" + a)
          this.$router.push('/')
        } else {
          console.log("response.errorMsg:" + result.errorMsg)
          this.$message({
            message: result.errorMsg,
            type: 'warning'
          });
        }
      });
    },
    register() {
      this.$message({
        message: 'This feature is not available yet',
        type: 'warning'
      });
    }
  }
}
</script>

<style>
.bar {
  margin-top: 30px;

}

.login-form {
  position: relative;
  width: 520px;
  padding: 35px 35px 0;
  margin: 0 auto;
}

.title-container {
  position: relative;


  font-size: 26px;
  color: #045268;
  margin: 0px auto 40px auto;
  text-align: center;
  font-weight: bold;

}

.background {
  width: 100%;
  height: 100%; /**宽高100%是为了图片铺满屏幕 */
  z-index: -1;
  position: absolute;
}

.front {
  z-index: 1;
}
</style>
