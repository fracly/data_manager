import { UserLayout, PanelLayout, BasicLayout, RouteView, BlankLayout, PageView } from '@/layouts'
import { bxAnaalyse } from '@/core/icons'

export const asyncRouterMap = [

  {
    path: '/',
    name: 'index',
    component: BasicLayout,
    meta: { title: '首页' },
    redirect: '/dashboard/analysis',
    children: [
      // dashboard
      {
        path: 'dashboard',
        name: 'dashboard',
        redirect: '/dashboard/analysis',
        component: RouteView,
        meta: { title: '数据总览', keepAlive: true, icon: bxAnaalyse, permission: [ 'dashboard' ] },
        children: [
          {
            path: 'analysis/:pageNo([1-9]\\d*)?',
            name: 'Analysis',
            component: () => import('@/views/dashboard/Analysis'),
            meta: { title: '数据统计', keepAlive: false, permission: [ 'dashboard' ] }
          }
        ]
      },

      // data manage
      {
        path: '/manage',
        redirect: '/manage/datasource',
        component: PageView,
        meta: { title: '数据控制台', icon: 'dashboard', permission: [ 'console' ] },
        children: [
          {
            path: '/manage/datasource',
            name: 'DataSource',
            component: () => import('@/views/manage/DataSource'),
            meta: { title: '数据源管理', keepAlive: true, permission: [ 'console' ] }
          },
          {
            path: '/manage/data',
            name: 'Data',
            component: () => import('@/views/manage/Data'),
            meta: { title: '数据管理', keepAlive: true, permission: [ 'console' ] }
          },
          {
            path: '/manage/data-lineage',
            name: 'DataLineage',
            component: () => import('@/views/manage/DataLineage'),
            meta: { title: '血缘关系', keepAlive: true, permission: [ 'console' ] }
          },
          {
            path: '/manage/data-encrypt',
            name: 'DataEncrypt',
            component: () => import('@/views/manage/DataEncrypt'),
            meta: { title: '数据加密', keepAlive: true, permission: [ 'console' ] }
          }
        ]
      },

      // list
      {
        path: '/collection',
        name: 'collection',
        component: PageView,
        redirect: '/collection/data',
        meta: { title: '数据采集', icon: 'upload', permission: [ 'collection' ] },
        children: [
          {
            path: '/collection/data',
            name: 'DataCollection',
            component: () => import('@/views/collection/DataCollection'),
            meta: { title: '数据导入', keepAlive: true, permission: [ 'collection' ] }
          },
          {
            path: '/collection/auto-job',
            name: 'AutoJob',
            component: () => import('@/views/collection/AutoJob'),
            meta: { title: '自动采集', keepAlive: true, permission: [ 'collection' ] }
          },
          {
            path: '/collection/manual-job',
            name: 'CollectionJob',
            component: () => import('@/views/collection/CollectionJob'),
            meta: { title: '任务查看', keepAlive: true, permission: [ 'collection' ] }
          }
        ]
      },

      // search
      {
        path: '/search',
        name: 'search',
        component: PageView,
        redirect: '/search/label',
        meta: { title: '数据检索', icon: 'profile', permission: [ 'search' ] },
        children: [
          {
            path: '/search/label',
            name: 'SearchLabel',
            component: () => import('@/views/search/label/Index'),
            meta: { title: '标签管理', permission: [ 'search' ] }
          },
          {
            path: '/search/advanced',
            name: 'SearchAdvanced',
            component: () => import('@/views/search/advanced/Advanced'),
            meta: { title: '高速检索', keepAlive: true, permission: [ 'search' ] }
          }
        ]
      },
      // Exception
      {
        path: '/tool',
        name: 'exception',
        component: RouteView,
        meta: { title: '交互式工具', icon: 'tool', permission: [ 'tool' ] },
        children: [
          {
            path: window.dmConfig.huePath,
            name: 'SQL交互查询',
            meta: { title: 'SQL交互查询', target: '_blank' }
          },
          {
            path: window.dmConfig.hdfWebPath,
            name: '文件系统-Web',
            meta: { title: '文件系统-Web', target: '_blank' }
          },
          {
            path: window.dmConfig.cdhPath,
            name: '集群管理',
            meta: { title: '集群管理', target: '_blank' }
          }
        ]
      },

      // Log
      {
        path: '/log',
        name: 'logPage',
        component: PageView,
        meta: { title: '日志查询', icon: 'slack', permission: [ 'log' ] },
        redirect: '/log/login',
        children: [
          {
            path: '/log/login',
            name: 'LogLogin',
            component: () => import('@/views/log/LogLogin'),
            meta: { title: '登录日志', icon: 'tool', keepAlive: true, permission: [ 'log' ] }
          },
          {
            path: '/log/search',
            name: 'LogSearch',
            component: () => import('@/views/log/LogSearch'),
            meta: { title: '搜索日志', icon: 'tool', keepAlive: true, permission: [ 'log' ] }
          },
          {
            path: '/log/download',
            name: 'LogDownload',
            component: () => import('@/views/log/LogDownload'),
            meta: { title: '下载日志', icon: 'tool', keepAlive: true, permission: [ 'log' ] }
          }
        ]
      },

      // Account
      {
        path: '/account',
        component: RouteView,
        redirect: '/account/center',
        name: 'account',
        meta: { title: '个人页', icon: 'user', keepAlive: true, permission: [ 'account' ] },
        children: [
          {
            path: '/account/settings',
            name: 'settings',
            component: () => import('@/views/account/settings/Index'),
            meta: { title: '个人设置', hideHeader: true, permission: [ 'account' ] },
            redirect: '/account/settings/base',
            hideChildrenInMenu: true,
            children: [
              {
                path: '/account/settings/base',
                name: 'BaseSettings',
                component: () => import('@/views/account/settings/BaseSetting'),
                meta: { title: '基本设置', hidden: true, permission: [ 'account' ] }
              },
              {
                path: '/account/settings/security',
                name: 'SecuritySettings',
                component: () => import('@/views/account/settings/Security'),
                meta: { title: '安全设置', hidden: true, keepAlive: true, permission: [ 'account' ] }
              },
              {
                path: '/account/settings/custom',
                name: 'CustomSettings',
                component: () => import('@/views/account/settings/Custom'),
                meta: { title: '个性化设置', hidden: true, keepAlive: true, permission: [ 'account' ] }
              },
              {
                path: '/account/settings/binding',
                name: 'BindingSettings',
                component: () => import('@/views/account/settings/Binding'),
                meta: { title: '账户绑定', hidden: true, keepAlive: true, permission: [ 'user' ] }
              },
              {
                path: '/account/settings/notification',
                name: 'NotificationSettings',
                component: () => import('@/views/account/settings/Notification'),
                meta: { title: '新消息通知', hidden: true, keepAlive: true, permission: [ 'account' ] }
              }
            ]
          }
        ]
      },

      // System
      {
        path: '/system',
        name: 'system',
        component: RouteView,
        redirect: '/system/user',
        meta: { title: '系统管理', icon: 'tool', permission: [ 'system' ] },
        children: [
          {
            path: '/system/user',
            name: 'SystemUser',
            component: () => import('@/views/system/SystemUser'),
            meta: { title: '用户管理', keepAlive: false, hiddenHeaderContent: true, permission: [ 'system' ] }
          },
          {
            path: '/system/role',
            name: 'SystemRole',
            component: () => import('@/views/system/SystemRole'),
            meta: { title: '角色分配', keepAlive: false, hiddenHeaderContent: true, permission: [ 'system' ] }
          },
          {
            path: '/system/permission',
            name: 'SystemPermission',
            component: () => import('@/views/system/SystemPermission'),
            meta: { title: '权限管理', keepAlive: false, hiddenHeaderContent: true, permission: [ 'system' ] }
          },
          {
            path: '/system/template',
            name: 'DataTemplate',
            component: () => import('@/views/system/DataTemplate'),
            meta: { title: '数据模板', keepAlive: false, hiddenHeaderContent: true, permission: [ 'system' ] }
          }
        ]
      }
    ]
  },
  {
    path: '*', redirect: '/404', hidden: true
  }
]

export const baseRouterMap = [

  {
    path: '/',
    name: 'index',
    component: BasicLayout,
    meta: { title: '首页' },
    redirect: '/dashboard/analysis',
    children: [
      // dashboard
      {
        path: 'dashboard',
        name: 'dashboard',
        redirect: '/dashboard/analysis',
        component: RouteView,
        meta: { title: '数据总览', keepAlive: true, icon: bxAnaalyse, permission: [ 'dashboard' ] },
        children: [
          {
            path: 'analysis/:pageNo([1-9]\\d*)?',
            name: 'Analysis',
            component: () => import('@/views/dashboard/Analysis'),
            meta: { title: '数据统计', keepAlive: false, permission: [ 'dashboard' ] }
          }
        ]
      },

      // data manage
      {
        path: '/manage',
        redirect: '/manage/datasource',
        component: PageView,
        meta: { title: '数据控制台', icon: 'dashboard', permission: [ 'console' ] },
        children: [
          {
            path: '/manage/datasource',
            name: 'DataSource',
            component: () => import('@/views/manage/DataSource'),
            meta: { title: '数据源管理', keepAlive: true, permission: [ 'console' ] }
          },
          {
            path: '/manage/data',
            name: 'Data',
            component: () => import('@/views/manage/Data'),
            meta: { title: '数据管理', keepAlive: true, permission: [ 'console' ] }
          },
          {
            path: '/manage/data-lineage',
            name: 'DataLineage',
            component: () => import('@/views/manage/DataLineage'),
            meta: { title: '血缘关系', keepAlive: true, permission: [ 'console' ] }
          },
          {
            path: '/manage/data-encrypt',
            name: 'DataEncrypt',
            component: () => import('@/views/manage/DataEncrypt'),
            meta: { title: '数据加密', keepAlive: true, permission: [ 'console' ] }
          }
        ]
      },

      // list
      {
        path: '/collection',
        name: 'collection',
        component: PageView,
        redirect: '/collection/data-collection',
        meta: { title: '数据采集', icon: 'upload', permission: [ 'collection' ] },
        children: [
          {
            path: '/collection/data-collection',
            name: 'DataCollection',
            component: () => import('@/views/collection/DataCollection'),
            meta: { title: '数据导入', keepAlive: true, permission: [ 'collection' ] }
          },
          {
            path: '/collection/collection-job',
            name: 'CollectionJob',
            component: () => import('@/views/collection/CollectionJob'),
            meta: { title: '任务查看', keepAlive: true, permission: [ 'collection' ] }
          }
        ]
      },

      // search
      {
        path: '/search',
        name: 'search',
        component: PageView,
        redirect: '/search/label',
        meta: { title: '数据检索', icon: 'profile', permission: [ 'search' ] },
        children: [
          {
            path: '/search/label',
            name: 'SearchLabel',
            component: () => import('@/views/search/label/Index'),
            meta: { title: '标签管理', permission: [ 'search' ] }
          },
          {
            path: '/search/advanced',
            name: 'SearchAdvanced',
            component: () => import('@/views/search/advanced/Advanced'),
            meta: { title: '高速检索', keepAlive: true, permission: [ 'search' ] }
          }
        ]
      },

      // result
      {
        path: '/visual',
        name: 'visual',
        component: PageView,
        redirect: '/404',
        meta: { title: '数据可视化', icon: 'check-circle-o', permission: [ 'visual' ] },
        children: [
          {
            path: '/404',
            name: 'ResultSuccess',
            meta: { title: '统计', keepAlive: false, hiddenHeaderContent: true, permission: [ 'visual' ] }
          },
          {
            path: '/405',
            name: 'ResultFail',
            meta: { title: '预览', keepAlive: false, hiddenHeaderContent: true, permission: [ 'visual' ] }
          }
        ]
      },

      // Exception
      {
        path: '/tool',
        name: 'exception',
        component: RouteView,
        meta: { title: '交互式工具', icon: 'tool', permission: [ 'tool' ] },
        children: [
          {
            path: window.dmConfig.huePath,
            name: 'SQL交互查询',
            meta: { title: 'SQL交互查询', target: '_blank' }
          },
          {
            path: window.dmConfig.hdfWebPath,
            name: '文件系统-Web',
            meta: { title: '文件系统-Web', target: '_blank' }
          },
          {
            path: window.dmConfig.cdhPath,
            name: '集群管理',
            meta: { title: '集群管理', target: '_blank' }
          }
        ]
      },

      // Log
      {
        path: '/log',
        name: 'logPage',
        component: PageView,
        meta: { title: '日志查询', icon: 'slack', permission: [ 'log' ] },
        redirect: '/log/login',
        children: [
          {
            path: '/log/login',
            name: 'LogLogin',
            component: () => import('@/views/log/LogLogin'),
            meta: { title: '登录日志', icon: 'tool', keepAlive: true, permission: [ 'log' ] }
          },
          {
            path: '/log/search',
            name: 'LogSearch',
            component: () => import('@/views/log/LogSearch'),
            meta: { title: '搜索日志', icon: 'tool', keepAlive: true, permission: [ 'log' ] }
          },
          {
            path: '/log/download',
            name: 'LogDownload',
            component: () => import('@/views/log/LogDownload'),
            meta: { title: '下载日志', icon: 'tool', keepAlive: true, permission: [ 'log' ] }
          }
        ]
      },

      // Account
      {
        path: '/account',
        component: RouteView,
        redirect: '/account/center',
        name: 'account',
        meta: { title: '个人页', icon: 'user', keepAlive: true, permission: [ 'account' ] },
        children: [
          {
            path: '/account/settings',
            name: 'settings',
            component: () => import('@/views/account/settings/Index'),
            meta: { title: '个人设置', hideHeader: true, permission: [ 'account' ] },
            redirect: '/account/settings/base',
            hideChildrenInMenu: true,
            children: [
              {
                path: '/account/settings/base',
                name: 'BaseSettings',
                component: () => import('@/views/account/settings/BaseSetting'),
                meta: { title: '基本设置', hidden: true, permission: [ 'account' ] }
              },
              {
                path: '/account/settings/security',
                name: 'SecuritySettings',
                component: () => import('@/views/account/settings/Security'),
                meta: { title: '安全设置', hidden: true, keepAlive: true, permission: [ 'account' ] }
              },
              {
                path: '/account/settings/custom',
                name: 'CustomSettings',
                component: () => import('@/views/account/settings/Custom'),
                meta: { title: '个性化设置', hidden: true, keepAlive: true, permission: [ 'account' ] }
              },
              {
                path: '/account/settings/binding',
                name: 'BindingSettings',
                component: () => import('@/views/account/settings/Binding'),
                meta: { title: '账户绑定', hidden: true, keepAlive: true, permission: [ 'user' ] }
              },
              {
                path: '/account/settings/notification',
                name: 'NotificationSettings',
                component: () => import('@/views/account/settings/Notification'),
                meta: { title: '新消息通知', hidden: true, keepAlive: true, permission: [ 'account' ] }
              }
            ]
          }
        ]
      },

      // System
      {
        path: '/system',
        name: 'system',
        component: RouteView,
        redirect: '/system/user',
        meta: { title: '系统管理', icon: 'tool', permission: [ 'system' ] },
        children: [
          {
            path: '/system/user',
            name: 'SystemUser',
            component: () => import('@/views/system/SystemUser'),
            meta: { title: '用户管理', keepAlive: false, hiddenHeaderContent: true, permission: [ 'system' ] }
          },
          {
            path: '/system/role',
            name: 'SystemRole',
            component: () => import('@/views/system/SystemRole'),
            meta: { title: '角色分配', keepAlive: false, hiddenHeaderContent: true, permission: [ 'system' ] }
          },
          {
            path: '/system/permission',
            name: 'SystemPermission',
            component: () => import('@/views/system/SystemPermission'),
            meta: { title: '权限管理', keepAlive: false, hiddenHeaderContent: true, permission: [ 'system' ] }
          }
        ]
      }
    ]
  },
  {
    path: '*', redirect: '/404', hidden: true
  }
]

/**
 * 基础路由
 * @type { *[] }
 */
export const constantRouterMap = [
  {
    path: '/user',
    component: UserLayout,
    redirect: '/user/login',
    hidden: true,
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import('@/views/user/Login')
      },
      {
        path: 'recover',
        name: 'recover',
        component: undefined
      }
    ]
  },
  {
    path: '/panel',
    component: PanelLayout,
    redirect: '/panel/index',
    hidden: true,
    children: [
      {
        path: 'index',
        name: 'index',
        component: () => import('@/views/user/Login')
      }
    ]
  },

  {
    path: '/test',
    component: BlankLayout,
    redirect: '/test/home',
    children: [
      {
        path: 'home',
        name: 'TestHome',
        component: () => import('@/views/Home')
      }
    ]
  },

  {
    path: '/404',
    component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/404')
  }

]
