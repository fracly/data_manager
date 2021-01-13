<template>
  <page-header-wrapper content="高级表单常见于一次性输入和提交大批量数据的场景">
    <a-card class="card" title="服务配置" :bordered="false">
      <a-form :form="form"  class="form">
        <a-row class="form-row" :gutter="16">
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item label="监听地址">
              <a-input
                placeholder="请输入监听地址"
                disabled="true"
                v-decorator="[
                  'ip',
                  {rules: [{ required: true, message: '请输入监听地址', whitespace: true}]}
                ]" />
            </a-form-item>
          </a-col>
          <a-col :xl="{span: 7, offset: 1}" :lg="{span: 8}" :md="{span: 12}" :sm="24">
            <a-form-item
              label="监听端口">
              <a-input
                addonBefore="(1024 - 65525)"
                placeholder="请输入监听端口"
                v-decorator="[
                  'port',
                  {rules: [{ required: true, message: '请输入监听端口', whitespace: true}, {validator: portValidate}]}
                ]" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-card>
    <a-card class="card" title="规则配置" :bordered="false">
      <a-table
        :columns="columns"
        :dataSource="ruleList"
        :pagination="false"
        :loading="loading"
      >
        <template v-for="(col, i) in ['name', 'offset', 'length', 'value']" :slot="col" slot-scope="text, record">
          <a-input
            :key="col"
            v-if="record.editable"
            style="margin: -5px 0"
            :value="text"
            :placeholder="columns[i].title"
            @change="e => handleChange(e.target.value, record.key, col)"
          />
          <template v-else>{{ text }}</template>
        </template>
        <template slot="target" slot-scope="text, record">
            <a-select style="width: 180px" @change="handleChange($event, record.key, 'target')" :disabled="!record.editable" :defaultValue="record.target">
              <a-select-option v-for="item in targetList" :key="item.id" :value="item.id"> {{ item.name }}</a-select-option>
            </a-select>
        </template>
        <template slot="operation" slot-scope="text, record">
          <template v-if="record.editable">
            <span v-if="record.isNew">
              <a @click="saveRow(record)">添加</a>
              <a-divider type="vertical" />
              <a-popconfirm title="是否要删除此行？" @confirm="remove(record.key)">
                <a>删除</a>
              </a-popconfirm>
            </span>
            <span v-else>
              <a @click="saveRow(record)">保存</a>
              <a-divider type="vertical" />
              <a @click="cancel(record.key)">取消</a>
            </span>
          </template>
          <span v-else>
            <a @click="toggle(record.key)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm title="是否要删除此行？" @confirm="remove(record.key)">
              <a>删除</a>
            </a-popconfirm>
          </span>
        </template>
      </a-table>
      <a-button style="width: 100%; margin-top: 16px; margin-bottom: 8px" type="dashed" icon="plus" @click="newRule">新增规则</a-button>
    </a-card>
    <a-card class="card" title="" :bordered="false" style="text-align: center">
      <a-button type="primary" size="large" @click="start" shape="round" v-if="!listening">开始接收</a-button>
      <a-button type="primary" size="large" shape="round" v-else :loading="true">接收中</a-button>
      <a-button
        type="danger"
        size="large"
        @click="stop"
        shape="round"
        :loading="stopLoading"
        style="margin-left: 20px">停止接收</a-button>
    </a-card>
  </page-header-wrapper>
</template>

<script>
  import { startAutoJob, stopAutoJob, initAutoJob, targetList } from '@/api/collection'
  import TagSelectOption from '../../components/TagSelect/TagSelectOption'
  export default {
    name: 'AutoJob',
    components: {
      TagSelectOption
    },
    data () {
      return {
        ruleList: [],
        targetList: [],
        listening: false,
        stopLoading: false,
        loading: false,
        form: this.$form.createForm(this),
        // table
        columns: [
          {
            title: '规则名称',
            dataIndex: 'name',
            key: 'name',
            width: '15%',
            scopedSlots: { customRender: 'name' }
          },
          {
            title: '偏移量(字节数)',
            dataIndex: 'offset',
            key: 'offset',
            width: '15%',
            scopedSlots: { customRender: 'offset' }
          },
          {
            title: '功能号长度（字节数）',
            dataIndex: 'length',
            key: 'length',
            width: '20%',
            scopedSlots: { customRender: 'length' }
          },
          {
            title: '功能号值（16进制字符）',
            dataIndex: 'value',
            key: 'value',
            width: '20%',
            scopedSlots: { customRender: 'value' }
          },
          {
            title: '目标数据',
            dataIndex: 'target',
            key: 'target',
            width: '20%',
            scopedSlots: { customRender: 'target' }
          },
          {
            title: '操作',
            key: 'action',
            scopedSlots: { customRender: 'operation' }
          }
        ]
      }
    },
    methods: {
      handleSubmit (e) {
        e.preventDefault()
      },
      newRule () {
        if (this.listening === true) {
            this.$message.error('UDPServer已启动，请先停止再修改规则！')
            return
        }
        const length = this.ruleList.length
        this.ruleList.push({
          key: length === 0 ? '1' : (parseInt(this.ruleList[length - 1].key) + 1).toString(),
          name: '',
          offset: '',
          length: '',
          value: '',
          target: '',
          editable: true,
          isNew: true
        })
      },
      remove (key) {
          if (this.listening === true) {
              this.$message.error('UDPServer已启动，请先停止再修改规则！')
              return
          }
          const newData = this.ruleList.filter(item => item.key !== key)
          this.ruleList = newData
      },
      saveRow (record) {
        this.loading = true
        const { key, name, offset, length, value, target } = record
        if (!name || !offset || !length || !value || !target) {
          this.loading = false
          this.$message.error('请填写完整规则信息。')
          return
        }
        const targetObj = this.ruleList.find(item => item.key === key)
        targetObj.editable = false
        targetObj.isNew = false
        this.loading = false
      },
      toggle (key) {
        debugger
        if (this.listening === true) {
          this.$message.error('UDPServer已启动，请先停止再修改规则！')
          return
        }
        const target = this.ruleList.find(item => item.key === key)
        target._originalData = { ...target }
        target.editable = !target.editable
      },
      cancel (key) {
        const target = this.data.find(item => item.key === key)
        Object.keys(target).forEach(key => { target[key] = target._originalData[key] })
        target._originalData = undefined
      },
      handleChange (value, key, column) {
        const newData = [...this.ruleList]
        const target = newData.find(item => key === item.key)
        if (target) {
          target[column] = value
          this.ruleList = newData
        }
      },
      start (e) {
          e.preventDefault()
          const that = this
          this.form.validateFields((err, values) => {
            if (!err) {
              if (that.ruleList.length === 0) {
                that.$message.error('没有填写解析规则，无法解析任何数据')
              } else {
                values.rulesJson = JSON.stringify(that.ruleList)
                startAutoJob(values).then((res) => {
                  if (res.code !== 0) {
                    that.$message.error(res.msg)
                  }
                })
                new Promise((resolve) => {
                  setTimeout(() => {
                    resolve({ loop: false })
                  }, 800)
                }).then(() => {
                  that.listening = true
                })
              }
              }
          })
      },
      stop () {
          this.stopLoading = true
          const that = this
          stopAutoJob().then((res) => {
            if (res.code === 0) {
              that.listening = false
              that.$message.success(res.msg)
            }
            that.stopLoading = false
          })
      },
      init () {
          this.loading = true
          const that = this
          // 获取已启动的 UDP socketServer
          initAutoJob().then((res) => {
            if (res.code === 0) {
              that.ruleList = res.data
              that.form.setFieldsValue({ 'ip': res.dataMap.ip, 'port': res.dataMap.port })
              if (res.dataMap.port !== null) {
                that.listening = true
              }
              that.loading = false
            } else {
              console.log('初始化失败')
            }
          })
      },
      portValidate (rule, value, callback) {
        if (value > 1024 && value < 65525) {
          callback()
        } else {
          // eslint-disable-next-line standard/no-callback-literal
          callback('端口范围不在推荐范围内')
        }
      }
    },
    mounted () {
      this.init()
      targetList().then((res) => {
          this.targetList = res.data
      })
    }
  }
</script>

<style lang="less" scoped>
  .card{
    margin-bottom: 24px;
  }
  .popover-wrapper {
    /deep/ .antd-pro-pages-forms-style-errorPopover .ant-popover-inner-content {
      min-width: 256px;
      max-height: 290px;
      padding: 0;
      overflow: auto;
    }
  }
  .antd-pro-pages-forms-style-errorIcon {
    user-select: none;
    margin-right: 24px;
    color: #f5222d;
    cursor: pointer;
    i {
      margin-right: 4px;
    }
  }
  .antd-pro-pages-forms-style-errorListItem {
    padding: 8px 16px;
    list-style: none;
    border-bottom: 1px solid #e8e8e8;
    cursor: pointer;
    transition: all .3s;

    &:hover {
      background: #e6f7ff;
    }
  }
</style>
