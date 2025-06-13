<template>
  <div id="addPictureBatchPage" class="gradient-background">
    <a-card class="batch-card">
      <h2 style="margin-bottom: 24px">批量创建图片</h2>

      <a-alert
        message="操作提示"
        description="此功能将从互联网批量抓取图片并自动创建，请填写搜索关键词和抓取数量"
        type="info"
        show-icon
        style="margin-bottom: 24px"
      />

      <a-form
        layout="vertical"
        :model="formData"
        @finish="handleSubmit"
        class="batch-form"
      >
      <a-form-item label="关键词" name="searchText">
        <a-input v-model:value="formData.searchText" placeholder="请输入关键词" />
      </a-form-item>
      <a-form-item label="抓取数量" name="count">
        <a-input-number
          v-model:value="formData.count"
          placeholder="请输入数量"
          style="min-width: 180px"
          :min="1"
          :max="30"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="名称前缀" name="namePrefix">
        <a-input v-model:value="formData.namePrefix" placeholder="请输入名称前缀，会自动补充序号" />
      </a-form-item>
        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            style="width: 100%"
            class="submit-button"
            :loading="loading"
          >
            执行批量创建任务
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref} from "vue";
import {
 uploadByBatchUsingPost
} from "@/api/pictureController.ts";
import {message} from "ant-design-vue";
import {useRouter} from "vue-router";

const router = useRouter();
const formData = reactive<API.PictureUploadByBatchRequest>({
  count: 10,
})
const loading = ref(false)

const handleSubmit = async (values: any) => {
  loading.value = true;
  const res = await uploadByBatchUsingPost({
    ...formData,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success(`创建成功，共 ${res.data.data} 条`)
    router.push({
      path: '/',
    })
  } else {
    message.error('创建失败，' + res.data.message)
  }
  loading.value = false;
}

</script>


<style scoped>
#addPictureBatchPage {
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(135deg, #f0f2f5 0%, #e6f7ff 100%);
}

.batch-card {
  max-width: 600px;
  margin: 0 auto;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  padding: 24px;
}

.batch-form {
  background: #f8fafd;
  padding: 24px;
  border-radius: 8px;
}

.submit-button {
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
  border: none;
  border-radius: 8px;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.3);
  transition: all 0.3s ease;
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(82, 196, 26, 0.4);
}

</style>
