<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  width: 100%;
}

.el-container {
  height: 100%;
}

.el-header {
  background-color: #2c3e50;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.el-aside {
  background-color: #304156;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.menu-item {
  color: #bfcbd9;
}

.el-menu {
  border-right: none;
}

.el-submenu__title {
  color: #bfcbd9;
}

.el-menu-item.is-active {
  color: #409eff;
  background-color: #263445;
}

.card-box {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 20px;
}

.chart-box {
  height: 300px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
