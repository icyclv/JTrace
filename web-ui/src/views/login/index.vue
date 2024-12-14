<template>
  <span style="margin-left: 20%;width: 40%;">
<div class="background">
    <img src="/img/background.jpg"  width="100%" height="100%" alt="" />
</div>
    <div class="front">
    <el-form ref="loginForm" :model="loginForm"  class="login-form" auto-complete="on"   label-position='left' style="margin-top: 5%;border: 1px solid #ccc; border-radius: 16px;" >
        <div class="title-container">
        <h3 class="title">Welcome to the system</h3>
       </div >
      <el-form-item prop="username"  class="bar">

          <el-input    placeholder="Username"  v-model="loginForm.username"     auto-complete="on"/>
      </el-form-item>
      <el-form-item  prop="password"  class="bar"  >

          <el-input  placeholder="Password"  v-model="loginForm.password" show-password ></el-input>
      </el-form-item>
      <el-form-item prop="code">
  </el-form-item>
      <div class="bar">
        <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="handleLogin">Login</el-button>
      </div>
       <div>
        <el-button  type="info" style="width:100%;margin-bottom:30px;" @click.native.prevent="register">Register</el-button>
       </div>

       </el-form>
  </div>
  </span>
</template>

<script>
import Vue from 'vue';
import {setSession,getSession} from '../../util/SessionUtil';

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
            let result   = response.data
            console.log("result:",result)
            if(result.success){
              console.log("result.data:",result.data)
              setSession(result.data)
              var a = getSession()
              console.log("sessionId:"+a)
              this.$router.push(  '/' )
            }else{
              console.log("response.errorMsg:"+result.errorMsg)
              this.$message({
                message: result.errorMsg,
                type: 'warning'
              });
            }
          });
    },
    register(){
      this.$message({
          message: 'This feature is not available yet',
          type: 'warning'
        });
  }
  }
}
</script>

<style >
  .bar{
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
  .background{
    width:100%;
    height:100%;  /**宽高100%是为了图片铺满屏幕 */
    z-index:-1;
    position: absolute;
  }
  .front{
    z-index:1;
  }
</style>
