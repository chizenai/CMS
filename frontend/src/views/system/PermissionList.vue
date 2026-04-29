<template>
  <div class="permission-list-container">
    <div class="card-box">
      <div class="header-row">
        <h3>权限管理</h3>
        <el-button type="primary" @click="handleAdd(0)">
          <i class="el-icon-plus"></i> 新增权限
        </el-button>
      </div>
      <el-table :data="permissionList" border row-key="id" default-expand-all>
        <el-table-column prop="permissionName" label="权限名称" min-width="180"></el-table-column>
        <el-table-column prop="permissionCode" label="权限编码" width="150"></el-table-column>
        <el-table-column prop="resourceType" label="资源类型" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.resourceType === 1" type="info">菜单</el-tag>
            <el-tag v-else-if="scope.row.resourceType === 2" type="primary">按钮</el-tag>
            <el-tag v-else-if="scope.row.resourceType === 3" type="success">API</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="url" label="API地址" min-width="200"></el-table-column>
        <el-table-column prop="method" label="请求方法" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.method === 'GET'" type="success">{{ scope.row.method }}</el-tag>
            <el-tag v-else-if="scope.row.method === 'POST'" type="primary">{{ scope.row.method }}</el-tag>
            <el-tag v-else-if="scope.row.method === 'PUT'" type="warning">{{ scope.row.method }}</el-tag>
            <el-tag v-else-if="scope.row.method === 'DELETE'" type="danger">{{ scope.row.method }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" @click="handleAdd(scope.row.id)">添加子权限</el-button>
            <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" style="color: #f56c6c" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form :model="permissionForm" :rules="permissionRules" ref="permissionFormRef" label-width="100px">
        <el-form-item label="上级权限" prop="parentId">
          <el-tree-select
            v-model="permissionForm.parentId"
            :data="permissionTreeData"
            :props="{ label: 'permissionName', value: 'id', children: 'children' }"
            placeholder="请选择上级权限"
            :default-expand-all="true"
            :check-strictly="true"
            clearable
          ></el-tree-select>
        </el-form-item>
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="permissionForm.permissionName" placeholder="请输入权限名称"></el-input>
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input v-model="permissionForm.permissionCode" placeholder="请输入权限编码"></el-input>
        </el-form-item>
        <el-form-item label="资源类型" prop="resourceType">
          <el-radio-group v-model="permissionForm.resourceType">
            <el-radio :label="1">菜单</el-radio>
            <el-radio :label="2">按钮</el-radio>
            <el-radio :label="3">API</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="API地址" prop="url" v-if="permissionForm.resourceType === 3">
          <el-input v-model="permissionForm.url" placeholder="请输入API地址"></el-input>
        </el-form-item>
        <el-form-item label="请求方法" prop="method" v-if="permissionForm.resourceType === 3">
          <el-select v-model="permissionForm.method" placeholder="请选择请求方法" clearable>
            <el-option label="GET" value="GET"></el-option>
            <el-option label="POST" value="POST"></el-option>
            <el-option label="PUT" value="PUT"></el-option>
            <el-option label="DELETE" value="DELETE"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="permissionForm.sort" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="permissionForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getPermissionTree, createPermission, updatePermission, deletePermission } from '@/api/permission'

export default {
  name: 'PermissionList',
  data() {
    return {
      permissionList: [],
      permissionTreeData: [],
      dialogVisible: false,
      dialogTitle: '新增权限',
      permissionForm: {
        id: null,
        parentId: 0,
        permissionName: '',
        permissionCode: '',
        resourceType: 3,
        url: '',
        method: '',
        sort: 0,
        status: 1
      },
      permissionRules: {
        permissionName: [
          { required: true, message: '请输入权限名称', trigger: 'blur' }
        ],
        permissionCode: [
          { required: true, message: '请输入权限编码', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await getPermissionTree()
        this.permissionList = res.data
        this.permissionTreeData = [
          { id: 0, permissionName: '根目录', children: res.data }
        ]
      } catch (error) {
        console.error('加载数据失败:', error)
        this.$message.error('加载数据失败')
      }
    },
    handleAdd(parentId) {
      this.dialogTitle = parentId === 0 ? '新增权限' : '添加子权限'
      this.permissionForm = {
        id: null,
        parentId: parentId,
        permissionName: '',
        permissionCode: '',
        resourceType: 3,
        url: '',
        method: '',
        sort: 0,
        status: 1
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑权限'
      this.permissionForm = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除这个权限吗？', '提示', {
          type: 'warning'
        })
        await deletePermission(row.id)
        this.$message.success('删除成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
          this.$message.error('删除失败')
        }
      }
    },
    async handleSubmit() {
      this.$refs.permissionFormRef.validate(async (valid) => {
        if (valid) {
          try {
            if (this.permissionForm.id) {
              await updatePermission(this.permissionForm)
              this.$message.success('更新成功')
            } else {
              await createPermission(this.permissionForm)
              this.$message.success('创建成功')
            }
            this.dialogVisible = false
            this.loadData()
          } catch (error) {
            console.error('提交失败:', error)
            this.$message.error('提交失败')
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.permission-list-container {
  padding: 0;
}
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
