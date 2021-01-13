<template>
  <a-config-provider :locale="locale">
    <div id="app" >
      <router-view />
    </div>
  </a-config-provider>
</template>

<script>
import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'
import { AppDeviceEnquire } from '@/utils/mixin'

export default {
  mixins: [AppDeviceEnquire],
  data () {
    return {
      locale: zhCN
    }
  },
  mounted () {
      window.addEventListener('hashchange', () => {
          const currentPath = window.location.hash.slice(1) // 获取输入的路由
          if (this.$router.path !== currentPath) {
              this.$router.push(currentPath) // 动态跳转
          }
      }, false)
  }
}
</script>
