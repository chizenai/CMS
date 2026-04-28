<template>
  <div class="article-edit-container">
    <el-card>
      <template slot="header">
        <span>{{ isEdit ? '编辑文章' : '新增文章' }}</span>
      </template>

      <el-form :model="form" :rules="rules" ref="articleForm" label-width="100px">
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文章标题"></el-input>
        </el-form-item>

        <el-form-item label="所属栏目" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择栏目" style="width: 100%;">
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="文章类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">文章</el-radio>
            <el-radio :label="2">图文</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="文章摘要" prop="summary">
          <el-input
            v-model="form.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入文章摘要"
          ></el-input>
        </el-form-item>

        <el-form-item label="封面图片">
          <el-upload
            class="avatar-uploader"
            action="/api/material"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="form.cover" :src="form.cover" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>

        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0"></el-input-number>
        </el-form-item>

        <el-form-item label="文章内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="15"
            placeholder="请输入文章内容"
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave">保存草稿</el-button>
          <el-button type="success" @click="handleSubmit">提交审核</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getArticleById, createArticle, updateArticle } from '@/api/article'
import { getCategoryList } from '@/api/category'

export default {
  name: 'ArticleEdit',
  data() {
    return {
      isEdit: false,
      categoryList: [],
      form: {
        id: null,
        title: '',
        categoryId: '',
        type: 1,
        summary: '',
        cover: '',
        content: '',
        sort: 0
      },
      rules: {
        title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择栏目', trigger: 'change' }],
        content: [{ required: true, message: '请输入文章内容', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadCategoryList()
    if (this.$route.params.id) {
      this.isEdit = true
      this.loadArticleDetail()
    }
  },
  methods: {
    async loadCategoryList() {
      try {
        const res = await getCategoryList()
        this.categoryList = res.data
      } catch (error) {
        console.error('加载栏目列表失败:', error)
      }
    },
    async loadArticleDetail() {
      try {
        const res = await getArticleById(this.$route.params.id)
        this.form = { ...res.data }
      } catch (error) {
        console.error('加载文章详情失败:', error)
      }
    },
    handleAvatarSuccess(res, file) {
      if (res.code === 200) {
        this.form.cover = URL.createObjectURL(file.raw)
      }
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传图片只能是 JPG 或 PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    },
    async handleSave() {
      this.$refs.articleForm.validate(async (valid) => {
        if (valid) {
          try {
            const data = { ...this.form, status: 0 }
            if (this.isEdit) {
              await updateArticle(data)
              this.$message.success('保存成功')
            } else {
              await createArticle(data)
              this.$message.success('保存成功')
            }
            this.$router.push('/article')
          } catch (error) {
            console.error('保存失败:', error)
          }
        }
      })
    },
    async handleSubmit() {
      this.$refs.articleForm.validate(async (valid) => {
        if (valid) {
          try {
            const data = { ...this.form, status: 1 }
            if (this.isEdit) {
              await updateArticle(data)
              this.$message.success('提交审核成功')
            } else {
              await createArticle(data)
              this.$message.success('提交审核成功')
            }
            this.$router.push('/article')
          } catch (error) {
            console.error('提交失败:', error)
          }
        }
      })
    },
    handleCancel() {
      this.$router.push('/article')
    }
  }
}
</script>

<style scoped>
.article-edit-container {
  padding: 0;
}

.avatar-uploader >>> .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader >>> .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
