import request from '@/utils/request'

export function getMaterialList(params) {
  return request({
    url: '/material/page',
    method: 'get',
    params
  })
}

export function getMaterialAll(params) {
  return request({
    url: '/material/list',
    method: 'get',
    params
  })
}

export function createMaterial(data) {
  return request({
    url: '/material',
    method: 'post',
    data
  })
}

export function updateMaterial(data) {
  return request({
    url: '/material',
    method: 'put',
    data
  })
}

export function deleteMaterial(id) {
  return request({
    url: `/material/${id}`,
    method: 'delete'
  })
}
