<template>
  <div class="profile-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>个人信息</span>
      </div>
      <el-form :model="profileForm" :rules="rules" ref="profileForm" label-width="100px" class="profile-form">
        <el-form-item label="用户名">
          <el-input v-model="profileForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="profileForm.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="profileForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload">
            <img v-if="profileForm.avatar" :src="profileForm.avatar" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSaveProfile">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>修改密码</span>
      </div>
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px" class="password-form">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getProfile, updateProfile, updatePassword } from '@/api/profile'

export default {
  name: 'Profile',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      uploadUrl: '/api/material/upload',
      profileForm: {
        id: null,
        username: '',
        nickname: '',
        email: '',
        phone: '',
        avatar: ''
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      rules: {
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadProfile()
  },
  methods: {
    async loadProfile() {
      try {
        const res = await getProfile()
        this.profileForm = { ...res.data }
      } catch (error) {
        console.error('加载个人信息失败:', error)
      }
    },
    handleAvatarSuccess(res) {
      if (res.code === 200) {
        this.profileForm.avatar = res.data.url
      } else {
        this.$message.error(res.message || '上传失败')
      }
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 或 PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    },
    async handleSaveProfile() {
      this.$refs.profileForm.validate(async (valid) => {
        if (valid) {
          try {
            await updateProfile(this.profileForm)
            this.$message.success('修改成功')
            this.loadProfile()
          } catch (error) {
            console.error('修改失败:', error)
          }
        }
      })
    },
    async handleChangePassword() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (valid) {
          try {
            await updatePassword(this.passwordForm.oldPassword, this.passwordForm.newPassword)
            this.$message.success('密码修改成功')
            this.passwordForm = {
              oldPassword: '',
              newPassword: '',
              confirmPassword: ''
            }
          } catch (error) {
            console.error('密码修改失败:', error)
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.profile-container {
  padding: 0;
}

.profile-form,
.password-form {
  max-width: 500px;
}

.avatar-uploader {
  text-align: center;
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
  width: 148px;
  height: 148px;
  line-height: 148px;
  text-align: center;
}

.avatar {
  width: 148px;
  height: 148px;
  display: block;
  object-fit: cover;
}
</style>
