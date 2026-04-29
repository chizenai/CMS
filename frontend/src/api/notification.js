import request from '@/utils/request'

export function getNotificationList(params) {
  return request({
    url: '/notification/list',
    method: 'get',
    params
  })
}

export function getUnreadCount(userId) {
  return request({
    url: '/notification/unread/count',
    method: 'get',
    params: {
      userId
    }
  })
}

export function getNotificationById(id) {
  return request({
    url: `/notification/${id}`,
    method: 'get'
  })
}

export function sendNotification(data) {
  return request({
    url: '/notification',
    method: 'post',
    data
  })
}

export function markAsRead(id, userId) {
  return request({
    url: `/notification/read/${id}`,
    method: 'put',
    params: {
      userId
    }
  })
}

export function markAllAsRead(userId) {
  return request({
    url: '/notification/read-all',
    method: 'put',
    params: {
      userId
    }
  })
}

export function deleteNotification(id) {
  return request({
    url: `/notification/${id}`,
    method: 'delete'
  })
}
