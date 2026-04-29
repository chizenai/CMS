import request from '@/utils/request'

export function getProfile() {
  return request({
    url: '/profile',
    method: 'get'
  })
}

export function updateProfile(data) {
  return request({
    url: '/profile',
    method: 'put',
    data
  })
}

export function changePassword(oldPassword, newPassword) {
  return request({
    url: '/profile/change-password',
    method: 'put',
    data: {
      oldPassword,
      newPassword
    }
  })
}
