<template>
  <div>
    <a-card :bordered="false" class="ant-pro-components-tag-select">
      <a-form :form="form" layout="inline">
        <standard-form-row title="标签" block style="padding-bottom: 11px;">
          <a-form-item>
            <a-select
              mode="multiple"
              style="width:800px"
              placeholder="请选择标签"
              @change="getData"
              v-model="labels">
              <a-select-option
                v-for="(item) in labelList"
                :key="item.id"
                :value="item.id">{{ item.name }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </standard-form-row>

        <standard-form-row title="时间范围" grid>
          <a-row>
            <a-col :md="24">
              <a-form-item :wrapper-col="{ 'span': 24 }">
                <a-range-picker
                  style="max-width: 468px; width: 100%;"
                  showTime
                  format="YYYY-MM-DD HH:mm:ss"
                  @change="handleDate"
                  v-model="date">
                </a-range-picker>
              </a-form-item>
            </a-col>
          </a-row>
        </standard-form-row>

      </a-form>
    </a-card>
    <a-card style="margin-top: 24px;" :bordered="false">
      <a-table :dataSource="dataList" :pagination="pagination" :columns="columns" rowKey="id">
        <span slot="serial" slot-scope="text, record, index">
          {{ index + 1 }}
        </span>
        <span slot="label" slot-scope="text">
          <a-tag v-for="tag in text" :key="tag.name" color="blue">
            {{ tag.name }}
          </a-tag>
        </span>
        <span slot="time" slot-scope="text">
          {{ text | moment }}
        </span>
        <span slot="action" slot-scope="text, record">
          <template>
            <a @click="handlePreview(record)">预览</a>
            <a-divider type="vertical" v-if="record.type !== 2"/>
            <a v-if="record.type !== 2" @click="handleDownload(record.id,record.type,record.dataName)" :href="url">下载</a>
            <a-divider type="vertical" v-if="record.type === 2"/>
            <a v-if="record.type === 2" @click="handleSend(record.id,record.type,record.dataName)">发送</a>
          </template>
        </span>
      </a-table>
      <data-preview-model ref="modal" :visible.sync="visible" :loading="loading"></data-preview-model>
      <data-detail-hdfs-modal ref="modal2"></data-detail-hdfs-modal>
      <data-download-model ref=":modal3" :recordId="id" :visible.sync="visible2"></data-download-model>
      <data-send-modal ref="modal4" :visible.sync="visible3"></data-send-modal>
    </a-card>
  </div>
</template>

<script>
    import { PageView } from '@/layouts'
    import { TagSelect, StandardFormRow, ArticleListContent, STable } from '@/components'
    import { getLabelFlatList } from '@/api/label'
    import { dataSearch, dataPreview, dataDownloadUrl, hdfsDataDownload, dataDownloadLog, hdfsDataDetail } from '@/api/data'
    import DataPreviewModel from './DataPreviewModel'
    import DataDetailHdfsModal from '../../manage/modules/DataDetailHdfsModal'
    import DataDownloadModel from './DataDownloadModel'
    import DataSendModal from './DataSendModel'

    const TagSelectOption = TagSelect.Option

    export default {
        components: {
            STable,
            PageView,
            TagSelect,
            TagSelectOption,
            StandardFormRow,
            ArticleListContent,
            DataPreviewModel,
            DataDetailHdfsModal,
            DataDownloadModel,
            DataSendModal
        },
        data () {
            return {
                search: true,
                tabs: {
                    items: [
                        {
                            key: '0',
                            title: '全部'
                        },
                        {
                            key: '1',
                            title: 'Hive'
                        },
                        {
                            key: '2',
                            title: 'HBase'
                        },
                        {
                            key: '3',
                            title: 'HDFS'
                        }
                    ],
                    active: (key) => {
                        switch (this.params.type) {
                            case '1':
                                return '1'
                            case '2':
                                return '2'
                            case '3':
                                return '3'
                            default:
                                return '0'
                        }
                    },
                    callback: (key) => {
                        this.params.type = key
                        this.getData()
                    }

                },
                searchOp: (key) => {
                    this.params.name = key
                    this.getData()
                },
                labelList: [],
                form: this.$form.createForm(this),
                dataList: [],
                pagination: {
                    total: 20,
                    pageSize: 10,
                    showTotal: (total) => `共${total}条`,
                    showSizeChanger: true,
                    pageSizeOptions: ['10', '20', '50', '100'],
                    onChange: (page, pageSize) => {
                        this.params.pageNo = page
                        this.params.pageSize = pageSize
                        this.getData()
                    },
                    onShowSizeChange: (current, size) => {
                        this.params.pageNo = current
                        this.params.pageSize = size
                        this.getData()
                    }
                },
                labels: [],
                date: [],
                params: {
                    labels: '',
                    name: '',
                    type: 0,
                    pageNo: 0,
                    pageSize: 10
                },
                columns: [
                    {
                        title: '#',
                        scopedSlots: { customRender: 'serial' }
                    },
                    {
                        title: '数据名称',
                        dataIndex: 'name',
                        key: 'name',
                        scopedSlots: { customRender: 'name' }
                    },
                    {
                        title: '表名/文件名',
                        dataIndex: 'dataName1',
                        key: 'dataName1'
                    },
                    {
                        title: '标签',
                        dataIndex: 'labelList',
                        scopedSlots: {
                            customRender: 'label'
                        }
                    },
                    {
                        title: '数据类型',
                        dataIndex: 'type',
                        key: 'type',
                        customRender: (text) => {
                            if (text === 1) {
                                return 'Hive表'
                            } else if (text === 2) {
                                return 'HBase表'
                            } else {
                                return 'HDFS文件'
                            }
                        }
                    },
                    {
                        title: '描述',
                        key: 'description',
                        dataIndex: 'description'
                    },
                    {
                        title: '创建时间',
                        key: 'createTime',
                        dataIndex: 'createTime',
                        scopedSlots: { customRender: 'time' }
                    },
                    {
                        title: '更新时间',
                        key: 'updateTime',
                        dataIndex: 'updateTime',
                        scopedSlots: { customRender: 'time' }
                    },
                    {
                        title: '操作',
                        key: 'action',
                        scopedSlots: { customRender: 'action' }
                    }
                ],
                url: '',
                visible: false,
                visible2: false,
                visible3: false,
                loading: false,
                id: 0
            }
        },
        methods: {
            loadMore () {
            },
            getLabels: function () {
                getLabelFlatList().then((res) => {
                    this.labelList = res.data
                }).catch(err => {
                    console.log('fail', err)
                })
            },
            // 时间改变的方法
            handleDate: function (date, dateString) {
                this.params.startTime = dateString[0]
                this.params.endTime = dateString[1]
                this.getData()
            },
           /**
           * 下载
           * @param id
           * @param type
           */
            handleDownload: function (id, type, name) {
              if (type === 3) {
                  this.url = 'javascript:;'
                  dataDownloadUrl({ fileName: name }).then(res => {
                      hdfsDataDownload({ url: res })
                      dataDownloadLog({ 'dataId': id }).then(() => {
                      }).catch(err => {
                          console.log(err)
                      })
                  }).catch(err => {
                      this.$message.error('下载失败，文件已丢失!')
                      console.log('下载失败' + err)
                  })
              } else {
                this.url = 'javascript:;' // 保持页面不跳转
                this.visible2 = true
                this.id = id
              }
            },
          // 预览
          handlePreview: function (record) {
            if (record.type === 1) {
                this.visible = true
                this.loading = true
              const params = {}
              params.dataId = record.id
              dataPreview(params).then((res) => {
                  this.$refs.modal.show(res)
                  this.loading = false
              }).catch(err => {
                console.log('fail', err)
              }).finally(() => {
              })
            } else if (record.type === 3) {
                hdfsDataDetail({ fileName: record.dataName }).then((res) => {
                    this.$refs.modal2.show(record.dataName, res.FileStatus.length)
                }).catch(() => {
                    this.$message.error('预览失败，文件已丢失！')
                })
            } else if (record.type === 2) {
              this.$message.info('HBase数据暂不支持预览')
            }
          },
          handleSend: function (id) {
              this.visible3 = true
              this.$refs.modal4.show(id)
          },
            getData: function () {
                this.params.labels = this.labels.toString()
                dataSearch(this.params).then((res) => {
                    this.dataList = res.data
                    for (let i = 0; i < this.dataList.length; i++) {
                      if (this.dataList[i].type === 3) {
                        this.dataList[i].dataName1 = this.dataList[i].dataName.split('/')[3]
                      } else {
                        this.dataList[i].dataName1 = this.dataList[i].dataName
                      }
                    }
                    this.pagination.pageSize = res.dataMap.pageSize
                    this.pagination.total = res.dataMap.total
                }).catch(err => {
                    this.$message.error('查询数据失败' + err)
                })
            }
        },
        mounted () {
            this.getLabels()
            this.getData()
        },
        computed: {
            title () {
                return this.$route.meta.title
            }
        }
    }
</script>

<style lang="less" scoped>
  .ant-pro-components-tag-select {
    /deep/ .ant-pro-tag-select .ant-tag {
      margin-right: 24px;
      padding: 0 8px;
      font-size: 14px;
    }
  }

  .list-articles-trigger {
    margin-left: 12px;
  }
</style>
