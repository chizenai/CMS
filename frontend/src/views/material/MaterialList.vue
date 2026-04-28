<template>
  <div class="material-list-container">
    <div class="card-box">
      <h3>素材管理</h3>
      <div class="material-grid">
        <div v-for="material in materialList" :key="material.id" class="material-item">
          <div class="material-preview">
            <i v-if="material.type === 'image'" class="el-icon-picture"></i>
            <i v-else-if="material.type === 'video'" class="el-icon-video-camera"></i>
            <i v-else class="el-icon-document"></i>
          </div>
          <div class="material-info">
            <p class="material-name">{{ material.name }}</p>
            <p class="material-type">{{ material.type }}</p>
          </div>
        </div>
      </div>
      <el-empty v-if="materialList.length === 0" description="暂无素材"></el-empty>
    </div>
  </div>
</template>

<script>
import { getMaterialAll } from '@/api/material'

export default {
  name: 'MaterialList',
  data() {
    return {
      materialList: []
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
    }
  }
}
</script>

<style scoped>
.material-list-container {
  padding: 0;
}

.material-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.material-item {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.material-preview {
  height: 150px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.material-preview i {
  font-size: 48px;
  color: #c0c4cc;
}

.material-info {
  padding: 10px;
}

.material-name {
  font-size: 14px;
  color: #303133;
  margin: 0 0 5px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.material-type {
  font-size: 12px;
  color: #909399;
  margin: 0;
}
</style>
