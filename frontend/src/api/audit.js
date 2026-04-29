import request from '@/utils/request'

export function getAuditQueue(params) {
  return request({
    url: '/audit/queue',
    method: 'get',
    params
  })
}

export function getAuditHistory(params) {
  return request({
    url: '/audit/history',
    method: 'get',
    params
  })
}

export function getAuditRecordById(id) {
  return request({
    url: `/audit/${id}`,
    method: 'get'
  })
}

export function auditArticle(data) {
  return request({
    url: '/audit',
    method: 'put',
    data
  })
}

export function batchAudit(ids, auditStatus, auditComment) {
  return request({
    url: '/audit/batch',
    method: 'post',
    data: {
      ids,
      auditStatus,
      auditComment
    }
  })
}
