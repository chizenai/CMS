<template>
  <div class="permission-list-container">
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd(0)">新增顶级权限</el-button>
    </div>

    <el-table :data="permissionList" border row-key="id" :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
      <el-table-column prop="permissionName" label="权限名称"></el-table-column>
      <el-table-column prop="permissionCode" label="权限编码"></el-table-column>
      <el-table-column prop="permissionType" label="类型" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.permissionType === 1 ? 'primary' : scope.row.permissionType === 2 ? 'success' : 'warning'">
            {{ scope.row.permissionType === 1 ? '菜单' : scope.row.permissionType === 2 ? '按钮' : '接口' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="path" label="路由路径"></el-table-column>
      <el-table-column prop="component" label="组件路径"></el-table-column>
      <el-table-column prop="icon" label="图标" width="100">
        <template slot-scope="scope">
          <i :class="scope.row.icon"></i>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80"></el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="text" @click="handleAdd(scope.row)">新增子级</el-button>
          <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="form.permissionName" placeholder="请输入权限名称"></el-input>
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input v-model="form.permissionCode" placeholder="请输入权限编码"></el-input>
        </el-form-item>
        <el-form-item label="权限类型" prop="permissionType">
          <el-select v-model="form.permissionType" placeholder="请选择权限类型">
            <el-option label="菜单" :value="1"></el-option>
            <el-option label="按钮" :value="2"></el-option>
            <el-option label="接口" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="路由路径">
          <el-input v-model="form.path" placeholder="请输入路由路径（菜单类型）"></el-input>
        </el-form-item>
        <el-form-item label="组件路径">
          <el-input v-model="form.component" placeholder="请输入组件路径（菜单类型）"></el-input>
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="请输入图标类名"></el-input>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
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
      dialogVisible: false,
      dialogTitle: '新增权限',
      form: {
        id: null,
        parentId: 0,
        permissionName: '',
        permissionCode: '',
        permissionType: 1,
        path: '',
        component: '',
        icon: '',
        sort: 0,
        status: 1
      },
      rules: {
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
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    handleAdd(parent) {
      this.dialogTitle = '新增权限'
      this.form = {
        id: null,
        parentId: typeof parent === 'object' ? parent.id : parent,
        permissionName: '',
        permissionCode: '',
        permissionType: 1,
        path: '',
        component: '',
        icon: '',
        sort: 0,
        status: 1
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑权限'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          try {
            if (this.form.id) {
              await updatePermission(this.form)
              this.$message.success('更新成功')
            } else {
              await createPermission(this.form)
              this.$message.success('创建成功')
            }
            this.dialogVisible = false
            this.loadData()
          } catch (error) {
            console.error('操作失败:', error)
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除这个权限吗？', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await deletePermission(row.id)
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
.permission-list-container {
  padding: 0;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
