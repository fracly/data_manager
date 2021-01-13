<template>
  <a-card :bordered="false">
    <a-steps class="steps" :current="currentTab">
      <a-step title="选择输入源" />
      <a-step title="选择输出源" />
      <a-step title="执行导入任务" />
    </a-steps>
    <div class="content">
      <step1 v-show="currentTab === 0" :formInfo="formInfo" @nextStep="nextStep" ref="step1"/>
      <step2 v-show="currentTab === 1" :formInfo="formInfo" @nextStep="nextStep" @prevStep="prevStep" ref="step2"/>
      <step3 v-if="currentTab === 2" :formInfo="formInfo" @prevStep="prevStep" @finish="finish"/>
    </div>
  </a-card>
</template>

<script>
import Step1 from './Step1'
import Step2 from './Step2'
import Step3 from './Step3'

export default {
  name: 'StepForm',
  components: {
    Step1,
    Step2,
    Step3
  },
  data () {
    return {
      description: '将各种来源的数据，导入到系统中相应的数据当中去，例如Hive/HBase/HDFS。',
      currentTab: 0,
      // form
      formInfo: { inputType: 0 }
    }
  },
  methods: {

    // handler
    nextStep () {
       if (this.currentTab === 0) {
           this.formInfo = this.$refs.step1.getFormData()
           this.$set(this.formInfo, 'inputType', this.$refs.step1.getInputType())
       }
      if (this.currentTab < 2) {
          this.currentTab += 1
      }
    },
    prevStep () {
        if (this.currentTab > 0) {
        this.currentTab -= 1
      }
    },
    finish () {
        this.$refs.step1.resetFormData()
        this.$refs.step2.resetFormData()
        this.currentTab = 0
    }
  }
}
</script>

<style lang="less" scoped>
  .steps {
    max-width: 750px;
    margin: 16px auto;
  }
</style>
