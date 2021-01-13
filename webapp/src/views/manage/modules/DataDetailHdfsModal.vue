<template>
  <a-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :closable="false"
  >
    <template slot="footer">
      <a-button type="primary" @click="handleOk">确定</a-button>
    </template>
    <a-spin :spinning="confirmLoading">
      <a-descriptions bordered :column=1 title="文件信息">
        <a-descriptions-item label="文件名">
          {{ name }}
        </a-descriptions-item>
        <a-descriptions-item label="大小">
          {{ size }}
        </a-descriptions-item>
        <a-descriptions-item label="完整路径">
          {{ path }}
        </a-descriptions-item>
        <a-descriptions-item label="下载接口">
          <a :href="hdfsWebApi">{{hdfsWebApi}}</a>
        </a-descriptions-item>
        <a-descriptions-item label="Matlab学习结果">
          <span v-if="matlabDownloadPath.length > 1">
            <a :href="matlabDownloadPath">{{matlabDownloadPath}}</a>
          </span>
          <span v-else> 暂无</span>
        </a-descriptions-item>
      </a-descriptions>
    </a-spin>
  </a-modal>
</template>

<script>
  import DetailList from '@/components/tools/DetailList'
  import { hdfsDataDetail } from '@/api/data'
  const DetailListItem = DetailList.Item

    export default {
        name: 'DataDetailHDFSModal',
        components: {
          DetailList,
          DetailListItem
        },
        data () {
            return {
                title: '数据详情',
                visible: false,
                visibleAdd: false,
                confirmLoading: false,
                name: '',
                path: '',
                size: '',
                hdfsWebApi: '',
                matlabDownloadPath: ''
            }
        },
        created () {
        },
        methods: {
            show (name, size) {
                this.name = name.split('/')[3]
                this.path = name
                this.size = this.renderSize(size)
                this.hdfsWebApi = window.dmConfig.hdfsRestApi + this.path + '?op=OPEN'
                hdfsDataDetail({ fileName: name.split('.')[0] + '.zip' }).then((res) => {
                  this.matlabDownloadPath = window.dmConfig.hdfsRestApi + this.path.split('.')[0] + '.zip?op=OPEN'
                })
                this.visible = true
            },
            handleOk () {
                this.visible = false
            },
            showAddColumn () {
                this.visibleAdd = true
            },
            // 文件大小
            renderSize (value) {
            if (value == null || value === '') {
              return '0 Bytes'
            }
              const unitArr = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
              let index = 0
              var srcsize = parseFloat(value)
            index = Math.floor(Math.log(srcsize) / Math.log(1024))
              let size = srcsize / Math.pow(1024, index)
              //  保留的小数位数
            size = size.toFixed(2)
            return size + unitArr[index]
          },
          find (str, cha, num) {
            let x = str.indexOf(cha)
            for (let i = 0; i < num; i++) {
              x = str.indexOf(cha, x + 1)
            }
            return x
          }
        }
    }
</script>
