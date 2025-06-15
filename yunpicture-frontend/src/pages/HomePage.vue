<template>
  <div id="homePage" class="gradient-background">
    <a-card class="main-card">
      <!-- Logo和标题 -->
      <div class="logo-container">
        <div class="logo-circle">
          <span class="logo-text">EY</span>
        </div>
        <h1 class="title">E云图库</h1>
        <p class="subtitle">集你所好</p>
      </div>

      <!-- 搜索框 -->
      <div class="search-bar">
        <a-input-search
          v-model:value="searchParams.searchText"
          placeholder="从海量图片中搜索"
          enter-button="搜索"
          size="large"
          @search="doSearch"
        />
      </div>

      <!-- 分类和标签筛选 -->
      <div class="filter-section">
        <a-tabs v-model:active-key="selectedCategory" @change="doSearch">
          <a-tab-pane key="all" tab="全部" />
          <a-tab-pane v-for="category in categoryList" :tab="category" :key="category" />
        </a-tabs>
        <div class="tag-bar">
          <span style="margin-right: 8px">标签：</span>
          <a-space :size="[0, 8]" wrap>
            <a-checkable-tag
              v-for="(tag, index) in tagList"
              :key="tag"
              v-model:checked="selectedTagList[index]"
              @change="doSearch"
            >
              {{ tag }}
            </a-checkable-tag>
          </a-space>
        </div>
      </div>

      <!-- 图片列表 -->
      <PictureList :dataList="dataList" :loading="loading" />

      <!-- 分页 -->
      <a-pagination
        class="pagination"
        v-model:current="searchParams.current"
        v-model:pageSize="searchParams.pageSize"
        :total="total"
        @change="onPageChange"
      />
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  listPictureTagCategoryUsingGet,
  listPictureVoByPageUsingPost,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import PictureList from '@/components/PictureList.vue' // 定义数据

// 定义数据
const dataList = ref<API.PictureVO[]>([])
const total = ref(0)
const loading = ref(true)

// 搜索条件
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 获取数据
const fetchData = async () => {
  loading.value = true
  // 转换搜索参数
  const params = {
    ...searchParams,
    tags: [] as string[],
  }
  if (selectedCategory.value !== 'all') {
    params.category = selectedCategory.value
  }
  // [true, false, false] => ['java']
  selectedTagList.value.forEach((useTag, index) => {
    if (useTag) {
      params.tags.push(tagList.value[index])
    }
  })
  const res = await listPictureVoByPageUsingPost(params)
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
  loading.value = false
}

// 页面加载时获取数据，请求一次
onMounted(() => {
  fetchData()
})

// 分页参数
const onPageChange = (page: number, pageSize: number) => {
  searchParams.current = page
  searchParams.pageSize = pageSize
  fetchData()
}

// 搜索
const doSearch = () => {
  // 重置搜索条件
  searchParams.current = 1
  fetchData()
}

// 标签和分类列表
const categoryList = ref<string[]>([])
const selectedCategory = ref<string>('all')
const tagList = ref<string[]>([])
const selectedTagList = ref<boolean[]>([])

/**
 * 获取标签和分类选项
 * @param values
 */
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    tagList.value = res.data.data.tagList ?? []
    categoryList.value = res.data.data.categoryList ?? []
  } else {
    message.error('获取标签分类列表失败，' + res.data.message)
  }
}

onMounted(() => {
  getTagCategoryOptions()
})
</script>

<style scoped>
#homePage {
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf5 100%);
}

.logo-container {
  text-align: center;
  margin-bottom: 24px;
}

.logo-circle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border-radius: 50%;
  margin: 0 auto 16px;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.logo-text {
  font-size: 28px;
  font-weight: 700;
  color: white;
}

.title {
  font-size: 26px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 16px;
  color: #666;
  margin-bottom: 32px;
}

.search-bar {
  max-width: 600px;
  margin: 0 auto 24px;
}

.filter-section {
  background: #f8fafd;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
}

.tag-bar {
  margin-top: 16px;
}


.picture-info h3 {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 600;
}

.picture-info p {
  margin: 0;
  font-size: 13px;
  opacity: 0.9;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

</style>
