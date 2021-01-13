<template>
  <a-modal
    :visible="visible"
    :closable="false"
    @ok="handleOk"
    @cancel="handleCancel"
    >
    <a-spin :spinning="loading">
      <p>下载文件编码格式为UTF-8</p>
      <a-radio-group name="radioGroup" :default-value="1" @change="handleChange">
        <a-radio :value="1">
          .csv
        </a-radio>
        <a-radio :value="2">
          .txt
        </a-radio>
      </a-radio-group>
    </a-spin>
  </a-modal>
</template>
<script>
    import { dataDownload } from '@/api/data'
    export default {
        data () {
          return {
            value: 1,
            loading: false
          }
        },
        props: {
            visible: {
                type: Boolean,
                required: true
            },
            recordId: {
                type: Number,
                required: true
            }
        },
        methods: {
            handleChange: function (e) {
              this.value = e.target.value
            },
            handleOk: function () {
              dataDownload({ 'dataId': this.recordId, 'downloadType': this.value })
              this.loading = true
              const that = this
              setTimeout(() => {
                that.$emit('update:visible', false)
                that.loading = false
              }, 3000)
            },
            handleCancel: function () {
              this.$emit('update:visible', false)
            }
        }
    }
</script>
