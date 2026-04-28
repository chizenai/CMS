<template>
  <div class="category-list-container">
    <div class="card-box">
      <h3>栏目管理</h3>
      <el-table :data="categoryList" row-key="id" border default-expand-all>
        <el-table-column prop="name" label="栏目名称"></el-table-column>
        <el-table-column prop="code" label="编码"></el-table-column>
        <el-table-column prop="sort" label="排序"></el-table-column>
        <el-table-column prop="level" label="层级"></el-table-column>
        <el-table-column label="状态">
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
import { getCategoryList } from '@/api/category'

export default {
  name: 'CategoryList',
  data() {
    return {
      categoryList: []
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await getCategoryList()
        this.categoryList = res.data
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    handleEdit(row) {
      this.$message.info('编辑功能开发中')
    },
    handleDelete(row) {
      this.$confirm('确定要删除这个栏目吗？', '提示', {
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
.category-list-container {
  padding: 0;
}
</style>
