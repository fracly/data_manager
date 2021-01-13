<template>
  <a-card :bordered="false" class="data">
    <a-row :gutter="8">
      <a-col :span="4">
        <s-tree
          :dataSource="dataTree"
          :openKeys.sync="openKeys"
          :search="true"
          @click="handleClick"
          @add="handleAdd"
          @titleClick="handleTitleClick"
          @handleSearch="handleSearch"></s-tree>
        <!--          :defaultSelectedKeys="defaultKey"-->
      </a-col>
      <a-col :span="20">
        <a-spin :spinning="tableSpinning">
          <a-table
            :columns="columns"
            :dataSource="dataList"
            :pagination="pagination"
            rowKey="key">
            <span slot="label" slot-scope="text">
              <a-tag v-for="tag in text" :key="tag.id" color="blue">
                {{ tag.name }}
              </a-tag>
            </span>
            <span slot="serial" slot-scope="text, record, index">
              {{ index + 1 }}
            </span>
            <span slot="time" slot-scope="text">
              {{ text | moment }}
            </span>
            <span slot="dataName" slot-scope="text, record">
              <template>
                <span v-if="record.type === '3'">
                  {{ text }}
                </span>
                <span v-else> {{ text.split('/')[text.split('/').length] }}</span>
              </template>
            </span>
            <span slot="action" slot-scope="text, record">
              <template>
                <a @click="handleDetail(record)">详情</a>
                <a-divider type="vertical"/>
                <a @click="handleEdit(record)">编辑</a>
                <a-divider type="vertical"/>
                <a @click="handleDelete(record)">删除</a>
              </template>
            </span>
          </a-table>
        </a-spin>
      </a-col>
    </a-row>

    <data-modal ref="modal" @ok="handleSaveOk" @close="handleSaveClose" />
    <data-detail-hive-modal ref="modal2" @detail="handleDetail"></data-detail-hive-modal>
    <data-detail-hdfs-modal ref="modal3"></data-detail-hdfs-modal>
    <data-detail-h-base-modal ref="modal4"></data-detail-h-base-modal>

  </a-card>
</template>

<script>
import STree from '@/components/Tree/Tree'
import { STable } from '@/components'
import DataModal from './modules/DataModal'
import DataDetailHiveModal from './modules/DataDetailHiveModal'
import DataDetailHdfsModal from './modules/DataDetailHdfsModal'
import DataDetailHBaseModal from './modules/DataDetailHBaseModal'
import { getDataSourceTree, getDataSourceDataList } from '@/api/manage'
import { dataDelete, dataDetail, hdfsDataDelete, hdfsDataDetail } from '@/api/data'
import $ from 'jquery'

const openKeys = ['key-01', 'key-02', 'key-03']

export default {
    name: 'DataList',
    components: {
        STable,
        STree,
        DataModal,
        DataDetailHiveModal,
        DataDetailHdfsModal,
        DataDetailHBaseModal
    },
    data () {
        return {
            // tree 相关变量
            openKeys,
            dataTree: [],
            // table 相关变量
            tableSpinning: false,
            columns: [
                {
                    title: '#',
                    scopedSlots: { customRender: 'serial' },
                    width: '5%'
                },
                {
                    title: '名称',
                    dataIndex: 'name',
                    width: '10%'
                },
                {
                    title: '数据名',
                    dataIndex: 'dataName',
                    needTotal: true,
                    width: '12%',
                    customRender: (text, record, index) => {
                        if (record.type === 3) {
                            const array = text.split('/')
                            return array[array.length - 1]
                        } else {
                            return text
                        }
                    }
                },
                {
                    title: '标签',
                    dataIndex: 'labelList',
                    scopedSlots: {
                        customRender: 'label'
                    },
                    width: '15%'
                },
                {
                    title: '描述',
                    dataIndex: 'description',
                    sorter: true,
                    needTotal: true,
                    width: '15%'
                },
                {
                    title: '更新时间',
                    dataIndex: 'updateTime',
                    sorter: true,
                    scopedSlots: { customRender: 'time' },
                    width: '13%'
                },
                {
                    title: '状态',
                    dataIndex: 'status',
                    needTotal: true,
                    customRender: (text) => {
                        if (text === 0) {
                            return '正常'
                        } else if (text === 1) {
                            return '损坏'
                        } else if (text === 2) {
                            return '上传中'
                        } else {
                            return '销毁'
                        }
                    },
                    width: '5%'
                },
                {
                    title: '类型',
                    dataIndex: 'type',
                    needTotal: true,
                    customRender: (text) => {
                        if (text === 1) {
                            return 'Hive'
                        } else if (text === 2) {
                            return 'HBase'
                        } else if (text === 3) {
                            return 'HDFS'
                        } else {
                            return '未知'
                        }
                    },
                    width: '6%'
                },
                {
                    title: '创建者',
                    dataIndex: 'owner',
                    width: '6%'
                },
                {
                    title: '操作',
                    dataIndex: 'action',
                    key: 'action',
                    width: '12%',
                    scopedSlots: { customRender: 'action' }
                }
            ],
            dataList: [],
            // 查询参数
            queryParam: {
              dataSourceId: 0,
              pageNo: 1,
              pageSize: 10,
              searchVal: ''
            },
            defaultKey: [],
            selectedRowKeys: [],
            selectedRows: [],
            pagination: {
                total: 20,
                pageSize: 10,
                showTotal: (total) => `共${total}条`,
                showSizeChanger: true,
                pageSizeOptions: ['10', '20', '50', '100'],
                onChange: (page, pageSize) => {
                  this.queryParam.pageNo = page
                  this.queryParam.pageSize = pageSize
                  this.getData()
                },
                onShowSizeChange: (current, size) => {
                  this.queryParam.pageNo = current
                  this.queryParam.pageSize = size
                  this.getData()
                }
            }

        }
    },
    created () {
        getDataSourceTree().then(res => {
            this.dataTree = res.data
            this.getData()
        })
    },
    methods: {
        handleClick ({ item, key, keyPath }) {
          this.queryParam = {
                dataSourceId: (key).substr(7)
            }
           this.getData()
        },
        handleAdd (item) {
          item.create = true
          this.$refs.modal.add(item)
        },
        handleEdit (item) {
            item.create = false
            item.labels = []
            for (let i = 0; i < item.labelList.length; i++) {
              item.labels.push(item.labelList[i].id)
            }
            this.$refs.modal.edit(item)
        },
        handleSearch () {
            const searchVal = $('#dataGlobalSearchId').val()
            this.queryParam.searchVal = searchVal
            this.getData()
        },
        handleDelete: function (item) {
          const _this = this
          this.tableSpinning = true
          if (item.type === 3) {
              dataDelete({ 'id': item.id }).then(res => {
                  if (res.code === 0) {
                      hdfsDataDelete({ fileName: item.dataName }).then(() => {
                          _this.$message.success(res.msg)
                      }).catch(err => {
                          _this.$message.error('hdfs文件删除失败:' + err)
                          _this.tableSpinning = false
                      })
                  } else {
                      _this.$message.error(res.msg)
                  }
                  _this.getData()
                  _this.tableSpinning = false
              }).catch(err => {
                  _this.$message.error(err)
                  _this.tableSpinning = false
              })
        } else {
          dataDelete({ 'id': item.id }).then(res => {
            if (res.code === 0) {
              this.$message.success(res.msg)
            } else {
              this.$message.error(res.msg)
            }
            this.getData()
            _this.tableSpinning = false
          }).catch(err => {
              this.$message.error('删除数据失败' + err)
              _this.tableSpinning = false
          })
        }
      },
        encodePath: function (absPath) {
            absPath = encodeURIComponent(absPath)
            const re = /%2F/g
            return absPath.replace(re, '/')
        },
        handleDetail (item) {
            const _this = this
            if (item.type === 3) {
                hdfsDataDetail({ fileName: item.dataName }).then((res) => {
                    _this.$refs.modal3.show(item.dataName, res.FileStatus.length)
                }).catch(() => {
                    this.$message.error('文件已丢失，请删除！')
                })
            } else {
                dataDetail({ 'dataId': item.id }).then(res => {
                  if (res.code === 0) {
                    if (item.type === 1) {
                        this.$refs.modal2.show(res.data, item)
                    } else {
                      this.$refs.modal4.show(res.data, item)
                    }
                  } else {
                    this.$message.error(res.msg)
                  }
                })
            }
        },
        handleTitleClick (item) {
            console.log('handleTitleClick', item)
        },
        handleSaveOk () {
            // 刷新当前数据
            this.getData()
        },
        handleSaveClose () {
        },
        onSelectChange (selectedRowKeys, selectedRows) {
            this.selectedRowKeys = selectedRowKeys
            this.selectedRows = selectedRows
        },
        getData () {
          getDataSourceDataList(this.queryParam).then((res) => {
            this.dataList = res.data
            this.pagination.total = res.dataMap.total
          }).catch(err => {
            this.$message.error('获取数据源的数据失败' + err)
          })
        }
    }
}
</script>

<style lang="less">
    .custom-tree {

        /deep/ .ant-menu-item-group-title {
            position: relative;

            &:hover {
                .btn {
                    display: block;
                }
            }
        }

        /deep/ .ant-menu-item {
            &:hover {
                .btn {
                    display: block;
                }
            }
        }

        /deep/ .btn {
            display: none;
            position: absolute;
            top: 0;
            right: 10px;
            width: 20px;
            height: 40px;
            line-height: 40px;
            z-index: 1050;

            &:hover {
                transform: scale(1.2);
                transition: 0.5s all;
            }
        }
    }

    .data .ant-table-placeholder {
        min-height: 600px;
    }
</style>
