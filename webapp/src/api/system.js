import { axios } from '@/utils/request'

const api = {

  systemPermissionList: '/system/search-permission',
  systemPermissionDisable: '/system/disable-permission',
  systemPermissionEnable: '/system/enable-permission',
  systemPermissionCreate: '/system/create-permission',
  systemPermissionUpdate: '/system/update-permission',
  systemRoleList: '/system/search-role',
  systemRoleDisable: '/system/disable-role',
  systemRoleEnable: '/system/enable-role',
  systemRoleCreate: '/system/create-role',
  systemRoleUpdate: '/system/update-role',
  systemUserList: '/system/search-user',
  systemUserDisable: '/system/disable-user',
  systemUserEnable: '/system/enable-user',
  systemUserCreate: '/system/create-user',
  systemUserUpdate: '/system/update-user',
  systemUserModifyPassword: '/system/user/modify-password',
  rolePermissionList: '/system/role-permission/list',
  rolePermissionUpdate: '/system/role-permission/update',
  userRoleList: '/system/user-role/list',
  userRoleUpdate: '/system/user-role/update',
  templateList: '/system/data-template/list',
  templateCreate: '/system/create-template',
  templateUpdate: '/system/update-template',
  templateDelete: '/system/delete-template'
}

export default api

export function systemPermissionList (parameter) {
    return axios({
        url: api.systemPermissionList,
        method: 'get',
        params: parameter
    })
}

export function systemPermissionDisable (parameter) {
  return axios({
    url: api.systemPermissionDisable,
    method: 'get',
    params: parameter
  })
}

export function systemPermissionEnable (parameter) {
  return axios({
    url: api.systemPermissionEnable,
    method: 'get',
    params: parameter
  })
}

export function systemPermissionCreate (parameter) {
  return axios({
    url: api.systemPermissionCreate,
    method: 'post',
    params: parameter
  })
}

export function systemPermissionUpdate (parameter) {
  return axios({
    url: api.systemPermissionUpdate,
    method: 'post',
    params: parameter
  })
}

export function systemRoleList (parameter) {
  return axios({
    url: api.systemRoleList,
    method: 'get',
    params: parameter
  })
}

export function systemRoleDisable (parameter) {
  return axios({
    url: api.systemRoleDisable,
    method: 'get',
    params: parameter
  })
}

export function systemRoleEnable (parameter) {
  return axios({
    url: api.systemRoleEnable,
    method: 'get',
    params: parameter
  })
}

export function systemRoleCreate (parameter) {
  return axios({
    url: api.systemRoleCreate,
    method: 'post',
    params: parameter
  })
}

export function systemRoleUpdate (parameter) {
  return axios({
    url: api.systemRoleUpdate,
    method: 'post',
    params: parameter
  })
}

export function systemUserList (parameter) {
  return axios({
    url: api.systemUserList,
    method: 'get',
    params: parameter
  })
}

export function systemUserDisable (parameter) {
  return axios({
    url: api.systemUserDisable,
    method: 'get',
    params: parameter
  })
}

export function systemUserEnable (parameter) {
  return axios({
    url: api.systemUserEnable,
    method: 'get',
    params: parameter
  })
}

export function systemUserCreate (parameter) {
  return axios({
    url: api.systemUserCreate,
    method: 'post',
    params: parameter
  })
}

/**
 * 更新用户信息
 */
export function systemUserUpdate (parameter) {
  return axios({
    url: api.systemUserUpdate,
    method: 'post',
    params: parameter
  })
}

/**
 * 重设用户密码
 */
export function systemUserModifyPassword (parameter) {
  return axios({
    url: api.systemUserModifyPassword,
    method: 'post',
    params: parameter
  })
}

/**
 * 获取角色权限列表
 */
export function rolePermissionList (parameter) {
  return axios({
    url: api.rolePermissionList,
    method: 'get',
    params: parameter
  })
}

/**
 * 更新角色的权限
 */
export function rolePermissionUpdate (parameter) {
  return axios({
      url: api.rolePermissionUpdate,
      method: 'post',
      params: parameter
   })
}

/**
 * 获取用户角色列表
 */
export function userRoleList (parameter) {
  return axios({
    url: api.userRoleList,
    method: 'get',
    params: parameter
  })
}

/**
 * 更新用户的角色
 */
export function userRoleUpdate (parameter) {
  return axios({
    url: api.userRoleUpdate,
    method: 'post',
    params: parameter
  })
}

export function systemTemplateList (parameter) {
  return axios({
    url: api.templateList,
    method: 'get',
    params: parameter
  })
}

export function systemTemplateCreate (parameter) {
  return axios({
    url: api.templateCreate,
    method: 'post',
    data: parameter
  })
}

export function systemTemplateUpdate (parameter) {
  return axios({
    url: api.templateUpdate,
    method: 'post',
    data: parameter
  })
}

export function systemTemplateDelete (parameter) {
  return axios({
    url: api.templateDelete,
    method: 'get',
    params: parameter
  })
}
