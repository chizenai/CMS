import request from '@/utils/request'

export function getPermissionTree() {
  return request({
    url: '/permission/tree',
    method: 'get'
  })
}

export function getPermissionsByUserId(userId) {
  return request({
    url: `/permission/user/${userId}`,
    method: 'get'
  })
}

export function getPermissionsByRoleId(roleId) {
  return request({
    url: `/permission/role/${roleId}`,
    method: 'get'
  })
}

export function getPermissionCodesByUserId(userId) {
  return request({
    url: `/permission/codes/${userId}`,
    method: 'get'
  })
}

export function checkPermission(userId, permissionCode) {
  return request({
    url: '/permission/check',
    method: 'get',
    params: {
      userId,
      permissionCode
    }
  })
}

export function getPermissionById(id) {
  return request({
    url: `/permission/${id}`,
    method: 'get'
  })
}

export function createPermission(data) {
  return request({
    url: '/permission',
    method: 'post',
    data
  })
}

export function updatePermission(data) {
  return request({
    url: '/permission',
    method: 'put',
    data
  })
}

export function deletePermission(id) {
  return request({
    url: `/permission/${id}`,
    method: 'delete'
  })
}

export function assignPermissions(roleId, permissionIds) {
  return request({
    url: '/permission/assign',
    method: 'post',
    data: {
      roleId,
      permissionIds
    }
  })
}
