<template>
  <el-container>
    <el-aside width="200px">
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
      >
        <el-menu-item index="/dashboard">
          <i class="el-icon-data-analysis"></i>
          <span slot="title">数据统计</span>
        </el-menu-item>
        <el-submenu index="1">
          <template slot="title">
            <i class="el-icon-document"></i>
            <span>内容管理</span>
          </template>
          <el-menu-item index="/article">
            <i class="el-icon-edit"></i>
            <span>文章管理</span>
          </el-menu-item>
          <el-menu-item index="/category">
            <i class="el-icon-menu"></i>
            <span>栏目管理</span>
          </el-menu-item>
        </el-submenu>
        <el-menu-item index="/material">
          <i class="el-icon-picture"></i>
          <span slot="title">素材管理</span>
        </el-menu-item>
        <el-menu-item index="/site">
          <i class="el-icon-office-building"></i>
          <span slot="title">站点管理</span>
        </el-menu-item>
        <el-submenu index="2">
          <template slot="title">
            <i class="el-icon-zoom-in"></i>
            <span>审核管理</span>
          </template>
          <el-menu-item index="/audit/queue">
            <i class="el-icon-time"></i>
            <span>待审核文章</span>
          </el-menu-item>
          <el-menu-item index="/audit/history">
            <i class="el-icon-notebook-1"></i>
            <span>审核记录</span>
          </el-menu-item>
        </el-submenu>
        <el-menu-item index="/notification">
          <i class="el-icon-message"></i>
          <span slot="title">
            通知中心
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="item" is-dot>
            </el-badge>
          </span>
        </el-menu-item>
        <el-submenu index="3">
          <template slot="title">
            <i class="el-icon-setting"></i>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/user">
            <i class="el-icon-user"></i>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/role">
            <i class="el-icon-s-custom"></i>
            <span>角色管理</span>
          </el-menu-item>
          <el-menu-item index="/menu">
            <i class="el-icon-menu"></i>
            <span>菜单管理</span>
          </el-menu-item>
          <el-menu-item index="/permission">
            <i class="el-icon-key"></i>
            <span>权限管理</span>
          </el-menu-item>
          <el-menu-item index="/log">
            <i class="el-icon-notebook-2"></i>
            <span>日志管理</span>
          </el-menu-item>
        </el-submenu>
        <el-menu-item index="/profile">
          <i class="el-icon-user"></i>
          <span slot="title">个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="logo">企业CMS管理系统</div>
        <div class="header-right">
          <span>{{ userInfo.nickname || userInfo.username }}</span>
          <el-button type="text" @click="handleProfile">个人中心</el-button>
          <el-button type="text" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import { getUnreadCount } from '@/api/notification'

export default {
  name: 'Layout',
  data() {
    return {
      unreadCount: 0,
      timer: null
    }
  },
  computed: {
    ...mapState(['userInfo']),
    activeMenu() {
      return this.$route.path
    }
  },
  created() {
    this.loadUnreadCount()
    // 定时刷新未读消息数量
    this.timer = setInterval(() => {
      this.loadUnreadCount()
    }, 60000)
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    ...mapActions(['logout']),
    async loadUnreadCount() {
      try {
        const res = await getUnreadCount()
        this.unreadCount = res.data.count
      } catch (error) {
        console.error('获取未读消息数量失败:', error)
      }
    },
    handleProfile() {
      this.$router.push('/profile')
    },
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.logout()
        this.$router.push('/login')
        this.$message.success('已退出登录')
      }).catch(() => {})
    }
  }
}
</script>
