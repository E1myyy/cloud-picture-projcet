<template>
  <div id="userLoginPage">
    <div class="login-container">
      <div class="login-header">
        <div class="logo-circle">
          <span class="logo-text">EY</span>
        </div>
        <h2 class="title">用户登录</h2>
        <div class="desc">Work harder, live better.</div>
      </div>

      <a-form
        :model="formState"
        name="basic"
        autocomplete="off"
        @finish="handleSubmit"
        class="login-form"
      >
        <a-form-item
          name="userAccount"
          :rules="[{ required: true, message: '请输入账号' }]"
        >
          <a-input
            v-model:value="formState.userAccount"
            placeholder="请输入账号"
            size="large"
          >
            <template #prefix>
              <UserOutlined style="color: rgba(0, 0, 0, 0.25)" />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item
          name="userPassword"
          :rules="[
            { required: true, message: '请输入密码' },
            { min: 8, message: '密码长度不能小于 8 位' },
          ]"
        >
          <a-input-password
            v-model:value="formState.userPassword"
            placeholder="请输入密码"
            size="large"
          >
            <template #prefix>
              <LockOutlined style="color: rgba(0, 0, 0, 0.25)" />
            </template>
          </a-input-password>
        </a-form-item>

        <div class="action-row">
          <div class="tips">
            没有账号？
            <RouterLink to="/user/register">去注册</RouterLink>
          </div>
          <a href="#" class="forgot-password">忘记密码?</a>
        </div>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            class="submit-btn"
          >
            登录
          </a-button>
        </a-form-item>
      </a-form>


    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue'
import { userLoginUsingPost } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'
import router from '@/router' // 用于接受表单输入的值

// 用于接受表单输入的值
const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const loginUserStore = useLoginUserStore()

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const res = await userLoginUsingPost(values)
  // 登录成功，把登录态保存到全局状态中
  if (res.data.code === 0 && res.data.data) {
    await loginUserStore.fetchLoginUser()
    message.success('登录成功')
    router.push({
      path: '/',
      replace: true,
    })
  } else {
    message.error('登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
#userLoginPage {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #f0f2f5 0%, #e6f7ff 100%);
  padding: 20px;
}

.login-container {
  max-width: 420px;
  width: 100%;
  margin: auto;
  padding: 40px 30px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-circle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border-radius: 50%;
  margin-bottom: 16px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: white;
}

.title {
  font-size: 22px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 4px;
}

.desc {
  font-size: 14px;
  color: #666;
}

.login-form {
  margin-top: 10px;
}

.action-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.tips {
  color: #666;
  font-size: 14px;
}

.tips a {
  color: #1890ff;
  margin-left: 4px;
}

.forgot-password {
  color: #666;
  font-size: 14px;
  text-decoration: none;
}

.forgot-password:hover {
  color: #1890ff;
  text-decoration: underline;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(24, 144, 255, 0.4);
}
</style>

