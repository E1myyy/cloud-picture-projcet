<template>
  <div id="homePage">
    <!-- 搜索框 -->
    <div class="search-bar">
      <a-input-search
        placeholder="从海量图片中搜索"
        v-model:value="searchParams.searchText"
        enter-button="搜索"
        size="large"
        @search="doSearch"
      />
    </div>
    <!-- 分类+标签 -->
    <a-tabs v-model:active-key="selectedCategory" @change="doSearch">
      <a-tab-pane key="all" tab="全部"></a-tab-pane>
      <a-tab-pane v-for="category in categoryList" :key="category" :tab="category"></a-tab-pane>
    </a-tabs>
    <div class="tag-bar">
      <span style="margin-right: 8px">标签:</span>
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
    <!-- 图片列表 -->
    <div class="gallery-container">
      <a-watermark content="EyPicture" :font="{ color: 'rgba(0,0,0,0.12)', fontSize: 24 }">
        <div class="masonry-grid">
          <div v-for="(column, cIndex) in columnCount" :key="cIndex" class="masonry-column">
            <div
              v-for="(picture, index) in columnData[cIndex]"
              :key="`${cIndex}-${index}`"
              class="masonry-item"
              @click="doClickPicture(picture)"
            >
              <!-- 占位容器 -->
              <div
                v-if="!picture.loaded"
                class="image-placeholder"
                :style="placeholderStyle(picture)"
                :data-pic-id="picture.id"
                ref="placeholderRefs"
              ></div>
              <!-- 图片容器 -->
              <transition name="fade">
                <img
                  v-show="picture.loaded"
                  :src="picture.url"
                  :alt="picture.name"
                  @load="handleImageLoad(picture)"
                />
              </transition>

              <div v-show="picture.loaded" class="hover-overlay">
                <!--              下载按钮-->
                <div class="action-bar">
                  <a-button shape="circle" type="primary" @click.stop="doDownload(picture.url)">
                    <template #icon>
                      <DownloadOutlined />
                    </template>
                  </a-button>
                  <a-button shape="circle">
                    <template #icon>
                      <HeartOutlined />
                    </template>
                  </a-button>
                </div>
                <div class="info-footer">
                  <a-tag color="green">{{ picture.category || '默认' }}</a-tag>
                  <span class="author">@{{ picture.user.userName }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </a-watermark>
      <!-- 加载状态提示 -->
      <div v-if="loading" class="loading-text">加载中...</div>
      <div v-if="!hasMore" class="no-more-text">已经到底啦~</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref, reactive, nextTick } from 'vue'
import { message } from 'ant-design-vue'
import {
  listPictureTagCategoryUsingGet,
  listPictureVoByPageUsingPost,
} from '@/api/pictureController.ts'
import { useRouter } from 'vue-router'
import { DownloadOutlined, HeartOutlined } from '@ant-design/icons-vue'
import { downloadImage } from '@/utils'
import { debounce } from 'lodash-es'

// 数据
const dataList = ref<API.PictureVO[]>([])
const total = ref(0)
const loading = ref(true)
const hasMore = ref(true)

const columnCount = ref(6)
const columnData = ref<API.PictureVO[][]>([])

// 响应式调整列数
const updateColumnCount = () => {
  const width = window.innerWidth
  columnCount.value = width > 1200 ? 6 : width > 992 ? 4 : width > 768 ? 3 : 2
}

// 图片分配方法
const distributeImages = () => {
  const columns = Array.from({ length: columnCount.value }, () => [])

  dataList.value.forEach((pic, index) => {
    // 找到最短的列
    const shortestColumn = columns.reduce(
      (acc, col, i) => (col.length < acc.min ? { index: i, min: col.length } : acc),
      { index: 0, min: Infinity },
    )

    columns[shortestColumn.index].push(pic)
  })

  columnData.value = columns
}
// 下载图片
// 处理下载
const doDownload = (url: string) => {
  downloadImage(url)
}

//搜索功能
const doSearch = () => {
  dataList.value = []
  searchParams.current = 1
  hasMore.value = true
  searchParams.category = selectedCategory.value === 'all' ? '' : selectedCategory.value
  fetchData()
}

// 添加常量
const PAGE_SIZE = 12

// 搜索参数
const searchParams = reactive({
  current: 1,
  pageSize: PAGE_SIZE,
  sortField: 'createTime',
  sortOrder: 'descend',
  category: '',
  tags: [] as string[],
})

// 获取数据
const fetchData = async () => {
  loading.value = true
  const params = {
    ...searchParams,
    tags: selectedTagList.value.filter((_, index) => tagList.value[index]),
    category: selectedCategory.value !== 'all' ? selectedCategory.value : undefined,
  }

  try {
    const res = await listPictureVoByPageUsingPost(params)
    if (res.data.data) {
      const newRecords = res.data.data.records.map((pic) => ({
        ...pic,
        loaded: false, // 添加加载状态
      }))
      dataList.value = [...dataList.value, ...newRecords] // 追加数据
      total.value = res.data.data.total || 0

      distributeImages()
      // 判断是否还有更多数据
      if (dataList.value.length >= total.value) {
        hasMore.value = false
      }
    } else {
      message.error('获取数据失败：' + res.data.message)
    }
  } catch (error) {
    message.error('网络错误，请重试')
  } finally {
    loading.value = false
    searchParams.current += 1
    nextTick(() => {
      initIntersectionObserver()
    })
  }
}

// 图片加载处理
const handleImageLoad = (picture: API.PictureVO) => {
  picture.loaded = true
}

// 占位符样式计算
const placeholderStyle = (picture: API.PictureVO) => {
  // 根据实际比例设置，这里假设服务器返回宽高
  if (picture.width && picture.height) {
    return { paddingBottom: `${(picture.height / picture.width) * 100}%` }
  }
  return { paddingBottom: '56.25%' } // 默认16:9
}

// 交叉观察器实现懒加载
let observer: IntersectionObserver
const placeholderRefs = ref<HTMLElement[]>([])

const initIntersectionObserver = () => {
  observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          const img = new Image()
          const placeholder = entry.target as HTMLElement
          const picture = dataList.value.find(
            (pic) => pic.id === (placeholder as HTMLElement).dataset.picId, // 确保类型断言
          )

          if (picture && !picture.loaded) {
            img.src = picture.url
            img.onload = () => {
              picture.loaded = true
            }
            observer.unobserve(placeholder)
          }
        }
      })
    },
    {
      rootMargin: '0px 0px 200px 0px', // 提前200px加载
    },
  )

  placeholderRefs.value.forEach((el) => observer.observe(el))
}

// 滚动事件处理
const handleScroll = debounce(() => {
  const scrollBottom = window.innerHeight + window.scrollY
  const threshold = document.documentElement.scrollHeight - 100

  if (scrollBottom >= threshold && !loading.value && hasMore.value) {
    fetchData()
  }
}, 200)

// 获取标签和分类选项
const categoryList = ref<string[]>([])
const tagList = ref<string[]>([])
const selectedCategory = ref<string>('all')
const selectedTagList = ref<string[]>([])

const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    // 转换成下拉选项组件接受的格式
    tagList.value = res.data.data.tagList ?? []
    categoryList.value = res.data.data.categoryList ?? []
  } else {
    message.error('获取标签分类选项失败，' + res.data.message)
  }
}

//点击图片跳转到详情页面
const router = useRouter()
const doClickPicture = (picture: API.PictureVO) => {
  router.push({
    path: `/picture/${picture.id}`,
  })
}

onMounted(() => {
  window.addEventListener('resize', updateColumnCount)
  updateColumnCount()
  window.addEventListener('scroll', handleScroll)
  fetchData() // 初始加载
  getTagCategoryOptions()
  nextTick(initIntersectionObserver)
})
onBeforeUnmount(() => {
  if (observer) observer.disconnect()
  window.removeEventListener('resize', updateColumnCount)
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* 新增加载提示样式 */
.loading-text,
.no-more-text {
  text-align: center;
  padding: 16px;
  color: rgba(0, 0, 0, 0.45);
}

/* 调整瀑布流容器最小高度 */
.gallery-container {
  min-height: 100vh;
}

#homePage .search-bar {
  max-width: 480px;
  margin: 0 auto 8px;
}

#homePage .tag-bar {
  margin-bottom: 16px;
}

/* 覆盖分类栏（Tabs）字体大小 */
#homePage :deep(.ant-tabs-tab) {
  font-size: 16px !important; /* 调整分类栏字体大小 */
}

/* 覆盖标签栏（Tags）字体大小 */
#homePage :deep(.ant-tag) {
  font-size: 14px !important; /* 调整标签栏字体大小 */
}

.masonry-grid {
  display: flex;
  gap: 16px;
  padding: 16px;
}

.masonry-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.masonry-item {
  position: relative;
  margin-bottom: 16px;
  break-inside: avoid;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s;
  position: relative;
  width: 100%;
}

.masonry-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.masonry-item img {
  width: 100%;
  height: auto;
  display: block;
}

.hover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0) 60%, rgba(0, 0, 0, 0.8) 100%);
  opacity: 0;
  transition: opacity 0.3s;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 16px;
}

.masonry-item:hover .hover-overlay {
  opacity: 1;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.info-footer {
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author {
  font-size: 0.9em;
  opacity: 0.8;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .masonry-grid {
    gap: 12px;
  }
}

@media (max-width: 992px) {
  .masonry-grid {
    gap: 8px;
  }
}

@media (max-width: 768px) {
  .masonry-grid {
    gap: 6px;
    padding: 8px;
  }
}

.image-placeholder {
  background: #f5f5f5;
  border-radius: 8px;
  position: relative;
  width: 100%;
}

.image-placeholder::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0) 0%,
    rgba(255, 255, 255, 0.3) 50%,
    rgba(255, 255, 255, 0) 100%
  );
  animation: loading 1.5s infinite;
}

@keyframes loading {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

.masonry-item .fade-enter-active {
  position: relative; /* 保持正常文档流 */
  display: block;
  width: 100%;
  height: auto;
}
</style>
