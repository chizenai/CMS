import request from '@/utils/request'

export function getAuditRecordList(params) {
  return request({
    url: '/audit-record/page',
    method: 'get',
    params
  })
}

export function getRecordsByArticle(articleId) {
  return request({
    url: `/audit-record/article/${articleId}`,
    method: 'get'
  })
}

export function getAuditRecordById(id) {
  return request({
    url: `/audit-record/${id}`,
    method: 'get'
  })
}

export function createAuditRecord(data) {
  return request({
    url: '/audit-record',
    method: 'post',
    data
  })
}

export function updateAuditRecord(data) {
  return request({
    url: '/audit-record',
    method: 'put',
    data
  })
}

export function deleteAuditRecord(id) {
  return request({
    url: `/audit-record/${id}`,
    method: 'delete'
  })
}
