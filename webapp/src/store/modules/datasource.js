import { createDataSource, updateDataSource, deleteDataSource, queryDataSourceById, queryDataSourceByName } from '@/api/datasource'

const datasource = {
  state: {},
  mutations: {},
  actions: {
    // 创建数据源
    CreateDataSource ({ commit }, dataSource) {
      return new Promise((resolve, reject) => {
        createDataSource(dataSource).then(response => {
          console.log('创建成功！')
          resolve()
        })
      }).catch(error => {
        // eslint-disable-next-line no-undef
        reject(error)
      })
    }
  },

  // 更新数据源
  UpdateDataSource ({ commit }, dataSource) {
    return new Promise((resolve, reject) => {
      updateDataSource(dataSource).then(response => {
        console.log('更新成功')
        resolve()
      })
    }).catch(error => {
      // eslint-disable-next-line no-undef
      reject(error)
    })
  },

  // 删除数据源
  DeleteDataSource ({ commit }, dataSource) {
    return new Promise((resolve, reject) => {
      deleteDataSource(dataSource).then(response => {
        console.log('删除成功')
        resolve()
      })
    }).catch(error => {
      // eslint-disable-next-line no-undef
      reject(error)
    })
  },

  // 查询数据源
  QueryDataSourceById ({ commit }, dataSourceId) {
    return new Promise((resolve, reject) => {
      queryDataSourceById(dataSourceId).then(response => {
        console.log('查询成功')
        resolve()
      })
    }).catch(error => {
      // eslint-disable-next-line no-undef
      reject(error)
    })
  },
  QueryDataSourceByName ({ commit }, dataSourceName) {
    return new Promise((resolve, reject) => {
      queryDataSourceByName(dataSourceName).then(response => {
        console.log('查询成功')
        resolve()
      })
    }).catch(error => {
      // eslint-disable-next-line no-undef
      reject(error)
    })
  }
}

export default datasource
