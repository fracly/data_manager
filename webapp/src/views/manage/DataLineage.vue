<template>
  <div>
    <a-card>
      <div>
        <a-form :form="form" layout="inline">
          <a-row :gutter="48">
            <a-col :md="5" :sm="12">
              <a-form-item label="数据源">
                <a-select
                  style="width:200px"
                  placeholder="请选择数据源"
                  @change="getDataList">
                  <a-select-option
                    v-for="(item) in dataSourceList"
                    :key="item.id"
                    :value="item.id">{{ item.name }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="12">
              <a-form-item label="数据">
                <a-select
                  style="width:200px"
                  placeholder="请选择数据"
                  @change="getDataId">
                  <a-select-option
                    v-for="(item) in dataList"
                    :key="item.id"
                    :value="item.id">{{ item.name }}
                  </a-select-option>
                </a-select>

              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="12">
              <a-button type="primary" @click="generateDiagram">生成关系图</a-button>
              <a-button type="primary" @click="enlargeDiagram" style="margin-right: 10px;margin-left: 10px">放大</a-button>
              <a-button type="primary" @click="narrowDiagram">缩小</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <a-divider style="margin-bottom: 22px"/>
      <a-card style="height: 600px" id="myDiagramDiv">
      </a-card>
    </a-card>
  </div>
</template>

<script>
import { PageView } from '@/layouts'
import { STable } from '@/components'
import { queryDataSource } from '@/api/datasource'
import { getDataSourceDataList } from '@/api/manage'
import { dataLineage } from '@/api/data'

import go from 'gojs'

const keyArray = [
    { key: 'Alpha', color: 'lightblue' },
    { key: 'Beta', color: 'orange' },
    { key: 'Gamma', color: 'lightgreen' },
    { key: 'Delta', color: 'pink' }
]
const relationArray = [
    { from: 'Alpha', to: 'Beta' },
    { from: 'Alpha', to: 'Gamma' },
    { from: 'Alpha', to: 'Delta' }
]
    export default {
        components: {
            PageView,
            STable
        },
        data () {
            return {
                dataSourceList: [],
                dataList: [],
                dataId: null,
                lineage: Object,
                form: {},
                relationArray,
                keyArray,
                myDiagram: null
            }
        },
        methods: {
            getDataSourceList () {
                queryDataSource({ 'type': 0 }).then(res => {
                    if (res.code === 0) {
                        this.dataSourceList = res.data
                    } else {
                        console.log('获取数据源列表失败')
                    }
                })
            },
            getDataList (dataSourceId) {
              debugger
                this.dataList = []
                getDataSourceDataList({ 'dataSourceId': dataSourceId, 'pageSize': 1000000 }).then(res => {
                    if (res.code === 0) {
                        this.dataList = res.data
                    } else {
                        console.log('获取数据列表失败')
                    }
                })
            },
            getDataId (val) {
              this.dataId = val
            },
            init: function () {
                var $ = go.GraphObject.make // for conciseness in defining templates

                this.myDiagram =
                    $(go.Diagram, 'myDiagramDiv', // create a Diagram for the DIV HTML element
                        { // enable undo & redo
                            'undoManager.isEnabled': true
                        })

                // define a simple Node template
                this.myDiagram.nodeTemplate =
                    $(go.Node, 'Auto', // the Shape will go around the TextBlock
                        $(go.Shape, 'RoundedRectangle',
                            { strokeWidth: 0, fill: 'white' }, // default fill is white
                            // Shape.fill is bound to Node.data.color
                            new go.Binding('fill', 'color')),
                        $(go.TextBlock,
                            { margin: 8 }, // some room around the text
                            // TextBlock.text is bound to Node.data.key
                            new go.Binding('text', 'key'))
                    )

                // but use the default Link template, by not setting Diagram.linkTemplate

                // create the model data that will be represented by Nodes and Links
            },
            generateDiagram: function () {
                dataLineage({
                    'dataId': this.dataId
                }).then((res) => {
                    this.myDiagram.model = new go.GraphLinksModel(res.data.keyList, res.data.relationList)
                })
            },
            enlargeDiagram: function () {
                this.myDiagram.scale += 0.1
            },
            narrowDiagram: function () {
                this.myDiagram.scale -= 0.1
            }
        },
        filters: {
        },
        mounted () {
            this.getDataSourceList()
            this.init()
        },
        computed: {
            title () {
                return this.$route.meta.title
            }
        }

    }
</script>

<style lang="less" scoped>
  .title {
    color: rgba(0, 0, 0, .85);
    font-size: 16px;
    font-weight: 500;
    margin-bottom: 16px;
  }
  canvas {
    outline: none;
  }
</style>
