<template>
  <div id="adminManagePage" class="gradient-background">
    <a-card class="admin-card">
      <a-tabs v-model:activeKey="activeTab" type="card">
      <!-- 图片管理标签页 -->
      <a-tab-pane key="picture" tab="图片管理">
        <a-flex justify="space-between" style="margin-bottom: 16px">
          <h2>图片管理</h2>
          <a-space>
            <a-button type="primary" href="/add_picture" target="_blank">+ 创建图片</a-button>
            <a-button type="primary" href="/add_picture/batch" target="_blank" ghost>+ 批量创建图片</a-button>
          </a-space>
        </a-flex>
        <!-- 搜索表单 -->
        <a-form layout="inline" :model="searchParams_Pic" @finish="doSearch_Pic">
          <a-form-item label="关键词">
            <a-input v-model:value="searchParams_Pic.searchText" placeholder="从名称和简介搜索" allow-clear />
          </a-form-item>
          <a-form-item label="类型">
            <a-input v-model:value="searchParams_Pic.category" placeholder="请输入类型" allow-clear />
          </a-form-item>
          <a-form-item label="标签">
            <a-select
              v-model:value="searchParams_Pic.tags"
              mode="tags"
              placeholder="请输入标签"
              style="min-width: 180px"
              allow-clear
            />
          </a-form-item>
          <a-form-item label="审核状态" name="reviewStatus">
            <a-select
              v-model:value="searchParams_Pic.reviewStatus"
              :options="PIC_REVIEW_STATUS_OPTIONS"
              placeholder="请输入审核状态"
              style="min-width: 180px"
              allow-clear
            />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" html-type="submit">搜索</a-button>
          </a-form-item>
        </a-form>
        <div style="margin-bottom: 16px" />
        <!-- 表格 -->
        <a-table
          :columns="columns_Pic"
          :data-source="dataList_Pic"
          :pagination="pagination_Pic"
          @change="doTableChange_Pic"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'url'">
              <a-image :src="record.url" :width="120" />
            </template>
            <!-- 标签 -->
            <template v-if="column.dataIndex === 'tags'">
              <a-space wrap>
                <a-tag v-for="tag in JSON.parse(record.tags || '[]')" :key="tag">{{ tag }}</a-tag>
              </a-space>
            </template>
            <!-- 图片信息 -->
            <template v-if="column.dataIndex === 'picInfo'">
              <div>格式：{{ record.picFormat }}</div>
              <div>宽度：{{ record.picWidth }}</div>
              <div>高度：{{ record.picHeight }}</div>
              <div>宽高比：{{ record.picScale }}</div>
              <div>大小：{{ (record.picSize / 1024).toFixed(2) }}KB</div>
            </template>
            <!-- 审核信息 -->
            <template v-if="column.dataIndex === 'reviewMessage'">
              <div>审核状态：{{ PIC_REVIEW_STATUS_MAP[record.reviewStatus] }}</div>
              <div>审核信息：{{ record.reviewMessage }}</div>
              <div>审核人：{{ record.reviewerId }}</div>
              <div v-if="record.reviewTime">
                审核时间：{{ dayjs(record.reviewTime).format('YYYY-MM-DD HH:mm:ss') }}
              </div>
              <div v-else>审核时间：-</div>
            </template>

            <template v-else-if="column.dataIndex === 'createTime'">
              {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
            </template>
            <template v-else-if="column.dataIndex === 'editTime'">
              {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
            </template>
            <template v-else-if="column.key === 'action'">
              <a-space wrap>
                <a-button
                  v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.PASS"
                  type="link"
                  @click="handleReview_Pic(record, PIC_REVIEW_STATUS_ENUM.PASS)"
                >
                  审核通过
                </a-button>
                <a-button
                  v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.REJECT"
                  type="link"
                  danger
                  @click="handleReview_Pic(record, PIC_REVIEW_STATUS_ENUM.REJECT)"
                >
                  审核拒绝
                </a-button>
                <a-button type="link" :href="`/add_picture?id=${record.id}`" target="_blank"
                >编辑
                </a-button>
                <a-popconfirm
                  title="确定删除当前图片？"
                  ok-text="是"
                  cancel-text="否"
                  @confirm="doDelete_Pic(record.id)"
                >
                  <a-button type="link" danger>删除</a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-tab-pane>

      <!-- 用户管理标签页 -->
      <a-tab-pane key="user" tab="用户管理">
        <h2 style="margin-bottom: 16px">用户管理</h2>
        <!-- 搜索表单 -->
        <a-form layout="inline" :model="searchParams_User" @finish="doSearch_User">
          <a-form-item label="账号">
            <a-input v-model:value="searchParams_User.userAccount" placeholder="输入账号" allow-clear />
          </a-form-item>
          <a-form-item label="用户名">
            <a-input v-model:value="searchParams_User.userName" placeholder="输入用户名" allow-clear />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" html-type="submit">搜索</a-button>
          </a-form-item>
        </a-form>
        <div style="margin-bottom: 16px" />
        <!-- 表格 -->
        <a-table
          :columns="columns_User"
          :data-source="dataList_User"
          :pagination="pagination_User"
          @change="doTableChange_User"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'userAvatar'">
              <a-image :src="record.userAvatar" :width="120" />
            </template>
            <template v-else-if="column.dataIndex === 'userRole'">
              <div v-if="record.userRole === 'admin'">
                <a-tag color="green">管理员</a-tag>
              </div>
              <div v-else>
                <a-tag color="blue">普通用户</a-tag>
              </div>
            </template>
            <template v-if="column.dataIndex === 'createTime'">
              {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
            </template>
            <template v-else-if="column.key === 'action'">
              <a-popconfirm
                title="确定删除该用户？"
                ok-text="是"
                cancel-text="否"
                @confirm="doDelete_User(record.id)"
              >
                <a-button danger>删除</a-button>
              </a-popconfirm>
            </template>
          </template>
        </a-table>
      </a-tab-pane>

      <!--空间管理标签页-->
        <a-tab-pane key="space" tab="空间管理">
          <a-flex justify="space-between" style="margin-bottom: 16px">
            <h2>空间管理</h2>
            <a-space>
              <a-button type="primary" href="/add_space" target="_blank">+ 创建空间</a-button>
              <a-button type="primary" ghost href="/space_analyze?queryPublic=1" target="_blank"
              >分析公共图库</a-button
              >
              <a-button type="primary" ghost href="/space_analyze?queryAll=1" target="_blank"
              >分析全部空间</a-button
              >
            </a-space>
          </a-flex>
          <!-- 搜索表单 -->
          <a-form layout="inline" :model="searchParams_Space" @finish="doSearch_Space">
            <a-form-item label="空间名称">
              <a-input v-model:value="searchParams_Space.spaceName" placeholder="请输入空间名称" allow-clear />
            </a-form-item>
            <a-form-item name="spaceLevel" label="空间级别">
              <a-select
                v-model:value="searchParams_Space.spaceLevel"
                style="min-width: 180px"
                placeholder="请选择空间级别"
                :options="SPACE_LEVEL_OPTIONS"
                allow-clear
              />
            </a-form-item>
            <!-- 新增空间类别搜索 -->
            <a-form-item label="空间类别" name="spaceType">
              <a-select
                v-model:value="searchParams_Space.spaceType"
                :options="SPACE_TYPE_OPTIONS"
                placeholder="请选择空间类别"
                style="min-width: 180px"
                allow-clear
              />
            </a-form-item>
            <a-form-item label="用户 id">
              <a-input v-model:value="searchParams_Space.userId" placeholder="请输入用户 id" allow-clear />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" html-type="submit">搜索</a-button>
            </a-form-item>
          </a-form>
          <div style="margin-bottom: 16px" />
          <!-- 表格 -->
          <a-table
            :columns="columns_Space"
            :data-source="dataList_Space"
            :pagination="pagination_Space"
            @change="doTableChange_Space"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'spaceLevel'">
                <div>{{ SPACE_LEVEL_MAP[record.spaceLevel] }}</div>
              </template>
              <!-- 新增空间类别展示 -->
              <template v-if="column.dataIndex === 'spaceType'">
                <a-tag>{{ SPACE_TYPE_MAP[record.spaceType] }}</a-tag>
              </template>
              <template v-if="column.dataIndex === 'spaceUseInfo'">
                <div>大小：{{ formatSize(record.totalSize) }} / {{ formatSize(record.maxSize) }}</div>
                <div>数量：{{ record.totalCount }} / {{ record.maxCount }}</div>
              </template>
              <template v-if="column.dataIndex === 'createTime'">
                {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
              </template>
              <template v-if="column.dataIndex === 'editTime'">
                {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
              </template>
              <template v-else-if="column.key === 'action'">
                <a-space wrap>
                  <!-- 新增分析按钮 -->
                  <a-button type="link" :href="`/space_analyze?spaceId=${record.id}`" target="_blank">
                    分析
                  </a-button>
                  <a-button type="link" :href="`/add_space?id=${record.id}`" target="_blank">
                    编辑
                  </a-button>
                  <a-button danger @click="doDelete_Space(record.id)">删除</a-button>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import {
  deletePictureUsingPost,
  doPictureReviewUsingPost,
  listPictureByPageUsingPost
} from '../../api/pictureController'
import {
  deleteUserUsingPost,
  listUserVoByPageUsingPost
} from '../../api/userController'
import dayjs from 'dayjs'
import {PIC_REVIEW_STATUS_ENUM, PIC_REVIEW_STATUS_MAP, PIC_REVIEW_STATUS_OPTIONS} from '../../constants/picture.ts'
import {
  SPACE_LEVEL_MAP,
  SPACE_LEVEL_OPTIONS,
  SPACE_TYPE_MAP,
  SPACE_TYPE_OPTIONS
} from '@/constants/space.ts';
import {formatSize} from "@/utils";
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteSpaceUsingPost, listSpaceByPageUsingPost } from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'


const activeTab = ref('picture');

const columns_Pic = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: '图片',
    dataIndex: 'url',
  },
  {
    title: '名称',
    dataIndex: 'name',
  },
  {
    title: '简介',
    dataIndex: 'introduction',
    ellipsis: true,
  },
  {
    title: '类型',
    dataIndex: 'category',
  },
  {
    title: '标签',
    dataIndex: 'tags',
  },
  {
    title: '图片信息',
    dataIndex: 'picInfo',
  },
  {
    title: '用户id',
    dataIndex: 'userId',
    width: 80,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '编辑时间',
    dataIndex: 'editTime',
  },
  {
    title: '审核信息',
    dataIndex: 'reviewMessage',
  },
  {
    title: '操作',
    key: 'action',
  },
]


const handleReview_Pic = async (record: API.Picture, reviewStatus: number) => {
  const reviewMessage = reviewStatus === PIC_REVIEW_STATUS_ENUM.PASS ? '管理员操作通过' : '管理员操作拒绝'
  const res = await doPictureReviewUsingPost({
    id: record.id,
    reviewStatus,
    reviewMessage,
  })
  if (res.data.code === 0) {
    message.success('审核操作成功')
    // 重新获取列表
    fetchData_Pic()
  } else {
    message.error('审核操作失败，' + res.data.message)
  }
}

const doDelete_Pic = async (id: number) => {
  const res = await deletePictureUsingPost({ id })
  console.log('res', res)
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    await fetchData_Pic()
  } else {
    message.error('删除失败')
  }
}

// 数据
const dataList_Pic = ref<API.PictureVO[]>([])
const total_Pic = ref(0)

// 搜索条件
// 确保分页参数命名正确
const searchParams_Pic = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'ascend',
})
// 获取数据
const fetchData_Pic = async () => {
  const res = await listPictureByPageUsingPost({
    ...searchParams_Pic,
  })
  if (res.data.data) {
    dataList_Pic.value = res.data.data.records ?? []
    total_Pic.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}
// 表格变化之后，重新获取数据
const doTableChange_Pic = (page: any) => {
  searchParams_Pic.current = page.current
  searchParams_Pic.pageSize = page.pageSize
  fetchData_Pic()
}
// 搜索数据
const doSearch_Pic = () => {
  // 重置页码
  searchParams_Pic.current = 1
  fetchData_Pic()
}
// 分页参数
const pagination_Pic = computed(() => {
  return {
    current: searchParams_Pic.current,
    pageSize: searchParams_Pic.pageSize,
    total: total_Pic.value,
    showSizeChanger: true,
    showTotal: (total) => `共 ${total} 条`,
    locale: {
      items_per_page: '条/页', // 设置每页条数后缀
    },
    pageSizeOptions: ['10', '20', '30'], // 保持数值选项
  }
})
const columns_User = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]
const doDelete_User = async (id: number) => {
  const res = await deleteUserUsingPost({ id })
  console.log('res', res)
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    await fetchData_User()
  } else {
    message.error('删除失败')
  }
}
// 数据
const dataList_User = ref<API.UserVO[]>([])
const total_User = ref(0)
// 搜索条件
const searchParams_User = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'ascend',
})
// 获取数据
const fetchData_User = async () => {
  const res = await listUserVoByPageUsingPost({
    ...searchParams_User,
  })
  if (res.data.data) {
    dataList_User.value = res.data.data.records ?? []
    total_User.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}
// 表格变化之后，重新获取数据
const doTableChange_User = (page: any) => {
  searchParams_User.current = page.current
  searchParams_User.pageSize = page.pageSize
  fetchData_User()
}
// 搜索数据
const doSearch_User = () => {
  // 重置页码
  searchParams_User.current = 1
  fetchData_User()
}
// 分页参数
const pagination_User = computed(() => {
  return {
    current: searchParams_User.current,
    pageSize: searchParams_User.pageSize,
    total: total_User.value,
    showSizeChanger: true,
    showTotal: (total) => `共 ${total} 条`,
    locale: {
      items_per_page: '条/页', // 设置每页条数后缀
    },
    pageSizeOptions: ['10', '20', '30'], // 保持数值选项
  }
})
const columns_Space = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: '空间名称',
    dataIndex: 'spaceName',
  },
  {
    title: '空间级别',
    dataIndex: 'spaceLevel',
  },
  // 新增空间类别列
  {
    title: '空间类别',
    dataIndex: 'spaceType',
  },
  {
    title: '使用情况',
    dataIndex: 'spaceUseInfo',
  },
  {
    title: '用户 id',
    dataIndex: 'userId',
    width: 80,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '编辑时间',
    dataIndex: 'editTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]
// 定义数据
const dataList_Space = ref<API.Space[]>([])
const total_Space = ref(0)
// 搜索条件
const searchParams_Space = reactive<API.SpaceQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
  // 新增空间类别搜索字段
  spaceType: undefined,
})
// 获取数据
const fetchData_Space = async () => {
  const res = await listSpaceByPageUsingPost({
    ...searchParams_Space,
  })
  if (res.data.code === 0 && res.data.data) {
    dataList_Space.value = res.data.data.records ?? []
    total_Space.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}
// 分页参数
const pagination_Space = computed(() => {
  return {
    current: searchParams_Space.current,
    pageSize: searchParams_Space.pageSize,
    total: total_Space.value,
    showSizeChanger: true,
    showTotal: (total) => `共 ${total} 条`,
  }
})
// 表格变化之后，重新获取数据
const doTableChange_Space = (page: any) => {
  searchParams_Space.current = page.current
  searchParams_Space.pageSize = page.pageSize
  fetchData_Space()
}
// 搜索数据
const doSearch_Space = () => {
  // 重置页码
  searchParams_Space.current = 1
  fetchData_Space()
}
// 删除数据
const doDelete_Space = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteSpaceUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    fetchData_Space()
  } else {
    message.error('删除失败')
  }
}
//页面加载时请求一次数据
onMounted(() => {
  fetchData_Pic()
  fetchData_User()
  fetchData_Space()
})


</script>


<style scoped>
#adminManagePage {
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(135deg, #f0f2f5 0%, #e6f7ff 100%);
}

.admin-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.9);
}

.admin-content {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

:deep(.ant-tabs-nav) {
  padding: 0 24px;
  background: #f8fafd;
}

:deep(.ant-tabs-tab) {
  padding: 12px 24px;
  border-radius: 8px 8px 0 0 !important;
}

:deep(.ant-tabs-content) {
  padding: 24px;
}

:deep(.ant-table) {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

:deep(.ant-form) {
  background: #f8fafd;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
}
</style>
