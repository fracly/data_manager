<template>
  <div>
    <a-form style="margin: 40px auto 0;">
      <result title="导入成功" :is-success="true" description="任务执行完成，请在列表页查看详情" style="max-width: 560px;">
        <div class="information">
          <a-row>
            <a-col :sm="8" :xs="24">输入源：</a-col>
            <a-col v-if="formInfo.inputType === 9" :sm="16" :xs="24">{{formInfo.file.name}}</a-col>
            <a-col v-else-if="formInfo.inputType === 1" :sm="16" :xs="24">Mysql</a-col>
            <a-col v-else-if="formInfo.inputType === 2" :sm="16" :xs="24">MongoDB</a-col>
          </a-row>
          <a-row>
            <a-col :sm="8" :xs="24">输出源：</a-col>
            <a-col :sm="16" :xs="24">{{formInfo.name}}</a-col>
          </a-row>
          <a-row>
            <a-col :sm="8" :xs="24">提交时间：</a-col>
            <a-col :sm="16" :xs="24">{{date}}</a-col>
          </a-row>
        </div>
        <div slot="action">
          <a-button type="primary" @click="finish">继续导入</a-button>
          <a-button style="margin-left: 8px" @click="toOrderList">查看详情</a-button>
        </div>
      </result>
    </a-form>
  </div>
</template>

<script>
import { Result } from '@/components'

export default {
  name: 'Step3',
  components: {
    Result
  },
  props: [
    'formInfo'
  ],
  data () {
    return {
      loading: false,
      date: ''
    }
  },
  methods: {
    finish () {
      this.$emit('finish')
    },
    toOrderList () {
      this.$router.push('/collection/manual-job')
    }
  },
  created () {
    this.date = new Date().toLocaleString()
  }
}
</script>
<style lang="less" scoped>
  .information {
    line-height: 22px;

    .ant-row:not(:last-child) {
      margin-bottom: 24px;
    }
  }
  .money {
    font-family: "Helvetica Neue",sans-serif;
    font-weight: 500;
    font-size: 20px;
    line-height: 14px;
  }
</style>
