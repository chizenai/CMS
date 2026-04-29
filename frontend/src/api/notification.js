import request from '@/utils/request'

export function getNotificationList(params) {
  return request({
    url: '/notification/page',
    method: 'get',
    params
  })
}

export function getUnreadNotifications(receiverId) {
  return request({
    url: '/notification/unread',
    method: 'get',
    params: { receiverId }
  })
}

export function getUnreadCount(receiverId) {
  return request({
    url: '/notification/unread-count',
    method: 'get',
    params: { receiverId }
  })
}

export function getNotificationById(id) {
  return request({
    url: `/notification/${id}`,
    method: 'get'
  })
}

export function createNotification(data) {
  return request({
    url: '/notification',
    method: 'post',
    data
  })
}

export function markAsRead(id) {
  return request({
    url: `/notification/read/${id}`,
    method: 'put'
  })
}

export function markAllAsRead(receiverId) {
  return request({
    url: '/notification/read-all',
    method: 'put',
    params: { receiverId }
  })
}

export function deleteNotification(id) {
  return request({
    url: `/notification/${id}`,
    method: 'delete'
  })
}
