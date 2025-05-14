<template>
  <div id="homePage">
    <!-- 搜索栏增强 -->
    <div class="hero-section">
      <h1 class="hero-title">数百万张免费正版素材</h1>
      <div class="search-bar">
        <a-input-search
          placeholder="搜索图片、视频和音乐"
          v-model:value="searchParams.searchText"
          enter-button="搜索"
          size="large"
          class="large-search"
          @search="doSearch"
        />
      </div>
    </div>

    <!-- 分类导航增强 -->
    <nav class="category-nav">
      <a-button
        v-for="category in ['全部', ...categoryList]"
        :key="category"
        :type="selectedCategory === category.toLowerCase() ? 'primary' : 'text'"
        @click="changeCategory(category.toLowerCase())"
      >
        {{ category }}
      </a-button>
    </nav>

    <!-- 图片列表增强 -->
    <div class="gallery-container">
      <a-watermark
        content="Pixabay"
        :font="{ color: 'rgba(0,0,0,0.12)', fontSize: 24 }"
      >
        <div class="masonry-grid">
          <div
            v-for="(picture, index) in dataList"
            :key="index"
            class="masonry-item"
            @click="doClickPicture(picture)"
          >
            <img :src="picture.url" :alt="picture.name"/>
            <div class="hover-overlay">
              <div class="action-bar">
                <a-button shape="circle" type="primary">
                  <template #icon><DownloadOutlined /></template>
                </a-button>
                <a-button shape="circle">
                  <template #icon><HeartOutlined /></template>
                </a-button>
              </div>
              <div class="info-footer">
                <a-tag color="green">{{ picture.category || '默认' }}</a-tag>
                <span class="author">@{{ picture.author }}</span>
              </div>
            </div>
          </div>
        </div>
      </a-watermark>
    </div>
  </div>
</template>


<script setup lang="ts">
import {computed, onMounted, reactive, ref} from "vue";
import {message} from "ant-design-vue";
import {listPictureTagCategoryUsingGet, listPictureVoByPageUsingPost} from "@/api/pictureController.ts";
import {useRouter} from "vue-router";

// 数据
const dataList = ref<API.PictureVO>([])
const total = ref(0)
const loading = ref(true)

//搜索功能
const doSearch = () => {
  // 重置页码
  searchParams.current = 1
  fetchData()
}

// 搜索条件
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    // 切换页号时，会修改搜索参数并获取数据
    onChange: (page, pageSize) => {
      searchParams.current = page
      searchParams.pageSize = pageSize
      fetchData()
    },
  }
})

// 获取数据
const fetchData = async () => {
  loading.value = true
  //转换搜索参数
  const params = {
    ...searchParams,
    tags: [] as string[],
  }
  if(selectedCategory.value !== 'all'){
    params.category = selectedCategory.value
  }
  selectedTagList.value.forEach((useTag, index) => {
    if(useTag){
      params.tags.push(tagList.value[index])
    }
  })
  const res = await listPictureVoByPageUsingPost(params)
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
  loading.value = false
}



// 获取标签和分类选项
const categoryList = ref<string[]>([])
const tagList = ref<string[]>([])
const selectedCategory=ref<string>("all")
const selectedTagList=ref<string[]>([])

const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    // 转换成下拉选项组件接受的格式
    tagList.value = res.data.data.tagList ?? [];
    categoryList.value = res.data.data.categoryList ?? [];
  } else {
    message.error('获取标签分类选项失败，' + res.data.message)
  }
}

//点击图片跳转到详情页面
const router = useRouter()
const doClickPicture = (picture : API.PictureVO) => {
  router.push({
    path:`/picture/${picture.id}`,
  })
}

onMounted(() => {
  getTagCategoryOptions()
})

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})

</script>

<style scoped>/* 新增样式 */
.hero-section {
  text-align: center;
  padding: 64px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.hero-title {
  font-size: 2.5rem;
  margin-bottom: 24px;
}

.large-search {
  max-width: 800px;
  margin: 0 auto;
}

.category-nav {
  padding: 16px;
  border-bottom: 1px solid #e8e8e8;
  white-space: nowrap;
  overflow-x: auto;
}

.masonry-grid {
  column-count: 6;
  column-gap: 16px;
  padding: 16px;
}

.masonry-item {
  position: relative;
  margin-bottom: 16px;
  break-inside: avoid;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s;
}

.masonry-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.15);
}

.masonry-item img {
  width: 100%;
  height: auto;
  vertical-align: bottom;
}

.hover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg, rgba(0,0,0,0) 60%, rgba(0,0,0,0.8) 100%);
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
  .masonry-grid { column-count: 4; }
}

@media (max-width: 992px) {
  .masonry-grid { column-count: 3; }
}

@media (max-width: 768px) {
  .hero-title { font-size: 1.8rem; }
  .masonry-grid { column-count: 2; }
}
</style>
