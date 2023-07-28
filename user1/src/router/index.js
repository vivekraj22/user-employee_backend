import { createRouter, createWebHistory } from "vue-router";
import Userform from '../components/Userform.vue';
import TableVue from '../components/Table.vue';
import Searchbar from '../components/searchbar.vue';

const routes = [
    {
      path: '/',
      redirect: '/createUser',
    },
    {
      path: '/createUser',
      name: 'createUser',
      component: Userform,
      props: (route) => ({
        propname: 'get',
        propJson: JSON.parse('{}'),
      }),
    },
    {
        path: '/editUser',
        name: 'editUser',
        component: Userform,
        props: (route) => ({
          propname: 'put',
          propJson: JSON.parse(route.query.propJson || '{}'),
        }),
      },
           
    {
      path: '/getAllUsers',
      name: 'getAllUsers',
      component: TableVue,
      props: {
        propname1: 'getall',
      },
    },
    {
      path: '/getBy',
      name: 'getBy',
      component: Searchbar,
      props: {
        propname: 'name',
      },
    },
  ];
  
  const router = createRouter({
    history: createWebHistory(),
    routes,
  });

  export default router;
