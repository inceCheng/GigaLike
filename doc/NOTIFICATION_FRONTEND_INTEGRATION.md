# 通知系统前端集成指南

## 1. API接口调用示例

### 1.1 获取通知列表

```javascript
// 获取通知列表
async function getNotificationList(params = {}) {
    const defaultParams = {
        current: 1,
        pageSize: 10,
        isRead: null, // null-全部，0-未读，1-已读
        type: null    // null-全部，'LIKE'-点赞，'COMMENT'-评论，'FOLLOW'-关注，'SYSTEM'-系统
    };
    
    const requestParams = { ...defaultParams, ...params };
    
    try {
        const response = await fetch('/api/notification/list', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestParams)
        });
        
        const result = await response.json();
        if (result.code === 0) {
            return result.data;
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error('获取通知列表失败:', error);
        throw error;
    }
}
```

### 1.2 获取未读通知数量

```javascript
// 获取未读通知数量
async function getUnreadCount() {
    try {
        const response = await fetch('/api/notification/unread/count');
        const result = await response.json();
        
        if (result.code === 0) {
            return result.data;
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error('获取未读数量失败:', error);
        throw error;
    }
}
```

### 1.3 标记通知为已读

```javascript
// 标记单个通知为已读
async function markAsRead(notificationId) {
    try {
        const response = await fetch(`/api/notification/read/${notificationId}`, {
            method: 'POST'
        });
        
        const result = await response.json();
        if (result.code === 0) {
            return result.data;
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error('标记已读失败:', error);
        throw error;
    }
}

// 批量标记所有通知为已读
async function markAllAsRead() {
    try {
        const response = await fetch('/api/notification/read/all', {
            method: 'POST'
        });
        
        const result = await response.json();
        if (result.code === 0) {
            return result.data;
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error('批量标记已读失败:', error);
        throw error;
    }
}
```

### 1.4 删除通知

```javascript
// 删除通知
async function deleteNotification(notificationId) {
    try {
        const response = await fetch(`/api/notification/${notificationId}`, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        if (result.code === 0) {
            return result.data;
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error('删除通知失败:', error);
        throw error;
    }
}
```

## 2. Vue 3 组件示例

### 2.1 通知列表组件

```vue
<template>
  <div class="notification-list">
    <!-- 头部操作栏 -->
    <div class="notification-header">
      <h3>消息通知</h3>
      <div class="header-actions">
        <el-button 
          type="text" 
          @click="markAllAsRead"
          :disabled="unreadCount === 0"
        >
          全部已读
        </el-button>
        <el-badge :value="unreadCount" :hidden="unreadCount === 0">
          <el-button type="primary" size="small">
            未读 ({{ unreadCount }})
          </el-button>
        </el-badge>
      </div>
    </div>

    <!-- 筛选器 -->
    <div class="notification-filters">
      <el-radio-group v-model="filters.isRead" @change="loadNotifications">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button :label="0">未读</el-radio-button>
        <el-radio-button :label="1">已读</el-radio-button>
      </el-radio-group>
      
      <el-select 
        v-model="filters.type" 
        placeholder="通知类型" 
        clearable
        @change="loadNotifications"
      >
        <el-option label="全部类型" :value="null"></el-option>
        <el-option label="点赞通知" value="LIKE"></el-option>
        <el-option label="评论通知" value="COMMENT"></el-option>
        <el-option label="关注通知" value="FOLLOW"></el-option>
        <el-option label="系统通知" value="SYSTEM"></el-option>
      </el-select>
    </div>

    <!-- 通知列表 -->
    <div class="notification-content" v-loading="loading">
      <div 
        v-for="notification in notifications" 
        :key="notification.id"
        class="notification-item"
        :class="{ 'unread': notification.isRead === 0 }"
        @click="handleNotificationClick(notification)"
      >
        <!-- 发送者头像 -->
        <div class="notification-avatar">
          <el-avatar 
            :src="notification.sender?.avatarUrl" 
            :size="40"
          >
            {{ notification.sender?.displayName?.charAt(0) || 'S' }}
          </el-avatar>
        </div>

        <!-- 通知内容 -->
        <div class="notification-body">
          <div class="notification-title">{{ notification.title }}</div>
          <div class="notification-content">{{ notification.content }}</div>
          <div class="notification-meta">
            <span class="notification-time">
              {{ formatTime(notification.createTime) }}
            </span>
            <el-tag 
              :type="getNotificationTypeColor(notification.type)" 
              size="small"
            >
              {{ getNotificationTypeName(notification.type) }}
            </el-tag>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="notification-actions">
          <el-button 
            v-if="notification.isRead === 0"
            type="text" 
            size="small"
            @click.stop="markAsRead(notification.id)"
          >
            标记已读
          </el-button>
          <el-button 
            type="text" 
            size="small"
            @click.stop="deleteNotification(notification.id)"
          >
            删除
          </el-button>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty 
        v-if="notifications.length === 0 && !loading"
        description="暂无通知"
      ></el-empty>
    </div>

    <!-- 分页 -->
    <div class="notification-pagination">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadNotifications"
        @current-change="loadNotifications"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 响应式数据
const loading = ref(false)
const notifications = ref([])
const unreadCount = ref(0)

const filters = reactive({
  isRead: null,
  type: null
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 加载通知列表
const loadNotifications = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      pageSize: pagination.pageSize,
      isRead: filters.isRead,
      type: filters.type
    }
    
    const data = await getNotificationList(params)
    notifications.value = data.records
    pagination.total = data.total
    
    // 同时更新未读数量
    await loadUnreadCount()
  } catch (error) {
    ElMessage.error('加载通知失败')
  } finally {
    loading.value = false
  }
}

// 加载未读数量
const loadUnreadCount = async () => {
  try {
    unreadCount.value = await getUnreadCount()
  } catch (error) {
    console.error('获取未读数量失败:', error)
  }
}

// 处理通知点击
const handleNotificationClick = async (notification) => {
  // 如果是未读通知，先标记为已读
  if (notification.isRead === 0) {
    await markAsRead(notification.id)
  }
  
  // 根据通知类型跳转到相应页面
  switch (notification.type) {
    case 'LIKE':
    case 'COMMENT':
      // 跳转到博客详情页
      if (notification.relatedId) {
        window.open(`/blog/${notification.relatedId}`, '_blank')
      }
      break
    case 'FOLLOW':
      // 跳转到用户主页
      if (notification.sender?.id) {
        window.open(`/user/${notification.sender.id}`, '_blank')
      }
      break
    default:
      break
  }
}

// 标记单个通知为已读
const markAsRead = async (notificationId) => {
  try {
    await markAsRead(notificationId)
    
    // 更新本地状态
    const notification = notifications.value.find(n => n.id === notificationId)
    if (notification) {
      notification.isRead = 1
      notification.readTime = new Date().toISOString()
    }
    
    // 更新未读数量
    await loadUnreadCount()
    
    ElMessage.success('已标记为已读')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 批量标记所有通知为已读
const markAllAsRead = async () => {
  try {
    await ElMessageBox.confirm('确定要将所有通知标记为已读吗？', '确认操作')
    
    await markAllAsRead()
    
    // 重新加载列表
    await loadNotifications()
    
    ElMessage.success('已全部标记为已读')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 删除通知
const deleteNotification = async (notificationId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条通知吗？', '确认删除')
    
    await deleteNotification(notificationId)
    
    // 从列表中移除
    const index = notifications.value.findIndex(n => n.id === notificationId)
    if (index > -1) {
      notifications.value.splice(index, 1)
      pagination.total--
    }
    
    // 更新未读数量
    await loadUnreadCount()
    
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 格式化时间
const formatTime = (timeStr) => {
  const time = new Date(timeStr)
  const now = new Date()
  const diff = now - time
  
  if (diff < 60000) { // 1分钟内
    return '刚刚'
  } else if (diff < 3600000) { // 1小时内
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) { // 1天内
    return `${Math.floor(diff / 3600000)}小时前`
  } else {
    return time.toLocaleDateString()
  }
}

// 获取通知类型名称
const getNotificationTypeName = (type) => {
  const typeMap = {
    'LIKE': '点赞',
    'COMMENT': '评论',
    'FOLLOW': '关注',
    'SYSTEM': '系统'
  }
  return typeMap[type] || '通知'
}

// 获取通知类型颜色
const getNotificationTypeColor = (type) => {
  const colorMap = {
    'LIKE': 'danger',
    'COMMENT': 'primary',
    'FOLLOW': 'success',
    'SYSTEM': 'warning'
  }
  return colorMap[type] || 'info'
}

// 组件挂载时加载数据
onMounted(() => {
  loadNotifications()
})
</script>

<style scoped>
.notification-list {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.notification-filters {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.notification-item {
  display: flex;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.notification-item:hover {
  background: #f8f9fa;
  border-color: #409eff;
}

.notification-item.unread {
  background: #f0f9ff;
  border-left: 4px solid #409eff;
}

.notification-avatar {
  margin-right: 15px;
}

.notification-body {
  flex: 1;
}

.notification-title {
  font-weight: bold;
  margin-bottom: 5px;
  color: #303133;
}

.notification-content {
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.4;
}

.notification-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.notification-actions {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-left: 15px;
}

.notification-pagination {
  margin-top: 20px;
  text-align: center;
}
</style>
```

### 2.2 通知徽章组件

```vue
<template>
  <el-badge 
    :value="unreadCount" 
    :hidden="unreadCount === 0"
    :max="99"
  >
    <el-button 
      type="text" 
      @click="showNotifications"
      class="notification-badge-button"
    >
      <el-icon><Bell /></el-icon>
    </el-button>
  </el-badge>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Bell } from '@element-plus/icons-vue'

const unreadCount = ref(0)
let pollingTimer = null

// 获取未读数量
const loadUnreadCount = async () => {
  try {
    unreadCount.value = await getUnreadCount()
  } catch (error) {
    console.error('获取未读数量失败:', error)
  }
}

// 显示通知列表
const showNotifications = () => {
  // 这里可以打开通知弹窗或跳转到通知页面
  window.open('/notifications', '_blank')
}

// 开始轮询
const startPolling = () => {
  // 每30秒检查一次未读数量
  pollingTimer = setInterval(loadUnreadCount, 30000)
}

// 停止轮询
const stopPolling = () => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
}

onMounted(() => {
  loadUnreadCount()
  startPolling()
})

onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped>
.notification-badge-button {
  font-size: 18px;
  color: #606266;
}

.notification-badge-button:hover {
  color: #409eff;
}
</style>
```

## 3. 实时通知推送（WebSocket）

### 3.1 WebSocket连接管理

```javascript
class NotificationWebSocket {
  constructor() {
    this.ws = null
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
    this.reconnectInterval = 5000
    this.callbacks = new Set()
  }

  connect() {
    try {
      this.ws = new WebSocket('ws://localhost:8123/ws/notifications')
      
      this.ws.onopen = () => {
        console.log('通知WebSocket连接成功')
        this.reconnectAttempts = 0
      }
      
      this.ws.onmessage = (event) => {
        try {
          const notification = JSON.parse(event.data)
          this.handleNotification(notification)
        } catch (error) {
          console.error('解析通知消息失败:', error)
        }
      }
      
      this.ws.onclose = () => {
        console.log('通知WebSocket连接关闭')
        this.reconnect()
      }
      
      this.ws.onerror = (error) => {
        console.error('通知WebSocket错误:', error)
      }
    } catch (error) {
      console.error('创建WebSocket连接失败:', error)
      this.reconnect()
    }
  }

  reconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++
      console.log(`尝试重连通知WebSocket (${this.reconnectAttempts}/${this.maxReconnectAttempts})`)
      
      setTimeout(() => {
        this.connect()
      }, this.reconnectInterval)
    } else {
      console.error('通知WebSocket重连失败，已达到最大重试次数')
    }
  }

  handleNotification(notification) {
    // 显示浏览器通知
    this.showBrowserNotification(notification)
    
    // 触发回调函数
    this.callbacks.forEach(callback => {
      try {
        callback(notification)
      } catch (error) {
        console.error('通知回调执行失败:', error)
      }
    })
  }

  showBrowserNotification(notification) {
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification(notification.title, {
        body: notification.content,
        icon: '/favicon.ico',
        tag: `notification-${notification.id}`
      })
    }
  }

  subscribe(callback) {
    this.callbacks.add(callback)
    return () => this.callbacks.delete(callback)
  }

  disconnect() {
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }
}

// 全局通知WebSocket实例
export const notificationWS = new NotificationWebSocket()
```

### 3.2 在Vue应用中使用

```javascript
// main.js
import { createApp } from 'vue'
import App from './App.vue'
import { notificationWS } from './utils/notificationWebSocket'

const app = createApp(App)

// 请求浏览器通知权限
if ('Notification' in window && Notification.permission === 'default') {
  Notification.requestPermission()
}

// 启动WebSocket连接
notificationWS.connect()

app.mount('#app')
```

```vue
<!-- 在组件中监听实时通知 -->
<script setup>
import { onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { notificationWS } from '@/utils/notificationWebSocket'

let unsubscribe = null

onMounted(() => {
  // 订阅实时通知
  unsubscribe = notificationWS.subscribe((notification) => {
    // 显示消息提示
    ElMessage({
      title: notification.title,
      message: notification.content,
      type: 'info',
      duration: 5000
    })
    
    // 更新未读数量等状态
    // ...
  })
})

onUnmounted(() => {
  // 取消订阅
  if (unsubscribe) {
    unsubscribe()
  }
})
</script>
```

## 4. 状态管理（Pinia）

```javascript
// stores/notification.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useNotificationStore = defineStore('notification', () => {
  // 状态
  const notifications = ref([])
  const unreadCount = ref(0)
  const loading = ref(false)

  // 计算属性
  const hasUnread = computed(() => unreadCount.value > 0)

  // 操作
  const loadNotifications = async (params = {}) => {
    loading.value = true
    try {
      const data = await getNotificationList(params)
      notifications.value = data.records
      return data
    } finally {
      loading.value = false
    }
  }

  const loadUnreadCount = async () => {
    try {
      unreadCount.value = await getUnreadCount()
    } catch (error) {
      console.error('获取未读数量失败:', error)
    }
  }

  const markAsRead = async (notificationId) => {
    await markAsRead(notificationId)
    
    // 更新本地状态
    const notification = notifications.value.find(n => n.id === notificationId)
    if (notification) {
      notification.isRead = 1
      notification.readTime = new Date().toISOString()
    }
    
    await loadUnreadCount()
  }

  const markAllAsRead = async () => {
    await markAllAsRead()
    
    // 更新本地状态
    notifications.value.forEach(n => {
      n.isRead = 1
      n.readTime = new Date().toISOString()
    })
    
    unreadCount.value = 0
  }

  const addNotification = (notification) => {
    notifications.value.unshift(notification)
    if (notification.isRead === 0) {
      unreadCount.value++
    }
  }

  return {
    notifications,
    unreadCount,
    loading,
    hasUnread,
    loadNotifications,
    loadUnreadCount,
    markAsRead,
    markAllAsRead,
    addNotification
  }
})
```

## 5. 使用说明

1. **安装依赖**: 确保项目中已安装Element Plus等UI库
2. **API集成**: 将API调用函数集成到项目中
3. **组件使用**: 在需要的页面中使用通知组件
4. **实时推送**: 根据需要集成WebSocket实时推送功能
5. **状态管理**: 使用Pinia管理通知相关状态

这个前端集成方案提供了完整的通知功能，包括列表展示、实时推送、状态管理等，可以根据实际需求进行调整和扩展。 