import request from '@/utils/request'

export function getMenuTree() {
  return request({
    url: '/menu/tree',
    method: 'get'
  })
}

export function getMenusByUserId(userId) {
  return request({
    url: `/menu/user/${userId}`,
    method: 'get'
  })
}

export function getMenusByRoleId(roleId) {
  return request({
    url: `/menu/role/${roleId}`,
    method: 'get'
  })
}

export function getMenuById(id) {
  return request({
    url: `/menu/${id}`,
    method: 'get'
  })
}

export function createMenu(data) {
  return request({
    url: '/menu',
    method: 'post',
    data
  })
}

export function updateMenu(data) {
  return request({
    url: '/menu',
    method: 'put',
    data
  })
}

export function deleteMenu(id) {
  return request({
    url: `/menu/${id}`,
    method: 'delete'
  })
}

export function assignMenus(roleId, menuIds) {
  return request({
    url: '/menu/assign',
    method: 'post',
    data: {
      roleId,
      menuIds
    }
  })
}
