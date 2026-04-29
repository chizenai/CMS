<template>
  <div class="material-list-container">
    <div class="card-box">
      <h3>素材管理</h3>
      <el-table :data="materialList" border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="文件名称" min-width="200"></el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.type === 'image'" type="success">图片</el-tag>
            <el-tag v-else-if="scope.row.type === 'video'" type="primary">视频</el-tag>
            <el-tag v-else-if="scope.row.type === 'audio'" type="warning">音频</el-tag>
            <el-tag v-else type="info">文件</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="栏目" width="120">
          <template slot-scope="scope">
            {{ scope.row.categoryName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="size" label="大小" width="120">
          <template slot-scope="scope">
            {{ formatSize(scope.row.size) }}
          </template>
        </el-table-column>
        <el-table-column prop="url" label="预览" width="150">
          <template slot-scope="scope">
            <el-image
              v-if="scope.row.type === 'image'"
              :src="scope.row.url"
              style="width: 60px; height: 60px;"
              fit="cover"
              :preview-src-list="[scope.row.url]"
            ></el-image>
            <span v-else>{{ scope.row.type }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button type="text" @click="handleView(scope.row)">查看</el-button>
            <el-button type="text" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="materialList.length === 0" description="暂无素材"></el-empty>
    </div>

    <el-dialog title="查看素材" :visible.sync="viewDialogVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ currentMaterial.id }}</el-descriptions-item>
        <el-descriptions-item label="文件名称">{{ currentMaterial.name }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag v-if="currentMaterial.type === 'image'" type="success">图片</el-tag>
          <el-tag v-else-if="currentMaterial.type === 'video'" type="primary">视频</el-tag>
          <el-tag v-else-if="currentMaterial.type === 'audio'" type="warning">音频</el-tag>
          <el-tag v-else type="info">文件</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="栏目">{{ currentMaterial.categoryName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="大小">{{ formatSize(currentMaterial.size) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentMaterial.createTime }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="currentMaterial.type === 'image' && currentMaterial.url" style="margin-top: 20px;">
        <label>预览：</label>
        <el-image
          :src="currentMaterial.url"
          style="max-width: 100%; max-height: 400px; margin-top: 10px;"
          fit="contain"
          :preview-src-list="[currentMaterial.url]"
        ></el-image>
      </div>
      <div v-if="currentMaterial.url" style="margin-top: 20px;">
        <label>访问地址：</label>
        <el-input :value="currentMaterial.url" readonly style="margin-top: 10px;"></el-input>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getMaterialAll, deleteMaterial } from '@/api/material'

export default {
  name: 'MaterialList',
  data() {
    return {
      materialList: [],
      viewDialogVisible: false,
      currentMaterial: {}
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await getMaterialAll()
        this.materialList = res.data
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    formatSize(size) {
      if (!size) return '-'
      if (size < 1024) {
        return size + ' B'
      } else if (size < 1024 * 1024) {
        return (size / 1024).toFixed(2) + ' KB'
      } else {
        return (size / (1024 * 1024)).toFixed(2) + ' MB'
      }
    },
    handleView(row) {
      this.currentMaterial = { ...row }
      this.viewDialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除这个素材吗？', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await deleteMaterial(row.id)
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
.material-list-container {
  padding: 0;
}
</style>
