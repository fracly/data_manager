<template>
  <a-modal
    title="新建标签"
    :width="600"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleSubmit"
    @cancel="handleCancel"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item>
          <a-input type="hidden" v-decorator="['id', {}]" disabled></a-input>
        </a-form-item>
        <a-form-item label="标签名称">
          <a-input v-decorator="['name', {rules: [{required: true, min: 1, message: '请输入至少1个字符的规则描述！'}]}]"/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>

</template>

<script>
    export default {
        name: 'CreateLabel',
        data () {
            return {
                visible: false,
                confirmLoading: false,
                form: this.$form.createForm(this)
            }
        },
        methods: {
            add () {
                this.visible = true
                this.form.setFieldsValue({ 'id': 0 })
            },
            edit (record) {
                this.form.resetFields()
                this.visible = true
                this.$nextTick(() => {
                    this.form.setFieldsValue({ ...record })
                })
            },
            handleSubmit () {
                const { form: { validateFields } } = this
                this.confirmLoading = true
                validateFields((errors, values) => {
                    if (!errors) {
                        this.visible = false
                        this.confirmLoading = false
                        this.$emit('ok', values)
                    } else {
                        this.confirmLoading = false
                    }
                })
            },
            handleCancel () {
                this.visible = false
            }
        }
    }
</script>

<style scoped>

</style>
