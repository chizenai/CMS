<template>
  <div class="role-list-container">
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">新增角色</el-button>
    </div>

    <el-table :data="roleList" border>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="roleName" label="角色名称"></el-table-column>
      <el-table-column prop="roleCode" label="角色编码"></el-table-column>
      <el-table-column prop="description" label="描述" show-overflow-tooltip></el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { getRoleAll, deleteRole } from '@/api/role'

export default {
  name: 'RoleList',
  data() {
    return {
      roleList: []
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await getRoleAll()
        this.roleList = res.data
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    handleAdd() {
      this.$message.info('新增角色功能开发中')
    },
    handleEdit(row) {
      this.$message.info('编辑功能开发中')
    },
    handleDelete(row) {
      this.$confirm('确定要删除这个角色吗？', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await deleteRole(row.id)
          this.$message.success('删除成功')
          this.loadData()
        } catch (error) {
          console.error('删除失败:', error)
        }
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.role-list-container {
  padding: 0;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
