<template>
  <div id="globalHeader">
    <a-row :wrap="false">
      <a-col flex="200px">
        <router-link to="/">
          <div class="title-bar">
            <img class="logo" src="../assets/logo.png" alt="logo" />
            <div class="title">E云图库</div>
          </div>
        </router-link>
      </a-col>
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          @click="onMenuClick"
          :items="items"
        />
      </a-col>
      <a-col flex="120px">
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <a-space>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                <div class="user-name">
                  {{ loginUserStore.loginUser.userName ?? '无名' }}
                </div>
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                  <a-menu-item
                    v-if="loginUserStore.loginUser.userRole === 'admin'"
                    >
                    <router-link to="/admin/userManage">
                      用户管理
                    </router-link>
                  </a-menu-item>
                  <a-menu-item
                    v-if="loginUserStore.loginUser.userRole === 'admin'"
                  >
                    <router-link to="/admin/pictureManage">
                      图片管理
                    </router-link>
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>

    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { h, ref } from 'vue'
import { HomeOutlined } from '@ant-design/icons-vue'
import {type MenuProps, message} from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import {userLogOutUsingPost} from '@/api/userController.ts'
import { computed } from 'vue' // 新增computed
import type { MenuInfo } from 'ant-design-vue/es/menu/src/interface' // 引入类型
// const doLogout = async () => {
//   const res = await userLogOutUsingPost()
//   console.log(res)
//   if (res.data.code === 0) {
//     loginUserStore.setLoginUser({
//       userName: '未登录',
//     })
//     message.success('退出登录成功')
//     await router.push('/user/login')
//   } else {
//     message.error('退出登录失败，' + res.data.message)
//   }
// }

const router = useRouter()
const current = ref<string[]>([''])


const doLogout = async () => {
  const res = await userLogOutUsingPost()
  console.log(res)
  if (res.data.code === 0) {
    // 修改此处：调用store的clearUserData方法
    loginUserStore.clearUserData() // 替换原来的setLoginUser
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}

// 菜单列表
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/add_picture',
    label: '创建图片',
    title: '创建图片',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/admin/pictureManage',
    label: '图片管理',
    title: '图片管理',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://github.com/E1myyy', target: '_blank' }, '作者主页'),
    title: '作者主页',
  },
]

// 过滤菜单项
const filterMenus = (menus: MenuProps['items'] = []) => {
  return menus.filter((menu) => {
    // 类型断言 + 可选链保护
    const menuKey = (menu as any)?.key; // 临时解决方案
    // 或使用类型守卫（推荐）
    // if (!menu || !('key' in menu)) return true;

    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser;
      return loginUser?.userRole === 'admin';
    }
    return true;
  });
};

// 展示在菜单的路由数组
const items = computed<MenuProps['items']>(() => filterMenus(originItems))


const onMenuClick = (e: MenuInfo) => {
  router.push({
    path: e.key
  })
}

router.afterEach((to) => {
  current.value = [to.path]
})

const loginUserStore = useLoginUserStore()
</script>

<style scoped>
/* 新增用户名容器样式 */
.user-login-status .user-name {
  max-width: 100px;  /* 根据实际布局调整 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: inline-block;
  vertical-align: middle;
}

.title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: black;
  font-size: 18px;
  margin-left: 16px;
}

.logo {
  height: 48px;
}
</style>

