<template>
  <div class="audit-record-container">
    <div class="search-box">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="审核状态">
          <el-select v-model="searchForm.auditStatus" placeholder="请选择状态" clearable>
            <el-option label="待审核" :value="0"></el-option>
            <el-option label="通过" :value="1"></el-option>
            <el-option label="拒绝" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="tableData" border>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="articleId" label="文章ID" width="100"></el-table-column>
      <el-table-column prop="submitterName" label="提交人" width="100"></el-table-column>
      <el-table-column prop="submitTime" label="提交时间" width="180"></el-table-column>
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
  </div>
</template>

<script>
import { getAuditRecordList } from '@/api/auditRecord'

export default {
  name: 'AuditRecord',
  data() {
    return {
      searchForm: {
        auditStatus: ''
      },
      tableData: [],
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
        const res = await getAuditRecordList(params)
        this.tableData = res.data.records
        this.pagination.total = res.data.total
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    handleSearch() {
      this.pagination.pageNum = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = {
        auditStatus: ''
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
    }
  }
}
</script>

<style scoped>
.audit-record-container {
  padding: 0;
}

.search-box {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
}
</style>
