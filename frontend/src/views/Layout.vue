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
        <el-submenu index="2">
          <template slot="title">
            <i class="el-icon-check"></i>
            <span>审核管理</span>
          </template>
          <el-menu-item index="/audit/pending">
            <i class="el-icon-time"></i>
            <span>待审核列表</span>
          </el-menu-item>
          <el-menu-item index="/audit/record">
            <i class="el-icon-document-checked"></i>
            <span>审核记录</span>
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
        <el-submenu index="3">
          <template slot="title">
            <i class="el-icon-bell"></i>
            <span>通知中心</span>
          </template>
          <el-menu-item index="/notification/system">
            <i class="el-icon-message"></i>
            <span>系统通知</span>
          </el-menu-item>
          <el-menu-item index="/notification/audit">
            <i class="el-icon-check-circle"></i>
            <span>审核通知</span>
          </el-menu-item>
          <el-menu-item index="/notification/task">
            <i class="el-icon-warning-outline"></i>
            <span>任务提醒</span>
          </el-menu-item>
        </el-submenu>
        <el-submenu index="4">
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
          <i class="el-icon-user-solid"></i>
          <span slot="title">个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="logo">企业CMS管理系统</div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="header-user">
              <i class="el-icon-user"></i>
              {{ userInfo.nickname || userInfo.username }}
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">
                <i class="el-icon-user"></i> 个人中心
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <i class="el-icon-switch-button"></i> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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

export default {
  name: 'Layout',
  computed: {
    ...mapState(['userInfo']),
    activeMenu() {
      return this.$route.path
    }
  },
  methods: {
    ...mapActions(['logout']),
    handleCommand(command) {
      if (command === 'logout') {
        this.handleLogout()
      } else if (command === 'profile') {
        this.$router.push('/profile')
      }
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

<style scoped>
.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.logo {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
}

.header-user {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
}

.header-user:hover {
  color: #409eff;
}
</style>
