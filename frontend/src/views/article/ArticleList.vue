<template>
  <div class="article-list-container">
    <div class="search-box">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="文章标题">
          <el-input v-model="searchForm.title" placeholder="请输入文章标题" clearable></el-input>
        </el-form-item>
        <el-form-item label="栏目">
          <el-select v-model="searchForm.categoryId" placeholder="请选择栏目" clearable>
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" :value="0"></el-option>
            <el-option label="待审核" :value="1"></el-option>
            <el-option label="已发布" :value="2"></el-option>
            <el-option label="已拒绝" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">新增文章</el-button>
      <el-button type="success" @click="handlePublish" :disabled="selectedIds.length === 0">批量发布</el-button>
      <el-button type="danger" @click="handleDelete" :disabled="selectedIds.length === 0">批量删除</el-button>
    </div>

    <el-table
      :data="tableData"
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="title" label="标题" min-width="200"></el-table-column>
      <el-table-column prop="summary" label="摘要" min-width="200" show-overflow-tooltip></el-table-column>
      <el-table-column prop="viewCount" label="浏览量" width="100"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" @click="handleView(scope.row)">查看</el-button>
          <el-button
            v-if="scope.row.status === 1"
            type="text"
            @click="handleAudit(scope.row)"
          >审核</el-button>
          <el-button
            v-if="scope.row.status === 0"
            type="text"
            @click="handleSinglePublish(scope.row)"
          >发布</el-button>
          <el-button type="text" @click="handleSingleDelete(scope.row)">删除</el-button>
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
      :total="pagination.total"
    >
    </el-pagination>

    <el-dialog title="审核文章" :visible.sync="auditDialogVisible" width="500px">
      <el-form :model="auditForm">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio :label="2">通过</el-radio>
            <el-radio :label="3">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input
            v-model="auditForm.comment"
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
import { getArticleList, deleteArticle, publishArticle, auditArticle } from '@/api/article'
import { getCategoryList } from '@/api/category'

export default {
  name: 'ArticleList',
  data() {
    return {
      searchForm: {
        title: '',
        categoryId: '',
        status: ''
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
      currentAuditId: null,
      auditForm: {
        status: 2,
        comment: ''
      }
    }
  },
  created() {
    this.loadCategoryList()
    this.loadArticleList()
  },
  methods: {
    async loadCategoryList() {
      try {
        const res = await getCategoryList()
        this.categoryList = res.data
      } catch (error) {
        console.error('加载栏目列表失败:', error)
      }
    },
    async loadArticleList() {
      try {
        const params = {
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize,
          ...this.searchForm
        }
        // 清除空值
        Object.keys(params).forEach(key => {
          if (params[key] === '' || params[key] === null) {
            delete params[key]
          }
        })
        const res = await getArticleList(params)
        this.tableData = res.data.records
        this.pagination.total = res.data.total
      } catch (error) {
        console.error('加载文章列表失败:', error)
      }
    },
    getStatusType(status) {
      const types = {
        0: 'info',
        1: 'warning',
        2: 'success',
        3: 'danger'
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        0: '草稿',
        1: '待审核',
        2: '已发布',
        3: '已拒绝'
      }
      return texts[status] || '未知'
    },
    handleSearch() {
      this.pagination.pageNum = 1
      this.loadArticleList()
    },
    handleReset() {
      this.searchForm = {
        title: '',
        categoryId: '',
        status: ''
      }
      this.pagination.pageNum = 1
      this.loadArticleList()
    },
    handleSelectionChange(selection) {
      this.selectedIds = selection.map(item => item.id)
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadArticleList()
    },
    handleCurrentChange(val) {
      this.pagination.pageNum = val
      this.loadArticleList()
    },
    handleAdd() {
      this.$router.push('/article/add')
    },
    handleEdit(row) {
      this.$router.push(`/article/edit/${row.id}`)
    },
    handleView(row) {
      this.$message.info('查看功能开发中')
    },
    async handleSinglePublish(row) {
      try {
        await publishArticle(row.id)
        this.$message.success('发布成功')
        this.loadArticleList()
      } catch (error) {
        console.error('发布失败:', error)
      }
    },
    handlePublish() {
      this.$confirm('确定要发布选中的文章吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          for (const id of this.selectedIds) {
            await publishArticle(id)
          }
          this.$message.success('批量发布成功')
          this.loadArticleList()
        } catch (error) {
          console.error('批量发布失败:', error)
        }
      }).catch(() => {})
    },
    handleAudit(row) {
      this.currentAuditId = row.id
      this.auditForm = {
        status: 2,
        comment: ''
      }
      this.auditDialogVisible = true
    },
    async submitAudit() {
      try {
        await auditArticle({
          id: this.currentAuditId,
          status: this.auditForm.status,
          comment: this.auditForm.comment
        })
        this.$message.success('审核成功')
        this.auditDialogVisible = false
        this.loadArticleList()
      } catch (error) {
        console.error('审核失败:', error)
      }
    },
    async handleSingleDelete(row) {
      this.$confirm('确定要删除这篇文章吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteArticle(row.id)
          this.$message.success('删除成功')
          this.loadArticleList()
        } catch (error) {
          console.error('删除失败:', error)
        }
      }).catch(() => {})
    },
    handleDelete() {
      this.$confirm('确定要删除选中的文章吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          for (const id of this.selectedIds) {
            await deleteArticle(id)
          }
          this.$message.success('批量删除成功')
          this.loadArticleList()
        } catch (error) {
          console.error('批量删除失败:', error)
        }
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.article-list-container {
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
</style>
