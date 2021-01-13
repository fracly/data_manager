import { axios } from '@/utils/request'

const api = {

  loginRecordList: '/log/login',
  downloadRecordList: '/log/download',
  searchRecordList: '/log/search',
  loginRecordExport: '/log/login/export',
  downloadRecordExport: '/log/download/export',
  searchRecordExport: '/log/search/export'
}

export default api

export function loginRecordList (parameter) {
    return axios({
        url: api.loginRecordList,
        method: 'get',
        params: parameter
    })
}

export function downloadRecordList (parameter) {
  return axios({
    url: api.downloadRecordList,
    method: 'get',
    params: parameter
  })
}

export function searchRecordList (parameter) {
  return axios({
    url: api.searchRecordList,
    method: 'get',
    params: parameter
  })
}

export function loginRecordExport (parameter) {
  const a = document.createElement('a')
  a.setAttribute('download', '')
  a.setAttribute('href', '/api' + api.loginRecordExport + `?name=${parameter['name']}&startTime=${parameter['startTime']}&endTime=${parameter['endTime']}`)
  console.log(a)
  a.click()
}

export function downloadRecordExport (parameter) {
  const a = document.createElement('a')
  a.setAttribute('download', '')
  a.setAttribute('href', '/api' + api.downloadRecordExport + `?name=${parameter['name']}&startTime=${parameter['startTime']}&endTime=${parameter['endTime']}`)
  console.log(a)
  a.click()
}

export function searchRecordExport (parameter) {
  const a = document.createElement('a')
  a.setAttribute('download', '')
  a.setAttribute('href', '/api' + api.searchRecordExport + `?name=${parameter['name']}&startTime=${parameter['startTime']}&endTime=${parameter['endTime']}`)
  console.log(a)
  a.click()
}
