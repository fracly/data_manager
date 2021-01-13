import { axios } from '@/utils/request'

const api = {
    creatLabel: '/label/create',
    updateLabel: '/label/update',
    deleteLabel: '/label/delete',
    getLabelList: '/label/list-tree',
    getTop10LabelList: '/label/top10',
    getLabelFlatList: '/label/list-flat'
}

export default api

export function createLabel (parameter) {
    return axios({
        url: api.creatLabel,
        method: 'post',
        params: parameter
    })
}

export function updateLabel (parameter) {
  return axios({
    url: api.updateLabel,
    method: 'post',
    params: parameter
  })
}

export function deleteLabel (parameter) {
  return axios({
    url: api.deleteLabel,
    method: 'get',
    params: parameter
  })
}

export function getLabelList (parameter) {
    return axios({
        url: api.getLabelList,
        method: 'get',
        params: parameter
    })
}
export function getTop10LabelList (parameter) {
    return axios({
        url: api.getTop10LabelList,
        method: 'get',
        params: parameter
    })
}

export function getLabelFlatList (parameter) {
  return axios({
    url: api.getLabelFlatList,
    method: 'get',
    params: parameter
  })
}
