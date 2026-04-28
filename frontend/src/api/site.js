import request from '@/utils/request'

export function getSiteList() {
  return request({
    url: '/site/list',
    method: 'get'
  })
}

export function getSiteById(id) {
  return request({
    url: `/site/${id}`,
    method: 'get'
  })
}

export function createSite(data) {
  return request({
    url: '/site',
    method: 'post',
    data
  })
}

export function updateSite(data) {
  return request({
    url: '/site',
    method: 'put',
    data
  })
}

export function deleteSite(id) {
  return request({
    url: `/site/${id}`,
    method: 'delete'
  })
}
