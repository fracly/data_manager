<template>
  <div class="data-source-form">
    <a-spin :spinning="spinning">
  <a-form :form="form">
    <a-form-item>
      <a-input type="hidden" v-decorator="['id']" />
    </a-form-item>
    <a-form-item
      label="数据源名称"
      :required="true"
    >
      <a-input v-decorator="['name', {rules:[{required: true, message: '请输入数据源名称'}]}]" />
    </a-form-item>
    <a-form-item
      label="数据源类型"
      :required="true"
    >
      <a-select v-decorator="['type', {rules:[{required: true, message: '请选择数据源类型'}]}]" @change="fillDeaultValue">
        <a-select-option :value="1">Hive数据库</a-select-option>
        <a-select-option :value="2">HBase数据库</a-select-option>
        <a-select-option :value="3">HDFS数据目录</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item
      label="数据源IP"
      :required="true"
    >
      <a-input style="width: 100%" :disabled="true" v-decorator="['ip', {rules:[{required: true, message: '请输入IP'}]}]" />
    </a-form-item>
    <a-form-item
      label="数据源端口"
      :required="true"
    >
      <a-input style="width: 100%" :disabled="true" v-decorator="['port', {rules:[{required: true, message: '请输入端口号'}]}]" />
    </a-form-item>
    <a-form-item
      label="数据源库/目录(首字符不允许是数字)"
      :required="true"
    >
      <a-input style="width: 100%" v-decorator="['category1', {rules:[{required: true, message: '请输入库名/目录名'}]}]" :disabled="disableSpaceName" />
    </a-form-item>
    <a-form-item
      label="数据源描述"
    >
      <a-textarea v-decorator="['description']"></a-textarea><a-button type="primary" @click="testConnection">连接测试</a-button>
    </a-form-item>
  </a-form>
    </a-spin>
  </div>
</template>

<script>
import pick from 'lodash.pick'
import { createDataSource, updateDataSource, testDataSource } from '../../../api/datasource'

export default {
  name: 'DataSourceForm',
  props: {
      record: Object
  },
  data () {
      return {
          form: this.$form.createForm(this),
          spinning: false,
          disableSpaceName: false
      }
  },
  mounted () {
    console.log('this.', this.record)
    if (this.record) {
        this.form.setFieldsValue(pick(this.record, ['id']))
        this.form.setFieldsValue(pick(this.record, ['name']))
        this.form.setFieldsValue(pick(this.record, ['type']))
        this.form.setFieldsValue(pick(this.record, ['ip']))
        this.form.setFieldsValue(pick(this.record, ['port']))
        this.form.setFieldsValue(pick(this.record, ['category1']))
        this.form.setFieldsValue(pick(this.record, ['description']))
        this.disableSpaceName = true
    }
  },
  methods: {
    onOk (e) {
      return new Promise(resolve => {
          const { form: { validateFields } } = this
          validateFields((errors, values) => {
              if (!errors) {
                  this.spinning = true
                  const dataSourceParams = { ...values }
                  if (dataSourceParams.id > 0) {
                      updateDataSource(dataSourceParams)
                          .then((res) => {
                              this.spinning = false
                              if (res.code === 0) {
                                  this.$message.success(res.msg)
                              } else {
                                  this.$message.error(res.msg)
                              }
                              this.$emit('refresh')
                              resolve(true)
                          }).catch(err => {
                          console.log('fail', err)
                      }).finally(() => {
                          // do nothing
                      })
                  } else {
                      if (values.type === 3) {
                        dataSourceParams.category1 = '/data_manager/' + dataSourceParams.category1
                      }
                      createDataSource(dataSourceParams)
                          .then((res) => {
                              this.spinning = false
                              if (res.code === 0) {
                                  this.$message.success(res.msg)
                              } else {
                                  this.$message.error(res.msg)
                              }
                              this.$emit('refresh')
                              resolve(true)
                          }).catch(err => {
                          console.log('fail', err)
                      }).finally(() => {
                          // do nothing
                      })
                  }
              } else {
                  console.log('填写项不正确')
              }
          })
      })
    },
    onCancel () {
      return new Promise(resolve => {
        resolve(true)
      })
    },
    testConnection () {
        const { form: { validateFields } } = this
        validateFields((errors, values) => {
            if (!errors) {
                this.spinning = true
                const dataSourceParams = { ...values }
                testDataSource(dataSourceParams)
                    .then((res) => {
                        if (res.code === 0) {
                            this.$message.success(res.msg)
                        } else {
                            this.$message.error(res.msg)
                        }
                        this.spinning = false
                    }).catch(err => {
                    console.log('fail', err)
                })
            } else {
                console.log('填写项不正确')
            }
        })
        console.log(1)
    },
    fillDeaultValue (type) {
      if (type === 1) {
          this.form.setFieldsValue({ 'ip': window.dmConfig.hiveServer2Host, 'port': window.dmConfig.hiveServer2Port, 'category1': '' })
      } else if (type === 2) {
          this.form.setFieldsValue({ 'ip': window.dmConfig.zookeeperHost, 'port': window.dmConfig.zookeeperPort, 'category1': '' })
      } else if (type === 3) {
          this.form.setFieldsValue({ 'ip': window.dmConfig.defaultFsHost, 'port': window.dmConfig.defaultFsPort, 'category1': '' })
      }
    }
  }
}
</script>
<style>
  .data-source-form .ant-form-item {
    margin-bottom : 0px;
  }
</style>
