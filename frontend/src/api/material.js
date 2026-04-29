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

export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/material/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function uploadMultipleFiles(files) {
  const formData = new FormData()
  for (let i = 0; i < files.length; i++) {
    formData.append('files', files[i])
  }
  return request({
    url: '/material/upload-multiple',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
