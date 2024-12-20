import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';

/* 引入element-ui */
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import 'normalize.css/normalize.css';
import {getSession} from './util/SessionUtil';
import axiosWrap from './util/AxiosUtil';
import VueAxios from 'vue-axios';

Vue.config.productionTip = false;

Vue.use(VueAxios, axiosWrap);
Vue.use(ElementUI, {
  size: 'small',
});

const whiteList = [];
router.beforeEach((to, from, next) => {
  if (to.path === '/login') {
    next();
    return;
  }
  if (getSession()) {
    next();
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next();
    } else {
      next('/#/login');
    }
  }
});

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount('#app');
