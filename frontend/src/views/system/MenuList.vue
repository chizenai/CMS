<template>
  <div class="menu-list-container">
    <div class="card-box">
      <div class="header-row">
        <h3>菜单管理</h3>
        <el-button type="primary" @click="handleAdd(0)">
          <i class="el-icon-plus"></i> 新增菜单
        </el-button>
      </div>
      <el-table :data="menuList" border row-key="id" default-expand-all>
        <el-table-column prop="menuName" label="菜单名称" min-width="200"></el-table-column>
        <el-table-column prop="menuCode" label="菜单编码" width="120"></el-table-column>
        <el-table-column prop="path" label="路由路径" width="150"></el-table-column>
        <el-table-column prop="component" label="组件路径" width="180"></el-table-column>
        <el-table-column prop="icon" label="图标" width="100">
          <template slot-scope="scope">
            <i :class="scope.row.icon" v-if="scope.row.icon"></i>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="menuType" label="类型" width="80">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.menuType === 1" type="info">目录</el-tag>
            <el-tag v-else-if="scope.row.menuType === 2" type="primary">菜单</el-tag>
            <el-tag v-else-if="scope.row.menuType === 3" type="success">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80"></el-table-column>
        <el-table-column prop="permission" label="权限标识" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" @click="handleAdd(scope.row.id)">添加子菜单</el-button>
            <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" style="color: #f56c6c" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form :model="menuForm" :rules="menuRules" ref="menuFormRef" label-width="100px">
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="menuForm.parentId"
            :data="menuTreeData"
            :props="{ label: 'menuName', value: 'id', children: 'children' }"
            placeholder="请选择上级菜单"
            :default-expand-all="true"
            :check-strictly="true"
            clearable
          ></el-tree-select>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="menuForm.menuName" placeholder="请输入菜单名称"></el-input>
        </el-form-item>
        <el-form-item label="菜单编码" prop="menuCode">
          <el-input v-model="menuForm.menuCode" placeholder="请输入菜单编码"></el-input>
        </el-form-item>
        <el-form-item label="路由路径" prop="path" v-if="menuForm.menuType !== 3">
          <el-input v-model="menuForm.path" placeholder="请输入路由路径"></el-input>
        </el-form-item>
        <el-form-item label="组件路径" prop="component" v-if="menuForm.menuType === 2">
          <el-input v-model="menuForm.component" placeholder="请输入组件路径"></el-input>
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="menuForm.menuType !== 3">
          <el-input v-model="menuForm.icon" placeholder="请输入图标类名"></el-input>
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="menuForm.menuType">
            <el-radio :label="1">目录</el-radio>
            <el-radio :label="2">菜单</el-radio>
            <el-radio :label="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="menuForm.sort" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="权限标识" prop="permission">
          <el-input v-model="menuForm.permission" placeholder="请输入权限标识"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="menuForm.status">
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
import { getMenuTree, createMenu, updateMenu, deleteMenu } from '@/api/menu'

export default {
  name: 'MenuList',
  data() {
    return {
      menuList: [],
      menuTreeData: [],
      dialogVisible: false,
      dialogTitle: '新增菜单',
      menuForm: {
        id: null,
        parentId: 0,
        menuName: '',
        menuCode: '',
        path: '',
        component: '',
        icon: '',
        menuType: 2,
        sort: 0,
        permission: '',
        status: 1
      },
      menuRules: {
        menuName: [
          { required: true, message: '请输入菜单名称', trigger: 'blur' }
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
        const res = await getMenuTree()
        this.menuList = res.data
        this.menuTreeData = [
          { id: 0, menuName: '根目录', children: res.data }
        ]
      } catch (error) {
        console.error('加载数据失败:', error)
        this.$message.error('加载数据失败')
      }
    },
    handleAdd(parentId) {
      this.dialogTitle = parentId === 0 ? '新增菜单' : '添加子菜单'
      this.menuForm = {
        id: null,
        parentId: parentId,
        menuName: '',
        menuCode: '',
        path: '',
        component: '',
        icon: '',
        menuType: parentId === 0 ? 1 : 2,
        sort: 0,
        permission: '',
        status: 1
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑菜单'
      this.menuForm = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除这个菜单吗？', '提示', {
          type: 'warning'
        })
        await deleteMenu(row.id)
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
      this.$refs.menuFormRef.validate(async (valid) => {
        if (valid) {
          try {
            if (this.menuForm.id) {
              await updateMenu(this.menuForm)
              this.$message.success('更新成功')
            } else {
              await createMenu(this.menuForm)
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
.menu-list-container {
  padding: 0;
}
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
