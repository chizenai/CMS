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

export function uploadFile(file, onProgress) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/material/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: onProgress
  })
}

export function uploadMultipleFiles(files, onProgress) {
  const formData = new FormData()
  files.forEach((file, index) => {
    formData.append('files', file)
  })
  
  return request({
    url: '/material/upload-multi',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: onProgress
  })
}
