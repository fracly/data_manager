
<style lang="less" scoped>
  @import url("../views/panel/index.less");
  @import url("../assets/bigData.less");
</style>

<template>
  <div class="shareData">
    <div class="header">
      <div class="title">
        多源数据管理系统
      </div>
      <div class="homepage">CETC</div>
    </div>
    <div id="body" class>
      <div class="wgts">
        <!-- 中间顶部 -->
        <div class="wgt middle">
          <div class="box_head">
            <app-widgetBig type="tech" title="数据云标签">
              <template>
                <div class="more-chart">

                </div>
              </template>
              <div class="mid-pool" style="width:100%;height:100%">
                <div class="pool-source">
                  <div class="source-name" :class="poolMethod(index)" v-for="(item,index) in poolType" :key="index">
                    <div class="source-echart">
                      <div class="source-img">

                      </div>
                      <div :class="sourceMethod(index)" style="width:100%;height:100%;">

                      </div>
                      <div class="source-number">
                        {{ item.total }}
                      </div>
                    </div>
                    <div class="sourec-info">{{ item.name }}</div>
                  </div>
                </div>
              </div>
            </app-widgetBig>
          </div>
        </div>
        <!-- 左侧 -->
        <div class="wgt left">
          <div class="box box_center">
            <app-widgetBig @hook:mounted="space_echart" type="tech" title="空间使用情况">
              <div class="spacewrap">
                <div class="space">
                </div>
                <div class="spaceDetail">
                  <div>总容量
                    <span class="weight">{{ spaceData.CapacityTotal }}</span>
                  </div>
                  <div>已使用空间
                    <span class="weight">{{ spaceData.CapacityUsed }}</span>
                  </div>
                  <div>空间使用率
                    <span class="weight">{{ spaceData.CapacityUsedPercentage }}</span>
                  </div>
                </div>
              </div>
            </app-widgetBig>
          </div>
          <div class="box box_footer">
            <app-widgetBig type="tech" title="集群负载情况">
              <div class="load">

              </div>
              <div class="select">
                <div class="rangeSelect">
                  <a-radio-group class="selectGroup" @change="rangeChangeCur">
                    <a-radio-button v-for="(item,index) in curRange" :key="index" :value="index" :class="curSelect==index?'btn-active':''">{{ item }}</a-radio-button>
                  </a-radio-group>
                </div>
              </div>
              <div class="curChart">

              </div>
            </app-widgetBig>
          </div>
        </div>
        <!-- 右侧 -->
        <div class="wgt right">
          <div class="box box_center">
            <app-widgetBig @hook:mounted="active_echart" type="tech" title="用户活跃情况">
              <div class="activewrap">
                <div class="select">
                  <div class="rangeSelect">
                    <a-radio-group class="selectGroup" @change="rangeChangeActive">
                      <a-radio-button v-for="(item,index) in trendRange" :key="index" :value="index" :class="activeSelect==index?'btn-active':''">{{ item }}</a-radio-button>
                    </a-radio-group>
                  </div>
                </div>
                <div class="active">
                </div>
              </div>

            </app-widgetBig>
          </div>
          <div class="box box_footer">
            <app-widgetBig @hook:mounted="increase_echart" type="tech" title="数据增长趋势">
              <div class="increasewrap">
                <div class="select">
                  <div class="rangeSelect">
                    <a-radio-group class="selectGroup" @change="rangeChangeIncrease">
                      <a-radio-button v-for="(item,index) in trendRange" :key="index" :value="index" :class="increaseSelect==index?'btn-active':''">{{ item }}</a-radio-button>
                    </a-radio-group>
                  </div>
                </div>
                <div class="increase">

                </div>
              </div>

            </app-widgetBig>
          </div>
        </div>
      </div>
      <a-button class="enter" @click="() => { this.$router.push('/user/login') }">
        ENTER
      </a-button>
    </div>
  </div>
</template>

<script>
  import moment from 'moment'
  import echarts from 'echarts'
  import appWidgetBig from '../views/panel/widgetBig/index'
  export default {
    name: 'ShareData',
    data () {
      return {
        isFullscreen: false,
        poolType: [],
        trendRange: ['近3日', '近7日', '近1月', '本年'],
        activeSelect: 0,
        increaseSelect: 0,
        spaceData: {}, // 空间使用数据
        activeDate: {
          startDate: moment().subtract('days', 2).format('YYYY-MM-DD'),
          endDate: moment().format('YYYY-MM-DD')
        },
        increaseDate: {
          startDate: moment().subtract('days', 2).format('YYYY-MM-DD'),
          endDate: moment().format('YYYY-MM-DD')
        },
        curSelect: 0,
        curRange: ['HDFS IO', '群集网络 IO', '群集磁盘 IO', '群集 CPU']
      }
    },
    methods: {
      rangeChangeCur (e) {
        var self = this
        self.curSelect = e.target.value
        self.cur_echart()
      },
      // 集群实时数据
      cur_echart () {
        var self = this
        var time = []
        var dateList1 = [] // 下载量
        var dateList2 = [] // 搜索量
        const select = self.curSelect + 1
        var legendData = []
        var seriesData = []
        var tooltipData = {}
        var axisLabelData = {}
        this.$http.get('/analysis/load/trend?type=' + select).then(res => {
          if (res.code === 0) {
            if (select === 4) {
              res.data.forEach((value) => {
                time.push(moment(value.xAxis).format('HH:mm'))
                dateList1.push(value.yAxis1) // cpu
              })
              legendData = [{
                name: 'CPU使用率',
                textStyle: {
                  color: 'rgba(44, 195, 44, 1)'
                }
              }
              ]
              seriesData = [
                {
                  name: 'CPU使用率',
                  type: 'line',
                  data: dateList1,
                  smooth: true,
                  showAllSymbol: true,
                  symbol: 'none',
                  symbolSize: 5,
                  lineStyle: {
                    normal: {
                      color: 'rgba(44, 195, 44, 1)'
                    }
                  },
                  itemStyle: {
                    color: 'rgba(44, 195, 44, 1)',
                    borderColor: '#7FBFFF',
                    borderWidth: 2
                  },
                  label: {
                    show: false
                  },
                  areaStyle: {
                    normal: {
                      barBorderRadius: 4,
                      color: new echarts.graphic.LinearGradient(
                              0, 0, 0, 1,
                              [
                                { offset: 0, color: 'rgba(44, 195, 44, 1)' },
                                { offset: 1, color: 'rgba(44, 195, 44, 0)' }
                              ]
                      )
                    }
                  }
                }
              ]
              tooltipData = {
                trigger: 'axis',
                axisPointer: {
                  type: 'line'
                },
                formatter: function (params) {
                  var relVal = params[0].name
                  for (var i = 0; i < params.length; i++) {
                    relVal += '<div>' + params[i].seriesName + ' : ' + params[i].data.toFixed(2) + '%</div>'
                  }
                  return relVal
                }
              }
              axisLabelData = {
                color: 'rgba(127, 191, 255, 0.25)',
                fontSize: 10,
                formatter: function (value) {
                  return value + '%'
                }
              }
            } else {
              res.data.forEach((value) => {
                time.push(moment(value.xAxis).format('HH:mm'))
                dateList1.push(value.yAxis1) // 写入速率
                dateList2.push(value.yAxis2) // 读取速率
              })
              legendData = [{
                name: '写入速率',
                textStyle: {
                  color: 'rgba(44, 195, 44, 1)'
                }
              },
                {
                  name: '读取速率',
                  textStyle: {
                    color: 'rgba(144, 63, 226, 1)'
                  }
                }
              ]
              seriesData = [
                {
                  name: '写入速率',
                  type: 'line',
                  data: dateList1,
                  smooth: true,
                  showAllSymbol: true,
                  symbol: 'none',
                  symbolSize: 5,
                  lineStyle: {
                    normal: {
                      color: 'rgba(44, 195, 44, 1)'
                    }
                  },
                  itemStyle: {
                    color: 'rgba(44, 195, 44, 1)',
                    borderColor: '#7FBFFF',
                    borderWidth: 2
                  },
                  label: {
                    show: false
                  },
                  areaStyle: {
                    normal: {
                      barBorderRadius: 4,
                      color: new echarts.graphic.LinearGradient(
                              0, 0, 0, 1,
                              [
                                { offset: 0, color: 'rgba(44, 195, 44, 1)' },
                                { offset: 1, color: 'rgba(44, 195, 44, 0)' }
                              ]
                      )
                    }
                  }
                },
                {
                  name: '读取速率',
                  type: 'line',
                  data: dateList2,
                  smooth: true,
                  showAllSymbol: true,
                  symbol: 'none',
                  symbolSize: 5,
                  lineStyle: {
                    normal: {
                      color: 'rgba(144, 63, 226, 1)'
                    }
                  },
                  itemStyle: {
                    color: 'rgba(144, 63, 226, 1)',
                    borderColor: 'RGBA(220, 158, 255, 1)',
                    borderWidth: 2
                  },
                  label: {
                    show: false
                  },
                  areaStyle: {
                    normal: {
                      barBorderRadius: 4,
                      color: new echarts.graphic.LinearGradient(
                              0, 0, 0, 1,
                              [
                                { offset: 0, color: 'rgba(144, 63, 226, 1)' },
                                { offset: 1, color: 'rgba(144, 63, 226, 0)' }
                              ]
                      )
                    }
                  }
                }
              ]
              tooltipData = {
                trigger: 'axis',
                axisPointer: {
                  type: 'line'
                },
                formatter: function (params) {
                  var relVal = params[0].name
                  const scale = 1024
                  const digitList = ['KB/s', 'MB/s', 'GB/s', 'TB/s']
                  var unit = 'Byte/s'
                  var _integer
                  for (var i = 0; i < params.length; i++) {
                    if (params[i].value >= scale) {
                      _integer = params[i].value / scale
                      let digit = 0
                      while (_integer >= scale) {
                        _integer = Math.round(_integer / scale)
                        digit++
                      }
                      unit = digitList[digit]
                    } else {
                      _integer = params[i].value
                    }
                    relVal += '<div>' + params[i].seriesName + ' : ' + _integer.toFixed(2) + unit + '</div>'
                  }
                  return relVal
                }
              }
              axisLabelData = {
                color: 'rgba(127, 191, 255, 0.25)',
                fontSize: 10,
                formatter: function (value) {
                  if ((value / (1024 * 1024 * 1024 * 1024)) > 1) {
                    return (value / (1024 * 1024 * 1024 * 1024)).toFixed(0) + 'TB/s'
                  } else if ((value / (1024 * 1024 * 1024)) > 1) {
                    return (value / (1024 * 1024 * 1024)).toFixed(0) + 'GB/s'
                  } else if ((value / (1024 * 1024)) > 1) {
                    return (value / (1024 * 1024)).toFixed(0) + 'MB/s'
                  } else if ((value / (1024)) > 1) {
                    return (value / (1024)).toFixed(0) + 'KB/s'
                  } else {
                    return value + 'Byte/s'
                  }
                }
              }
            }
            var colors = ['rgba(0,128,255,1)']
            var option = {
              color: colors,
              grid: {
                left: 55,
                top: '14%',
                right: 28,
                bottom: 40
              },
              legend: {
                top: '3%',
                x: 'center',
                itemWidth: 24, // 设置图片宽度为10
                itemHeight: 8, // 设置图片高度为10
                data: legendData
              },
              tooltip: tooltipData,
              dataZoom: {
                type: 'inside',
                xAxisIndex: [0, 0],
                start: 0,
                end: 100
              },
              xAxis: {
                type: 'category',
                data: time,
                axisLabel: {
                  color: 'rgba(127, 191, 255, 0.25)'
                },
                axisLine: {
                  lineStyle: {
                    color: '#314661'
                  }
                },
                axisTick: {
                  alignWithLabel: false
                },
                boundaryGap: false// 控制文字位置
              },
              yAxis: {
                type: 'value',
                splitLine: {
                  show: false
                },
                axisLine: {
                  lineStyle: {
                    color: '#314661'
                  }
                },
                axisLabel: axisLabelData
              },
              series: seriesData
            }
            var curChart = echarts.init(this.$el.querySelector('.curChart'))
            curChart.clear()
            curChart.setOption(option)
            this.echartResize(this.$el.querySelector('.curChart'), curChart)
          }
        })
      },
      space_echart () {
        var option = {
          angleAxis: {
            max: 100,
            clockwise: true, // 逆时针
            startAngle: 180, // 开始角度
            axisLine: {
              show: false
            },
            axisTick: {
              show: false
            },
            axisLabel: {
              show: false
            },
            splitLine: {
              show: false
            }
          },
          radiusAxis: {
            type: 'category',
            axisLine: {
              show: false
            },
            axisTick: {
              show: false
            },
            axisLabel: {
              show: false
            },
            splitLine: {
              show: false
            }
          },
          polar: {
            center: ['50%', '50%'],
            radius: '110%'
          },
          title: {
            text: '使用率',
            textStyle: {
              color: 'rgba(127,191,255,1)',
              fontSize: 14
            },
            subtext: this.spaceData.CapacityUsedPercentage,
            subtextStyle: {
              fontSize: 20,
              color: '#FFFFFF',
              rich: {
                a: {
                  color: 'rgba(227,241,255,0.9)',
                  fontSize: 12
                }
              }
            },
            itemGap: 8,
            left: 'center',
            top: '35%'
          },
          series: [
            {
              type: 'bar',
              data: [{
                name: '空间使用情况',
                value: this.spaceData.UsedNum,
                itemStyle: {
                  normal: {
                    color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [{
                      offset: 0,
                      color: '#8000FF'
                    }, {
                      offset: 1,
                      color: '#0066CC'
                    }])
                  }
                }
              }],
              coordinateSystem: 'polar',
              roundCap: true,
              barWidth: 8,
              barGap: '-100%',
              z: 2
            },
            { // 灰色环
              type: 'bar',
              data: [{
                value: 100,
                itemStyle: {
                  color: '#022E61'
                }
              }],
              coordinateSystem: 'polar',
              roundCap: true,
              barWidth: 8,
              barGap: '-100%',
              z: 1
            }
          ]
        }
        var space = echarts.init(this.$el.querySelector('.space'))
        space.setOption(option)
        this.echartResize(this.$el.querySelector('.space'), space)
      },
      rangeChangeIncrease (e) {
        var self = this
        self.increaseSelect = e.target.value
        if (self.increaseSelect === 0) { // 近3日
          self.increaseDate = {
            startDate: moment().subtract('days', 2).format('YYYY-MM-DD'),
            endDate: moment().format('YYYY-MM-DD')
          }
        } else if (self.increaseSelect === 1) { // 近7日
          self.increaseDate = {
            startDate: moment().subtract('days', 6).format('YYYY-MM-DD'),
            endDate: moment().format('YYYY-MM-DD')
          }
        } else if (self.increaseSelect === 2) { // 近1月
          self.increaseDate = {
            startDate: moment().subtract('month', 1).format('YYYY-MM-DD'),
            endDate: moment().format('YYYY-MM-DD')
          }
        } else { // 近1年
          self.increaseDate = {
            startDate: moment().subtract('year', 1).format('YYYY-MM-DD'),
            endDate: moment().format('YYYY-MM-DD')
          }
        }
        self.increase_echart()
      },
      rangeChangeActive (e) {
        var self = this
        self.activeSelect = e.target.value
        if (self.activeSelect === 0) { // 近3日
          self.activeDate = {
            startDate: moment().subtract('days', 2).format('YYYY-MM-DD'),
            endDate: moment().format('YYYY-MM-DD')
          }
        } else if (self.activeSelect === 1) { // 近7日
          self.activeDate = {
            startDate: moment().subtract('days', 6).format('YYYY-MM-DD'),
            endDate: moment().format('YYYY-MM-DD')
          }
        } else if (self.activeSelect === 2) { // 近1月
          self.activeDate = {
            startDate: moment().subtract('month', 1).format('YYYY-MM-DD'),
            endDate: moment().format('YYYY-MM-DD')
          }
        } else { // 近1年
          self.activeDate = {
            startDate: moment().subtract('year', 1).format('YYYY-MM-DD'),
            endDate: moment().format('YYYY-MM-DD')
          }
        }
        self.active_echart()
      },
      init () {
        var self = this
        self.$http.get('/analysis/capacity').then(res => {
          if (res.code === 0) {
            self.spaceData = res.data
            self.spaceData.UsedNum = self.spaceData.CapacityUsedPercentage.slice(0, self.spaceData.CapacityUsedPercentage.length - 1)
            self.space_echart()
          }
        })
        var params = {
          startDate: moment().subtract('month', 1).format('YYYY-MM-DD'),
          endDate: moment().format('YYYY-MM-DD')
        }
        self.$http.get('/analysis/count-by-label?startDate=' + params.startDate + '&endDate=' + params.endDate).then(res => {
          if (res.code === 0) {
            self.poolType = res.data
          }
          setTimeout(() => { self.source_echart() }, 500)
        })
        self.load_echart()
        self.cur_echart()
      },
      poolMethod (index) {
        return 'sourecType' + index
      },
      // 云标签
      sourceMethod (index) {
        return 'source' + index
      },
      // 集群负载情况
      load_echart () {
        this.$http.get('/analysis/load/realtime').then(res => {
          if (res.code === 0) {
            var loadData = [
              { name: 'CPU', value: res.data.cpu.toFixed(2) },
              { name: '内存', value: res.data.memory.toFixed(2) },
              { name: '磁盘IO', value: res.data.disk }
            ]
            const option = {
              tooltip: {
                formatter: '{b} : {c}%'
              },
              series: [
                {
                  name: loadData[0].name,
                  type: 'gauge',
                  center: ['17%', '50%'],
                  min: 0,
                  max: 100,
                  startAngle: 205,
                  endAngle: -25,
                  splitNumber: 10,
                  radius: '68%',
                  axisLine: { // 坐标轴线
                    lineStyle: { // 属性lineStyle控制线条样式
                      color: [[0.09, 'lime'], [0.82, 'rgba(0,128,255,0.8)'], [1, '#FF2727']],
                      width: 3,
                      shadowColor: '#fff', // 默认透明
                      shadowBlur: 10
                    }
                  },
                  axisLabel: { // 坐标轴小标记
                    color: '#fff',
                    shadowColor: '#fff', // 默认透明
                    shadowBlur: 10
                  },
                  axisTick: { // 坐标轴小标记
                    length: 15, // 属性length控制线长
                    lineStyle: { // 属性lineStyle控制线条样式
                      color: 'auto',
                      shadowColor: '#fff', // 默认透明
                      shadowBlur: 10
                    }
                  },
                  splitLine: { // 分隔线
                    length: 20, // 属性length控制线长
                    lineStyle: { // 属性lineStyle（详见lineStyle）控制线条样式
                      width: 3,
                      color: 'rgba(198,225,255,1)',
                      shadowColor: 'rgba(198,225,255,1)', // 默认透明
                      shadowBlur: 5
                    }
                  },
                  pointer: { // 分隔线
                    shadowColor: '#fff', // 默认透明
                    shadowBlur: 5
                  },
                  title: {
                    offsetCenter: [0, '100%'],
                    textStyle: { // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                      fontWeight: 'bolder',
                      fontSize: 16,
                      color: '#608fbf',
                      shadowColor: '#608fbf', // 默认透明
                      shadowBlur: 10
                    }
                  },
                  detail: {
                    backgroundColor: 'rgba(0,128,255,0.8)',
                    borderWidth: 1,
                    borderColor: 'rgba(13, 87, 161, 0.5)',
                    shadowColor: 'rgba(13, 87, 161, 0.5)', // 默认透明
                    shadowBlur: 5,
                    offsetCenter: [0, '50%'], // x, y，单位px
                    textStyle: { // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                      fontWeight: 'bolder',
                      color: 'rgba(227, 241, 255, 0.9)',
                      fontSize: 18,
                      padding: [8, 8, 2, 8]
                    },
                    formatter: '{value}%'
                  },
                  data: [loadData[0]]
                },
                {
                  name: loadData[1].name,
                  type: 'gauge',
                  center: ['50%', '50%'],
                  min: 0,
                  max: 100,
                  startAngle: 205,
                  endAngle: -25,
                  splitNumber: 10,
                  radius: '68%',
                  axisLine: { // 坐标轴线
                    lineStyle: { // 属性lineStyle控制线条样式
                      color: [[0.09, 'lime'], [0.82, 'rgba(0,128,255,0.8)'], [1, '#FF2727']],
                      width: 3,
                      shadowColor: '#fff', // 默认透明
                      shadowBlur: 10
                    }
                  },
                  axisLabel: { // 坐标轴小标记
                    color: '#fff',
                    shadowColor: '#fff', // 默认透明
                    shadowBlur: 10
                  },
                  axisTick: { // 坐标轴小标记
                    length: 15, // 属性length控制线长
                    lineStyle: { // 属性lineStyle控制线条样式
                      color: 'auto',
                      shadowColor: '#fff', // 默认透明
                      shadowBlur: 10
                    }
                  },
                  splitLine: { // 分隔线
                    length: 20, // 属性length控制线长
                    lineStyle: { // 属性lineStyle（详见lineStyle）控制线条样式
                      width: 3,
                      color: 'rgba(198,225,255,1)',
                      shadowColor: 'rgba(198,225,255,1)', // 默认透明
                      shadowBlur: 5
                    }
                  },
                  pointer: { // 分隔线
                    shadowColor: '#fff', // 默认透明
                    shadowBlur: 5
                  },
                  title: {
                    offsetCenter: [0, '100%'],
                    textStyle: { // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                      fontWeight: 'bolder',
                      fontSize: 16,
                      color: '#608fbf',
                      shadowColor: '#608fbf', // 默认透明
                      shadowBlur: 10
                    }
                  },
                  detail: {
                    backgroundColor: 'rgba(0,128,255,0.8)',
                    borderWidth: 1,
                    borderColor: 'rgba(13, 87, 161, 0.5)',
                    shadowColor: 'rgba(13, 87, 161, 0.5)', // 默认透明
                    shadowBlur: 5,
                    offsetCenter: [0, '50%'], // x, y，单位px
                    textStyle: { // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                      fontWeight: 'bolder',
                      color: 'rgba(227, 241, 255, 0.9)',
                      fontSize: 18,
                      padding: [8, 8, 2, 8]
                    },
                    formatter: '{value}%'
                  },
                  data: [loadData[1]]
                },
                {
                  name: loadData[2].name,
                  type: 'gauge',
                  center: ['83%', '50%'],
                  min: 0,
                  max: 100,
                  startAngle: 205,
                  endAngle: -25,
                  splitNumber: 10,
                  radius: '68%',
                  axisLine: { // 坐标轴线
                    lineStyle: { // 属性lineStyle控制线条样式
                      color: [[0.09, 'lime'], [0.82, 'rgba(0,128,255,0.8)'], [1, '#FF2727']],
                      width: 3,
                      shadowColor: '#fff', // 默认透明
                      shadowBlur: 10
                    }
                  },
                  axisLabel: { // 坐标轴小标记
                    color: '#fff',
                    shadowColor: '#fff', // 默认透明
                    shadowBlur: 10
                  },
                  axisTick: { // 坐标轴小标记
                    length: 15, // 属性length控制线长
                    lineStyle: { // 属性lineStyle控制线条样式
                      color: 'auto',
                      shadowColor: '#fff', // 默认透明
                      shadowBlur: 10
                    }
                  },
                  splitLine: { // 分隔线
                    length: 20, // 属性length控制线长
                    lineStyle: { // 属性lineStyle（详见lineStyle）控制线条样式
                      width: 3,
                      color: 'rgba(198,225,255,1)',
                      shadowColor: 'rgba(198,225,255,1)', // 默认透明
                      shadowBlur: 5
                    }
                  },
                  pointer: { // 分隔线
                    shadowColor: '#fff', // 默认透明
                    shadowBlur: 5
                  },
                  title: {
                    offsetCenter: [0, '100%'],
                    textStyle: { // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                      fontWeight: 'bolder',
                      fontSize: 16,
                      color: '#608fbf',
                      shadowColor: '#608fbf', // 默认透明
                      shadowBlur: 10
                    }
                  },
                  detail: {
                    backgroundColor: 'rgba(0,128,255,0.8)',
                    borderWidth: 1,
                    borderColor: 'rgba(13, 87, 161, 0.5)',
                    shadowColor: 'rgba(13, 87, 161, 0.5)', // 默认透明
                    shadowBlur: 5,
                    offsetCenter: [0, '50%'], // x, y，单位px
                    textStyle: { // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                      fontWeight: 'bolder',
                      color: 'rgba(227, 241, 255, 0.9)',
                      fontSize: 18,
                      padding: [8, 8, 2, 8]
                    },
                    formatter: function (val) {
                      if (val === 0.0) return '0 B/s'
                      const k = 1024
                      const sizes = ['B/s', 'KB/s', 'MB/s', 'GB/s', 'TB/s']
                      const i = Math.floor(Math.log(val) / Math.log(k))
                      return (val / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i]
                    }
                  },
                  data: [loadData[2]]
                }
              ]
            }
            var load = echarts.init(this.$el.querySelector('.load'))
            load.setOption(option)
            this.echartResize(this.$el.querySelector('.load'), load)
          }
        })
      },
      // 数据增长趋势
      increase_echart () {
        var self = this
        var params = self.increaseDate
        this.$http.get('/analysis/increase-by-day?startDate=' + params.startDate + '&endDate=' + params.endDate).then(res => {
          if (res.code === 0) {
            var date = []// x轴
            var bar1Data = []// y轴
            res.data.forEach((value, i) => {
              date.push(value.x)
              bar1Data.push(value.y)
            })
            var option = {
              grid: {
                left: 45,
                top: '20%',
                right: 28,
                bottom: 40
              },
              legend: {
                data: ['增长量'],
                top: '5%',
                itemWidth: 10,
                itemHeight: 10,
                textStyle: {
                  color: ['rgba(128,0,255,1)', 'rgba(0,64,128,1)'],
                  padding: [0, 3, -3, 3]
                }
              },
              tooltip: {
                trigger: 'axis',
                axisPointer: {
                  type: 'cross'
                }
              },
              dataZoom: {
                type: 'inside',
                xAxisIndex: [0, 0],
                start: 0,
                end: 100
              },
              xAxis: {
                type: 'category',
                data: date,
                axisLabel: {
                  color: 'rgba(127, 191, 255, 0.25)',
                  interval: 0
                  // rotate:-40
                },
                axisLine: {
                  lineStyle: {
                    color: '#314661'
                  }
                },
                axisTick: {
                  alignWithLabel: false
                },
                boundaryGap: true// 刻度开始位置
              },
              yAxis: {
                name: '总数',
                nameTextStyle: {
                  padding: [0, -6],
                  align: 'right',
                  color: '#314661'
                },
                type: 'value',
                splitLine: {
                  show: false
                },
                axisLabel: {
                  color: 'rgba(127, 191, 255, 0.25)',
                  fontSize: 10
                },
                axisLine: {
                  lineStyle: {
                    color: '#314661'
                  }
                }
              },
              series: [
                {
                  name: '增长量',
                  type: 'bar',
                  barWidth: '26',
                  itemStyle: {
                    normal: {
                      barBorderRadius: [10, 0, 0, 0],
                      color: new echarts.graphic.LinearGradient(
                              0, 0, 0, 1, [{
                                offset: 0.2,
                                color: 'rgba(128,0,255,1)'

                              },
                                {
                                  offset: 1,
                                  color: 'rgba(0,64,128,1)'
                                }
                              ]
                      )
                    }
                  },
                  label: {
                    show: false,
                    position: 'top',
                    color: 'rgba(158,207,255,0.83)'
                  },
                  data: bar1Data,
                  zlevel: 11

                }
              ]
            }
            // 初始化图表
            var increase = echarts.init(this.$el.querySelector('.increase'))
            increase.setOption(option)
            self.echartResize(this.$el.querySelector('.increase'), increase)
          }
        })
      },
      // 用户活跃情况
      active_echart () {
        var self = this
        var time = []
        var dateList1 = [] // 下载量
        var dateList2 = [] // 搜索量
        var dateList3 = [] // 登陆量
        const params = self.activeDate
        this.$http.get('/analysis/user-active?startDate=' + params.startDate + '&endDate=' + params.endDate).then(res => {
          if (res.code === 0) {
            res.data.forEach((value) => {
              time.push(value.x)
              dateList1.push(value.downloadY)
              dateList2.push(value.searchY)
              dateList3.push(value.loginY)
            })
            var colors = ['rgba(0,128,255,1)']
            var option = {
              color: colors,
              grid: {
                left: 45,
                top: '20%',
                right: 28,
                bottom: 40
              },
              legend: {
                top: '5%',
                x: 'center',
                itemWidth: 24, // 设置图片宽度为10
                itemHeight: 8, // 设置图片高度为10
                data: [{
                  name: '登录量',
                  textStyle: {
                    color: 'rgba(0, 128, 255, 1)'
                  }
                }, {
                  name: '下载量',
                  textStyle: {
                    color: 'rgba(44, 195, 44, 1)'
                  }
                },
                  {
                    name: '搜索量',
                    textStyle: {
                      color: 'rgba(144, 63, 226, 1)'
                    }
                  }
                ]
              },
              tooltip: {
                trigger: 'axis',
                axisPointer: {
                  type: 'cross'
                }
              },
              dataZoom: {
                type: 'inside',
                xAxisIndex: [0, 0],
                start: 0,
                end: 100
              },
              xAxis: {
                type: 'category',
                data: time,
                axisLabel: {
                  color: 'rgba(127, 191, 255, 0.25)'
                },
                axisLine: {
                  lineStyle: {
                    color: '#314661'
                  }
                },
                axisTick: {
                  alignWithLabel: false
                },
                boundaryGap: false// 控制文字位置
              },
              yAxis: {
                type: 'value',
                splitLine: {
                  show: false
                },
                axisLabel: {
                  color: 'rgba(127, 191, 255, 0.25)',
                  fontSize: 10
                },
                axisLine: {
                  lineStyle: {
                    color: '#314661'
                  }
                }
              },
              series: [
                {
                  name: '登录量',
                  type: 'line',
                  data: dateList3,
                  smooth: true,
                  showAllSymbol: true,
                  // symbol: 'none',
                  symbol: 'circle',
                  symbolSize: 5,
                  lineStyle: {
                    normal: {
                      color: 'rgba(0,128,255,1)'
                    }
                  },
                  itemStyle: {
                    color: 'rgba(0,128,255,1)',
                    borderColor: '#7FBFFF',
                    borderWidth: 2
                  },
                  label: {
                    show: false
                  },
                  areaStyle: {
                    normal: {
                      barBorderRadius: 4,
                      color: new echarts.graphic.LinearGradient(
                              0, 0, 0, 1,
                              [
                                { offset: 0, color: 'rgba(0,128,255,1)' },
                                { offset: 1, color: 'rgba(0,128,255,0)' }
                              ]
                      )
                    }
                  }
                },
                {
                  name: '下载量',
                  type: 'line',
                  data: dateList1,
                  smooth: true,
                  showAllSymbol: true,
                  // symbol: 'none',
                  symbol: 'circle',
                  symbolSize: 5,
                  lineStyle: {
                    normal: {
                      color: 'rgba(44, 195, 44, 1)'
                    }
                  },
                  itemStyle: {
                    color: 'rgba(44, 195, 44, 1)',
                    borderColor: '#7FBFFF',
                    borderWidth: 2
                  },
                  label: {
                    show: false
                  },
                  areaStyle: {
                    normal: {
                      barBorderRadius: 4,
                      color: new echarts.graphic.LinearGradient(
                              0, 0, 0, 1,
                              [
                                { offset: 0, color: 'rgba(44, 195, 44, 1)' },
                                { offset: 1, color: 'rgba(44, 195, 44, 0)' }
                              ]
                      )
                    }
                  }
                },
                {
                  name: '搜索量',
                  type: 'line',
                  data: dateList2,
                  smooth: true,
                  showAllSymbol: true,
                  symbol: 'circle',
                  symbolSize: 5,
                  lineStyle: {
                    normal: {
                      color: 'rgba(144, 63, 226, 1)'
                    }
                  },
                  itemStyle: {
                    color: 'rgba(144, 63, 226, 1)',
                    borderColor: 'RGBA(220, 158, 255, 1)',
                    borderWidth: 2
                  },
                  label: {
                    show: false
                  },
                  areaStyle: {
                    normal: {
                      barBorderRadius: 4,
                      color: new echarts.graphic.LinearGradient(
                              0, 0, 0, 1,
                              [
                                { offset: 0, color: 'rgba(144, 63, 226, 1)' },
                                { offset: 1, color: 'rgba(144, 63, 226, 0)' }
                              ]
                      )
                    }
                  }
                }
              ]
            }
            var active = echarts.init(this.$el.querySelector('.active'))
            active.setOption(option)
            self.echartResize(this.$el.querySelector('.active'), active)
          }
        })
      },
      source_echart () {
        var self = this
        var pieOption = {
          background: 'transparent',
          series: [
            {
              name: '资源池情况',
              type: 'pie',
              radius: ['23', '28'],
              center: ['50%', '50%'],
              avoidLabelOverlap: false,
              hoverOffset: 3,
              selectedOffset: 3,
              labelLine: {
                normal: {
                  show: false
                }
              },
              label: {
                normal: {
                  show: false,
                  position: 'center'
                },
                emphasis: {
                  show: false,
                  rich: {
                    num: {
                      color: '#fff',
                      fontWeight: 'bold',
                      fontSize: '32'
                    },
                    unit: {
                      color: '#fff',
                      fontSize: '16'
                    }
                  }
                }
              },
              itemStyle: {
                normal: {
                  color: { // 完成的圆环的颜色
                    colorStops: [{
                      offset: 0,
                      color: 'rgba(0,96,191,1)' // 0% 处的颜色
                    }, {
                      offset: 0.4,
                      color: 'rgba(128,0,255,1)' // 100% 处的颜色
                    }]
                  }
                }
              },
              data: [
                { name: '资源池', value: '100' }
              ]
            }
          ]
        }
        for (var i = 0; i < self.poolType.length; i++) {
          // eslint-disable-next-line camelcase
          var source_echart = echarts.init(this.$el.querySelector('.source' + i))
          source_echart.setOption(pieOption)
        }
      },
      resize () {
        const self = this
        if (window.innerWidth === screen.width && window.innerHeight === screen.height) {
          self.isFullscreen = true
        } else {
          self.isFullscreen = false
        }
      },
      // echart重新渲染效果
      echartResize (modelEcharts, echart) {
        echart.resize({ height: modelEcharts.clientHeight, width: modelEcharts.clientWidth })
        var option = echart.getOption()
        echart.clear()
        echart.setOption(option)
      }
    },
    mounted () {
      var self = this
      self.init()
      window.addEventListener('resize', function () {
        if (self.$resizeLimit) {
          clearTimeout(self.$resizeLimit)
        }
        self.$resizeLimit = setTimeout(function () {
          self.resize()
        }, 200)
      })
      this.timer = setInterval(() => {
        this.cur_echart()
        this.load_echart()
      }, 60000)
    },
    beforeDestroy () {
      clearInterval(this.timer)
    },
    watch: {
      'isFullscreen': function (value) {
        var self = this
        self.source_echart()
        self.space_echart()
      }
    },
    components: {
      appWidgetBig
    }
  }
</script>
