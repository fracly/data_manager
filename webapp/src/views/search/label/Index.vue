<template>
  <div>
    <a-card :bordered="isShow">
      <div>
        <a-input placeholder="搜索标签" v-model="params.searchVal" style="width: 280px;margin-right: 10px;"/>
        <a-button type="primary" style="margin-right: 10px;" @click="search">搜索</a-button>
        <a-button type="primary" @click="$refs.createLabel.add()">新建标签</a-button>
      </div>
      <a-divider style="margin-bottom: 22px"/>
      <a-card :bordered="isShow">
        <a-card-grid
          style="width:30%;text-align:left;box-shadow:none;"
          v-for="(item, index) in labelList"
          :key="item.name">
          <span style="font-weight: bold;color:dodgerblue;margin-bottom: 20px;">{{ index }}</span>
          <br/><br/>
          <span style="font-size: 12px;" v-for="(i,index2) in labelList[index]" :key="i.id">
            <span
              style="font-weight: bold;"
              @mouseover="handleFocus(index,index2)"
              >{{ i.name }}</span> ({{ i.total }})
            <a-icon
              @click="handleDelete(i.id)"
              v-show="index===i1&&index2===i2"
              style="color:red;font-size:12px;"
              type="minus-circle"></a-icon>
            <a-icon
              @click="handleEdit(i)"
              v-show="index===i1&&index2===i2"
              style="color:#00A0E9;font-size:12px;margin-left: 4px;"
              type="edit"></a-icon>
            <br/>
          </span>
        </a-card-grid>
      </a-card>
    </a-card>
    <create-label ref="createLabel" @ok="handleOk"/>
  </div>
</template>

<script>
    import { PageView } from '@/layouts'
    import { STable } from '@/components'
    import CreateLabel from './CreateLabel'
    import { createLabel, updateLabel, getLabelList, deleteLabel } from '@/api/label'
    export default {
        components: {
            CreateLabel,
            PageView,
            STable
        },
        data () {
            return {
                isShow: false,
                labelList: [],
                params: {
                    name: '',
                    searchVal: ''
                },
                i1: 0,
                i2: 0
            }
        },
        methods: {
            /**
             * 新增标签
             */
            handleOk: function (params) {
                if (params.id > 0) {
                    updateLabel(params).then((res) => {
                        if (res.code === 0) {
                            this.$message.success(res.msg)
                        } else {
                            this.$message.error(res.msg)
                        }
                        this.getData()
                    }).catch(() => {
                        this.$message.error('更新标签失败')
                    })
                } else {
                    createLabel(params).then((res) => {
                        if (res.code === 0) {
                            this.$message.success(res.msg)
                        } else {
                            this.$message.error(res.msg)
                        }
                        this.getData()
                    }).catch(err => {
                        this.$message.error('创建标签失败')
                        console.log('fail', err)
                    })
                }
            },
            getData: function () {
                getLabelList(this.params).then((res) => {
                    this.labelList = res.data
                }).catch(err => {
                    console.log('fail', err)
                }).finally(() => {
                    // do nothing
                })
            },
            search: function () {
                this.getData()
            },
            handleDelete (id) {
                deleteLabel({ 'labelId': id }).then((res) => {
                    if (res.code === 0) {
                        this.$message.success(res.msg)
                    } else {
                        this.$message.error(res.msg)
                    }
                    this.getData()
                })
            },
            handleEdit (record) {
                this.$refs.createLabel.edit(record)
            },
            handleFocus (i, i2) {
              this.i1 = i
              this.i2 = i2
            }
        },
        filters: {
            statusFilter (status) {
                const statusMap = {
                    'processing': '进行中',
                    'success': '完成',
                    'failed': '失败'
                }
                return statusMap[status]
            }
        },
        mounted () {
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
  .title {
    color: rgba(0, 0, 0, .85);
    font-size: 16px;
    font-weight: 500;
    margin-bottom: 16px;
  }
</style>
