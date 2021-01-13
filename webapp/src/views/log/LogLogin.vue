<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="4" :sm="12">
            <a-form-item label="用户姓名">
              <a-input v-model="queryParam.name" placeholder="姓名模糊匹配" ></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="登录时间范围">
              <a-range-picker
                style="max-width: 468px; width: 100%;"
                showTime
                format="YYYY-MM-DD HH:mm:ss"
                @change="handleDate"
                v-model="date">
              </a-range-picker>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" @click="handleSearch()">查询</a-button>
              <a-button type="primary" style="margin-left: 8px" @click="handleExport()">导出</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-table
      ref="table"
      size="default"
      :columns="columns"
      :dataSource="recordList"
      :pagination="pagination"
    >
      <span slot="serial" slot-scope="text, record, index">
        {{ index + 1 }}
      </span>
      <span slot="status" slot-scope="text">
        <a-badge :status="text | statusTypeFilter" :text="text | statusFilter" />
      </span>
      <span slot="description" slot-scope="text">
        <ellipsis :length="4" tooltip>{{ text }}</ellipsis>
      </span>
      <span slot="time" slot-scope="text">
          {{ text | moment }}
      </span>
    </a-table>
  </a-card>
</template>

<script>
    import { loginRecordList, loginRecordExport } from '@/api/log'

    const statusMap = {
        1: {
            status: 'success',
            text: '正常'
        },
        9: {
            status: 'default',
            text: '禁用'
        },
        8: {
            status: 'warning',
            text: '锁定'
        }
    }
    export default {
        name: 'LogLogin',
        components: {
        },
        data () {
            return {
                // 查询参数
                queryParam: {
                    startTime: '',
                    endTime: '',
                    name: '',
                    pageNo: 1,
                    pageSize: 10
                },
                // 表头
                columns: [
                    {
                        title: '#',
                        dataIndex: 'id',
                        scopedSlots: { customRender: 'serial' }
                    },
                    {
                        title: '姓名',
                        dataIndex: 'name'
                    },
                    {
                        title: '账号',
                        dataIndex: 'username'
                    },
                    {
                        title: '登陆时间',
                        dataIndex: 'loginTime',
                        scopedSlots: { customRender: 'time' }
                    },
                    {
                        title: '登陆IP',
                        dataIndex: 'loginIp'
                    }
                ],
                recordList: [],
                date: [],
                pagination: {
                    total: 0,
                    pageSize: 10,
                    showTotal: (total) => `共${total}条`,
                    showSizeChanger: true,
                    pageSizeOptions: ['10', '20', '50', '100'],
                    onChange: (page, pageSize) => {
                        this.queryParam.pageNo = page
                        this.queryParam.pageSize = pageSize
                        this.list()
                    },
                    onShowSizeChange: (current, size) => {
                        this.queryParam.pageNo = current
                        this.queryParam.pageSize = size
                        this.list()
                    }
                }
            }
        },
        filters: {
            statusFilter (type) {
                return statusMap[type].text
            },
            statusTypeFilter (type) {
                return statusMap[type].status
            }
        },
        created () {
            this.list()
        },
        methods: {
            handleSearch () {
                this.list()
            },
            handleExport () {
                this.url = ''
                loginRecordExport(this.queryParam)
            },
            handleDate: function (date, dateString) {
                this.queryParam.startTime = dateString[0]
                this.queryParam.endTime = dateString[1]
                this.list()
            },
            list () {
                loginRecordList(this.queryParam).then(res => {
                    this.recordList = res.data
                })
            }
        }
    }
</script>
