import request from '@/utils/request'

export function getPermissionList(params) {
  return request({
    url: '/permission/page',
    method: 'get',
    params
  })
}

export function getPermissionTree() {
  return request({
    url: '/permission/tree',
    method: 'get'
  })
}

export function getUserMenus() {
  return request({
    url: '/permission/menus',
    method: 'get'
  })
}

export function getUserPermissionCodes() {
  return request({
    url: '/permission/codes',
    method: 'get'
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

export function getPermissionsByRole(roleId) {
  return request({
    url: `/permission/role/${roleId}`,
    method: 'get'
  })
}

export function assignPermissionsToRole(roleId, permissionIds) {
  return request({
    url: '/permission/role/assign',
    method: 'post',
    data: {
      roleId,
      permissionIds
    }
  })
}
