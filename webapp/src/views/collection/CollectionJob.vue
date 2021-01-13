<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="4" :sm="12">
            <a-form-item label="任务状态">
              <a-select v-model="queryParam.status" placeholder="请选择" default-value="999">
                <a-select-option value="999">全部</a-select-option>
                <a-select-option value="9">杀死</a-select-option>
                <a-select-option value="2">运行中</a-select-option>
                <a-select-option value="-1">失败</a-select-option>
                <a-select-option value="1">完成</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" @click="handleSearch()">查询</a-button>
              <a-button style="margin-left: 8px" @click="() => queryParam = {}">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-table
      ref="table"
      size="default"
      :columns="columns"
      :dataSource="jobList"
      showPagination="auto"
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

      <span slot="action" slot-scope="text, record">
        <template>
          <a @click="handleDelete(record.id)">删除</a>
        </template>
      </span>
      <span slot="time" slot-scope="text">
          {{ text | moment }}
      </span>
    </a-table>
  </a-card>
</template>

<script>
import { jobList, jobDelete } from '@/api/collection'

export default {
  name: 'TableList',
  components: {
  },
  data () {
    return {
      // 查询参数
      queryParam: {
          status: '999',
          name: ''
      },
      // 表头
      columns: [
        {
          title: '#',
          dataIndex: 'id'
          // scopedSlots: { customRender: 'serial' }
        },
        {
          title: '输入源类型',
          dataIndex: 'inputType',
          customRender: (text) => {
              if (text === 1) {
                  return 'Mysql'
              } else if (text === 2) {
                  return 'MongoDB'
              } else if (text === 3) {
                  return 'SqlServer'
              } else if (text === 4) {
                  return 'DB2'
              } else if (text === 9) {
                  return '上传文件'
              }
          }
        },
        {
          title: '输出源类型',
          dataIndex: 'outputType',
          sorter: true,
          needTotal: true,
          customRender: (text) => {
              if (text === 1) {
                  return 'Hive表'
              } else if (text === 2) {
                  return 'HBase表'
              } else if (text === 3) {
                  return 'HDFS目录'
              }
          }
        },
        {
            title: '输出源数据',
            dataIndex: 'outputName',
            sorter: true,
            needTotal: true
        },
        {
          title: '状态',
          dataIndex: 'status',
          scopedSlots: { customRender: 'status' },
          customRender: (text) => {
              if (text === 1) {
                  return '完成'
              } else if (text === 2) {
                  return '正在运行'
              } else if (text === -1) {
                  return '失败'
              } else if (text === 9) {
                  return '被杀死'
              } else if (text === 0) {
                  return '初始化'
              }
          }
        },
        {
          title: '开始时间',
          dataIndex: 'startTime',
          sorter: true,
          scopedSlots: { customRender: 'time' }
        },
        {
          title: '操作',
          dataIndex: 'action',
          width: '150px',
          scopedSlots: { customRender: 'action' }
        }
      ],

      jobList: []
    }
  },
  filters: {
  },
  created () {
    this.list()
  },
  methods: {
    handleDelete (id) {
        jobDelete({ 'id': id }).then(res => {
            this.$message.info('删除成功')
            this.list()
        })
    },
    handleSearch () {
      this.list()
    },
    handleOk () {
      this.$refs.table.refresh()
    },
    list () {
        jobList(this.queryParam).then(res => {
            this.jobList = res.data
        })
    }
  }
}
</script>
