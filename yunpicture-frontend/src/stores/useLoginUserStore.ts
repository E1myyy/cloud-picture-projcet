import { defineStore } from "pinia";
import { ref } from "vue";
import { getLoginUserUsingGet } from '@/api/userController.ts'

// export const useLoginUserStore = defineStore("loginUser", () => {
//   const loginUser = ref<API.LoginUserVO>({
//       userName: '未登录',
//       userRole: '', // 必须包含该字段
//     });
//
//   async function fetchLoginUser() {
//     const res = await getLoginUserUsingGet()
//     if (res.data.code === 0 && res.data.data) {
//       loginUser.value = res.data.data
//     }
//   }
//
//   function setLoginUser(newLoginUser: any) {
//     loginUser.value = newLoginUser;
//   }
//
//   function clearUserData() {
//     loginUser.value = {
//       userName: '未登录',
//       userRole: '', // 重置角色信息
//       // 其他字段重置...
//     }
//   }
//
//   return { loginUser, setLoginUser, fetchLoginUser };
// });
export const useLoginUserStore = defineStore("loginUser", () => {
  const loginUser = ref<API.LoginUserVO>({
    userName: '未登录',
    userRole: '', // 必须包含该字段
  });

  async function fetchLoginUser() {
    const res = await getLoginUserUsingGet()
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data
    }
  }

  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser;
  }

  function clearUserData() {
    loginUser.value = {
      userName: '未登录',
      userRole: '',  // 确保清空角色信息
      userAvatar: '',
      id: undefined,
    }
    localStorage.removeItem('loginUser') // 清除本地存储
  }

  return {
    loginUser,
    setLoginUser,
    fetchLoginUser,
    clearUserData // 确保导出该方法
  }
})



