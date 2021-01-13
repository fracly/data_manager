<template>
  <div>
    <a-spin v-bind:spinning="loading">
    <a-form :form="form" style="max-width: 500px; margin: 40px auto 0;">
      <a-form-item
        label="类型"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-select
          placeholder="请选择输入源类型"
          v-decorator="['inputType', { rules: [{required: true, message: '输入源类型必须选择'}] }]">
          <a-select-option :value='9'>文件</a-select-option>
          <a-select-option :value='1'>mysql</a-select-option>
          <a-select-option :value='2'>mongoDB</a-select-option>
        </a-select>
      </a-form-item>
      <div v-if="form.getFieldValue('inputType') === 1 || form.getFieldValue('inputType') === 2">
        <a-form-item
          label="IP"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-input-group
            style="display: inline-block; vertical-align: middle"
            :compact="true"
          >
            <a-input
              :style="{width: 'calc(100% - 100px)'}"
              placeholder="请输入数据库IP"
              v-decorator="['ip', { rules: [{required: true, message: '数据库IP必须填写'}]}]"
            />
          </a-input-group>
        </a-form-item>
        <a-form-item
          label="端口"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-input-group
            style="display: inline-block; vertical-align: middle"
            :compact="true"
          >
            <a-input
              v-if="form.getFieldValue('inputType') === 1"
              :style="{width: 'calc(100% - 100px)'}"
              placeholder="请输入数据库端口"
              v-decorator="['port', {initialValue: '3306'}, { rules: [{required: true, message: '数据库Port必须填写'}]}]"
            />
            <a-input
                    v-else
                    :style="{width: 'calc(100% - 100px)'}"
                    placeholder="请输入数据库端口"
                    v-decorator="['port', {initialValue: '27017'}, { rules: [{required: true, message: '数据库Port必须填写'}]}]"
            />
          </a-input-group>
        </a-form-item>
        <a-form-item
          label="库名"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          >
          <a-input
            placeholder="请输入数据库名"
            v-decorator="['database', { rules: [{required: true, message: '数据库名必须填写'}] }]"/>
        </a-form-item>
        <a-form-item
          label="表名"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-input
            placeholder="请输入数据库表名"
            v-decorator="['table', { rules: [{required: true, message: '数据库表名必须填写'}] }]"/>
        </a-form-item>
        <a-form-item
          label="账号"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-input
            v-if="form.getFieldValue('inputType') === 1"
            placeholder="请输入数据库账号"
            v-decorator="['username', { rules: [{required: true, message: '数据库账号必须填写'}] }]"/>
          <a-input
                  v-else
                  placeholder="请输入数据库账号"
                  v-decorator="['username']"/>
        </a-form-item>
        <a-form-item
          label="密码"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-input
            v-if="form.getFieldValue('inputType') === 1"
            prefix=""
            type="password"
            placeholder="请输入数据库密码"
            v-decorator="['password', { rules: [{required: true, message: '数据库密码必须填写'}] }]"/>
          <a-input
                  v-else
                  prefix=""
                  type="password"
                  placeholder="请输入数据库密码"
                  v-decorator="['password']"/>
        </a-form-item>
      </div>
      <div v-if="form.getFieldValue('inputType') === 9">
        <a-form-item
          label="请选择本地文件"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-upload
            name="file"
            v-model="file"
            :multiple="true"
            :before-upload="beforeUpload"
            @change="handleChange"
          >
            <a-button> <a-icon type="upload" /> 选择文件 </a-button>
          </a-upload>
        </a-form-item>
      </div>
      <a-form-item :wrapperCol="{span: 19, offset: 5}">
        <a-button
          type="primary"
          @click="testConnect"
          v-if="form.getFieldValue('inputType') === 1 || form.getFieldValue('inputType') === 2">连接测试</a-button>
        <a-button style="margin-left: 8px" @click="nextStep" :disabled="step">下一步</a-button>
      </a-form-item>
    </a-form>
    </a-spin>
    <a-divider />
    <div class="step-form-style-desc">
      <h3>说明</h3>
      <h4>目前支持数据导入的方式分为两大类: </h4>
      <p>1、从RDBS导入数据，一般是选择到Hive，一定要注意，提供相应的驱动jar包一般会以sqoop为基础，进行封装，将MySQL等数据库中的数据导入进来。</p>
      <p>2、利用上传的文件导入，可以选择将数据导入到系统中三种数据类型的数据，都可以。</p>
    </div>
  </div>
</template>
<script>
  import { test } from '@/api/collection'

  export default {
  name: 'Step1',
  props: [
      'formInfo'
  ],
  data () {
    return {
      labelCol: { lg: { span: 5 }, sm: { span: 5 } },
      wrapperCol: { lg: { span: 19 }, sm: { span: 18 } },
      form: this.$form.createForm(this),
      file: {},
      uploading: false,
      step: true,
      loading: false
    }
  },
  methods: {
      nextStep () {
      const { form: { validateFields } } = this
      // 先校验，通过表单校验后，才进入下一步
      validateFields((err, values) => {
        if (!err) {
          this.formInfo.file = this.file
          this.$emit('nextStep')
        }
      })
    },
      getFormData () {
          if (this.form.getFieldsValue().inputType === 9) {
              const formData = new FormData()
              formData.file = this.file
              return formData
          } else {
              return this.form.getFieldsValue()
          }
      },
      getInputType () {
        return this.form.getFieldsValue().inputType
      },
      resetFormData () {
          this.form.resetFields()
          this.file = {}
      },
      testConnect () {
          const { form: { validateFields } } = this
          // 先校验，通过表单校验后，才进入下一步
          validateFields((err, values) => {
            if (!err) {
              this.loading = true
              test(values).then((res) => {
                if (res.code === 0) {
                  this.$message.success(res.msg)
                  this.step = false
                } else {
                  this.$message.error(res.msg)
                }
                this.loading = false
              }).catch(() => {
                this.loading = false
                this.$message.error('链接测试失败！')
              })
            }
          })
    },
      handleChange (info) {
          this.step = false
          if (info.file.status !== 'uploading') {
              console.log(info.file, info.fileList)
          }
          if (info.file.status === 'done') {
              this.$message.success(`${info.file.name} 文件上传成功`)
          } else if (info.file.status === 'error') {
              this.$message.error(`${info.file.name} 文件上传失败.`)
          }
      },
      handleRemove (file) {
          this.file = {}
      },
      beforeUpload (file) {
          this.file = file
          return false
      }
  }
}
</script>
