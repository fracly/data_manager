<template>
  <a-modal
    title="角色分配"
    :width="640"
    :visible="visible"
    :confirmLoading="loading2"
    @ok="() => { $emit('ok', this.selectedRowKeys) }"
    @cancel="() => { $emit('cancel') }"
  >
    <a-spin :spinning="loading">
      <a-table :columns="columns" :data-source="list" :pagination="false" :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange, rowKey: 'id' }" />
    </a-spin>
  </a-modal>
</template>

<script>

const columns = [
    {
        title: '#',
        dataIndex: 'id',
        key: 'id'
    },
    {
        title: '中文名称',
        dataIndex: 'cn_name',
        key: 'cn_name'
    },
    {
        title: '英文名称',
        dataIndex: 'name',
        key: 'name'
    }]

export default {
  props: {
    visible: {
      type: Boolean,
      required: true
    },
    loading: {
      type: Boolean,
      default: () => false
    },
    list: {
      type: Object,
      default: () => []
    },
    selectedRowKeys: {
        type: Array,
        default: () => []
    }
  },
  data () {
    return {
        form: this.$form.createForm(this),
        columns
    }
  },
    created () {
    },
    computed: {
    },
    methods: {
        onSelectChange (selectedRowKeys) {
            console.log('selectedRowKeys changed: ', selectedRowKeys)
            this.selectedRowKeys = selectedRowKeys
        }
    }
}
</script>
