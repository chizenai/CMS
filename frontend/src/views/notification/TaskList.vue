<template>
  <div class="task-notification-container">
    <h2 class="page-title">任务提醒</h2>
    <NotificationList :notification-type="3" />
  </div>
</template>

<script>
import NotificationList from './NotificationList.vue'

export default {
  name: 'TaskList',
  components: {
    NotificationList
  }
}
</script>

<style scoped>
.task-notification-container {
  padding: 0;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #303133;
}
</style>
