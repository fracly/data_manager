<template>
  <a-modal
          :title="title"
          :width="800"
          :visible="visible"
          :confirmLoading="confirmLoading"
          :closable="false"
  >
    <template slot="footer">
      <a-button type="primary" @click="handleAddColumn">确认添加字段</a-button>
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
          v-for="col in ['name', 'type', 'comment']"
          :slot="col"
          slot-scope="text, record"
        >
          <div :key="col">
            <a-input
              v-if="record.editable"
              style="margin: -5px 0"
              :value="text"
              @change="e => handleChange(e.target.value, record.name, col)"
            />
            <template v-else>
              {{ text }}
            </template>
          </div>
        </template>
        <template slot="operation" slot-scope="text, record">
          <div class="editable-row-operations">
              <span v-if="record.editable">
                <a @click="() => save(record.key)">保存</a> |
                <a-popconfirm title="确认取消?" @confirm="() => cancel(record.key)">
                  <a>取消</a>
                </a-popconfirm>
              </span>
                  <span v-else>
                <a :disabled="editingKey !== ''" @click="() => edit(record.key)">编辑</a>
              </span>
          </div>
        </template>
        <span slot="serial" slot-scope="text, record, index">
          {{ index + 1 }}
        </span>
      </a-table>
    </a-spin>
    <a-input-group
            v-for="(item, index) in column"
            :key="index"
            compact
            v-show="visibleAdd"
            style="margin-top: 10px"
    >
      <a-input style="width:25%" placeholder="请输入字段名" v-model="item.name"></a-input>
      <a-select style="width:25%;margin-left: 5px" placeholder="请选择字段类型" v-model="item.type">
        <a-select-option
          :key="index"
          v-for="(columnType, index) in columnTypeList"
          :value="columnType">{{ columnType }}
        </a-select-option>
      </a-select>
      <a-input style="width:25%;margin-left: 5px" placeholder="请输入字段描述" v-model="item.comment"></a-input>
      <a-button style="margin-left: 5px" icon="delete" @click="handleDelete(index)"/>
    </a-input-group>
    <a-button style="margin-top: 10px" @click="showAddColumn" type="primary" >添加字段</a-button>
  </a-modal>
</template>

<script>

    import { dataAddColumn, dataModifyColumn, dataDetail } from '@/api/data'
    const columnTypeList = ['STRING', 'BIGINT', 'INT', 'FLOAT', 'DOUBLE', 'DECIMAL', 'TIMESTAMP', 'DATE']

  export default {
    name: 'DataDetailModal',
    data () {
      return {
        title: '数据详情',
        visible: false,
        visibleAdd: false,
        confirmLoading: false,
        columnTypeList,
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
              width: '10%'
          },
          {
              title: '字段名',
              dataIndex: 'name',
              scopedSlots: { customRender: 'name' },
              width: '25%'
          },
          {
              title: '字段类型',
              dataIndex: 'type',
              scopedSlots: { customRender: 'type' },
              width: '25%'
          },
          {
              title: '字段描述',
              dataIndex: 'comment',
              scopedSlots: { customRender: 'comment' },
              width: '25%'
          },
          {
              title: '操作',
              dataIndex: 'operation',
              scopedSlots: { customRender: 'operation' },
              width: '15%'
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
        this.columnList = item
        this.info = info
      },
      handleOk () {
        this.visible = false
      },
      showAddColumn () {
        this.visibleAdd = true
        this.column.push({ 'name': '', 'type': '', 'comment': '' })
      },
      handleAddColumn () {
        const self = this
        if (this.column.length > 0) {
          for (let i = 0; i < this.column.length; i++) {
            if (this.column[i].name === '') {
              self.$message.warn('新增字段名不能为空')
              return false
            }
          }
          const params = { columns: JSON.stringify(this.column), dataId: this.info.id }
          dataAddColumn(params).then((res) => {
            if (res.code === 0) {
              self.$message.success(res.msg)
              this.visibleAdd = true
              this.column = []
              this.getDetailList()
            } else {
              self.$message.error(res.msg)
              this.visibleAdd = true
              this.column = []
            }
          }).catch(() => {
            // Do something
          }).finally(() => {
          })
        } else {
          self.$message.warn('新增字段不能为空')
        }
      },
      handleDelete (i) {
        this.column.splice(i, 1)
      },
      getDetailList () {
        dataDetail({ 'dataId': this.info.id }).then(res => {
          if (res.code === 0) {
            this.columnList = res.data
          } else {
            this.$message.error(res.msg)
          }
        })
      },
        handleChange (value, key, column) {
            const newData = [...this.columnList]
            const target = newData.filter(item => key === item.name)[0]
            if (target) {
                target[column] = value
                this.columnList = newData
            }
        },
        edit (key) {
            const newData = [...this.columnList]
            const target = newData.filter(item => key === item.key)[0]
            this.editingKey = key
            if (target) {
                target.editable = true
                this.columnList = newData
            }
        },
        save (key) {
            // const that = this
            const newData = [...this.columnList]
            const newCacheData = [...this.cacheData]
            const target = newData.filter(item => key === item.key)[0]
            const targetCache = newCacheData.filter(item => key === item.key)[0]
            if (target && targetCache) {
                delete target.editable
                this.columnList = newData
                Object.assign(targetCache, target)
                this.cacheData = newCacheData
            }
            delete target.editable
            this.editingKey = ''
            target.dataId = this.info.id
            dataModifyColumn(target).then((res) => {
                if (res.code === 0) {
                    this.$message.success(res.msg)
                } else {
                    this.$message.error(res.msg)
                }
                if (target) {
                    Object.assign(target, this.cacheData.filter(item => key === item.key)[0])
                    delete target.editable
                    this.columnList = newData
                }
                dataDetail({ 'dataId': this.info.id }).then(res => {
                    this.columnList = res.data
                })
            })
        },
        cancel (key) {
            const newData = [...this.columnList]
            const target = newData.filter(item => key === item.key)[0]
            target.name = target.key
            this.editingKey = ''
            if (target) {
                // Object.assign(target, this.cacheData.filter(item => key === item.key)[0])
                delete target.editable
                this.columnList = newData
            }
        }
    }
  }
</script>
