<template>
  <div id="homePage" class="home-container">
    <div class="home-content">
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

      <!-- 瀑布流图片列表 -->
      <div ref="waterfallContainer" class="waterfall-container">
        <div
          v-for="(item, index) in dataList"
          :key="index"
          class="waterfall-item"
          :style="{ height: itemHeight(item) }"
        >
          <a-image
            :src="item.url"
            :alt="item.name"
            class="waterfall-image"
          />
          <div class="picture-info">
            <h3>{{ item.name }}</h3>
            <p>{{ item.introduction }}</p>
          </div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-indicator">
        <a-spin size="large" />
        <p>加载中...</p>
      </div>
      <div v-else-if="dataList.length > 0 && dataList.length >= total" class="loading-indicator">
        <p>没有更多图片了</p>
      </div>
      <div v-else-if="!loading && dataList.length === 0" class="loading-indicator">
        <p>暂无图片数据</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useInfiniteScroll } from '@vueuse/core'
import {
  listPictureTagCategoryUsingGet,
  listPictureVoByPageUsingPost,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'

// 定义数据
const dataList = ref<API.PictureVO[]>([])
const total = ref(0)
const loading = ref(false) // 初始改为false
const waterfallContainer = ref<HTMLElement | null>(null)

// 搜索条件
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 获取数据
const fetchData = async (append = false) => {
  // 移除 if (loading.value) return 这一行
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

  try {
    const res = await listPictureVoByPageUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      if (append) {
        // 追加数据
        dataList.value = [...dataList.value, ...(res.data.data.records ?? [])]
      } else {
        // 重置数据
        dataList.value = res.data.data.records ?? []
      }
      total.value = res.data.data.total ?? 0
    } else {
      message.error('获取数据失败，' + (res.data.message || '未知错误'))
    }
  } catch (e) {
    console.error('请求失败:', e)
    message.error('请求失败，请检查网络')
  } finally {
    loading.value = false
  }
}

// 无限滚动 - 修复条件判断
useInfiniteScroll(
  waterfallContainer,
  async () => {
    // 添加更多条件检查
    if (!loading.value &&
      dataList.value.length > 0 &&
      dataList.value.length < total.value) {
      searchParams.current += 1
      await fetchData(true)
    }
  },
  { distance: 100 }
)
// 搜索
const doSearch = () => {
  // 重置搜索条件
  searchParams.current = 1
  fetchData(false)
}

// 标签和分类列表
const categoryList = ref<string[]>([])
const selectedCategory = ref<string>('all')
const tagList = ref<string[]>([])
const selectedTagList = ref<boolean[]>([])

/**
 * 获取标签和分类选项
 */
const getTagCategoryOptions = async () => {
  try {
    const res = await listPictureTagCategoryUsingGet()
    if (res.data.code === 0 && res.data.data) {
      tagList.value = res.data.data.tagList ?? []
      categoryList.value = res.data.data.categoryList ?? []
      // 初始化选中状态
      selectedTagList.value = new Array(tagList.value.length).fill(false)
    } else {
      message.error('获取标签分类列表失败，' + (res.data.message || '未知错误'))
    }
  } catch (e) {
    console.error('获取标签分类失败:', e)
    message.error('获取标签分类失败')
  }
}

onMounted(() => {
  // 确保初始化加载
  fetchData(false)
  getTagCategoryOptions()
})
</script>

<style scoped>
#homePage {
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf5 100%);
}

.home-container {
  max-width: 1200px;
  margin: 0 auto;
}

.home-content {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
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

/* 瀑布流样式 */
.waterfall-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.waterfall-item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  aspect-ratio: 3/4; /* 添加固定宽高比 */
}

.waterfall-image {
  width: 100%;
  height: 100%; /* 修复高度问题 */
  object-fit: cover; /* 确保图片填充 */
  display: block;
}

.waterfall-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.picture-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
  color: white;
  padding: 16px;
  transform: translateY(100%);
  transition: transform 0.3s ease;
}

.waterfall-item:hover .picture-info {
  transform: translateY(0);
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

.loading-indicator {
  text-align: center;
  padding: 40px 0;
  color: #666;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .waterfall-container {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 15px;
  }
}

@media (max-width: 480px) {
  .waterfall-container {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 12px;
  }
}
</style>
