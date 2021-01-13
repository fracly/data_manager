<template>
  <div>
    <a-card :bordered="false">
      <a-row>
        <a-col :sm="8" :xs="24">
          <head-info title="Hive库" v-bind:content="hiveCount" :bordered="true"/>
        </a-col>
        <a-col :sm="8" :xs="24">
          <head-info title="HBase库" v-bind:content="hbaseCount" :bordered="true"/>
        </a-col>
        <a-col :sm="8" :xs="24">
          <head-info title="HDFS数据目录" v-bind:content="hdfsCount"/>
        </a-col>
      </a-row>
    </a-card>
    <a-card
      style="margin-top: 24px"
      :bordered="false"
      title="数据源列表">

      <div slot="extra">
        <a-radio-group v-model="queryParam.type"  @change="list" buttonStyle="solid">
          <a-radio-button value="0" >全部</a-radio-button>
          <a-radio-button value="1">Hive</a-radio-button>
          <a-radio-button value="2">HBase</a-radio-button>
          <a-radio-button value="3">HDFS</a-radio-button>
        </a-radio-group>
        <a-input-search style="margin-left: 16px; width: 272px;" v-model="queryParam.name" placeholder="名称模糊匹配" @pressEnter="list" @search="list"/>
      </div>

      <div class="operate">
        <a-button type="dashed" style="width: 100%" icon="plus" @click="add()">添加</a-button>
      </div>
      <a-table :dataSource="datasourceList" :columns="columns" :pagination="{pageSize:5}">
        <span slot="action" slot-scope="text, record">
          <a @click="edit(record)">编辑</a>
          <a-divider type="vertical" />
          <a @click="del(record)">删除</a>
        </span>
      </a-table>
    </a-card>
  </div>
</template>

<script>
    import HeadInfo from '@/components/tools/HeadInfo'
    import DataSourceForm from './modules/DataSourceForm'
    import { queryDataSource, deleteDataSource, statisticDataSource } from '../../api/datasource'

    export default {
        name: 'DataSource',
        components: {
            HeadInfo,
            DataSourceForm
        },
        data () {
            return {
                datasourceList: [],
                hiveCount: '0个',
                hbaseCount: '0个',
                hdfsCount: '0个',
                // form
                form: this.$form.createForm(this),
                // 查询参数
                queryParam: {
                    type: '0',
                    name: ''
                },
                columns: [
                    {
                        title: '数据源名称',
                        dataIndex: 'name',
                        key: 'name',
                        scopedSlots: { customRender: 'name' }
                    },
                    {
                        title: '数据源类型',
                        dataIndex: 'type',
                        key: 'type',
                        customRender: (text) => {
                            if (text === 1) {
                                return 'Hive库'
                            } else if (text === 2) {
                                return 'HBase库'
                            } else {
                                return 'HDFS目录'
                            }
                        }
                    },
                    {
                        title: 'IP',
                        dataIndex: 'ip',
                        key: 'ip'
                    },
                    {
                        title: '端口',
                        key: 'port',
                        dataIndex: 'port'
                    },
                    {
                        title: '命名空间',
                        key: 'category1',
                        dataIndex: 'category1'
                    },
                    {
                        title: '描述',
                        key: 'description',
                        dataIndex: 'description'
                    },
                    {
                        title: '操作',
                        scopedSlots: { customRender: 'action' }
                    }
                ]
            }
        },
        methods: {
            edit (record) {
                this.$dialog(DataSourceForm,
                    // component props
                    {
                        record,
                        on: {
                            refresh: this.refresh
                        }
                    },
                    // modal props
                    {
                        title: '操作',
                        width: 600,
                        centered: true,
                        maskClosable: false
                    })
                this.list()
            },
            add () {
                this.$dialog(DataSourceForm,
                    {
                        on: {
                            refresh: this.refresh
                        }
                    },
                    {
                        title: '新增数据源',
                        width: 600,
                        centered: true,
                        maskClosable: false
                    })
            },
            del (record) {
                return new Promise(resolve => {
                    deleteDataSource({ 'id': record.id })
                        .then((res) => {
                            if (res.code === 0) {
                                this.$message.success(res.msg)
                            } else {
                                this.$message.error(res.msg)
                            }
                            resolve(true)
                        }).catch(err => {
                        console.log('fail', err)
                    }).finally(() => {
                        this.list()
                    })
                })
            },
            list () {
                this.statistic()
                return new Promise(resolve => {
                    queryDataSource(this.queryParam)
                        .then((res) => {
                            this.datasourceList = res.data
                            resolve(true)
                        }).catch(err => {
                        console.log('fail', err)
                    }).finally(() => {
                        // do nothing
                    })
                })
            },
            statistic () {
                return new Promise(resolve => {
                    statisticDataSource().then((res) => {
                        this.hiveCount = res.data.type1 ? res.data.type1 + '个' : '0个'
                        this.hbaseCount = res.data.type2 ? res.data.type2 + '个' : '0个'
                        this.hdfsCount = res.data.type3 ? res.data.type3 + '个' : '0个'
                    })
                })
            },
            // handler
            handleSubmit (e) {
                e.preventDefault()
                this.form.validateFields((err, values) => {
                    if (!err) {
                        // eslint-disable-next-line no-console
                        console.log('Received values of form: ', values)
                    }
                })
            },

            refresh () {
                this.list()
            }
        },
        mounted () {
            this.list()
        }
    }
</script>
