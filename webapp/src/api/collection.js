import { axios } from '@/utils/request'

const api = {
    jobList: '/data-collection/job-list',
    jobDelete: '/data-collection/job-delete',
    test: '/data-collection/test',
    sqoop: '/data-collection/sqoop',
    mongodb: '/data-collection/mongodb',
    file: '/data-collection/file',
    startAutoJob: '/data-collection/start-auto',
    stopAutoJob: '/data-collection/stop-auto',
    initAutoJob: '/data-collection/init-auto',
    targetList: '/data-collection/target-list'
}
export default api

export function jobList (parameter) {
    return axios({
        url: api.jobList,
        method: 'get',
        params: parameter
    })
}

export function jobDelete (parameter) {
  return axios({
    url: api.jobDelete,
    method: 'get',
    params: parameter
  })
}

export function test (parameter) {
    return axios({
        url: api.test,
        method: 'post',
        data: parameter,
        timeout: 3000
    })
}

export function sqoop (parameter) {
    return axios({
        url: api.sqoop,
        method: 'post',
        data: parameter,
        timeout: 0
    })
}

export function mongodb (parameter) {
    return axios({
        url: api.mongodb,
        method: 'post',
        data: parameter,
        timeout: 0
    })
}

export function file (parameter) {
    return axios({
        url: api.file,
        method: 'post',
        data: parameter,
        processData: false,
        timeout: 0
    })
}

export function startAutoJob (parameter) {
    return axios({
        url: api.startAutoJob,
        method: 'post',
        data: parameter,
        timeout: 2000
    })
}

export function stopAutoJob (parameter) {
    return axios({
        url: api.stopAutoJob,
        method: 'get',
        params: parameter,
        timeout: 0
    })
}

export function initAutoJob (parameter) {
    return axios({
        url: api.initAutoJob,
        method: 'get',
        params: parameter,
        timeout: 0
    })
}

export function targetList (parameter) {
    return axios({
        url: api.targetList,
        method: 'get',
        params: parameter,
        timeout: 0
    })
}
