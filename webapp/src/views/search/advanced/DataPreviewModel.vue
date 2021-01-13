<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :closable="false"
  >
    <template slot="footer">
      <a-button type="primary" @click="handleOk">确定</a-button>
    </template>
    <a-spin :spinning="loading">
      <a-table
        ref="table"
        size="default"
        :columns="columns"
        rowKey="name"
        :dataSource="columnList"
        :pagination="false"
        :visible="visible"
        style="overflow-x:auto;overflow-y:auto;height:600px;">
        <span slot="serial" slot-scope="text, record, index">
          {{ index + 1 }}
        </span>
      </a-table>
    </a-spin>
  </a-modal>
</template>

<script>
    export default {
        name: 'DataPreviewModel',
        components: {},
        data () {
            return {
                title: '数据预览',
                visibleAdd: false,
                confirmLoading: false,
                // 查询条件
                queryParam: {
                },
                // 表头
                columns: [
                    {
                        title: '#',
                        scopedSlots: { customRender: 'serial' }
                    }
                ],
                columnList: [],
                tableName: '',
                column: []
            }
        },
        props: {
            visible: {
                type: Boolean,
                required: true
            },
            loading: {
                type: Boolean,
                required: true
            }
        },
        created () {
        },
        methods: {
            show: function (item) {
                this.columns = []
                this.columnList = []
                const columnArr = item.dataMap.columnNames
                for (let i = 0; i < columnArr.length; i++) {
                    if (columnArr[i].comment.length === 0) {
                        this.columns.push({
                            title: columnArr[i].name,
                            dataIndex: columnArr[i].name,
                            key: columnArr[i].name
                        })
                    } else {
                        this.columns.push({
                            title: columnArr[i].comment,
                            dataIndex: columnArr[i].comment,
                            key: columnArr[i].comment
                        })
                    }
                }
                this.columnList = item.data
            },
            handleOk () {
                this.$emit('update:visible', false)
            }
        }
    }
</script>
