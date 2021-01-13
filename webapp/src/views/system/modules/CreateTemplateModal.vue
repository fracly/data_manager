<template>
  <a-modal
          title="新增数据模板"
          class="data-modal"
          :width="600"
          :visible="visible"
          :confirmLoading="submitLoading"
          :maskClosable="false"
          @ok="handleOk"
          @cancel="handleCancel">
    <a-form :form="form" :style="formStyle">
      <a-form-item label="数据模板名称">
        <a-input v-decorator="['name', {rules: [{required: true, min: 1, message: '请输入至少1个字符的名称！'}]}]"/>
      </a-form-item>
      <a-form-item label="数据模板代码">
        <a-input v-decorator="['code', {rules: [{required: true, min: 1, message: '请输入至少1个字符的代码！'}]}]"/>
      </a-form-item>
      <a-form-item label="模板字段信息">
        <a-input-group
                v-for="(item, index) in columnList"
                :key="index"
                compact>
          <a-input style="width:30%" placeholder="请输入字段名(英文)" v-model="item.name"></a-input>
          <a-select style="width:30%;margin-left: 5px" placeholder="请选择字段类型" v-model="item.type">
            <a-select-option
                    :key="index"
                    v-for="(columnType, index) in columnTypeList"
                    :value="columnType">{{ columnType }}
            </a-select-option>
          </a-select>
          <a-input
                  style="width:30%;margin-left: 5px"
                  placeholder="请输入字段描述"
                  v-model="item.comment"></a-input>
          <a-button style="margin-left: 5px" icon="delete" @click="handleDelete(index)"/>
        </a-input-group>
        <a-button style="margin-left: 10px" @click="handleAddColumn" type="primary">添加字段
          <a-icon type="plus-circle"/>
        </a-button>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>

  import { systemTemplateCreate, systemTemplateUpdate } from '@/api/system'
  import { getLabelFlatList } from '@/api/label'
  import { testContainsHZ } from '@/utils/util'

  const columnTypeList = ['STRING', 'BIGINT', 'INT', 'FLOAT', 'DOUBLE', 'DECIMAL', 'TIMESTAMP', 'DATE']
  export default {
    name: 'DataModal',
    data () {
      return {
        visible: false,
        uploading: false,
        submitLoading: false,
        isCreate: true,
        columnList: [],
        tableName: '',
        create: true,
        id: '',
        form: this.$form.createForm(this),
        params: {},
        columnTypeList
      }
    },
    methods: {
      add () {
        this.id = ''
        this.create = true
        this.visible = true
      },
      edit (record) {
        this.create = false
        this.form.resetFields()
        this.visible = true
        this.id = record.id
        this.columnList = JSON.parse(record.columnJson)
        this.$nextTick(() => {
          this.form.setFieldsValue({ ...record })
        })
      },
      handleDelete (i) {
        this.columnList.splice(i, 1)
      },
      close () {
        this.$emit('ok')
        this.visible = false
      },
      initLabelList () {
        getLabelFlatList().then((res) => {
          this.labelList = res.data
        })
      },
      handleOk () {
        const that = this
        this.form.validateFields((err, values) => {
          if (!err) {
            that.submitLoading = true
            that.confirmLoading = true
            if (that.create) {
                if (testContainsHZ(values.tableName)) {
                  that.$message.error('表名不能包含中文')
                  that.confirmLoading = false
                  that.submitLoading = false
                  return
                }
                for (let i = 0; i < that.columnList.length; i++) {
                  if (testContainsHZ(that.columnList[i].name)) {
                    that.$message.error('字段名不能包含中文')
                    that.confirmLoading = false
                    that.submitLoading = false
                    return
                  }
                }
                const formData = new FormData()
                for (const key in values) {
                  formData.append(key, values[key])
                }
                formData.append('columnJson', JSON.stringify(that.columnList))
                systemTemplateCreate(formData).then((res) => {
                  if (res.code === 0) {
                    that.$message.success(res.msg)
                  } else {
                    that.$message.error(res.msg)
                  }
                  that.confirmLoading = false
                  that.submitLoading = false
                  that.close()
                }).catch(err => {
                  console.log(err)
                  that.$message.error('创建表失败！')
                  that.confirmLoading = false
                  that.submitLoading = false
                })
            } else {
              that.confirmLoading = true
              values.id = this.id
              values.columnJson = JSON.stringify(that.columnList)
              systemTemplateUpdate(values).then((res) => {
                if (res.code === 0) {
                  that.$message.success(res.msg)
                  that.$emit('ok')
                } else {
                  that.$message.error(res.msg)
                  that.$emit('close')
                }
              }).catch(() => {
              }).finally(() => {
                that.submitLoading = false
                that.close()
              })
            }
          }
        })
      },
      handleCancel () {
        if (this.uploading) {
          this.$confirm({
            title: '警告',
            content: '当前文件正在上传，确认中断上传吗 ?',
            onOk: () => {
              return this.close()
            },
            onCancel () {
            }
          })
        } else {
          this.close()
        }
      },
      beforeUpload () {
        return false
      },
      handleChange (info) {
        this.fileList = info.fileList
        for (let j = 0; j < info.fileList.length; j++) {
          this.progressList[j] = { 'name': info.fileList[j].name, 'percent': 0 }
        }
      },
      handleAddColumn: function () {
          this.columnList.push({ 'name': '', 'type': '', 'comment': '' })
      },
      handleFileOK: function () {
        this.fileIsExist = false
        this.confirmLoading = false
      },
      handlePercentIncrease (index, value, name) {
        this.$set(this.progressList, index, { percent: value, name: name })
      }
    }
  }
</script>
<style>
  .data-modal .ant-form-item {
    margin-bottom: 0px;
  }
</style>
