<template>
  <a-modal
    title="新建用户"
    :width="640"
    :visible="visible"
    :confirmLoading="loading"
    @ok="() => { $emit('ok') }"
    @cancel="() => { $emit('cancel') }"
  >
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="formLayout">
        <a-form-item v-show="model && model.id > 0" label="主键ID">
          <a-input v-decorator="['id']" disabled="true"/>
        </a-form-item>
        <a-form-item  label="姓名">
          <a-input v-decorator="['name', {rules: [{required: true, message: '请输入姓名'}]}]" />
        </a-form-item>
        <a-form-item label="账号">
          <a-input v-decorator="['username', {rules: [{required: true, message: '请输入账号'}]}]" />
        </a-form-item>
        <a-form-item label="密码">
          <a-input type="password" v-decorator="['password', {rules: [{required: true, message: '请输入密码'}]}]" />
        </a-form-item>
        <a-form-item label="手机">
          <a-input v-decorator="['phone', {rules: [{required: true, message: '请输入手机'}]}]" />
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input v-decorator="['email']" />
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-decorator="['desc', {rules: [{required: true, message: '请输入用户描述'}]}]" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import pick from 'lodash.pick'

// 表单字段
const fields = ['id', 'name', 'username', 'phone', 'email', 'desc']

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
    model: {
      type: Object,
      default: () => null
    }
  },
  data () {
    this.formLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 13 }
      }
    }
    return {
      form: this.$form.createForm(this)
    }
  },
  created () {
    // 防止表单未注册
    fields.forEach(v => this.form.getFieldDecorator(v))

    // 当 model 发生改变时，为表单设置值
    this.$watch('model', () => {
      this.model && this.form.setFieldsValue(pick(this.model, fields))
    })
  }
}
</script>
