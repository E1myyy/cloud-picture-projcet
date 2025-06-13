<template>
  <div id="addSpacePage" class="gradient-background">
    <a-card class="form-card">
      <div class="header-section">
        <div class="logo-circle">
          <span class="logo-text">EY</span>
        </div>
        <h2 class="title">
          {{ route.query?.id ? '修改空间' : '创建空间' }}
        </h2>
        <p class="subtitle">管理您的图片存储空间</p>
      </div>

      <!-- 空间信息表单 -->
      <a-form name="spaceForm" layout="vertical" :model="spaceForm" @finish="handleSubmit">
        <a-form-item name="spaceName" label="空间名称">
          <a-input
            v-model:value="spaceForm.spaceName"
            placeholder="请输入空间名称"
            allow-clear
            size="large"
          >
            <template #prefix>
              <FolderOutlined style="color: rgba(0, 0, 0, 0.25)" />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="spaceLevel" label="空间级别">
          <a-select
            v-model:value="spaceForm.spaceLevel"
            placeholder="请选择空间级别"
            :options="SPACE_LEVEL_OPTIONS"
            size="large"
            allow-clear
          >
            <template #suffixIcon>
              <CaretDownOutlined />
            </template>
          </a-select>
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            :loading="loading"
            class="submit-button"
            size="large"
          >
            <template #icon>
              <CheckOutlined />
            </template>
            {{ route.query?.id ? '更新空间' : '创建空间' }}
          </a-button>
        </a-form-item>
      </a-form>

      <!-- 空间级别介绍 -->
      <a-card class="info-card" title="空间级别介绍">
        <div class="info-content">
          <a-alert
            message="提示信息"
            type="info"
            show-icon
            style="margin-bottom: 16px"
          >
            <template #description>
              目前仅支持开通普通版，如需升级空间，请联系
              <a href="https://github.com/E1myyy" target="_blank" class="contact-link">E1my</a>
            </template>
          </a-alert>

          <div class="level-grid">
            <div v-for="spaceLevel in spaceLevelList" :key="spaceLevel.level" class="level-card">
              <div class="level-header">
                <div class="level-icon">
                  <template v-if="spaceLevel.level === 'normal'">
                    <StarFilled style="color: #1890ff; font-size: 24px" />
                  </template>
                  <template v-else-if="spaceLevel.level === 'advanced'">
                    <CrownFilled style="color: #ffc53d; font-size: 24px" />
                  </template>
                  <template v-else>
                    <RocketFilled style="color: #722ed1; font-size: 24px" />
                  </template>
                </div>
                <h3>{{ spaceLevel.text }}</h3>
              </div>
              <div class="level-details">
                <p><CloudOutlined /> 存储空间：{{ formatSize(spaceLevel.maxSize) }}</p>
                <p><FileImageOutlined /> 图片数量：{{ spaceLevel.maxCount }} 张</p>
              </div>
            </div>
          </div>
        </div>
      </a-card>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  addSpaceUsingPost,
  getSpaceVoByIdUsingGet,
  listSpaceLevelUsingGet,
  updateSpaceUsingPost,
} from '@/api/spaceController.ts'
import { useRoute, useRouter } from 'vue-router'
import { SPACE_LEVEL_OPTIONS } from '@/constants/space.ts'
import { formatSize } from '../utils'

import {
  FolderOutlined,
  CheckOutlined,
  CaretDownOutlined,
  CloudOutlined,
  FileImageOutlined,
  StarFilled,
  CrownFilled,
  RocketFilled
} from '@ant-design/icons-vue'

const space = ref<API.SpaceVO>()
const spaceForm = reactive<API.SpaceAddRequest | API.SpaceEditRequest>({})
const loading = ref(false)

const spaceLevelList = ref<API.SpaceLevel[]>([])

// 获取空间级别
const fetchSpaceLevelList = async () => {
  const res = await listSpaceLevelUsingGet()
  if (res.data.code === 0 && res.data.data) {
    spaceLevelList.value = res.data.data
  } else {
    message.error('获取空间级别失败，' + res.data.message)
  }
}

onMounted(() => {
  fetchSpaceLevelList()
})

const router = useRouter()

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const spaceId = space.value?.id
  loading.value = true
  let res
  if (spaceId) {
    // 更新
    res = await updateSpaceUsingPost({
      id: spaceId,
      ...spaceForm,
    })
  } else {
    // 创建
    res = await addSpaceUsingPost({
      ...spaceForm,
    })
  }
  // 操作成功
  if (res.data.code === 0 && res.data.data) {
    message.success('操作成功')
    // 跳转到空间详情页
    router.push({
      path: `/space/${res.data.data}`,
    })
  } else {
    message.error('操作失败，' + res.data.message)
  }
  loading.value = false
}

const route = useRoute()

// 获取老数据
const getOldSpace = async () => {
  // 获取到 id
  const id = route.query?.id
  if (id) {
    const res = await getSpaceVoByIdUsingGet({
      id,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      space.value = data
      // 填充表单
      spaceForm.spaceName = data.spaceName
      spaceForm.spaceLevel = data.spaceLevel
    }
  }
}

onMounted(() => {
  getOldSpace()
})
</script>

<style scoped>
#addSpacePage {
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(135deg, #f0f2f5 0%, #e6f7ff 100%);
  display: flex;
  justify-content: center;
  align-items: center;
}

.form-card {
  max-width: 800px;
  width: 100%;
  border-radius: 16px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  padding: 32px;
}

.header-section {
  text-align: center;
  margin-bottom: 32px;
}

.logo-circle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border-radius: 50%;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.logo-text {
  font-size: 32px;
  font-weight: 700;
  color: white;
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 16px;
  color: #666;
  margin-bottom: 0;
}

.submit-button {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border: none;
  border-radius: 8px;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
  transition: all 0.3s ease;
  margin-top: 16px;
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(24, 144, 255, 0.4);
}

.info-card {
  margin-top: 32px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
}

:deep(.info-card .ant-card-head) {
  background: #f8fafd;
  border-bottom: 1px solid #f0f0f0;
  border-radius: 12px 12px 0 0;
  padding: 0 16px;
}

:deep(.info-card .ant-card-body) {
  padding: 24px;
}

.contact-link {
  color: #1890ff;
  font-weight: 500;
  text-decoration: none;
}

.contact-link:hover {
  text-decoration: underline;
}

.level-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  margin-top: 16px;
}

.level-card {
  background: #f8fafd;
  border-radius: 10px;
  padding: 16px;
  border: 1px solid #e8e8e8;
  transition: all 0.3s ease;
}

.level-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  border-color: #1890ff;
}

.level-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px dashed #e8e8e8;
}

.level-header h3 {
  margin: 0 0 0 12px;
  font-size: 16px;
  font-weight: 600;
}

.level-icon {
  background: rgba(24, 144, 255, 0.1);
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.level-details p {
  margin: 8px 0;
  display: flex;
  align-items: center;
  font-size: 14px;
}

.level-details p :deep(svg) {
  margin-right: 8px;
  color: #1890ff;
}
</style>
