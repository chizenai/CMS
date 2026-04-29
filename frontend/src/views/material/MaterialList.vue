<template>
  <div class="material-list-container">
    <div class="card-box">
      <div class="header-row">
        <h3>素材管理</h3>
        <el-upload
          class="upload-dragger"
          drag
          multiple
          :http-request="handleHttpRequest"
          :file-list="fileList"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :on-progress="handleUploadProgress"
          :before-upload="beforeUpload"
          :limit="20"
          :on-exceed="handleExceed"
          :auto-upload="false"
          ref="uploadRef"
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <div class="el-upload__tip" slot="tip">
            支持多文件上传，单文件不超过50MB
          </div>
        </el-upload>
        <el-button type="primary" @click="submitUpload" style="margin-top: 10px;">
          开始上传
        </el-button>
      </div>

      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane label="图片" name="image"></el-tab-pane>
        <el-tab-pane label="视频" name="video"></el-tab-pane>
        <el-tab-pane label="音频" name="audio"></el-tab-pane>
        <el-tab-pane label="文件" name="file"></el-tab-pane>
      </el-tabs>

      <el-table :data="materialList" border v-loading="loading">
        <el-table-column type="selection" width="55"></el-table-column>
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
              style="width: 60px; height: 60px; cursor: pointer;"
              fit="cover"
              :preview-src-list="[scope.row.url]"
              @click="handlePreview(scope.row)"
            ></el-image>
            <i v-else-if="scope.row.type === 'video'" class="el-icon-video-camera" style="font-size: 32px; color: #409eff;"></i>
            <i v-else-if="scope.row.type === 'audio'" class="el-icon-microphone" style="font-size: 32px; color: #e6a23c;"></i>
            <i v-else class="el-icon-document" style="font-size: 32px; color: #909399;"></i>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" @click="handleView(scope.row)">查看</el-button>
            <el-button type="text" @click="handleCopyUrl(scope.row)">复制链接</el-button>
            <el-button type="text" style="color: #f56c6c" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.pageNum"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        style="margin-top: 20px; text-align: right;"
      ></el-pagination>
    </div>

    <el-dialog title="查看素材" :visible.sync="viewDialogVisible" width="700px">
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
      
      <div v-if="currentMaterial.type === 'image' && currentMaterial.url" class="preview-section">
        <h4>预览：</h4>
        <el-image
          :src="currentMaterial.url"
          style="max-width: 100%; max-height: 400px;"
          fit="contain"
          :preview-src-list="[currentMaterial.url]"
        ></el-image>
      </div>
      
      <div v-else-if="currentMaterial.type === 'video' && currentMaterial.url" class="preview-section">
        <h4>视频预览：</h4>
        <video :src="currentMaterial.url" controls style="max-width: 100%;"></video>
      </div>
      
      <div v-else-if="currentMaterial.type === 'audio' && currentMaterial.url" class="preview-section">
        <h4>音频播放：</h4>
        <audio :src="currentMaterial.url" controls style="width: 100%;"></audio>
      </div>
      
      <div v-if="currentMaterial.url" class="url-section">
        <h4>访问地址：</h4>
        <el-input :value="currentMaterial.url" readonly>
          <el-button slot="append" @click="handleCopyUrl(currentMaterial)">
            <i class="el-icon-document-copy"></i> 复制
          </el-button>
        </el-input>
      </div>
      
      <span slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <el-dialog title="图片预览" :visible.sync="previewDialogVisible" width="800px">
      <el-image
        :src="currentPreviewUrl"
        style="width: 100%;"
        fit="contain"
      ></el-image>
      <span slot="footer" class="dialog-footer">
        <el-button @click="previewDialogVisible = false">关闭</el-button>
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
      loading: false,
      uploading: false,
      activeTab: 'all',
      materialList: [],
      fileList: [],
      viewDialogVisible: false,
      previewDialogVisible: false,
      currentMaterial: {},
      currentPreviewUrl: '',
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize
        }
        if (this.activeTab !== 'all') {
          params.type = this.activeTab
        }
        const res = await getMaterialList(params)
        this.materialList = res.data.records
        this.pagination.total = res.data.total
      } catch (error) {
        console.error('加载数据失败:', error)
        this.$message.error('加载数据失败')
      } finally {
        this.loading = false
      }
    },
    handleTabClick(tab) {
      this.activeTab = tab.name
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
    beforeUpload(file) {
      const isLt50M = file.size / 1024 / 1024 < 50
      if (!isLt50M) {
        this.$message.error('上传文件大小不能超过 50MB!')
      }
      return isLt50M
    },
    handleExceed(files, fileList) {
      this.$message.warning(`当前限制选择 20 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`)
    },
    handleUploadSuccess(response, file, fileList) {
      if (response.code === 200) {
        this.$message.success('上传成功')
        this.loadData()
      } else {
        this.$message.error(response.message || '上传失败')
      }
      this.fileList = []
    },
    handleUploadError(error, file, fileList) {
      console.error('上传失败:', error)
      this.$message.error('上传失败: ' + error.message)
      this.fileList = []
    },
    handleUploadProgress(event, file, fileList) {
      // 可以在这里显示上传进度
    },
    handleView(row) {
      this.currentMaterial = { ...row }
      this.viewDialogVisible = true
    },
    handlePreview(row) {
      this.currentPreviewUrl = row.url
      this.previewDialogVisible = true
    },
    handleCopyUrl(row) {
      const input = document.createElement('input')
      input.value = row.url
      document.body.appendChild(input)
      input.select()
      document.execCommand('copy')
      document.body.removeChild(input)
      this.$message.success('链接已复制到剪贴板')
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
.header-row {
  margin-bottom: 20px;
}
.upload-dragger {
  margin-top: 15px;
}
.preview-section {
  margin-top: 20px;
}
.preview-section h4 {
  margin-bottom: 10px;
  color: #606266;
}
.url-section {
  margin-top: 20px;
}
.url-section h4 {
  margin-bottom: 10px;
  color: #606266;
}
</style>
