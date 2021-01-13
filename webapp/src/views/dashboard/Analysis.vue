<template>
  <div class="page-header-index-wide">
    <a-row :gutter="24">
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="数据空间总量" :total="useCapacity">
          <a-tooltip title="现有数据量/总容量大小/使用率" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">系统总容量</span>
              {{ this.totalCapacity }}
            </trend>
            <trend flag="up">
              <span slot="term">空间使用率</span>
              {{ this.usePercentage }}
            </trend>
          </div>
          <template slot="footer"> <span v-if="usePercentage <= '80%'">数据存储空间目前容量充足</span><span v-else>数据存储空间消耗较高，请注意空间告警</span></template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="数据健康度" :total="dataNormalPercentage">
          <a-tooltip title="系统中正常状态的数据百分比" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <mini-progress color="rgb(19, 194, 194)" :target="100" :percentage="this.dataNormalCount/this.dataTotalCount * 100" height="8px" />
          </div>
          <template slot="footer">
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">数据总数</span>{{ this.dataTotalCount }}
            </trend>
            <trend flag="up">
              <span slot="term">正常数据数</span>{{ this.dataNormalCount }}
            </trend>
          </template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="数据源数量" :total="this.dataSourceCount">
          <a-tooltip title="系统中数据源数量以及种类详情" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">Hive</span>
              {{ this.hiveDataSourceCount }}
            </trend>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">HBase</span>
              {{ this.hbaseDataSourceCount }}
            </trend>
            <trend flag="up">
              <span slot="term">HDFS</span>
              {{ this.hdfsDataSourceCount }}
            </trend>
          </div>
          <template slot="footer">数据源的连接信息统计!</template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="下载文件数" :total="downloadCount">
          <a-tooltip title="每日系统中下载文件的数量" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <mini-smooth-area :style="{ height: '45px' }" :dataSource="downloadByDay" :scale="downloadScale" />
          </div>
          <template slot="footer">日均下载文件数 <span>{{ avgDownloadCount }}</span></template>
        </chart-card>
      </a-col>
    </a-row>

    <a-card :loading="loading" :bordered="false" :body-style="{padding: '0'}">
      <div class="salesCard">
        <a-tabs default-active-key="1" size="large" :tab-bar-style="{marginBottom: '24px', paddingLeft: '16px'}">
          <div class="extra-wrapper" slot="tabBarExtraContent">
            <div class="extra-item">
              <a-button type="primary"><a @click="handleDownload" :href="url">导出报表</a></a-button> &nbsp;&nbsp;
              <a-radio-group v-model="increateDateType"  @change="handleIncreateDateTypeChange" buttonStyle="solid">
                <a-radio-button value="7">近7天</a-radio-button>
                <a-radio-button value="30">近30天</a-radio-button>
                <a-radio-button value="90">近90天</a-radio-button>
                <a-radio-button value="180">近180天</a-radio-button>
              </a-radio-group>
            </div>
            <a-range-picker :style="{width: '256px'}" v-model="increaseDateRange" @change="handleIncreateDateRange"/>
          </div>
          <a-tab-pane loading="true" tab="新增量" key="1">
            <a-row>
              <a-col :xl="16" :lg="12" :md="12" :sm="24" :xs="24">
                <bar :data="barData" :scale="increaseByDayScale" title="每日新增趋势" />
              </a-col>
              <a-col :xl="8" :lg="12" :md="12" :sm="24" :xs="24">
                <rank-list title="标签排行榜" :list="rankList"/>
              </a-col>
            </a-row>
          </a-tab-pane>
        </a-tabs>
      </div>
    </a-card>

    <div class="antd-pro-pages-dashboard-analysis-twoColLayout" :class="isDesktop() ? 'desktop' : ''">
      <a-row :gutter="24" type="flex" :style="{ marginTop: '24px' }">
        <a-col :xl="12" :lg="24" :md="24" :sm="24" :xs="24">
          <a-card :loading="loading" :bordered="false" title="数据搜索情况" :style="{ height: '100%' }">
            <a-row :gutter="68">
              <a-col :xs="24" :sm="12" :style="{ marginBottom: ' 24px'}">
                <number-info :total=searchCountTotal >
                  <span slot="subtitle">
                    <span>搜索次数</span>
                    <a-tooltip title="系统所有用户近30日的搜索次数" slot="action">
                      <a-icon type="info-circle-o" :style="{ marginLeft: '8px' }" />
                    </a-tooltip>
                  </span>
                </number-info>
                <!-- miniChart -->
                <div>
                  <mini-smooth-area :style="{ height: '45px' }" :dataSource="searchCountData" :scale="searchCountScale" />
                </div>
              </a-col>
              <a-col :xs="24" :sm="12" :style="{ marginBottom: ' 24px'}">
                <number-info :total=searchUserTotal >
                  <span slot="subtitle">
                    <span>搜索用户数</span>
                    <a-tooltip title="系统近30日搜索的用户数" slot="action">
                      <a-icon type="info-circle-o" :style="{ marginLeft: '8px' }" />
                    </a-tooltip>
                  </span>
                </number-info>
                <!-- miniChart -->
                <div>
                  <mini-smooth-area :style="{ height: '45px' }" :dataSource="searchUserData" :scale="searchUserScale" />
                </div>
              </a-col>
            </a-row>
            <div class="ant-table-wrapper">
              <a-table
                row-key="index"
                size="small"
                :columns="searchTableColumns"
                :dataSource="searchData"
                :pagination="{ pageSize: 5 }"
              >
                <span slot="range" slot-scope="text, record">
                  <trend :flag="record.status === 0 ? 'up' : 'down'">
                    {{ text }}%
                  </trend>
                </span>
              </a-table>
            </div>
          </a-card>
        </a-col>
        <a-col :xl="12" :lg="24" :md="24" :sm="24" :xs="24">
          <a-card class="antd-pro-pages-dashboard-analysis-salesCard" :loading="loading" :bordered="false" title="文件类型占比" :style="{ height: '100%' }">
            <div slot="extra" style="height: inherit;">
              <div class="analysis-salesTypeRadio">
                <a-radio-group defaultValue="0" v-model="typePercentageDateType" @change="handleTypePercentageDateTypeChange">
                  <a-radio-button value="0">全部</a-radio-button>
                  <a-radio-button value="1">今日</a-radio-button>
                  <a-radio-button value="7">近一周</a-radio-button>
                  <a-radio-button value="30">近一月</a-radio-button>
                  <a-radio-button value="180">近半年</a-radio-button>
                </a-radio-group>
              </div>

            </div>
            <h4>文件大小(GB)</h4>
            <div>
              <div>
                <v-chart :force-fit="true" :height="405" :data="pieData" :scale="pieScale">
                  <v-tooltip :showTitle="false" dataKey="item*percent" />
                  <v-axis />
                  <v-legend dataKey="item"/>
                  <v-pie position="percent" color="item" :vStyle="pieStyle" />
                  <v-coord type="theta" :radius="0.75" :innerRadius="0.6" />
                </v-chart>
              </div>

            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script>
import moment from 'moment'
import { ChartCard, MiniArea, MiniBar, MiniProgress, RankList, Bar, Trend, NumberInfo, MiniSmoothArea } from '@/components'
import { mixinDevice } from '@/utils/mixin'
import { getCapacity, getDataCount, getDataSourceCount, getDownloadCount, getIncreaseByDay, getLabelTop10,
    getSearchCountByDay, getDataTypePercentage, getSearchUserByDay, getSearchKeyWordTop10, getReportDownload } from '@/api/analysis'

const searchUserData = []
for (let i = 0; i < 7; i++) {
  searchUserData.push({
    x: moment().add(i, 'days').format('YYYY-MM-DD'),
    y: Math.ceil(Math.random() * 10)
  })
}
const searchUserScale = [
  {
    dataKey: 'x',
    alias: '时间'
  },
  {
    dataKey: 'y',
    alias: '用户数'
  }]

const increaseByDayScale = [
    {
        dataKey: 'x',
        alias: '时间'
    },
    {
        dataKey: 'y',
        alias: '新增数'
    }]

const searchCountScale = [
    {
        dataKey: 'x',
        alias: '时间'
    },
    {
        dataKey: 'y',
        alias: '搜索次数'
    }
]

const downloadScale = [
    {
        dataKey: 'x',
        alias: '时间'
    },
    {
        dataKey: 'y',
        alias: '下载数'
    }]

const searchTableColumns = [
  {
    dataIndex: 'index',
    title: '排名',
    width: 90
  },
  {
    dataIndex: 'keyword',
    title: '搜索关键词'
  },
  {
    dataIndex: 'count',
    title: '次数'
  },
  {
    dataIndex: 'userCount',
    title: '用户数'
  }
]

const DataSet = require('@antv/data-set')

const pieScale = [{
  dataKey: 'percent',
  min: 0,
  formatter: '.0%'
}]

export default {
  name: 'Analysis',
  mixins: [mixinDevice],
  components: {
    ChartCard,
    MiniArea,
    MiniBar,
    MiniProgress,
    RankList,
    Bar,
    Trend,
    NumberInfo,
    MiniSmoothArea
  },
  data () {
    return {
      loading: true,
      rankList: [],

      // 容量统计
      totalCapacity: '0TB',
      usePercentage: '0%',
      useCapacity: '0TB',

      // 数据数量统计
      dataNormalPercentage: '0%',
      dataTotalCount: 0,
      dataNormalCount: 0,

      // 数据源数量统计
      dataSourceCount: 0,
      hiveDataSourceCount: 0,
      hbaseDataSourceCount: 0,
      hdfsDataSourceCount: 0,

      // 下载量统计
      downloadScale,
      downloadCount: 0,
      avgDownloadCount: 0,
      downloadByDay: [],

      // 每日新增数据量初始值
      increateDateType: '7',
      endDate: moment().format('YYYY-MM-DD'),
      startDate: moment().subtract(7, 'days').format('YYYY-MM-DD'),

      // 数据类型初始值
      typePercentageDateType: '7',
      searchCountTotal: 0,
      searchUserTotal: 0,
      searchUserData: [],
      searchUserScale,
      increaseByDayScale,
      searchCountData: [],
      searchCountScale,
      searchTableColumns,
      searchData: [],
      increaseDateRange: [],
      barData: [],
      //
      sourceData: [],
      pieScale,
      pieData: [],
      pieStyle: {
        stroke: '#fff',
        lineWidth: 1
      },
      url: ''
    }
  },
  methods: {
     getCapacity () {
         getCapacity().then(res => {
             this.totalCapacity = res.data.CapacityTotal == null ? 0 : res.data.CapacityTotal
             this.useCapacity = res.data.CapacityUsed == null ? 0 : res.data.CapacityUsed
             this.usePercentage = res.data.CapacityUsedPercentage == null ? 0 : res.data.CapacityUsedPercentage
         })
     },
     getDataCount () {
         getDataCount().then(res => {
             this.dataTotalCount = res.data.total == null ? 0 : res.data.total
             this.dataNormalCount = res.data.normal == null ? 0 : res.data.normal
             this.dataNormalPercentage = res.data.normalPercentage == null ? 0 : res.data.normalPercentage
         })
     },
     getDataSourceCount () {
         getDataSourceCount().then(res => {
             this.dataSourceCount = res.data.total == null ? 0 : res.data.total
             this.hiveDataSourceCount = res.data.Hive == null ? 0 : res.data.Hive
             this.hbaseDataSourceCount = res.data.HBase == null ? 0 : res.data.HBase
             this.hdfsDataSourceCount = res.data.HDFS == null ? 0 : res.data.HDFS
         })
     },
     getDownloadCount () {
         getDownloadCount().then(res => {
             this.downloadCount = res.dataMap.total
             this.avgDownloadCount = res.dataMap.avgDay
             this.downloadByDay = res.data
         })
     },
     getIncreaseByDay () {
         getIncreaseByDay({
             'startDate': this.startDate,
             'endDate': this.endDate
         }).then(res => {
             this.barData = res.data
         })
     },
     getLabelTop10 () {
         getLabelTop10({
             'startDate': this.startDate,
             'endDate': this.endDate
         }).then(res => {
             this.rankList = res.data
         })
     },
     getSearchKeyWordTop10 () {
         getSearchKeyWordTop10().then(res => {
             this.searchData = res.data
         })
     },
     getDataTypePercentage () {
         getDataTypePercentage({ 'type': this.typePercentageDateType }).then(res => {
             this.sourceData = res.data
             const dv = new DataSet.View().source(this.sourceData)
             dv.transform({
                 type: 'percent',
                 field: 'count',
                 dimension: 'item',
                 as: 'percent'
             })
             this.pieData = dv.rows
         })
     },
     getSearchUserByDay () {
        getSearchUserByDay().then(res => {
            this.searchUserData = res.data
            this.searchUserTotal = res.dataMap.total
        })
     },
     getSerachCountByDay () {
        getSearchCountByDay().then(res => {
            this.searchCountData = res.data
            this.searchCountTotal = res.dataMap.total
        })
     },
     handleIncreateDateTypeChange () {
        if (this.increateDateType === '7') {
            this.startDate = moment().subtract(7, 'days').format('YYYY-MM-DD')
        } else if (this.increateDateType === '30') {
            this.startDate = moment().subtract(30, 'days').format('YYYY-MM-DD')
        } else if (this.increateDateType === '90') {
            this.startDate = moment().subtract(90, 'days').format('YYYY-MM-DD')
        } else if (this.increateDateType === '180') {
            this.startDate = moment().subtract(180, 'days').format('YYYY-MM-DD')
        }
        this.getIncreaseByDay()
        this.getLabelTop10()
     },
     handleIncreateDateRange (res) {
         this.startDate = res[0].format('YYYY-MM-DD')
         this.endDate = res[1].format('YYYY-MM-DD')
         this.getIncreaseByDay()
         this.getLabelTop10()
     },
     handleTypePercentageDateTypeChange () {
         this.getDataTypePercentage()
     },
     handleDownload () {
       this.url = 'javascript:;'
       getReportDownload({ 'startDate': this.startDate, 'endDate': this.endDate })
     }
  },
  created () {
    setTimeout(() => {
      this.getCapacity()
      this.getDataCount()
      this.getDataSourceCount()
      this.getDownloadCount()
      this.getIncreaseByDay()
      this.getLabelTop10()
      this.getSearchKeyWordTop10()
      this.getDataTypePercentage()
      this.getSearchUserByDay()
      this.getSerachCountByDay()
      this.loading = !this.loading
    }, 1000)
  }
}
</script>

<style lang="less" scoped>
  .extra-wrapper {
    line-height: 55px;
    padding-right: 24px;

    .extra-item {
      display: inline-block;
      margin-right: 24px;

      a {
        margin-left: 24px;
      }
    }
  }

  .antd-pro-pages-dashboard-analysis-twoColLayout {
    position: relative;
    display: flex;
    display: block;
    flex-flow: row wrap;
  }

  .antd-pro-pages-dashboard-analysis-salesCard {
    height: calc(100% - 24px);
    /deep/ .ant-card-head {
      position: relative;
    }
  }

  .dashboard-analysis-iconGroup {
    i {
      margin-left: 16px;
      color: rgba(0,0,0,.45);
      cursor: pointer;
      transition: color .32s;
      color: black;
    }
  }
  .analysis-salesTypeRadio {
    position: absolute;
    right: 54px;
    bottom: 12px;
  }
</style>
