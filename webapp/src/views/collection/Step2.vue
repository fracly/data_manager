<template>
  <div>
    <a-spin :spinning="loading">
      <a-form :form="form" style="max-width: 500px; margin: 40px auto 0;" >
        <a-form-item
          label="输出源类型"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          class="stepFormText"
        >
          <a-select
            placeholder="请选择输出源类型"
            @change="getTypeDataSource"
            v-decorator="['outputType', { rules: [{required: true, message: '输入源类型必须选择'}] }]">
            <a-select-option v-if="this.formInfo.inputType == 9 || this.formInfo.inputType == 1" value="1">Hive</a-select-option>
            <a-select-option v-if="this.formInfo.inputType == 2" value="2">Hbase</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          label="输出源列表"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          class="stepFormText"
        >
          <a-select
            placeholder="请选择输出源列表"
            v-decorator="['outputList', { rules: [{required: true, message: '输入源列表必须选择'}] }]"
            @change="getTypeData"
          >
            <a-select-option
              v-for="(item) in targetList"
              :key="item.id"
              :value="item.id">{{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          label="输出源数据"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          class="stepFormText"
        >
          <a-select
            placeholder="请选择输出源数据"
            v-decorator="['outputId', { rules: [{required: true, message: '输入源数据必须选择'}] }]"
            @change="getDataText"
            :labelInValue="true"
          >
            <a-select-option :key="item.id" v-for="item in dataList" >{{ item.name }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          label="是否覆盖"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          class="stepFormText"
        >
          <a-radio-group
            v-decorator="['overwrite', { rules: [{required: true, message: '请选择是否覆盖表原有数据'}] }]">
            <a-radio value="true">是</a-radio>
            <a-radio value="false">否</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-divider />
        <a-form-item :wrapperCol="{span: 19, offset: 5}">
          <a-button type="primary" @click="submit" >提交</a-button>
          <a-button style="margin-left: 8px" @click="prevStep">上一步</a-button>
        </a-form-item>
      </a-form>
    </a-spin>
  </div>
</template>

<script>
    import { getDataSourceDataList } from '@/api/manage'
    import { queryDataSource } from '@/api/datasource'
    import { sqoop, file, mongodb } from '@/api/collection'

export default {
  name: 'Step2',
  props: [
      'formInfo'
  ],
  data () {
    return {
      labelCol: { lg: { span: 5 }, sm: { span: 5 } },
      wrapperCol: { lg: { span: 19 }, sm: { span: 19 } },
      form: this.$form.createForm(this),
      loading: false,
      timer: 0,
      dataList: [],
      outputType: 1,
      dataSourceType: 1,
      targetList: [],
        // 查询参数
      queryParam: {
            dataSourceId: 0,
            pageNo: 1,
            pageSize: 100
        },
      dataText: '',
      needCreate: true
    }
  },
  methods: {
      submit () {
        const that = this
          const { form: { validateFields } } = this
          validateFields((err, values) => {
            if (!err) {
                that.loading = true
                that.formInfo.name = values.outputId.label
                that.formInfo.overwrite = values.overwrite
                if (that.formInfo.inputType === 1) {
                    that.formInfo.outputType = values.outputType
                    that.formInfo.outputId = values.outputId.key
                    console.log(that.formInfo)
                    sqoop(that.formInfo).then((res) => {
                        if (res.code === 0) {
                            that.$emit('nextStep')
                        } else {
                            that.$message.error(res.msg)
                        }
                        that.loading = false
                    }).catch(() => {
                        that.$message.error('提交出错')
                        that.loading = false
                    })
                } else if (that.formInfo.inputType === 2) {
                      that.formInfo.outputType = values.outputType
                      that.formInfo.outputId = values.outputId.key
                      console.log(that.formInfo)
                      mongodb(that.formInfo).then((res) => {
                        if (res.code === 0) {
                          that.$emit('nextStep')
                        } else {
                          that.$message.error(res.msg)
                        }
                        that.loading = false
                      }).catch(() => {
                        that.$message.error('提交出错')
                        that.loading = false
                      })
                } else if (that.formInfo.inputType === 9) {
                    const formData = new FormData()
                    formData.append('dataId', values.outputId.key)
                    formData.append('overwrite', values.overwrite)
                    formData.append('file', that.formInfo.file)
                    file(formData).then((res) => {
                      if (res.code === 0) {
                        that.$emit('nextStep')
                      } else {
                        that.$message.warning(res.msg)
                        that.loading = false
                      }
                    })
                }
            } else {
              that.loading = false
            }
          })
      },
      prevStep () {
          this.$emit('prevStep')
      },
      getDataText (e) {
          this.dataText = e.label
      },
      getTypeData (e) {
          this.queryParam.dataSourceId = e
            getDataSourceDataList(this.queryParam).then((res) => {
                this.dataList = res.data
            }).catch(err => {
                console.log('fail', err)
            })
      },
      resetFormData () {
          this.form.resetFields()
      },
      getTypeDataSource (e) {
          console.log(e)
          queryDataSource({ 'type': e }).then(res => {
            this.targetList = res.data
          })
      }
  },
  created () {
  },
  beforeDestroy () {
    clearTimeout(this.timer)
  }
}
</script>

<style lang="less" scoped>
  .stepFormText {
    margin-bottom: 24px;

    .ant-form-item-label,
    .ant-form-item-control {
      line-height: 22px;
    }
  }

</style>
