<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="4" :sm="12">
            <a-form-item label="角色名称">
              <a-input v-model="queryParam.name" placeholder="名称模糊匹配" ></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="12">
            <a-form-item label="角色状态">
              <a-select v-model="queryParam.status" placeholder="请选择" default-value="999">
                <a-select-option value="0">全部</a-select-option>
                <a-select-option value="1">正常</a-select-option>
                <a-select-option value="9">禁用</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" @click="handleSearch()">查询</a-button>
              <a-button type="primary" style="margin-left: 8px" @click="handleAdd">新建角色</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-table
      ref="table"
      size="default"
      :columns="columns"
      :dataSource="roleList"
      showPagination="auto"
    >
      <span slot="serial" slot-scope="text, record, index">
        {{ index + 1 }}
      </span>
      <span slot="status" slot-scope="text">
        <a-badge :status="text | statusTypeFilter" :text="text | statusFilter" />
      </span>
      <span slot="description" slot-scope="text">
        <ellipsis :length="4" tooltip>{{ text }}</ellipsis>
      </span>

      <span slot="action" slot-scope="text, record">
        <template>
          <a v-if="record.status === 1" @click="handleDisable(record.id)">禁用</a>
          <a v-else @click="handleEnable(record.id)">启用</a>
          <a-divider type="vertical"></a-divider>
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"></a-divider>
          <a @click="permissionAssign(record)">权限分配</a>
        </template>
      </span>
      <span slot="time" slot-scope="text">
          {{ text | moment }}
      </span>
    </a-table>
    <create-role-modal
      ref="createModal"
      :visible="visible"
      :loading="loading"
      :model="mdl"
      @cancel="handleCancel"
      @ok="handleOk"></create-role-modal>
    <assign-permissions-modal
      ref="assignPermissionModal"
      :visible="visible2"
      :loading="loading2"
      :selectedRowKeys="selectedRowKeys"
      :list="permissionList"
      @cancel="handleCancel2"
      @ok="handleOk2"></assign-permissions-modal>
  </a-card>
</template>

<script>
    import { systemRoleList, systemRoleDisable, systemRoleEnable, systemRoleCreate, systemRoleUpdate,
        systemPermissionList, rolePermissionList, rolePermissionUpdate } from '@/api/system'

    import CreateRoleModal from './modules/CreateRoleModal'
    import AssignPermissionsModal from './modules/AssignPermissionsModal'

    const statusMap = {
        1: {
            status: 'success',
            text: '正常'
        },
        9: {
            status: 'error',
            text: '禁用'
        }
    }
    export default {
        name: 'SystemRole',
        components: {
            AssignPermissionsModal,
            CreateRoleModal
        },
        data () {
            return {
                // 查询参数
                queryParam: {
                    status: '0',
                    name: ''
                },
                // 表头
                columns: [
                    {
                        title: '#',
                        dataIndex: 'id'
                    },
                    {
                        title: '中文名称',
                        dataIndex: 'cn_name'
                    },
                    {
                        title: '英文名称',
                        dataIndex: 'name'
                    },
                    {
                        title: '状态',
                        dataIndex: 'status',
                        scopedSlots: { customRender: 'status' }
                    },
                    {
                        title: '描述',
                        dataIndex: 'desc'
                    },
                    {
                        title: '创建时间',
                        dataIndex: 'createTime',
                        scopedSlots: { customRender: 'time' }
                    },
                    {
                        title: '操作',
                        dataIndex: 'action',
                        scopedSlots: { customRender: 'action' }
                    }
                ],
                roleList: [],
                permissionList: [],
                selectedRowKeys: [],
                visible: false,
                loading: false,
                visible2: false,
                loading2: false,
                mdl: null,
                currentId: null
            }
        },
        filters: {
            statusFilter (type) {
                return statusMap[type].text
            },
            statusTypeFilter (type) {
                return statusMap[type].status
            }
        },
        created () {
            this.list()
        },
        methods: {
            handleDisable (id) {
                systemRoleDisable({ 'id': id }).then(res => {
                    if (res.code === 0) {
                        this.$message.success(res.msg)
                    } else {
                        this.$message.error(res.msg)
                    }
                    this.list()
                })
            },
            handleEnable (id) {
                systemRoleEnable({ 'id': id }).then(res => {
                    if (res.code === 0) {
                        this.$message.success(res.msg)
                    } else {
                        this.$message.error(res.msg)
                    }
                    this.list()
                })
            },
            permissionAssign (record) {
                this.currentId = record.id
                this.visible2 = true
                this.loading2 = true
                this.permissionList = []
                this.selectedRowKeys = []
                systemPermissionList({
                    name: '',
                    status: 1
                }).then(res => {
                    this.permissionList = res.data
                    rolePermissionList({
                        'roleId': record.id
                    }).then(res => {
                        this.selectedRowKeys = res.data
                        this.loading2 = false
                    })
                }).catch(() => {
                    this.loading2 = true
                })
            },
            handleSearch () {
                this.list()
            },
            handleOk () {
                const form = this.$refs.createModal.form
                this.loading = true
                const that = this
                form.validateFields((errors, values) => {
                    if (!errors) {
                        if (that.mdl !== null) {
                            systemRoleUpdate(values).then(res => {
                                if (res.code === 0) {
                                    this.$message.success(res.msg)
                                } else {
                                    this.$message.error(res.msg)
                                }
                                this.list()
                            })
                        } else {
                            systemRoleCreate(values).then(res => {
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
            handleOk2 (rowKeys) {
                rolePermissionUpdate({
                    'roleId': this.currentId,
                    'idArray': rowKeys.join()
                }).then((res) => {
                    if (res.code === 0) {
                        this.$message.success(res.msg)
                    } else {
                        this.$message.error(res.msg)
                    }
                    this.visible2 = false
                })
            },

            handleAdd () {
                this.mdl = null
                this.visible = true
            },
            handleEdit (record) {
                this.mdl = record
                this.visible = true
            },
            handleCancel () {
                this.visible = false
            },
            handleCancel2 () {
                this.visible2 = false
            },
            list () {
                systemRoleList(this.queryParam).then(res => {
                    this.roleList = res.data
                })
            }
        }
    }
</script>
