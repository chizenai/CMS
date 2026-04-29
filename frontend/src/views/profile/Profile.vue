<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <div class="card-box">
          <div class="avatar-section">
            <el-avatar :size="120" :src="userInfo.avatar">
              <i class="el-icon-user"></i>
            </el-avatar>
            <h3>{{ userInfo.nickname || userInfo.username }}</h3>
            <p>{{ userInfo.roleName || '普通用户' }}</p>
          </div>
          <el-divider></el-divider>
          <div class="info-section">
            <div class="info-item">
              <i class="el-icon-user"></i>
              <span>{{ userInfo.username }}</span>
            </div>
            <div class="info-item">
              <i class="el-icon-message"></i>
              <span>{{ userInfo.email || '未设置' }}</span>
            </div>
            <div class="info-item">
              <i class="el-icon-phone"></i>
              <span>{{ userInfo.phone || '未设置' }}</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="16">
        <div class="card-box">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本信息" name="basic">
              <el-form :model="basicForm" :rules="basicRules" ref="basicFormRef" label-width="100px" class="form-section">
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="basicForm.nickname" placeholder="请输入昵称"></el-input>
                </el-form-item>
                <el-form-item label="头像" prop="avatar">
                  <el-upload
                    class="avatar-uploader"
                    :action="uploadUrl"
                    :show-file-list="false"
                    :on-success="handleAvatarSuccess"
                    :before-upload="beforeAvatarUpload"
                  >
                    <img v-if="basicForm.avatar" :src="basicForm.avatar" class="avatar">
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                  </el-upload>
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="basicForm.email" placeholder="请输入邮箱"></el-input>
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="basicForm.phone" placeholder="请输入手机号"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleSaveBasic">保存</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="password">
              <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="120px" class="form-section">
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input 
                    v-model="passwordForm.oldPassword" 
                    type="password" 
                    placeholder="请输入原密码"
                    show-password
                  ></el-input>
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input 
                    v-model="passwordForm.newPassword" 
                    type="password" 
                    placeholder="请输入新密码"
                    show-password
                  ></el-input>
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input 
                    v-model="passwordForm.confirmPassword" 
                    type="password" 
                    placeholder="请再次输入新密码"
                    show-password
                  ></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { getProfile, updateProfile, changePassword } from '@/api/profile'
import { uploadFile } from '@/api/material'

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
      activeTab: 'basic',
      uploadUrl: '/api/material/upload',
      basicForm: {
        nickname: '',
        avatar: '',
        email: '',
        phone: ''
      },
      basicRules: {
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
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
  computed: {
    ...mapState(['userInfo'])
  },
  created() {
    this.loadProfile()
  },
  methods: {
    async loadProfile() {
      try {
        const res = await getProfile()
        this.basicForm = {
          nickname: res.data.nickname || '',
          avatar: res.data.avatar || '',
          email: res.data.email || '',
          phone: res.data.phone || ''
        }
      } catch (error) {
        console.error('加载个人信息失败:', error)
      }
    },
    handleAvatarSuccess(response, file, fileList) {
      if (response.code === 200) {
        this.basicForm.avatar = response.data.url
      } else {
        this.$message.error('上传失败')
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
    async handleSaveBasic() {
      this.$refs.basicFormRef.validate(async (valid) => {
        if (valid) {
          try {
            await updateProfile(this.basicForm)
            this.$message.success('保存成功')
            this.loadProfile()
            this.$store.commit('SET_USER_INFO', {
              ...this.$store.state.userInfo,
              nickname: this.basicForm.nickname,
              avatar: this.basicForm.avatar,
              email: this.basicForm.email,
              phone: this.basicForm.phone
            })
          } catch (error) {
            console.error('保存失败:', error)
            this.$message.error('保存失败')
          }
        }
      })
    },
    async handleChangePassword() {
      this.$refs.passwordFormRef.validate(async (valid) => {
        if (valid) {
          try {
            await changePassword(this.passwordForm.oldPassword, this.passwordForm.newPassword)
            this.$message.success('密码修改成功，请重新登录')
            this.passwordForm = {
              oldPassword: '',
              newPassword: '',
              confirmPassword: ''
            }
            this.$store.dispatch('logout')
            this.$router.push('/login')
          } catch (error) {
            console.error('修改密码失败:', error)
            this.$message.error('修改密码失败')
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
.avatar-section {
  text-align: center;
  padding: 20px 0;
}
.avatar-section h3 {
  margin: 15px 0 5px 0;
  font-size: 18px;
}
.avatar-section p {
  color: #909399;
  font-size: 14px;
}
.info-section {
  padding: 0 10px;
}
.info-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  color: #606266;
}
.info-item i {
  margin-right: 10px;
  color: #909399;
}
.form-section {
  max-width: 500px;
  padding: 20px 0;
}
.avatar-uploader {
  display: flex;
  align-items: center;
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
.avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}
</style>
