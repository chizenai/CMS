<template>
  <div class="notification-list-container">
    <div class="card-box">
      <div class="header-row">
        <h3>通知中心</h3>
        <el-button type="primary" @click="handleMarkAllRead" v-if="unreadCount > 0">
          全部标记已读
        </el-button>
      </div>

      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部通知" name="all"></el-tab-pane>
        <el-tab-pane label="系统通知" name="system">
          <span slot="label">
            系统通知
            <el-badge :value="getUnreadByType(1)" :hidden="getUnreadByType(1) === 0" class="item">
            </el-badge>
          </span>
        </el-tab-pane>
        <el-tab-pane label="审核通知" name="audit">
          <span slot="label">
            审核通知
            <el-badge :value="getUnreadByType(2)" :hidden="getUnreadByType(2) === 0" class="item">
            </el-badge>
          </span>
        </el-tab-pane>
        <el-tab-pane label="任务提醒" name="task">
          <span slot="label">
            任务提醒
            <el-badge :value="getUnreadByType(3)" :hidden="getUnreadByType(3) === 0" class="item">
            </el-badge>
          </span>
        </el-tab-pane>
      </el-tabs>

      <div class="notification-list" v-loading="loading">
        <div 
          v-for="notification in notificationList" 
          :key="notification.id"
          class="notification-item"
          :class="{ unread: notification.isRead === 0 }"
          @click="handleView(notification)"
        >
          <div class="notification-icon">
            <i :class="getIconByType(notification.notificationType)"></i>
          </div>
          <div class="notification-content">
            <div class="notification-title">
              <span v-if="notification.isRead === 0" class="unread-dot"></span>
              {{ notification.title }}
              <el-tag :type="getPriorityType(notification.priority)" size="mini" v-if="notification.priority > 1">
                {{ getPriorityText(notification.priority) }}
              </el-tag>
            </div>
            <div class="notification-detail">{{ notification.content }}</div>
            <div class="notification-meta">
              <span>{{ notification.senderName || '系统' }}</span>
              <span>{{ notification.createTime }}</span>
            </div>
          </div>
        </div>
        <el-empty v-if="notificationList.length === 0" description="暂无通知"></el-empty>
      </div>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.pageNum"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        style="margin-top: 20px; text-align: right;"
      ></el-pagination>
    </div>

    <el-dialog title="通知详情" :visible.sync="viewDialogVisible" width="500px">
      <el-descriptions :column="1" border v-if="currentNotification">
        <el-descriptions-item label="标题">
          <el-tag :type="getPriorityType(currentNotification.priority)" size="mini" v-if="currentNotification.priority > 1">
            {{ getPriorityText(currentNotification.priority) }}
          </el-tag>
          {{ currentNotification.title }}
        </el-descriptions-item>
        <el-descriptions-item label="类型">
          {{ getTypeText(currentNotification.notificationType) }}
        </el-descriptions-item>
        <el-descriptions-item label="发送者">
          {{ currentNotification.senderName || '系统' }}
        </el-descriptions-item>
        <el-descriptions-item label="发送时间">
          {{ currentNotification.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="内容">
          {{ currentNotification.content }}
        </el-descriptions-item>
      </el-descriptions>
      <span slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getNotificationList, getUnreadCount, markAsRead, markAllAsRead } from '@/api/notification'
import { EventBus } from '@/utils/eventBus'

export default {
  name: 'NotificationList',
  data() {
    return {
      loading: false,
      activeTab: 'all',
      notificationList: [],
      currentNotification: null,
      viewDialogVisible: false,
      unreadCount: 0,
      unreadByType: {
        1: 0,
        2: 0,
        3: 0
      },
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  created() {
    this.loadData()
    this.loadUnreadCount()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        let notificationType = null
        if (this.activeTab === 'system') {
          notificationType = 1
        } else if (this.activeTab === 'audit') {
          notificationType = 2
        } else if (this.activeTab === 'task') {
          notificationType = 3
        }

        const res = await getNotificationList({
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize,
          notificationType
        })
        this.notificationList = res.data.records
        this.pagination.total = res.data.total
      } catch (error) {
        console.error('加载数据失败:', error)
        this.$message.error('加载数据失败')
      } finally {
        this.loading = false
      }
    },
    async loadUnreadCount() {
      try {
        const res = await getUnreadCount()
        this.unreadCount = res.data.count
      } catch (error) {
        console.error('获取未读数量失败:', error)
      }
    },
    handleTabClick(tab) {
      this.activeTab = tab.name
      this.pagination.pageNum = 1
      this.loadData()
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.pagination.pageNum = val
      this.loadData()
    },
    async handleView(notification) {
      this.currentNotification = notification
      this.viewDialogVisible = true
      
      if (notification.isRead === 0) {
        try {
          await markAsRead(notification.id)
          notification.isRead = 1
          this.loadUnreadCount()
          EventBus.$emit('notification-read')
        } catch (error) {
          console.error('标记已读失败:', error)
        }
      }
    },
    async handleMarkAllRead() {
      try {
        await markAllAsRead()
        this.$message.success('已全部标记为已读')
        this.loadData()
        this.loadUnreadCount()
        EventBus.$emit('notification-read')
      } catch (error) {
        console.error('标记已读失败:', error)
        this.$message.error('标记已读失败')
      }
    },
    getIconByType(type) {
      switch (type) {
        case 1:
          return 'el-icon-bell'
        case 2:
          return 'el-icon-zoom-in'
        case 3:
          return 'el-icon-time'
        default:
          return 'el-icon-message'
      }
    },
    getTypeText(type) {
      switch (type) {
        case 1:
          return '系统通知'
        case 2:
          return '审核通知'
        case 3:
          return '任务提醒'
        default:
          return '其他'
      }
    },
    getPriorityType(priority) {
      switch (priority) {
        case 2:
          return 'warning'
        case 3:
          return 'danger'
        default:
          return 'info'
      }
    },
    getPriorityText(priority) {
      switch (priority) {
        case 2:
          return '重要'
        case 3:
          return '紧急'
        default:
          return '普通'
      }
    },
    getUnreadByType(type) {
      // 简化处理，实际应该从后端获取按类型统计的未读数量
      return 0
    }
  }
}
</script>

<style scoped>
.notification-list-container {
  padding: 0;
}
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.notification-list {
  margin-top: 20px;
}
.notification-item {
  display: flex;
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background-color 0.2s;
}
.notification-item:hover {
  background-color: #f5f7fa;
}
.notification-item.unread {
  background-color: #fafafa;
}
.notification-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  flex-shrink: 0;
}
.notification-icon i {
  color: #fff;
  font-size: 18px;
}
.notification-content {
  flex: 1;
  min-width: 0;
}
.notification-title {
  font-weight: 500;
  margin-bottom: 5px;
  display: flex;
  align-items: center;
}
.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #f56c6c;
  margin-right: 8px;
  flex-shrink: 0;
}
.notification-detail {
  color: #606266;
  font-size: 14px;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.notification-meta {
  color: #909399;
  font-size: 12px;
  display: flex;
  gap: 15px;
}
</style>
