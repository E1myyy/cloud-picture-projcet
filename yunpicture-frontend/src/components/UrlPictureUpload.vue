<template>
  <div class="url-picture-upload">
    <a-input-group compact style="margin-bottom: 16px" :before-upload="beforeUpload">
      <a-input v-model:value="fileUrl" style="width: calc(100% - 120px)" placeholder="请输入图片 URL" />
      <a-button type="primary" :loading="loading" @click="handleUpload" style="width: 120px">提交</a-button>
    </a-input-group>
    <div class="image-wraper">
      <img v-if="picture?.url" :src="picture?.url" alt="avatar" />
    </div>
  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import { message } from 'ant-design-vue';
import type {UploadProps } from 'ant-design-vue';
import {uploadPictureByUrlUsingPost} from "@/api/pictureController.ts";

interface Props {
  picture?: API.PictureVO
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>();
const loading = ref<boolean>(false)
const fileUrl = ref<string>()
/**
 * 上传
 */
const handleUpload = async () => {
  loading.value = true
  try {
    const params: API.PictureUploadRequest = { fileUrl: fileUrl.value }
    if (props.picture) {
      params.id = props.picture.id
    }
    const res = await uploadPictureByUrlUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      message.success('图片上传成功')
      // 将上传成功的图片信息传递给父组件
      props.onSuccess?.(res.data.data)
    } else {
      message.error('图片上传失败，' + res.data.message)
    }
  } catch (error) {
    message.error('图片上传失败')
  } finally {
    loading.value = false
  }
}



const beforeUpload = (file: UploadProps['fileList'][number]) => {
  //校验图片格式
    const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
    if (!isJpgOrPng) {
      message.error('不支持上传该格式的图片，推荐 jpg 或 png')
    }
    //校验图片大小
    const isLt2M = file.size / 1024 / 1024 < 2
    if (!isLt2M) {
      message.error('不能上传超过 2M 的图片')
    }
    return isJpgOrPng && isLt2M
  };
</script>
<style scoped>
/* 新增图片预览样式 */

.url-picture-upload :deep(.ant-upload){
  margin-bottom: 16px;
}

.url-picture-upload img {
  max-width: 100%;
  max-height: 480px;
}
.url-picture-upload .image-wraper{
  text-align: center;
  margin-top: 16px;
}

</style>
