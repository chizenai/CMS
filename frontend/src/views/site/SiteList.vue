<template>
  <div class="site-list-container">
    <div class="card-box">
      <h3>站点管理</h3>
      <el-table :data="siteList" border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="站点名称"></el-table-column>
        <el-table-column prop="domain" label="域名"></el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip></el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { getSiteList } from '@/api/site'

export default {
  name: 'SiteList',
  data() {
    return {
      siteList: []
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await getSiteList()
        this.siteList = res.data
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    handleEdit(row) {
      this.$message.info('编辑功能开发中')
    },
    handleDelete(row) {
      this.$confirm('确定要删除这个站点吗？', '提示', {
        type: 'warning'
      }).then(() => {
        this.$message.success('删除成功')
        this.loadData()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.site-list-container {
  padding: 0;
}
</style>
