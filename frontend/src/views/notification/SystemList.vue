<template>
  <div class="system-notification-container">
    <h2 class="page-title">系统通知</h2>
    <NotificationList :notification-type="1" />
  </div>
</template>

<script>
import NotificationList from './NotificationList.vue'

export default {
  name: 'SystemList',
  components: {
    NotificationList
  }
}
</script>

<style scoped>
.system-notification-container {
  padding: 0;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #303133;
}
</style>
