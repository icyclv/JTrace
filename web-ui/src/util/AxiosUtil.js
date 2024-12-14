import axios from 'axios';
import {Message} from 'element-ui';

import {getSession} from './SessionUtil';
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




export default axiosWrap;
