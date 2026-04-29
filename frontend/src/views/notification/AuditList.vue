<template>
  <div class="audit-notification-container">
    <h2 class="page-title">审核通知</h2>
    <NotificationList :notification-type="2" />
  </div>
</template>

<script>
import NotificationList from './NotificationList.vue'

export default {
  name: 'AuditList',
  components: {
    NotificationList
  }
}
</script>

<style scoped>
.audit-notification-container {
  padding: 0;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #303133;
}
</style>
