<template>
  <div>
    <a-card>
      <div>
        <a-form :form="form" layout="inline">
          <a-row :gutter="48">
            <a-col :md="5" :sm="12">
              <a-form-item label="数据源">
                <a-select
                  style="width:200px"
                  placeholder="请选择数据源"
                  v-model="queryParam.dataSourceId">
                  <a-select-option value="0">全部</a-select-option>
                  <a-select-option
                    v-for="(item) in dataSourceList"
                    :key="item.id"
                    :value="item.id">{{ item.name }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="16">
              <a-form-item label="数据名称">
                <a-input
                  style="width:200px"
                  placeholder="名称模糊匹配"
                  v-model="queryParam.searchVal">
                </a-input>

              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="12">
              <a-button type="primary" @click="search">搜索</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <a-divider style="margin-bottom: 22px"/>
      <a-spin :spinning="tableSpinning">
        <a-table
          :columns="columns"
          :dataSource="dataList"
          :pagination="pagination"
          rowKey="name">
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
                <a-switch :disabled="disabled" v-if="record.zzEncrypt === 0" checked-children="是" un-checked-children="否" @click="onChange($event, record)"/>
                <a-switch v-else default-checked checked-children="是" un-checked-children="否" @click="onChange($event, record)"/>
              </template>
            </span>
        </a-table>
      </a-spin>
    </a-card>
  </div>
</template>

<script>
import { PageView } from '@/layouts'
import { STable } from '@/components'
import { queryDataSource } from '@/api/datasource'
import { dataEncryptSearch, dataEncrypt, dataDecrypt, dataBatchEncrypt, dataBatchDecrypt } from '@/api/data'
import { getDataSourceDataList } from '@/api/manage'

    export default {
        components: {
            PageView,
            STable
        },
        data () {
            return {
                disabled: true,
                dataSourceList: [],
                dataList: [],
                queryParam: {
                    dataSourceId: '0',
                    pageNo: 1,
                    pageSize: 10,
                    searchVal: ''
                },
                lineage: Object,
                form: {},
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
                        title: '是否加密',
                        dataIndex: 'action',
                        key: 'action',
                        width: '12%',
                        scopedSlots: { customRender: 'action' }
                    }
                ],
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
        methods: {
            getDataSourceList () {
                queryDataSource({ 'type': 3 }).then(res => {
                    if (res.code === 0) {
                        this.dataSourceList = res.data
                    } else {
                        console.log('获取数据源列表失败')
                    }
                })
            },
            getDataList (dataSourceId) {
                this.dataList = []
                getDataSourceDataList({ 'dataSourceId': dataSourceId }).then(res => {
                    if (res.code === 0) {
                        this.dataList = res.data
                    } else {
                        console.log('获取数据列表失败')
                    }
                })
            },
            getData () {
                dataEncryptSearch(this.queryParam).then((res) => {
                    this.dataList = res.data
                    this.pagination.total = res.dataMap.total
                }).catch(err => {
                    this.$message.error('获取数据源的数据失败' + err)
                })
            },
            encrypt () {
                dataBatchEncrypt().then((res) => {
                    this.loading = false
                    if (res.code === 0) {
                        this.$message.success(res.msg)
                    } else {
                        this.$message.error(res.msg)
                    }
                    this.$router.go(0)
                })
            },
            decrypt () {
                dataBatchDecrypt().then((res) => {
                    this.loading = false
                    if (res.code === 0) {
                        this.$message.success(res.msg)
                    } else {
                        this.$message.error(res.msg)
                    }
                    this.$router.go(0)
                })
            },
            search () {
                this.getData()
            },
            onChange (e, record) {
                if (e === true) {
                    dataEncrypt({ 'dataId': record.id }).then((res) => {
                        if (res.code === 0) {
                            this.$message.success(res.msg)
                        } else {
                            this.$message.error(res.msg)
                        }
                    })
                } else {
                    dataDecrypt({ 'dataId': record.id }).then((res) => {
                        if (res.code === 0) {
                            this.$message.success(res.msg)
                        } else {
                            this.$message.error(res.msg)
                        }
                    })
                }
            }
        },
        filters: {
        },
        mounted () {
            this.getDataSourceList()
            this.getData()
        },
        computed: {
            title () {
                return this.$route.meta.title
            }
        }

    }
</script>

<style lang="less" scoped>
  .title {
    color: rgba(0, 0, 0, .85);
    font-size: 16px;
    font-weight: 500;
    margin-bottom: 16px;
  }
  canvas {
    outline: none;
  }
</style>
