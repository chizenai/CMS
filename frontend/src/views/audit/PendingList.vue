<template>
  <div class="pending-list-container">
    <div class="search-box">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入文章标题" clearable></el-input>
        </el-form-item>
        <el-form-item label="栏目">
          <el-select v-model="searchForm.categoryId" placeholder="请选择栏目" clearable>
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="toolbar">
      <el-button type="primary" @click="handleBatchAudit(2)" :disabled="selectedIds.length === 0">批量通过</el-button>
      <el-button type="danger" @click="handleBatchAudit(3)" :disabled="selectedIds.length === 0">批量拒绝</el-button>
      <span style="margin-left: 20px; color: #606266;">已选择 {{ selectedIds.length }} 项</span>
    </div>

    <el-table :data="tableData" border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="title" label="标题" show-overflow-tooltip></el-table-column>
      <el-table-column prop="categoryName" label="栏目" width="120"></el-table-column>
      <el-table-column prop="author" label="作者" width="100"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag type="warning">待审核</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template slot-scope="scope">
          <el-button type="text" @click="handleView(scope.row)">查看</el-button>
          <el-button type="text" @click="handleAudit(scope.row, 2)">通过</el-button>
          <el-button type="text" @click="handleAudit(scope.row, 3)">拒绝</el-button>
          <el-button type="text" @click="handleViewRecords(scope.row)">审核记录</el-button>
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

    <el-dialog title="审核文章" :visible.sync="auditDialogVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ currentArticle.id }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ currentArticle.title }}</el-descriptions-item>
        <el-descriptions-item label="栏目">{{ currentArticle.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ currentArticle.authorId }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ currentArticle.createTime }}</el-descriptions-item>
      </el-descriptions>
      <el-divider></el-divider>
      <el-descriptions title="文章内容" :column="1" border>
        <el-descriptions-item>
          <div v-html="currentArticle.content" style="max-height: 300px; overflow-y: auto;"></div>
        </el-descriptions-item>
      </el-descriptions>
      <el-divider></el-divider>
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="审核意见">
          <el-input
            v-model="auditForm.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入审核意见">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="auditDialogVisible = false">取 消</el-button>
        <el-button type="danger" @click="submitAudit(3)">拒 绝</el-button>
        <el-button type="primary" @click="submitAudit(2)">通 过</el-button>
      </span>
    </el-dialog>

    <el-dialog title="审核记录" :visible.sync="recordDialogVisible" width="700px">
      <el-table :data="auditRecords" border>
        <el-table-column prop="submitTime" label="提交时间" width="180"></el-table-column>
        <el-table-column prop="submitterName" label="提交人" width="100"></el-table-column>
        <el-table-column label="审核状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.auditStatus === 1 ? 'success' : scope.row.auditStatus === 2 ? 'danger' : 'warning'">
              {{ scope.row.auditStatus === 1 ? '通过' : scope.row.auditStatus === 2 ? '拒绝' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditTime" label="审核时间" width="180"></el-table-column>
        <el-table-column prop="auditorName" label="审核人" width="100"></el-table-column>
        <el-table-column prop="auditComment" label="审核意见" show-overflow-tooltip></el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog title="审核意见" :visible.sync="batchAuditDialogVisible" width="500px">
      <el-form :model="batchAuditForm" label-width="100px">
        <el-form-item label="审核意见">
          <el-input
            v-model="batchAuditForm.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入审核意见">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="batchAuditDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitBatchAudit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getPendingArticles, getArticleById, auditArticle, batchAudit } from '@/api/article'
import { getRecordsByArticle } from '@/api/auditRecord'
import { getCategoryList } from '@/api/category'

export default {
  name: 'PendingList',
  data() {
    return {
      searchForm: {
        title: '',
        categoryId: ''
      },
      tableData: [],
      categoryList: [],
      selectedIds: [],
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      auditDialogVisible: false,
      recordDialogVisible: false,
      batchAuditDialogVisible: false,
      currentArticle: {},
      auditRecords: [],
      auditForm: {
        comment: ''
      },
      batchAuditForm: {
        comment: '',
        status: 2
      }
    }
  },
  created() {
    this.loadCategories()
    this.loadData()
  },
  methods: {
    async loadCategories() {
      try {
        const res = await getCategoryList()
        this.categoryList = res.data
      } catch (error) {
        console.error('加载栏目失败:', error)
      }
    },
    async loadData() {
      try {
        const params = {
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize,
          ...this.searchForm
        }
        Object.keys(params).forEach(key => {
          if (params[key] === '' || params[key] === null) {
            delete params[key]
          }
        })
        const res = await getPendingArticles(params)
        this.tableData = res.data.records
        this.pagination.total = res.data.total
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    handleSelectionChange(selection) {
      this.selectedIds = selection.map(item => item.id)
    },
    handleSearch() {
      this.pagination.pageNum = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = {
        title: '',
        categoryId: ''
      }
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
    async handleView(row) {
      try {
        const res = await getArticleById(row.id)
        this.currentArticle = res.data
        this.auditForm.comment = ''
        this.auditDialogVisible = true
      } catch (error) {
        console.error('获取文章详情失败:', error)
      }
    },
    handleAudit(row, status) {
      this.handleView(row)
    },
    async submitAudit(status) {
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        const data = {
          id: this.currentArticle.id,
          status: status,
          comment: this.auditForm.comment,
          auditorId: userInfo.id,
          auditorName: userInfo.nickname || userInfo.username
        }
        await auditArticle(data)
        this.$message.success(status === 2 ? '审核通过' : '已拒绝')
        this.auditDialogVisible = false
        this.loadData()
      } catch (error) {
        console.error('审核失败:', error)
      }
    },
    async handleViewRecords(row) {
      try {
        const res = await getRecordsByArticle(row.id)
        this.auditRecords = res.data
        this.recordDialogVisible = true
      } catch (error) {
        console.error('获取审核记录失败:', error)
      }
    },
    handleBatchAudit(status) {
      this.batchAuditForm.status = status
      this.batchAuditForm.comment = ''
      this.batchAuditDialogVisible = true
    },
    async submitBatchAudit() {
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        const data = {
          ids: this.selectedIds,
          status: this.batchAuditForm.status,
          comment: this.batchAuditForm.comment,
          auditorId: userInfo.id,
          auditorName: userInfo.nickname || userInfo.username
        }
        const res = await batchAudit(data)
        this.$message.success(`批量审核完成：成功 ${res.data.successCount} 条，失败 ${res.data.failCount} 条`)
        this.batchAuditDialogVisible = false
        this.selectedIds = []
        this.loadData()
      } catch (error) {
        console.error('批量审核失败:', error)
      }
    }
  }
}
</script>

<style scoped>
.pending-list-container {
  padding: 0;
}

.search-box {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
}
</style>
