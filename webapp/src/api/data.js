import { axios } from '@/utils/request'
import { tranferDomain2IP } from '@/utils/util'

const api = {
  dataCreate: '/data/create',
  dataDelete: '/data/delete',
  dataUpdate: '/data/update',
  dataSearch: '/data/search',
  dataDetail: '/data/detail',
  initDataSend: '/data/init-send',
  dataSend: '/data/send',
  dataSendStop: '/data/stop-send',
  dataCount: '/data/send/init',
  dataLineage: '/data/lineage',
  dataPreview: '/data/preview',
  dataDownload: '/data/download',
  dataAddColumn: '/data/add-column',
  dataDeleteLog: '/data/delete/log',
  updateStatus: '/data/updateStatus',
  dataDownloadLog: '/data/download/log',
  dataDownloadUrl: '/data/download/url',
  dataModifyColumn: '/data/modify-column',

  dataEncryptSearch: '/data/encrypt-search',
  dataEncrypt: '/data/encrypt',
  dataDecrypt: '/data/decrypt',
  dataBatchDecrypt: '/data/batch-decrypt',
  dataBatchEncrypt: '/data/batch-encrypt'
}

export default api

export function dataCreate (parameter) {
    return axios({
        url: api.dataCreate,
        method: 'post',
        data: parameter,
        timeout: 0
    })
}

export function dataSearch (parameter) {
    return axios({
        url: api.dataSearch,
        method: 'get',
        params: parameter,
        timeout: 0
    })
}

export function dataDetail (parameter) {
    return axios({
        url: api.dataDetail,
        method: 'get',
        params: parameter
    })
}

export function initDataSend (parameter) {
    return axios({
        url: api.initDataSend,
        method: 'get',
        params: parameter
    })
}

export function dataSend (parameter) {
    return axios({
        url: api.dataSend,
        method: 'get',
        params: parameter
    })
}

export function dataSendStop (parameter) {
    return axios({
        url: api.dataSendStop,
        method: 'get',
        params: parameter
    })
}

export function dataCount (parameter) {
    return axios({
        url: api.dataCount,
        method: 'get',
        params: parameter
    })
}

export function dataDelete (parameter) {
  return axios({
    url: api.dataDelete,
    method: 'get',
    params: parameter
  })
}

export function dataDeleteLog (parameter) {
    return axios({
        url: api.dataDeleteLog,
        method: 'get',
        params: parameter
    })
}

export function dataAddColumn (parameter) {
    return axios({
        url: api.dataAddColumn,
        method: 'post',
        data: parameter
    })
}

export function dataModifyColumn (parameter) {
  return axios({
    url: api.dataModifyColumn,
    method: 'post',
    data: parameter
  })
}

export function dataPreview (parameter) {
  return axios({
    url: api.dataPreview,
    method: 'get',
    params: parameter,
    timeout: 0
  })
}

export function dataDownloadUrl (parameter) {
  return axios({
    url: api.dataDownloadUrl + '?fileName=' + encodePath(parameter.fileName),
    method: 'get',
    timeout: 0
  })
}

export function dataUpdate (parameter) {
    return axios({
        url: api.dataUpdate,
        method: 'post',
        data: parameter
    })
}

export function dataDownload (parameter) {
  const a = document.createElement('a')
  a.setAttribute('download', '')
  a.setAttribute('href', '/api' + api.dataDownload + `?dataId=${parameter['dataId']}&downloadType=${parameter['downloadType']}`)
  console.log(a)
  a.click()
}

export function dataLineage (parameter) {
    return axios({
        url: api.dataLineage,
        method: 'get',
        params: parameter
    })
}

export function dataDownloadLog (parameter) {
  return axios({
    url: api.dataDownloadLog + '?dataId=' + parameter.dataId,
    method: 'get',
    timeout: 0
  })
}

export function hdfsDataDownload (parameter) {
  const b = document.createElement('a')
  b.setAttribute('download', '')
  b.setAttribute('href', parameter.url)
  console.log(b)
  b.click()
}

// encrypt
export function dataEncrypt (parameter) {
  return axios({
    url: api.dataEncrypt,
    method: 'get',
    params: parameter,
    timeout: 0
  })
}

export function dataDecrypt (parameter) {
  return axios({
    url: api.dataDecrypt,
    method: 'get',
    params: parameter,
    timeout: 0
  })
}

export function dataBatchEncrypt (parameter) {
  return axios({
    url: api.dataBatchEncrypt,
    method: 'get',
    params: parameter,
    timeout: 0
  })
}

export function dataBatchDecrypt (parameter) {
  return axios({
    url: api.dataBatchDecrypt,
    method: 'get',
    params: parameter,
    timeout: 0
  })
}

export function dataEncryptSearch (parameter) {
  return axios({
    url: api.dataEncryptSearch,
    method: 'get',
    params: parameter,
    timeout: 0
  })
}

// Hadoop functions
export function hdfsDataPreCreate (parameter) {
    return axios({
      url: '/webhdfs/v1' + encodePath(parameter.fileName) + '?op=CREATE&noredirect=true',
      method: 'put',
      baseURL: '/',
      timeout: 0
    })
}

export function hdfsDataActCreate (parameter) {
  return axios({
    url: tranferDomain2IP(parameter.url),
    data: parameter.data,
    method: 'put',
    baseURL: '/',
    timeout: 0
  })
}

export function hdfsDataUploadPercent (parameter) {
  return axios({
    url: '/webhdfs/v1' + encodePath(parameter.fileName) + '?op=GETFILESTATUS',
    method: 'get',
    baseURL: '/',
    timeout: 30000
  })
}

export function hdfsDataDelete (parameter) {
  return axios({
    url: '/webhdfs/v1' + encodePath(parameter.fileName) + '?op=DELETE&recursive=true',
    method: 'delete',
    baseURL: '/',
    timeout: 0
  })
}

export function hdfsDataDetail (parameter) {
  return axios({
    url: '/webhdfs/v1' + encodePath(parameter.fileName) + '?op=GETFILESTATUS',
    method: 'get',
    baseURL: '/',
    timeout: 30000
  })
}

function encodePath (absPath) {
  absPath = encodeURIComponent(absPath)
  const re = /%2F/g
  return absPath.replace(re, '/')
}
