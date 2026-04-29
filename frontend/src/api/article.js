import request from '@/utils/request'

export function getArticleList(params) {
  return request({
    url: '/article/page',
    method: 'get',
    params
  })
}

export function getArticleById(id) {
  return request({
    url: `/article/${id}`,
    method: 'get'
  })
}

export function createArticle(data) {
  return request({
    url: '/article',
    method: 'post',
    data
  })
}

export function updateArticle(data) {
  return request({
    url: '/article',
    method: 'put',
    data
  })
}

export function deleteArticle(id) {
  return request({
    url: `/article/${id}`,
    method: 'delete'
  })
}

export function publishArticle(id) {
  return request({
    url: `/article/publish/${id}`,
    method: 'put'
  })
}

export function revokeArticle(id) {
  return request({
    url: `/article/revoke/${id}`,
    method: 'put'
  })
}

export function auditArticle(data) {
  return request({
    url: '/article/audit',
    method: 'put',
    data
  })
}
