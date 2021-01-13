<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :closable="false"
  >
    <a-card class="card" :bordered="false">
      <a-form :form="form" class="form">
        <a-row class="form-row" :gutter="16">
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item label="接收地址">
              <a-input placeholder="请输入接收地址" v-decorator="['ip', {rules: [{ required: true, message: '请输入接收地址', whitespace: true }]}]" />
            </a-form-item>
          </a-col>
          <a-col :xl="{span: 7, offset: 1}" :lg="{span: 8}" :md="{span: 12}" :sm="24">
            <a-form-item
              label="接收端口端口">
              <a-input
                addonBefore="(1024 - 65525)"
                placeholder="请输入接收端口"
                v-decorator="['port',{rules: [{ required: true, message: '请输入接收端口', whitespace: true}]}]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="16">
          <a-col>
            <a-form-item label="数据时间范围">
                            <a-slider style="max-width: 480px; width: 100%;" :tip-formatter="formatter" range  :min="min" :max="max" />
              <!--              <a-range-picker-->
              <!--                style="max-width: 468px; width: 100%;"-->
              <!--                showTime-->
              <!--                format="YYYY-MM-DD HH:mm:ss"-->
              <!--                @change="handleChange"-->
              <!--              >-->
              <!--              </a-range-picker>-->
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="16">
          <a-col>
            <p style="font-weight: bold;font-size: 18px;">查询计算出, 共有 <span> {{ count }} </span>条数据需要发送</p>
          </a-col>
        </a-row>
        <a-radio-group name="radioGroup" :default-value="1" @change="handleLoopChange">
          <p style="font-width: bold;font-size: 18px;">是否需要循环发送?</p>
          <a-radio :value="1" @click="toggle">
            是
          </a-radio>
          <a-radio :value="2" @click="toggle">
            否
          </a-radio>
        </a-radio-group>
        <a-row class="form-row" :gutter="16">
          <a-card class="card" title="" :bordered="false" style="text-align: center">
            <a-button type="primary" size="large" @click="handleSend" shape="round" v-if="!sending">开始发送</a-button>
            <a-button type="primary" size="large" shape="round" v-else :loading="true">发送中</a-button>
            <a-button
              type="danger"
              size="large"
              @click="stop"
              shape="round"
              :loading="stopLoading"
              style="margin-left: 20px">停止发送</a-button>
          </a-card>
        </a-row>
      </a-form>
    </a-card>
    <template slot="footer">
      <a-button type="primary" @click="handleCancel">关闭</a-button>
    </template>
  </a-modal>
</template>

<script>
  import { dataCount, dataSend, dataSendStop, initDataSend } from '@/api/data'
  import moment from 'moment'

  export default {
        name: 'DataSendModel',
        components: {},
        data () {
            return {
                title: '数据发送',
                dataId: 0,
                // 查询条件
                queryParam: {
                    startTime: null,
                    endTime: null
                },
                form: this.$form.createForm(this),
                count: 0,
                min: 0,
                max: 100,
                loop: 1,
                minutes: 1,
                disabled: false,
                sending: false,
                stopSending: false,
                stopLoading: false
            }
        },
        props: {
            visible: {
                type: Boolean,
                required: true
            }
        },
        created () {
        },
        methods: {
            show: function (dataId) {
                  this.dataId = dataId
                  const that = this
                  that.queryParam.dataId = dataId
                  dataCount(that.queryParam).then((res) => {
                      that.count = res.data.count
                      that.min = res.data.min
                      that.max = res.data.max
                    console.log(that.min)
                    console.log(that.max)
                  })
            },
            toggle () {
              this.disabled = !this.disabled
            },
            handleCancel () {
                this.$emit('update:visible', false)
            },
            init () {
              this.loading = true
              const that = this
              initDataSend().then((res) => {
                if (res.code === 0) {
                  that.sending = false
                  that.loading = false
                } else {
                  that.sending = true
                }
              })
            },
            handleSend () {
              const that = this
              this.form.validateFields((err, values) => {
                  if (!err) {
                      values.minStamp = that.min
                      values.maxStamp = that.max
                      values.dataId = that.dataId
                      values.loop = that.loop
                      that.sending = true
                      dataSend(values).then(res => {
                          if (res.code === 0) {
                              that.$message.success(res.msg)
                          } else {
                              that.$message.error(res.msg)
                          }
                      })
                  }
              })
            },
            stop () {
              this.stopLoading = true
              const that = this
              dataSendStop().then((res) => {
                if (res.code === 0) {
                  that.sending = false
                  that.$message.success(res.msg)
                }
                that.stopLoading = false
              })
            },
            handleLoopChange (e) {
              this.loop = e.target.value
            },
            // handleChange (value) {
            //     this.queryParam.dataId = this.dataId
            //     dataCount(this.queryParam).then(res => {
            //         this.count = res.data.count
            //         this.min = res.data.min
            //         this.max = res.data.max
            //     })
            // },
            formatter (value) {
              return moment(value).format('YYYY-MM-DD HH:mm:ss')
            }
        },
        mounted () {
          this.init()
        }
  }
</script>
