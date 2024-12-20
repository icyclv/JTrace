import Vue from 'vue';
import VueRouter from 'vue-router';

import ClientDetail from '../views/client/ClientDetail.vue';
import ClientList from '../views/client/ClientList.vue';
import Login from '../views/login/index.vue';

Vue.use(VueRouter);

export const routes = [
    {
        path: '/',
        component: ClientList,
        name: 'ClientList',
    },
    {
        path: '/client/detail',
        component: ClientDetail,
        name: 'ClientDetail',
    },
    {
        path: '/login',
        component: Login,
        name: 'Login',
    },
];

const router = new VueRouter({
    mode: 'hash',
    base: process.env.BASE_URL,
    routes,
});

export default router;
