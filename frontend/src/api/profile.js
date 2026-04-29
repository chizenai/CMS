import request from '@/utils/request'

export function getProfile() {
  return request({
    url: '/user/profile/info',
    method: 'get'
  })
}

export function updateProfile(data) {
  return request({
    url: '/user/profile/info',
    method: 'put',
    data
  })
}

export function updatePassword(oldPassword, newPassword) {
  return request({
    url: '/user/profile/password',
    method: 'put',
    data: {
      oldPassword,
      newPassword
    }
  })
}
