<template>
  <a-card :bordered="false" class="data">
    <a-form :form="form" layout="inline"  @submit="handleSubmit">
      <a-form-item label="模板名称">
        <a-input
                placeholder="名称模糊匹配"
                v-model="queryParam.name"
        />
      </a-form-item>
      <a-form-item label="模板代码">
        <a-input
                v-model="queryParam.code"
                placeholder="代码模糊匹配"
        >
        </a-input>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" @click="handleSearch()">
           查询
        </a-button>
        <a-button type="primary" html-type="submit" style="margin-left: 20px;" @click="handleAdd">
          添加模板
        </a-button>
      </a-form-item>
    </a-form>
    <a-spin :spinning="tableSpinning" style="margin-top: 20px;">
      <a-table
              :columns="columns"
              :dataSource="templateList"
              :pagination="pagination"
              rowKey="name">
        <span slot="serial" slot-scope="text, record, index">
          {{ index + 1 }}
        </span>
        <span slot="time" slot-scope="text">
          {{ text | moment }}
        </span>
        <span slot="action" slot-scope="text, record">
          <template>
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical"/>
            <a @click="handleDelete(record)">删除</a>
          </template>
        </span>
      </a-table>
    </a-spin>
    <create-template-modal ref="modal" @ok="handleSaveOk" @close="handleSaveClose" />

  </a-card>
</template>

<script>
  import STree from '@/components/Tree/Tree'
  import { STable } from '@/components'
  import CreateTemplateModal from './modules/CreateTemplateModal'
  import { systemTemplateList, systemTemplateDelete } from '../../api/system'
  import $ from 'jquery'

  export default {
    name: 'DataList',
    components: {
      STable,
      STree,
      CreateTemplateModal
    },
    data () {
      return {
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
            title: '代码',
            dataIndex: 'code',
            width: '12%'
          },
          {
            title: '创建时间',
            dataIndex: 'createTime',
            scopedSlots: { customRender: 'time' },
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
            title: '创建者',
            dataIndex: 'creatorName',
            needTotal: true,
            width: '15%'
          },
          {
            title: '操作',
            dataIndex: 'action',
            key: 'action',
            width: '12%',
            scopedSlots: { customRender: 'action' }
          }
        ],
        templateList: [],
        // 查询参数
        queryParam: {
          name: '',
          code: ''
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
    },
    mounted () {
      this.getData()
    },
    methods: {
      handleClick ({ item, key, keyPath }) {
        this.queryParam = {
          dataSourceId: (key).substr(7)
        }
        this.getData()
      },
      handleAdd () {
        this.$refs.modal.add()
      },
      handleEdit (item) {
        item.create = false
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
        systemTemplateDelete({ 'id': item.id }).then(res => {
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
      },
      encodePath: function (absPath) {
        absPath = encodeURIComponent(absPath)
        const re = /%2F/g
        return absPath.replace(re, '/')
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
        systemTemplateList(this.queryParam).then((res) => {
          this.templateList = res.data
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
