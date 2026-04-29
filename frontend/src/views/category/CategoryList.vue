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

    <el-dialog title="编辑栏目" :visible.sync="editDialogVisible" width="600px">
      <el-form :model="editForm" :rules="editRules" ref="editForm" label-width="100px">
        <el-form-item label="栏目名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入栏目名称"></el-input>
        </el-form-item>
        <el-form-item label="栏目编码" prop="code">
          <el-input v-model="editForm.code" placeholder="请输入栏目编码"></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="editForm.sort" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="层级" prop="level">
          <el-input-number v-model="editForm.level" :min="1" :max="3"></el-input-number>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="editForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getCategoryList, updateCategory, getCategoryById, deleteCategory } from '@/api/category'

export default {
  name: 'CategoryList',
  data() {
    return {
      categoryList: [],
      editDialogVisible: false,
      editForm: {
        id: null,
        name: '',
        code: '',
        sort: 0,
        level: 1,
        status: 1,
        description: '',
        parentId: 0
      },
      editRules: {
        name: [{ required: true, message: '请输入栏目名称', trigger: 'blur' }],
        code: [{ required: true, message: '请输入栏目编码', trigger: 'blur' }]
      }
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
    async handleEdit(row) {
      try {
        const res = await getCategoryById(row.id)
        if (res.code === 200) {
          this.editForm = { ...res.data }
          this.editDialogVisible = true
        }
      } catch (error) {
        console.error('获取栏目详情失败:', error)
      }
    },
    async submitEdit() {
      this.$refs.editForm.validate(async (valid) => {
        if (valid) {
          try {
            const res = await updateCategory(this.editForm)
            if (res.code === 200) {
              this.$message.success('更新成功')
              this.editDialogVisible = false
              this.loadData()
            }
          } catch (error) {
            console.error('更新失败:', error)
            this.$message.error('更新失败')
          }
        }
      })
    },
    async handleDelete(row) {
      this.$confirm('确定要删除这个栏目吗？', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await deleteCategory(row.id)
          this.$message.success('删除成功')
          this.loadData()
        } catch (error) {
          console.error('删除失败:', error)
          this.$message.error('删除失败')
        }
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
