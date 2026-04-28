import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/views/Layout'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard'),
        meta: { title: '数据统计' }
      },
      {
        path: 'article',
        name: 'Article',
        component: () => import('@/views/article/ArticleList'),
        meta: { title: '文章管理' }
      },
      {
        path: 'article/add',
        name: 'ArticleAdd',
        component: () => import('@/views/article/ArticleEdit'),
        meta: { title: '新增文章' }
      },
      {
        path: 'article/edit/:id',
        name: 'ArticleEdit',
        component: () => import('@/views/article/ArticleEdit'),
        meta: { title: '编辑文章' }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/category/CategoryList'),
        meta: { title: '栏目管理' }
      },
      {
        path: 'material',
        name: 'Material',
        component: () => import('@/views/material/MaterialList'),
        meta: { title: '素材管理' }
      },
      {
        path: 'site',
        name: 'Site',
        component: () => import('@/views/site/SiteList'),
        meta: { title: '站点管理' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/system/UserList'),
        meta: { title: '用户管理' }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/system/RoleList'),
        meta: { title: '角色管理' }
      },
      {
        path: 'log',
        name: 'Log',
        component: () => import('@/views/system/LogList'),
        meta: { title: '日志管理' }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login') {
    if (token) {
      next('/dashboard')
    } else {
      next()
    }
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
