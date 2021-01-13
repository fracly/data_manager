import { axios } from '@/utils/request'

const api = {
  createDataSource: '/datasource/create',
  updateDataSource: '/datasource/update',
  deleteDataSource: '/datasource/delete',
  queryDataSourceById: '/datasource/queryById',
  queryDataSource: '/datasource/query',
  statisticDataSource: '/datasource/statistic',
  testDataSource: '/datasource/test'
}

export default api

export function createDataSource (parameter) {
  return axios({
    url: api.createDataSource,
    method: 'post',
    params: parameter
  })
}

export function updateDataSource (parameter) {
  return axios({
    url: api.updateDataSource,
    method: 'post',
    params: parameter
  })
}

export function deleteDataSource (parameter) {
  return axios({
    url: api.deleteDataSource,
    method: 'get',
    params: parameter
  })
}

export function queryDataSourceById (parameter) {
  return axios({
    url: api.queryDataSourceById,
    method: 'get',
    params: parameter
  })
}

export function queryDataSource (parameter) {
  return axios({
    url: api.queryDataSource,
    method: 'get',
    params: parameter
  })
}

export function statisticDataSource (parameter) {
  return axios({
    url: api.statisticDataSource,
    method: 'get',
    params: parameter
  })
}

export function testDataSource (parameter) {
  return axios({
    url: api.testDataSource,
    method: 'post',
    params: parameter
  })
}
