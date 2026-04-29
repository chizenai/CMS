<template>
  <div class="material-list-container">
    <div class="toolbar">
      <el-button type="primary" @click="uploadDialogVisible = true">
        <i class="el-icon-upload"></i> 上传文件
      </el-button>
    </div>

    <div class="search-box">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="文件名称">
          <el-input v-model="searchForm.name" placeholder="请输入文件名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
            <el-option label="图片" value="image"></el-option>
            <el-option label="视频" value="video"></el-option>
            <el-option label="音频" value="audio"></el-option>
            <el-option label="文件" value="file"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="material-grid">
      <div
        v-for="item in tableData"
        :key="item.id"
        class="material-item"
        @click="handleView(item)">
        <div class="material-preview">
          <el-image
            v-if="item.type === 'image'"
            :src="item.url"
            fit="cover"
            :preview-src-list="[item.url]">
          </el-image>
          <div v-else class="material-icon">
            <i :class="getTypeIcon(item.type)"></i>
          </div>
        </div>
        <div class="material-info">
          <div class="material-name" :title="item.name">{{ item.name }}</div>
          <div class="material-meta">
            <span>{{ formatSize(item.size) }}</span>
            <span>{{ item.createTime }}</span>
          </div>
        </div>
        <div class="material-actions" @click.stop>
          <el-button type="text" size="small" @click="handleDelete(item)">
            <i class="el-icon-delete"></i> 删除
          </el-button>
        </div>
      </div>
      <div v-if="tableData.length === 0" class="empty-item">
        <el-empty description="暂无素材"></el-empty>
      </div>
    </div>

    <el-pagination
      class="pagination"
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pagination.pageNum"
      :page-sizes="[12, 24, 48, 96]"
      :page-size="pagination.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total">
    </el-pagination>

    <el-dialog title="文件上传" :visible.sync="uploadDialogVisible" width="700px" :close-on-click-modal="false">
      <div class="upload-area">
        <el-upload
          class="upload-dragger"
          drag
          :action="uploadUrl"
          :multiple="true"
          :limit="20"
          :auto-upload="false"
          :file-list="fileList"
          :on-change="handleFileChange"
          :on-exceed="handleExceed"
          list-type="picture">
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">
            支持多文件上传，单次最多20个文件，支持图片、视频、音频、文档等格式
          </div>
        </el-upload>
      </div>

      <div v-if="fileList.length > 0" class="upload-progress">
        <h4>待上传文件 ({{ fileList.length }} 个)</h4>
        <el-table :data="fileList" size="small">
          <el-table-column prop="name" label="文件名" min-width="200"></el-table-column>
          <el-table-column label="大小" width="100">
            <template slot-scope="scope">
              {{ formatSize(scope.row.size) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.status === 'success'" type="success">上传成功</el-tag>
              <el-tag v-else-if="scope.row.status === 'uploading'" type="primary">上传中</el-tag>
              <el-tag v-else-if="scope.row.status === 'fail'" type="danger">上传失败</el-tag>
              <el-tag v-else>待上传</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="handleRemoveFile(scope.$index, scope.row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="handleClearFiles">清空</el-button>
        <el-button type="primary" @click="handleUpload" :loading="uploading">
          {{ uploading ? '上传中...' : '开始上传' }}
        </el-button>
      </span>
    </el-dialog>

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
        <el-descriptions-item label="大小">{{ formatSize(currentMaterial.size) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ currentMaterial.createTime }}</el-descriptions-item>
      </el-descriptions>

      <div v-if="currentMaterial.type === 'image' && currentMaterial.url" class="preview-area">
        <h4>图片预览</h4>
        <el-image
          :src="currentMaterial.url"
          style="max-width: 100%; max-height: 400px;"
          fit="contain"
          :preview-src-list="[currentMaterial.url]"
          class="preview-image">
        </el-image>
      </div>

      <div v-if="currentMaterial.url" class="url-area">
        <h4>访问地址</h4>
        <el-input :value="currentMaterial.url" readonly>
          <template slot="append">
            <el-button @click="copyUrl(currentMaterial.url)">复制</el-button>
          </template>
        </el-input>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getMaterialList, deleteMaterial, uploadMultipleFiles } from '@/api/material'

export default {
  name: 'MaterialList',
  data() {
    return {
      uploadUrl: '/api/material/upload-multi',
      searchForm: {
        name: '',
        type: ''
      },
      tableData: [],
      pagination: {
        pageNum: 1,
        pageSize: 12,
        total: 0
      },
      uploadDialogVisible: false,
      viewDialogVisible: false,
      currentMaterial: {},
      fileList: [],
      uploading: false
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
        const res = await getMaterialList(params)
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
        name: '',
        type: ''
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
    },
    getTypeIcon(type) {
      switch (type) {
        case 'video':
          return 'el-icon-video-camera'
        case 'audio':
          return 'el-icon-microphone'
        default:
          return 'el-icon-document'
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
    },
    handleFileChange(file, fileList) {
      this.fileList = fileList
    },
    handleExceed(files, fileList) {
      this.$message.warning(`当前限制选择 20 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`)
    },
    handleRemoveFile(index, file) {
      this.fileList.splice(index, 1)
    },
    handleClearFiles() {
      this.fileList = []
    },
    async handleUpload() {
      if (this.fileList.length === 0) {
        this.$message.warning('请选择要上传的文件')
        return
      }

      this.uploading = true
      try {
        const files = this.fileList.map(f => f.raw)
        const res = await uploadMultipleFiles(files)
        this.$message.success(`成功上传 ${res.data.length} 个文件`)
        this.uploadDialogVisible = false
        this.fileList = []
        this.loadData()
      } catch (error) {
        console.error('上传失败:', error)
        this.$message.error('上传失败')
      } finally {
        this.uploading = false
      }
    },
    copyUrl(url) {
      const input = document.createElement('textarea')
      input.value = url
      document.body.appendChild(input)
      input.select()
      document.execCommand('copy')
      document.body.removeChild(input)
      this.$message.success('复制成功')
    }
  }
}
</script>

<style scoped>
.material-list-container {
  padding: 0;
}

.toolbar {
  margin-bottom: 20px;
}

.search-box {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.material-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.material-item {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  cursor: pointer;
}

.material-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.material-preview {
  height: 150px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.material-preview >>> .el-image {
  width: 100%;
  height: 100%;
}

.material-icon {
  font-size: 48px;
  color: #909399;
}

.material-info {
  padding: 12px;
}

.material-name {
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 8px;
}

.material-meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  justify-content: space-between;
}

.material-actions {
  padding: 8px 12px;
  border-top: 1px solid #ebeef5;
}

.empty-item {
  grid-column: 1 / -1;
  text-align: center;
  padding: 40px;
}

.pagination {
  text-align: center;
}

.upload-area {
  margin-bottom: 20px;
}

.upload-dragger >>> .el-upload-dragger {
  width: 100%;
  height: 180px;
}

.upload-progress {
  margin-top: 20px;
}

.upload-progress h4 {
  margin-bottom: 10px;
  color: #606266;
}

.preview-area,
.url-area {
  margin-top: 20px;
}

.preview-area h4,
.url-area h4 {
  margin-bottom: 10px;
  color: #606266;
}

.preview-image {
  border-radius: 4px;
}
</style>
