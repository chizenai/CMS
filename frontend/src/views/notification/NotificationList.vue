<template>
  <div class="notification-list-container">
    <div class="toolbar">
      <el-button type="primary" @click="handleMarkAllRead" :disabled="unreadCount === 0">全部已读</el-button>
      <span style="margin-left: 20px; color: #606266;">未读: {{ unreadCount }} 条</span>
    </div>

    <el-table :data="tableData" border>
      <el-table-column label="状态" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isRead === 0" type="danger" size="small">未读</el-tag>
          <el-tag v-else type="info" size="small">已读</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题"></el-table-column>
      <el-table-column prop="content" label="内容" show-overflow-tooltip></el-table-column>
      <el-table-column prop="senderName" label="发送人" width="100"></el-table-column>
      <el-table-column prop="createTime" label="发送时间" width="180"></el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="text" @click="handleView(scope.row)">查看</el-button>
          <el-button v-if="scope.row.isRead === 0" type="text" @click="handleMarkRead(scope.row)">标记已读</el-button>
          <el-button type="text" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pagination"
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pagination.pageNum"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pagination.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total">
    </el-pagination>

    <el-dialog title="通知详情" :visible.sync="detailDialogVisible" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="标题">{{ currentNotification.title }}</el-descriptions-item>
        <el-descriptions-item label="发送人">{{ currentNotification.senderName || '系统' }}</el-descriptions-item>
        <el-descriptions-item label="发送时间">{{ currentNotification.createTime }}</el-descriptions-item>
        <el-descriptions-item label="内容">
          <div style="white-space: pre-wrap;">{{ currentNotification.content }}</div>
        </el-descriptions-item>
      </el-descriptions>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getNotificationList, getUnreadCount, markAsRead, markAllAsRead, deleteNotification } from '@/api/notification'

export default {
  name: 'NotificationList',
  props: {
    notificationType: {
      type: Number,
      default: null
    }
  },
  data() {
    return {
      tableData: [],
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      unreadCount: 0,
      detailDialogVisible: false,
      currentNotification: {}
    }
  },
  created() {
    this.loadData()
    this.loadUnreadCount()
  },
  methods: {
    async loadData() {
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        const params = {
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize,
          receiverId: userInfo.id
        }
        if (this.notificationType !== null) {
          params.notificationType = this.notificationType
        }
        const res = await getNotificationList(params)
        this.tableData = res.data.records
        this.pagination.total = res.data.total
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    async loadUnreadCount() {
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        const res = await getUnreadCount(userInfo.id)
        this.unreadCount = res.data.count
      } catch (error) {
        console.error('获取未读数量失败:', error)
      }
    },
    handleView(row) {
      this.currentNotification = row
      this.detailDialogVisible = true
      if (row.isRead === 0) {
        this.handleMarkRead(row)
      }
    },
    async handleMarkRead(row) {
      try {
        await markAsRead(row.id)
        this.$message.success('已标记为已读')
        this.loadData()
        this.loadUnreadCount()
      } catch (error) {
        console.error('标记已读失败:', error)
      }
    },
    async handleMarkAllRead() {
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        await markAllAsRead(userInfo.id)
        this.$message.success('已全部标记为已读')
        this.loadData()
        this.loadUnreadCount()
      } catch (error) {
        console.error('标记全部已读失败:', error)
      }
    },
    handleDelete(row) {
      this.$confirm('确定要删除这条通知吗？', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await deleteNotification(row.id)
          this.$message.success('删除成功')
          this.loadData()
          this.loadUnreadCount()
        } catch (error) {
          console.error('删除失败:', error)
        }
      }).catch(() => {})
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.pagination.pageNum = val
      this.loadData()
    }
  }
}
</script>

<style scoped>
.notification-list-container {
  padding: 0;
}

.toolbar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
}
</style>
