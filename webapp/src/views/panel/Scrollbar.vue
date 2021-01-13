// app-scrollbar
<style lang="less" scoped>
// 基本样式
.app-scrollbar {
    position: relative;
    width: 100%;
    height: 100%;
}
</style>

<template>
  <section class="app-scrollbar" :is="$props.tagname" @mouseover.once="update" v-on="$listeners">
    <slot></slot>
  </section>
</template>

<script>
import PerfectScrollbar from 'perfect-scrollbar'
export default {
  name: 'AppScrollbar',
  props: {
    settings: {
        default: undefined
    },
    tagname: {
        type: String,
        default: 'section'
    }
  },
  data () {
      return {
        // ps: null
      }
  },
  methods: {
        update () {
            if (this.ps) {
                setTimeout(() => {
                    this.ps.update()
                }, 50)
            }
        },
        scrollTop () {
            this.$el.scrollTop = 0
         },
        __init () {
            if (!this.ps) {
                this.ps = new PerfectScrollbar(this.$el, this.settings)
            }
        },
        __uninit () {
            if (this.ps) {
                this.ps.destroy()
                this.ps = null
            }
        }
    },
    watch: {
        $route () {
            this.update()
        }
    },
    mounted () {
        // for support ssr
        if (!this.$isServer) {
            this.__init()
        }
    },
    updated () {
        this.$nextTick(this.update)
    },
    activated () {
        this.__init()
    },
    deactivated () {
        this.__uninit()
    },
    beforeDestroy () {
        this.__uninit()
    }
}
</script>
