<template>
  <div id="pictureDetailPage">
    <a-row :gutter="[16, 16]">
      <!-- 图片预览 -->
      <a-col :sm="24" :md="16" :xl="18">
        <a-card title="图片预览">
          <a-image :src="picture.url" style="max-height: 600px; object-fit: contain" />
        </a-card>
      </a-col>
      <!-- 图片信息区域 -->
      <a-col :sm="24" :md="8" :xl="6">
        <a-card title="图片信息">
          <a-descriptions :column="1">
            <a-descriptions-item label="作者">
              <a-space>
                <a-avatar :size="24" :src="picture.user?.userAvatar" />
                <div>{{ picture.user?.userName }}</div>
              </a-space>
            </a-descriptions-item>
            <a-descriptions-item label="名称">
              {{ picture.name ?? '未命名' }}
            </a-descriptions-item>
            <a-descriptions-item label="简介">
              {{ picture.introduction ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="分类">
              {{ picture.category ?? '默认' }}
            </a-descriptions-item>
            <a-descriptions-item label="标签">
              <a-tag v-for="tag in picture.tags" :key="tag">
                {{ tag }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="格式">
              {{ picture.picFormat ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽度">
              {{ picture.picWidth ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="高度">
              {{ picture.picHeight ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽高比">
              {{ picture.picScale ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="大小">
              {{ formatSize(picture.picSize) }}
            </a-descriptions-item>
            <a-descriptions-item label="主色调">
              <a-space>
                {{ picture.picColor ?? '-' }}
                <div
                  v-if="picture.picColor"
                  :style="{
                    width: '16px',
                    height: '16px',
                    backgroundColor: toHexColor(picture.picColor),
                  }"
                />
              </a-space>
            </a-descriptions-item>
          </a-descriptions>
          <!--审核按钮行-->
          <div style="display: flex; flex-direction: column; gap: 16px">
            <a-space v-if="(picture.userId)">
              <a-button
                v-if="canAdminEdit"
                :icon="h(CheckCircleOutlined)"
                type="default"
                @click="handleReview(picture, PIC_REVIEW_STATUS_ENUM.PASS)"
              >
                审核通过
              </a-button>
              <a-button
                v-if="canAdminEdit"
                :icon="h(CloseCircleOutlined)"
                type="default"
                danger
                @click="handleReview(picture, PIC_REVIEW_STATUS_ENUM.REJECT)"
              >
                审核不通过
              </a-button>
            </a-space>
            <!-- 编辑和删除按钮行 -->
            <a-space>
              <a-button v-if="canEdit" :icon="h(EditOutlined)" type="default" @click="doEdit">
                编辑
              </a-button>
              <a-popconfirm
                v-if="canEdit"
                title="确定删除当前图片？"
                ok-text="是"
                cancel-text="否"
                @confirm="doDelete"
              >
                <a-button type="link" danger :icon="h(DeleteOutlined)">删除</a-button>
              </a-popconfirm>
            </a-space>
            <!-- 下载按钮行 -->
            <a-space>
              <a-button type="primary" @click="doDownload">
                免费下载
                <template #icon>
                  <DownloadOutlined />
                </template>
              </a-button>
              <a-button :icon="h(ShareAltOutlined)" type="primary" ghost @click="doShare">
                分享
              </a-button>
            </a-space>
          </div>
        </a-card>
      </a-col>
    </a-row>
    <ShareModal ref="shareModalRef" :link="shareLink" />
  </div>
</template>

<script setup lang="ts">
import {computed, h, onMounted, reactive, ref} from 'vue'
import {
  deletePictureUsingPost,
  doPictureReviewUsingPost,
  getPictureVoByIdUsingGet,
  listPictureByPageUsingPost
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import {
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
  ShareAltOutlined,
  CloseCircleOutlined,
  CheckCircleOutlined
} from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { useRouter } from 'vue-router'
import { downloadImage, formatSize } from '@/utils'
import ShareModal from '@/components/ShareModal.vue'
import {PIC_REVIEW_STATUS_ENUM} from "@/constants/picture.ts";

interface Props {
  id: string | number
}

const props = defineProps<Props>()
const picture = ref<API.PictureVO>({})

const loginUserStore = useLoginUserStore()

//审核操作
const handleReview = async (picture: API.PictureVO, reviewStatus: number) => {
  const reviewMessage = reviewStatus === PIC_REVIEW_STATUS_ENUM.PASS ? '管理员操作通过' : '管理员操作拒绝'
  const res = await doPictureReviewUsingPost({
    id: picture.id,
    reviewStatus,
    reviewMessage,
  })
  if (res.data.code === 0) {
    message.success('审核操作成功')
    // 重新获取列表
    fetchData()
  } else {
    message.error('审核操作失败，' + res.data.message)
  }
}
// 数据
const dataList = ref<API.PictureVO[]>([])
const total = ref(0)
// 搜索条件
// 确保分页参数命名正确
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'ascend',
})
// 获取数据
const fetchData = async () => {
  const res = await listPictureByPageUsingPost({
    ...searchParams,
  })
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}

// 是否具有编辑权限
const canEdit = computed(() => {
  const loginUser = loginUserStore.loginUser
  // 未登录不可编辑
  if (!loginUser.id) {
    return false
  }
  // 仅本人或管理员可编辑
  const user = picture.value.user || {}
  return loginUser.id === user.id || loginUser.userRole === 'admin'
})

const canAdminEdit = computed(() => {
  const loginUser = loginUserStore.loginUser
  // 未登录不可编辑
  if (!loginUser.id) {
    return false
  }
  // 管理员可编辑
  return loginUser.userRole === 'admin'
})

// 获取图片详情
const fetchPictureDetail = async () => {
  try {
    const res = await getPictureVoByIdUsingGet({
      id: props.id,
    })
    if (res.data.code === 0 && res.data.data) {
      picture.value = res.data.data
    } else {
      message.error('获取图片详情失败，' + res.data.message)
    }
  } catch (e: any) {
    message.error('获取图片详情失败：' + e.message)
  }
}

onMounted(() => {
  fetchPictureDetail()
})

const router = useRouter()

// 编辑
const doEdit = () => {
  router.push({
    path: '/add_picture',
    query: {
      id: picture.value.id,
      spaceId: picture.value.spaceId,
    },
  })
}

// 删除数据
const doDelete = async () => {
  const id = picture.value.id
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
  } else {
    message.error('删除失败')
  }
}

// 下载图片
const doDownload = () => {
  downloadImage(picture.value.url)
}

// ----- 分享操作 ----
const shareModalRef = ref()
// 分享链接
const shareLink = ref<string>()
// 分享
const doShare = () => {
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/${picture.value.id}`
  if (shareModalRef.value) {
    shareModalRef.value.openModal()
  }
}

</script>

<style scoped>
#pictureDetailPage {
  margin-bottom: 16px;
}
</style>
