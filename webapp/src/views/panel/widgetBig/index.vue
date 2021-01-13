// app-widget
<style scoped lang="less">
// 基本样式
// .app-widget {
//     width: 100%;
//     height: 100%;
//     background: rgba(255,255,255,0.97);
//     position: relative;
//     .title {
//         position: absolute;
//         z-index: 1;
//         i {
//             float: left;
//             font-size: 20px;
//         }
//     }
//     .more {
//         position: absolute;
//         z-index: 2;
//         top: 10px;
//         right: 8px;
//         line-height: 20px;
//         i {
//             float: left;
//             font-size: 20px;
//             margin: 0px 5px;
//             cursor: pointer;
//             opacity: 0.5;
//             &:hover {
//                 opacity: 0.8;
//             }
//         }
//         span {
//             float: left;
//             padding-right: 5px;
//             cursor: pointer;
//             opacity: 0.5;
//         }
//     }
//     .slotWrap {
//         position: absolute;
//         z-index: 1;
//         top: 8px;
//         right: 8px;
//         bottom: 8px;
//         left: 8px;
//         overflow: auto;
//         .pScrollbar {

//         }
//     }
// }
// // 默认样式
// .app-widget-default {
//     color: #1b2b3c;
//     box-shadow: 0px 0px 16px rgba(0,0,0,0.15);
//     .title {
//         top: 0px;
//         right: 8px;
//         left: 8px;
//         height: 40px;
//         line-height: 40px;
//         font-weight: bold;
//         border-bottom: 1px solid #f2f2f2;
//         i {
//             margin: 10px 5px 0px 0px;
//         }
//     }
//     .more {
//         right: 8px;
//     }
//     .decorator {
//         // > span {
//         //     position: absolute;
//         //     width: 10px;
//         //     height: 10px;
//         //     background: url('./default/default.png') 0px 0px no-repeat;
//         //     &.tl {
//         //         top: -2px;
//         //         left: -2px;
//         //         background-position: 0px 0px;
//         //     }
//         //     &.tr {
//         //         top: -2px;
//         //         right: -2px;
//         //         background-position: -10px 0px;
//         //     }
//         //     &.br {
//         //         bottom: -2px;
//         //         right: -2px;
//         //         background-position: -10px -10px;
//         //     }
//         //     &.bl {
//         //         bottom: -2px;
//         //         left: -2px;
//         //         background-position: 0px -10px;
//         //     }
//         // }
//     }
//     &.isDark {
//         color: #6f85a9;
//         background: rgba(0,25,51,0.95);
//         box-shadow: 0px 0px 16px rgba(0,170,255,0.15);
//         .title {
//             color: #0af;
//             border-bottom: 1px solid #082542;
//         }
//         .decorator {
//             > span {
//                 background-image: url('./default/defaultDark.png');
//             }
//         }
//     }
//     &.hasTitle {
//         .slotWrap {
//             top: 48px;
//         }
//     }
// }
//科技样式
.app-widget{
    width: 100%;
    height: 100%;
    position: relative;
    color: #134e8a;
    background: none;
    .title {
        text-align: center;
        color: #cce6ff;
        line-height: 32px;
        font-size: 16px;
        text-shadow: 0 0 2px #CCE6FF, 0 0 4px #CCE6FF, 0 0 6px #0af, 0 0 8px #0AF, 0 0 6px #0af;
        i {
            vertical-align: sub;
            font-size: 18px;
        }
    }
    .more {
        top: 0px;
        right: 0px;
        color: #7fb5ff;
        i {
            font-size: 18px;
            max-resolution: 0px 3px;
            background-image: -webkit-linear-gradient(bottom,#134e8a,#3b8bdb);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
    }
    .slotWrap {
        top: 32px;
        right: 14px;
        bottom: 11px;
        left: 14px;
        position: absolute;
        z-index: 1;
    }
    .decorator {
        > span {
            position: absolute;
            &.tl {
                top: 0px;
                left: -14px;
                width: 114px;
                height: 132px;
                background: url('./imager/topLeft.png') top left no-repeat;
            }
            &.tm {
                top: 0px;
                left: 100px;
                right: 100px;
                height: 132px;
                background: url('./imager/topMid.png') top center repeat-x;
            }
            &.tr {
                top: 0px;
                right: -14px;
                width: 114px;
                height: 132px;
                background: url('./imager/topRight.png') top right no-repeat;
            }
            &.ml,
            &.mm,
            &.mr {
                top: 132px;
                bottom: 46px;
            }
            &.ml {
                left: -14px;
                width: 114px;
                background: url('./imager/midLeft.png') left center repeat-y;
            }
            &.mm {
                left: 100px;
                right: 100px;
                background: url('./imager/midMid.png') center center repeat;
            }
            &.mr {
                right: -14px;
                width: 114px;
                background: url('./imager/midRight.png') right center repeat-y;
            }
            &.bl,
            &.bm,
            &.br {
                bottom: -14px;
                height: 60px;
            }
            &.bl {
                left: -14px;
                width: 114px;
                background: url('./imager/bottomLeft.png') left bottom no-repeat;
            }
            &.bm {
                left: 100px;
                right: 100px;
                background: url('./imager/bottomMid.png') center bottom repeat-x;
            }
            &.br {
                right: -14px;
                width: 114px;
                background: url('./imager/bottomRight.png') right bottom no-repeat;
            }
        }
    }
}
</style>

<template>
  <!-- <div class="app-widget" :class="'app-widget-' + type"> -->
  <div class="app-widget" :class="[typeClass, {isDark: dark}, {hasTitle: hasTitle}]">
    <!-- 更多操作 -->
    <!-- <template v-if="$slots.more">
            <div class="more">
                <slot name="more"></slot>
            </div>
        </template> -->

    <!-- 标题插槽 -->
    <template v-if="hasTitle">
      <div class="title">
        <slot name="title">
          <!-- <i class="material-icons">sort</i> -->
          {{ title }}
        </slot>
      </div>
    </template>

    <!-- 内容插槽 -->
    <div class="slotWrap">
      <pSrollbar class="pScrollbar">
        <slot></slot>
      </pSrollbar>
    </div>

    <!-- 组件装饰 -->
    <div class="decorator">
      <!-- 科技类装饰 -->
      <template v-if="type === 'tech'">
        <span class="tl"></span><span class="tm"></span><span class="tr"></span>
        <span class="ml"></span><span class="mm"></span><span class="mr"></span>
        <span class="bl"></span><span class="bm"></span><span class="br"></span>
      </template>

      <!-- xx类装饰 -->
      <template v-else-if="type === 'xxx'">

      </template>
      <!-- 默认装饰 -->
      <template v-else>
        <span class="tl"></span><span class="tr"></span><span class="br"></span><span class="bl"></span>
      </template>
    </div>
  </div>
</template>

<script>
import pSrollbar from '../Scrollbar.vue'
export default {
  name: 'AppWidget',
  components: {
      pSrollbar
  },
  props: {
      type: {
          type: String,
          default: 'default'
      },
      dark: {
          type: Boolean,
          default: false
      },
      title: {
          type: String,
          default: function () {
              if (this.type === 'tech') {
                  return '未定义标题'
              } else {
                  return ''
              }
          }
      }
  },
  computed: {
      typeClass () {
          return 'app-widget-' + this.type
      },
      hasTitle () {
          const hasTitleProp = !!this.title
          const hasTitleSlot = !!this.$slots.title
          return hasTitleProp || hasTitleSlot
      }
  },
  methods: {

  },
  mounted () {

  }
}
</script>
