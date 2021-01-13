import { axios } from '@/utils/request'

const api = {
    getCapacity: '/analysis/capacity',
    getDataCount: '/analysis/data-count',
    getDataSourceCount: '/analysis/datasource-count',
    getDownloadCount: '/analysis/download-count',
    getIncreaseByDay: '/analysis/increase-by-day',
    getLabelTop10: '/analysis/count-by-label',
    getSearchKeyWordTop10: '/analysis/search-keyword',
    getDataTypePercentage: '/analysis/data-type-percentage',
    getSearchUserByDay: '/analysis/search-user-by-day',
    getSearchCountByDay: '/analysis/search-count-by-day',
    getReportDownload: '/analysis/report/download'
}
export default api

export function getCapacity (parameter) {
    return axios({
        url: api.getCapacity,
        method: 'get',
        params: parameter
    })
}

export function getDataCount (parameter) {
  return axios({
    url: api.getDataCount,
    method: 'get',
    params: parameter
  })
}

export function getDataSourceCount (parameter) {
  return axios({
    url: api.getDataSourceCount,
    method: 'get',
    params: parameter
  })
}
export function getDownloadCount (parameter) {
  return axios({
    url: api.getDownloadCount,
    method: 'get',
    params: parameter
  })
}

export function getIncreaseByDay (parameter) {
  return axios({
    url: api.getIncreaseByDay,
    method: 'get',
    params: parameter
  })
}

export function getLabelTop10 (parameter) {
  return axios({
    url: api.getLabelTop10,
    method: 'get',
    params: parameter
  })
}

export function getSearchKeyWordTop10 (parameter) {
  return axios({
    url: api.getSearchKeyWordTop10,
    method: 'get',
    params: parameter
  })
}

export function getDataTypePercentage (parameter) {
  return axios({
    url: api.getDataTypePercentage,
    method: 'get',
    params: parameter
  })
}

export function getSearchUserByDay (parameter) {
  return axios({
    url: api.getSearchUserByDay,
    method: 'get',
    params: parameter
  })
}

export function getSearchCountByDay (parameter) {
  return axios({
    url: api.getSearchCountByDay,
    method: 'get',
    params: parameter
  })
}

export function getReportDownload (parameter) {
    const a = document.createElement('a')
    a.setAttribute('download', '')
    a.setAttribute('href', '/api' + api.getReportDownload + `?startDate=${parameter['startDate']}&endDate=${parameter['endDate']}`)
    console.log(a)
    a.click()
}
