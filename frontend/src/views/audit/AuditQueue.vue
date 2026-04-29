<template>
  <div class="audit-queue-container">
    <div class="card-box">
      <div class="header-row">
        <h3>待审核文章</h3>
        <div class="action-buttons">
          <el-button 
            type="success" 
            :disabled="selectedIds.length === 0"
            @click="handleBatchAudit(1)"
          >
            批量通过
          </el-button>
          <el-button 
            type="danger" 
            :disabled="selectedIds.length === 0"
            @click="handleBatchAudit(2)"
          >
            批量拒绝
          </el-button>
        </div>
      </div>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="文章标题">
          <el-input v-model="searchForm.title" placeholder="请输入文章标题" clearable></el-input>
        </el-form-item>
        <el-form-item label="栏目">
          <el-select v-model="searchForm.categoryId" placeholder="请选择栏目" clearable>
            <el-option
              v-for="category in categoryList"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table 
        :data="auditList" 
        border 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="articleTitle" label="文章标题" min-width="200"></el-table-column>
        <el-table-column prop="categoryName" label="栏目" width="120"></el-table-column>
        <el-table-column prop="submitterName" label="提交人" width="120"></el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180"></el-table-column>
        <el-table-column label="操作" width="250">
          <template slot-scope="scope">
            <el-button type="text" @click="handleView(scope.row)">查看</el-button>
            <el-button type="text" style="color: #67c23a" @click="handleAudit(scope.row, 1)">通过</el-button>
            <el-button type="text" style="color: #f56c6c" @click="handleAudit(scope.row, 2)">拒绝</el-button>
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

    <el-dialog title="文章详情" :visible.sync="viewDialogVisible" width="800px">
      <el-descriptions :column="2" border v-if="currentArticle">
        <el-descriptions-item label="标题" :span="2">{{ currentArticle.title }}</el-descriptions-item>
        <el-descriptions-item label="栏目">{{ currentArticle.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ currentArticle.authorId }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentArticle.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentArticle.updateTime }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="currentArticle && currentArticle.content" style="margin-top: 20px;">
        <h4>文章内容：</h4>
        <div class="article-content" v-html="currentArticle.content"></div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <el-dialog :title="auditDialogTitle" :visible.sync="auditDialogVisible" width="500px">
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="审核意见">
          <el-input
            v-model="auditForm.auditComment"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见"
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getAuditQueue, batchAudit, auditArticle, getAuditRecordById } from '@/api/audit'
import { getArticleById } from '@/api/article'
import { getCategoryAll } from '@/api/category'

export default {
  name: 'AuditQueue',
  data() {
    return {
      loading: false,
      auditList: [],
      categoryList: [],
      selectedIds: [],
      currentArticle: null,
      viewDialogVisible: false,
      auditDialogVisible: false,
      auditDialogTitle: '审核',
      currentAuditRecord: null,
      currentAuditStatus: 1,
      searchForm: {
        title: '',
        categoryId: null
      },
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      auditForm: {
        auditComment: ''
      }
    }
  },
  created() {
    this.loadData()
    this.loadCategories()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getAuditQueue({
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize,
          title: this.searchForm.title,
          categoryId: this.searchForm.categoryId
        })
        this.auditList = res.data.records
        this.pagination.total = res.data.total
      } catch (error) {
        console.error('加载数据失败:', error)
        this.$message.error('加载数据失败')
      } finally {
        this.loading = false
      }
    },
    async loadCategories() {
      try {
        const res = await getCategoryAll()
        this.categoryList = res.data
      } catch (error) {
        console.error('加载栏目失败:', error)
      }
    },
    resetSearch() {
      this.searchForm = {
        title: '',
        categoryId: null
      }
      this.loadData()
    },
    handleSelectionChange(selection) {
      this.selectedIds = selection.map(item => item.id)
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
        const recordRes = await getAuditRecordById(row.id)
        if (recordRes.data) {
          const articleRes = await getArticleById(recordRes.data.articleId)
          this.currentArticle = articleRes.data
          this.viewDialogVisible = true
        }
      } catch (error) {
        console.error('获取文章详情失败:', error)
        this.$message.error('获取文章详情失败')
      }
    },
    handleAudit(row, status) {
      this.currentAuditRecord = row
      this.currentAuditStatus = status
      this.auditDialogTitle = status === 1 ? '审核通过' : '审核拒绝'
      this.auditForm.auditComment = ''
      this.auditDialogVisible = true
    },
    async submitAudit() {
      try {
        await auditArticle({
          id: this.currentAuditRecord.id,
          auditStatus: this.currentAuditStatus,
          auditComment: this.auditForm.auditComment
        })
        this.$message.success('审核成功')
        this.auditDialogVisible = false
        this.loadData()
      } catch (error) {
        console.error('审核失败:', error)
        this.$message.error('审核失败')
      }
    },
    handleBatchAudit(status) {
      this.currentAuditStatus = status
      this.auditDialogTitle = status === 1 ? '批量通过' : '批量拒绝'
      this.auditForm.auditComment = ''
      this.auditDialogVisible = true
    }
  }
}
</script>

<style scoped>
.audit-queue-container {
  padding: 0;
}
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.search-form {
  margin-bottom: 20px;
}
.article-content {
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  max-height: 400px;
  overflow-y: auto;
}
</style>
