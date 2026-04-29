<template>
  <div class="audit-history-container">
    <div class="card-box">
      <h3>审核记录</h3>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="审核状态">
          <el-select v-model="searchForm.auditStatus" placeholder="请选择状态" clearable>
            <el-option label="已通过" :value="1"></el-option>
            <el-option label="已拒绝" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="historyList" border v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="articleTitle" label="文章标题" min-width="200"></el-table-column>
        <el-table-column prop="categoryName" label="栏目" width="120"></el-table-column>
        <el-table-column prop="submitterName" label="提交人" width="120"></el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180"></el-table-column>
        <el-table-column prop="auditorName" label="审核人" width="120"></el-table-column>
        <el-table-column prop="auditTime" label="审核时间" width="180"></el-table-column>
        <el-table-column prop="auditStatus" label="审核状态" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.auditStatus === 1" type="success">已通过</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === 2" type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button type="text" @click="handleView(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog title="审核记录详情" :visible.sync="viewDialogVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentRecord">
        <el-descriptions-item label="文章标题" :span="2">{{ currentRecord.articleTitle }}</el-descriptions-item>
        <el-descriptions-item label="提交人">{{ currentRecord.submitterName }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ currentRecord.submitTime }}</el-descriptions-item>
        <el-descriptions-item label="审核人">{{ currentRecord.auditorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ currentRecord.auditTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核状态" :span="2">
          <el-tag v-if="currentRecord.auditStatus === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="currentRecord.auditStatus === 1" type="success">已通过</el-tag>
          <el-tag v-else-if="currentRecord.auditStatus === 2" type="danger">已拒绝</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核意见" :span="2">
          {{ currentRecord.auditComment || '-' }}
        </el-descriptions-item>
      </el-descriptions>
      <span slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getAuditHistory, getAuditRecordById } from '@/api/audit'

export default {
  name: 'AuditHistory',
  data() {
    return {
      loading: false,
      historyList: [],
      currentRecord: null,
      viewDialogVisible: false,
      searchForm: {
        auditStatus: null
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
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getAuditHistory({
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize,
          auditStatus: this.searchForm.auditStatus
        })
        this.historyList = res.data.records
        this.pagination.total = res.data.total
      } catch (error) {
        console.error('加载数据失败:', error)
        this.$message.error('加载数据失败')
      } finally {
        this.loading = false
      }
    },
    resetSearch() {
      this.searchForm = {
        auditStatus: null
      }
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
    async handleView(row) {
      try {
        const res = await getAuditRecordById(row.id)
        this.currentRecord = res.data
        this.viewDialogVisible = true
      } catch (error) {
        console.error('获取审核记录失败:', error)
        this.$message.error('获取审核记录失败')
      }
    }
  }
}
</script>

<style scoped>
.audit-history-container {
  padding: 0;
}
.search-form {
  margin-bottom: 20px;
}
</style>
