import axios from 'axios';

import {getSession,clearSession} from './SessionUtil';
let axiosWrap = axios;

// 创建axios实例
axiosWrap = axios.create({
  timeout: 60000, // 请求超时时间
});
axiosWrap.defaults.headers.post['Content-Type'] = 'application/json';

axiosWrap.defaults.withCredentials = true;

// request拦截器
axiosWrap.interceptors.request.use( 
  
  config => {
  let token = getSession();
  if(token) config.headers['Authorization'] = token
  return config
},
error => {
  console.log(error)
  return Promise.reject(error)
  }
);

axiosWrap.interceptors.response.use(
  response => {
    return response;
  },
  (error) => {
    if (error.response) {
      const { status } = error.response;
      if (status === 401) {
        // 清除可能过期的 Token
        clearSession();
        console.log("清除session");
        // 跳转到登录页面
        window.location.href = '/login';
      }
    }
    // 返回 Promise 拒绝错误
    return Promise.reject(error);
  }
);


export default axiosWrap;
