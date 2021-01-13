<template>
  <a-modal
    title="新增数据资产"
    class="data-modal"
    :width="600"
    :visible="visible"
    :confirmLoading="submitLoading"
    :maskClosable="false"
    @ok="handleOk"
    @cancel="handleCancel">
    <!-- 进度列表 -->
    <div style="z-index:99999;position:absolute;width:90%;" v-if="uploading">
      <a-progress v-for="item in progressList" :key="item.name" :percent="item.percent"></a-progress>
    </div>
    <!-- 表单内容 -->
    <a-form :form="form" :style="formStyle">
      <a-form-item
        :required="true"
        label="所属数据源"
      >
        <a-input type="hidden" v-decorator="['dataSourceId', {}]" disabled></a-input>
        <a-input type="hidden" v-decorator="['category1', {}]"></a-input>
        <a-input v-decorator="['dataSourceName', {}]" disabled></a-input>
      </a-form-item>
      <a-form-item
        :required="true"
        label="数据资产名称"
      >
        <a-input
          :required="true"
          v-decorator="['name', {rules: [{required: true, min: 1, message: '请输入至少1个字符的名称！'}]}]"/>
      </a-form-item>
      <a-form-item
        :required="true"
        label="数据资产类型"
      >
        <a-select v-decorator="['type', {}]" disabled>
          <a-select-option value="1">Hive表</a-select-option>
          <a-select-option value="2">Hbase表</a-select-option>
          <a-select-option value="3">HDFS文件</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        :required="false"
        label="数据资产标签"
      >
        <a-select mode="multiple" v-decorator="['labels', []]">
          <a-select-option :key="label.id" v-for="label in labelList">{{ label.name }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        :required="true"
        label="数据资产描述"
      >
        <a-textarea
          v-decorator="['description', {rules: [{required: true, min: 1, message: '请输入至少1个字符的描述！'}]}]"
          placeholder="请输入数据资产描述"
          rows="2"
        >
        </a-textarea>
      </a-form-item>
      <a-form-item
        :required="true"
        label="文件权限"
      >
        <a-radio-group
          name="radioGroup2"
          v-decorator="['zzPublic', {rules: [{required: true, message: '请选择文件是否公开'}], initialValue: '0'}]">
          <a-radio value="0">不公开</a-radio>
          <a-radio value="1">公开</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item
        :required="true"
        label="资产创建方式"
        v-show="create"
      >
        <a-radio-group
          name="radioGroup"
          v-decorator="['createMethod', {}]"
          v-show="form.getFieldValue('type')==='1'"
          @change="handleCreateMethodChange">
          <a-radio value="1" v-if="form.getFieldValue('type')==='1'">建表语句</a-radio>
          <a-radio value="2">字段信息</a-radio>
          <a-radio value="3">数据模板</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item v-if="form.getFieldValue('type')==='1' || form.getFieldValue('type')==='2'">
        <a-textarea
          v-show="form.getFieldValue('createMethod') == '1'"
          rows="4"
          placeholder="请输入建表语句"
          v-decorator="['createSql',
                        {rules: [{ message: '请输入建表语句，示例：<br /> create table xx(col string, ...)' }]}
          ]"></a-textarea>
        <div v-if="form.getFieldValue('createMethod') == '2'&&create">
          <a-input
            style="width:50%"
            v-decorator="['tableName', {}]"
            placeholder="请输入表名(英文)"></a-input>
          <a-input-group
            v-if="form.getFieldValue('type')==='1'"
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
          <a-button style="margin-left: 10px" @click="handleAddColumn" type="primary" v-if="form.getFieldValue('type') === '1'">添加字段
            <a-icon type="plus-circle"/>
          </a-button>
        </div>
        <div v-if="form.getFieldValue('createMethod') === '3' && create">
          <a-select style="width:70%"  placeholder="请选择数据模板" @change="((value)=>{handleTemplateChange(value, index)})">
              <a-select-option v-for="item in templateList" :key="item.key" :value="item.columnJson"> {{ item.name }}</a-select-option>
          </a-select>
          <a-input
                  style="width:50%"
                  v-decorator="['tableName', {}]"
                  placeholder="请输入表名(英文)"></a-input>
          <a-input-group
                  v-if="form.getFieldValue('type')==='1'"
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
          <a-button style="margin-left: 10px" @click="handleAddColumn" type="primary" v-if="form.getFieldValue('type') === '1'">添加字段
            <a-icon type="plus-circle"/>
          </a-button>
        </div>
      </a-form-item>
      <a-form-item v-if="form.getFieldValue('type')==='3' && create">
        <a-upload-dragger
          name="file"
          :multiple="true"
          :beforeUpload="beforeUpload"
          @change="handleChange"
        >
          <p class="ant-upload-drag-icon">
            <a-icon type="inbox" />
          </p>
          <p class="ant-upload-text">
            将文件拖至此处，或者点击该区域上传
          </p>
        </a-upload-dragger>
      </a-form-item>
    </a-form>
    <!-- 框中框，清理残留所用 -->
    <a-modal
      title="文件提醒"
      :width="400"
      :visible="fileIsExist"
      @ok="handleFileOK"
      :confirmLoading="false"
      @cancel="handleFileOK"
    >
      <div v-if="isDelete">
        <span>该文件存在残留，请清理之后重新上传！</span>
        <a-button size="small" type="danger" @click="deleteFile" style="margin-left: 10px;">清理残留</a-button>
      </div>
      <div v-else>
        <span>该目录已经存在同名文件，请检查！</span>
      </div>
    </a-modal>
  </a-modal>
</template>

<script>
    import $ from 'jquery'

    import { dataUpdate, dataCreate, hdfsDataPreCreate, hdfsDataUploadPercent, hdfsDataDelete } from '@/api/data'
    import { getLabelFlatList } from '@/api/label'
    import { systemTemplateList } from '@/api/system'
    import { tranferDomain2IP, testContainsHZ } from '@/utils/util'
    import TagSelectOption from '../../../components/TagSelect/TagSelectOption'

    const columnTypeList = ['STRING', 'BIGINT', 'INT', 'FLOAT', 'DOUBLE', 'DECIMAL', 'TIMESTAMP', 'DATE']
    const dataType = { 'HIVE': '1', 'HBASE': '2', 'HDFS': '3' }

    export default {
        name: 'DataModal',
      components: { TagSelectOption },
      data () {
            return {
                visible: false,
                uploading: false,
                submitLoading: false,
                isCreate: true,
                isFileExist: false,
                isFile2Delete: false,
                columnList: [],
                tableName: '',
                labels: [],
                fileList: [],
                progressList: [],
                completeNum: 0,
                create: true,
                id: '',
                // 进度条控制
                formStyle: {},
                labelList: [],
                timer: null,
                fileIsExist: false,
                isDelete: false,
                // 批量上传
                form: this.$form.createForm(this),
                params: {},
                columnTypeList,
                dataType,
                templateList: []
            }
        },
        created () {
            this.initLabelList()
        },
        mounted () {
            this.listenPage() // 如果正在上传文件，窗口离开需警示
        },
        destroyed () {
            clearInterval(this.timer)
        },
        methods: {
            add (item) {
                this.edit({
                    dataSourceId: item.value,
                    dataSourceName: item.title,
                    type: item.key.substr(5, 1),
                    createMethod: item.key.substr(5, 1),
                    create: item.create,
                    category1: item.category1
                })
            },
            edit (record) {
                this.form.resetFields()
                this.visible = true
                this.create = record.create
                this.id = record.id
                if (!record.create) {
                    record.zzPublic = record.zzPublic.toString()
                }
                this.$nextTick(() => {
                    this.form.setFieldsValue({ ...record })
                })
            },
            handleDelete (i) {
                this.columnList.splice(i, 1)
            },
            handleCreateMethodChange (e) {
                const that = this
                if (e.target.value === '3') {
                    systemTemplateList().then((res) => {
                        that.templateList = res.data
                    })
                }
            },
            handleTemplateChange (val) {
              debugger
              this.columnList = JSON.parse(val)
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
                            if (values.type === dataType.HDFS) {
                                // 检测文件是否为空
                                if (that.fileList.length === 0) {
                                    that.$message.error('必须上传至少一个文件')
                                    that.submitLoading = false
                                    that.confirmLoading = false
                                } else {
                                    const tmpMap = []
                                    let flag = false
                                    that.fileList.forEach(item => {
                                      if (tmpMap[item.fileName] === 1) {
                                        flag = true
                                        that.$message.error('上传的文件有重复，请检查')
                                        that.fileList = []
                                        that.submitLoading = false
                                        that.confirmLoading = false
                                      } else {
                                        tmpMap[item.fileName] = 1
                                      }
                                    })
                                  if (!flag) {
                                    // 先逐个判断文件是否存在
                                    for (let i = 0; i < that.fileList.length; i++) {
                                      const formData = new FormData()
                                      values.labels = values.labels ? values.labels.toString() : ''
                                      for (const key in values) {
                                        formData.append(key, values[key])
                                      }
                                      const params = { 'fileName': values.category1 + '/' + that.fileList[i].name }
                                      hdfsDataUploadPercent(params).then((res) => {
                                        // 正确返回代表文件已存在，此时需要清理文件
                                        that.fileIsExist = true
                                        that.submitLoading = false
                                        if (res.FileStatus.length !== that.fileList[i].size) {
                                          that.params = params
                                          that.isDelete = true
                                        }
                                      }).catch(err => {
                                        // 404 错误表示文件不存储，则可以继续上传，不做处理
                                        that.uploading = true
                                        that.formStyle = { opacity: 0.3 }
                                        console.log('请求返回404，表示文件不存在' + err)
                                        formData.set('fileName', values.category1 + '/' + that.fileList[i].name)
                                        if (that.fileList.length > 1) {
                                          formData.set('name', values.name + '-' + (i + 1))
                                        }
                                        params.file = that.fileList[i].originFileObj
                                        that.hdfsDataUpload(params, formData)
                                        this.timer = setInterval(() => {
                                          for (let k = 0; k < that.progressList.length; k++) {
                                            if (that.progressList[k].percent === 100) {
                                              continue
                                            }
                                            hdfsDataUploadPercent({ 'fileName': values.category1 + '/' + that.progressList[k].name }).then((res) => {
                                              const percent = parseInt(((res.FileStatus.length / that.fileList[k].size) * 100).toFixed(2))
                                              that.handlePercentIncrease(k, percent, that.progressList[k].name)
                                            })
                                          }
                                        }, 3000)
                                      })
                                    }
                                  }
                                }
                            } else if (values.type === dataType.HIVE) {
                                  if (testContainsHZ(values.tableName)) {
                                    that.$message.error('表名不能包含中文')
                                    that.confirmLoading = false
                                    return
                                  }
                                  for (let i = 0; i < that.columnList.length; i++) {
                                    if (testContainsHZ(that.columnList[i].name)) {
                                      that.$message.error('字段名不能包含中文')
                                      that.confirmLoading = false
                                      return
                                    }
                                  }
                                  const formData = new FormData()
                                  values.labels = values.labels ? values.labels.toString() : ''
                                  for (const key in values) {
                                    formData.append(key, values[key])
                                  }
                                  formData.append('columns', JSON.stringify(that.columnList))
                                  dataCreate(formData).then((res) => {
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
                            } else if (values.type === dataType.HBASE) {
                                  if (testContainsHZ(values.tableName)) {
                                    that.$message.error('表名不能包含中文')
                                    that.confirmLoading = false
                                    return
                                  }
                                  const formData = new FormData()
                                  values.labels = values.labels ? values.labels.toString() : ''
                                  for (const key in values) {
                                    formData.append(key, values[key])
                                  }
                                  dataCreate(formData).then((res) => {
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
                            }
                        } else {
                            that.confirmLoading = true
                            values.id = this.id
                            values.labels = values.labels ? values.labels.toString() : ''
                            dataUpdate(values).then((res) => {
                            if (res.code === 0) {
                                that.$message.success(res.msg)
                                that.$emit('ok')
                            } else {
                                that.$message.error(res.msg)
                                that.$emit('close')
                            }
                          }).catch(() => {
                            // Do something
                          }).finally(() => {
                              that.submitLoading = false
                              that.confirmLoading = false
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
                if (this.form.getFieldValue('type') === '1') {
                    this.columnList.push({ 'name': '', 'type': '', 'comment': '' })
                } else if (this.form.getFieldValue('type') === '2') {
                    this.columnList.push('')
                }
            },
            hdfsDataUpload: function (param, formData) {
                const self = this
                hdfsDataPreCreate(param).then((res) => {
                    $.ajax({
                        type: 'PUT',
                        url: tranferDomain2IP(res.Location),
                        data: param.file,
                        processData: false,
                        crossDomain: true,
                        success: function (data) {
                            console.log('文件上传成功')
                            // 保存信息
                            dataCreate(formData).then((res) => {
                                if (res.code === 0) {
                                    self.completeNum++
                                } else {
                                    self.$message.fail(res.msg)
                                    self.close()
                                }
                                if (self.fileList.length === self.completeNum) {
                                    self.uploading = false
                                    self.formStyle = {}
                                    self.submitLoading = false
                                    clearInterval(self.timer)
                                    self.close()
                                    if (res.code === 0) {
                                        self.$message.success(res.msg)
                                    } else {
                                        self.$message.fail(res.msg)
                                    }
                                }
                            })
                        }
                    })
                }).catch((err) => {
                    console.log(err)
                    this.$message.error('保存HDFS文件失败！')
                })
            },
            deleteFile: function () {
                 const that = this
                 hdfsDataDelete(that.params).then(() => {
                     that.$message.success('删除成功')
                     that.isDelete = false
                     that.uploading = false
                     that.submitLoading = false
                     that.handleFileOK()
                 }).catch(() => {
                     that.$message.error('删除失败！')
                 })
          },
            handleFileOK: function () {
                this.fileIsExist = false
                this.confirmLoading = false
            },
            listenPage () {
                const that = this
                window.onbeforeunload = function (e) {
                    if (that.uploading === true) {
                        e = e || window.event
                        if (e) {
                            e.returnValue = '文件正在上传！'
                        }
                        return '文件正在上传！'
                    }
                }
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
