<template>
  <div id="userRegisterPage">
    <div class="register-container">
      <div class="register-header">
        <div class="logo-circle">
          <span class="logo-text">EY</span>
        </div>
        <h2 class="title">用户注册</h2>
        <div class="desc">Live better,smile happier.</div>
      </div>

      <a-form
        :model="formState"
        name="basic"
        autocomplete="off"
        @finish="handleSubmit"
        class="register-form"
      >
        <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
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

        <a-form-item
          name="checkPassword"
          :rules="[
            { required: true, message: '请输入确认密码' },
            { min: 8, message: '确认密码长度不能小于 8 位' },
          ]"
        >
          <a-input-password
            v-model:value="formState.checkPassword"
            placeholder="请输入确认密码"
            size="large"
          >
            <template #prefix>
              <SafetyOutlined style="color: rgba(0, 0, 0, 0.25)" />
            </template>
          </a-input-password>
        </a-form-item>

        <div class="action-row">
          <div class="tips">
            已有账号？
            <RouterLink to="/user/login">去登录</RouterLink>
          </div>
        </div>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            class="submit-btn"
          >
            注册
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue'
import { userRegisterUsingPost } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import router from '@/router' // 用于接受表单输入的值

// 用于接受表单输入的值
const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})



/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  // 校验两次输入的密码是否一致
  if (values.userPassword !== values.checkPassword) {
    message.error('两次输入的密码不一致')
    return
  }
  const res = await userRegisterUsingPost(values)
  // 注册成功，跳转到登录页面
  if (res.data.code === 0 && res.data.data) {
    message.success('注册成功')
    router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error('注册失败，' + res.data.message)
  }
}
</script>

<style scoped>
#userRegisterPage {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  padding: 20px;
}

.register-container {
  max-width: 420px;
  width: 100%;
  margin: auto;
  padding: 40px 30px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-circle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #ff9c6e 0%, #ff7a45 100%);
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

.register-form {
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
  color: #ff7a45;
  margin-left: 4px;
  font-weight: 500;
  text-decoration: none;
}

.tips a:hover {
  text-decoration: underline;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(to right, #ff9c6e, #ff7a45);
  border: none;
  box-shadow: 0 4px 12px rgba(255, 122, 69, 0.3);
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 122, 69, 0.4);
  background: linear-gradient(to right, #ffa37e, #ff8a5e);
}
</style>

