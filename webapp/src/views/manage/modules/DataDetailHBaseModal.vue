<template>
  <a-modal
          :title="title"
          :width="800"
          :visible="visible"
          :confirmLoading="confirmLoading"
          :closable="false"
  >
    <template slot="footer">
      <a-button type="primary" @click="handleOk">确定</a-button>
    </template>
    <a-spin :spinning="confirmLoading">
      <a-table
              ref="table"
              size="default"
              :columns="columns"
              rowKey="name"
              :dataSource="columnList"
              :pagination="false">
        <template
          v-for="col in ['name']"
          :slot="col"
          slot-scope="text"
        >
          <div :key="col">
            <template>
              {{ text }}
            </template>
          </div>
        </template>
        <span slot="serial" slot-scope="text, record, index">
          {{ index + 1 }}
        </span>
      </a-table>
    </a-spin>
  </a-modal>
</template>

<script>
  export default {
    name: 'DataDetailModal',
    data () {
      return {
        title: '数据详情',
        visible: false,
        visibleAdd: false,
        confirmLoading: false,
        editingKey: '',
        cacheData: [],
        // 查询条件
        queryParam: {
        },
        // 表头
        columns: [
          {
              title: '#',
              scopedSlots: { customRender: 'serial' },
              width: '50%'
          },
          {
              title: '列簇名称',
              dataIndex: 'name',
              scopedSlots: { customRender: 'name' },
              width: '50%'
          }
        ],
        columnList: [],
        tableName: '',
        column: [],
        info: {}
      }
    },
    created () {
        this.cacheData = this.columnList.map(item => ({ ...item }))
    },
    methods: {
      show (item, info) {
        this.visible = true
        this.columnList = [{ 'name': item[0] }]
        this.info = info
      },
      handleOk () {
        this.visible = false
      }
    }
  }
</script>
