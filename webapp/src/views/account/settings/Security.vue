<template>
  <div>
  <a-list
    itemLayout="horizontal"
    :dataSource="data"
  >
    <a-list-item slot="renderItem" slot-scope="item, index" :key="index">
      <a-list-item-meta>
        <a slot="title">{{ item.title }}</a>
        <span slot="description">
          <span class="security-list-description">{{ item.description }}</span>
          <span v-if="item.value"> : </span>
          <span class="security-list-value">{{ item.value }}</span>
        </span>
      </a-list-item-meta>
      <template v-if="item.actions">
        <a slot="actions" @click="show">{{ item.actions.title }}</a>
      </template>

    </a-list-item>
  </a-list>
  <modify-password-modal
    ref="modifyModal"
    :visible="visible"
    :loading="loading"
    :user="user"
    @cancel="handleCancel"
    @ok="handleOk"
  ></modify-password-modal>
  </div>
</template>

<script>
import { getInfo } from '@/api/login'
import ModifyPasswordModal from './ModifyPasswordModal'
import { systemUserModifyPassword } from '@/api/system'
import md5 from 'md5'

export default {
  components: {
      ModifyPasswordModal
  },
  data () {
    return {
      data: [
        { title: '账户密码', description: '当前账号状态', value: '正常', actions: { title: '修改密码' } },
        { title: '绑定手机', description: '已绑定手机', value: '' },
        { title: '绑定邮箱', description: '已绑定邮箱', value: '' }
      ],
        visible: false,
        loading: false,
        user: {}
    }
  },
  methods: {
      show () {
          this.visible = true
      },
      handleOk () {
          const form = this.$refs.modifyModal.form
          this.loading = true
          form.validateFields((errors, values) => {
              if (!errors) {
                  if (values.password1 !== values.password2) {
                      this.$message.error('两次输入的密码不一致')
                      this.loading = false
                  } else {
                      systemUserModifyPassword({
                          'id': this.user.id,
                          'password': md5(values.password1)
                      }).then(res => {
                          if (res.code === 0) {
                              this.$message.success(res.msg)
                          } else {
                              this.$message.error(res.msg)
                          }
                          this.list()
                      })
                  }
                  this.visible = false
                  this.loading = false
              } else {
                  this.loading = false
              }
          })
      },
      handleCancel () {
        this.visible = false
      },
      getUserInfo () {
          getInfo().then(res => {
              this.user = res.data
              this.data[1].value = res.data.phone
              this.data[2].value = res.data.email
          })
      }
  },
    created () {
        this.getUserInfo()
    }
}
</script>

<style scoped>

</style>
