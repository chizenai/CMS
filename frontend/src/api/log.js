import request from '@/utils/request'

export function getLogList(params) {
  return request({
    url: '/log/page',
    method: 'get',
    params
  })
}

export function getLogAll() {
  return request({
    url: '/log/list',
    method: 'get'
  })
}

export function getLogById(id) {
  return request({
    url: `/log/${id}`,
    method: 'get'
  })
}

export function deleteLog(id) {
  return request({
    url: `/log/${id}`,
    method: 'delete'
  })
}

export function clearLog() {
  return request({
    url: '/log/clear',
    method: 'delete'
  })
}
