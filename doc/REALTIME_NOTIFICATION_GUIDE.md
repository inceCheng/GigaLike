# 实时通知系统集成指南

## 概述

GigaLike 实时通知系统基于 WebSocket 技术，为用户提供即时的消息推送功能。当用户收到点赞、评论、关注等通知时，系统会立即推送到客户端。

## 系统架构

```
用户操作 → Pulsar消息队列 → 通知消费者 → 数据库存储 + WebSocket实时推送 → 前端接收
```

## 后端接口

### WebSocket 连接

**连接地址：** `ws://localhost:8080/ws/notification?userId={userId}`

**支持 SockJS：** `http://localhost:8080/ws/notification`

### REST API

#### 1. 获取在线状态
```http
GET /api/realtime/status
Authorization: 需要登录
```

**响应示例：**
```json
{
  "code": 0,
  "data": {
    "isOnline": true,
    "onlineUserCount": 15,
    "userId": 123
  },
  "message": "ok"
}
```

#### 2. 获取连接信息
```http
GET /api/realtime/connection-info
```

**响应示例：**
```json
{
  "code": 0,
  "data": {
    "websocketUrl": "/ws/notification",
    "connectionParams": "userId",
    "example": "ws://localhost:8080/ws/notification?userId=123",
    "supportsSockJS": true,
    "sockJSUrl": "/ws/notification"
  },
  "message": "ok"
}
```

## 前端集成

### 1. 原生 WebSocket

```javascript
class NotificationWebSocket {
    constructor(userId) {
        this.userId = userId;
        this.ws = null;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectInterval = 3000;
        this.heartbeatInterval = 30000;
        this.heartbeatTimer = null;
    }

    connect() {
        try {
            const wsUrl = `ws://localhost:8080/ws/notification?userId=${this.userId}`;
            this.ws = new WebSocket(wsUrl);
            
            this.ws.onopen = this.onOpen.bind(this);
            this.ws.onmessage = this.onMessage.bind(this);
            this.ws.onclose = this.onClose.bind(this);
            this.ws.onerror = this.onError.bind(this);
            
        } catch (error) {
            console.error('WebSocket连接失败:', error);
            this.reconnect();
        }
    }

    onOpen(event) {
        console.log('WebSocket连接已建立');
        this.reconnectAttempts = 0;
        this.startHeartbeat();
    }

    onMessage(event) {
        try {
            const message = JSON.parse(event.data);
            console.log('收到消息:', message);
            
            switch (message.type) {
                case 'CONNECTED':
                    console.log('连接成功:', message.message);
                    break;
                case 'NOTIFICATION':
                    this.handleNotification(message.data);
                    break;
                case 'BROADCAST':
                    this.handleBroadcast(message.data);
                    break;
                case 'PONG':
                    console.log('心跳响应');
                    break;
                default:
                    console.log('未知消息类型:', message.type);
            }
        } catch (error) {
            console.error('解析消息失败:', error);
        }
    }

    onClose(event) {
        console.log('WebSocket连接关闭:', event.code, event.reason);
        this.stopHeartbeat();
        
        if (event.code !== 1000) { // 非正常关闭
            this.reconnect();
        }
    }

    onError(error) {
        console.error('WebSocket错误:', error);
    }

    handleNotification(notification) {
        // 处理通知消息
        console.log('收到通知:', notification);
        
        // 显示通知
        this.showNotification(notification);
        
        // 更新未读数量
        this.updateUnreadCount();
        
        // 触发自定义事件
        window.dispatchEvent(new CustomEvent('newNotification', {
            detail: notification
        }));
    }

    handleBroadcast(broadcast) {
        // 处理系统广播
        console.log('收到广播:', broadcast);
        
        // 显示系统消息
        this.showSystemMessage(broadcast);
    }

    showNotification(notification) {
        // 浏览器通知
        if (Notification.permission === 'granted') {
            new Notification(notification.title, {
                body: notification.content,
                icon: '/favicon.ico'
            });
        }
        
        // 页面内通知
        const notificationElement = document.createElement('div');
        notificationElement.className = 'notification-toast';
        notificationElement.innerHTML = `
            <div class="notification-header">${notification.title}</div>
            <div class="notification-content">${notification.content}</div>
        `;
        document.body.appendChild(notificationElement);
        
        // 3秒后自动移除
        setTimeout(() => {
            document.body.removeChild(notificationElement);
        }, 3000);
    }

    showSystemMessage(message) {
        alert(`系统消息: ${message.message}`);
    }

    updateUnreadCount() {
        // 调用API获取最新未读数量
        fetch('/api/notification/unread/count', {
            credentials: 'include'
        })
        .then(response => response.json())
        .then(data => {
            if (data.code === 0) {
                // 更新页面上的未读数量显示
                const unreadElement = document.getElementById('unread-count');
                if (unreadElement) {
                    unreadElement.textContent = data.data;
                    unreadElement.style.display = data.data > 0 ? 'block' : 'none';
                }
            }
        })
        .catch(error => console.error('获取未读数量失败:', error));
    }

    startHeartbeat() {
        this.heartbeatTimer = setInterval(() => {
            if (this.ws && this.ws.readyState === WebSocket.OPEN) {
                this.ws.send('PING');
            }
        }, this.heartbeatInterval);
    }

    stopHeartbeat() {
        if (this.heartbeatTimer) {
            clearInterval(this.heartbeatTimer);
            this.heartbeatTimer = null;
        }
    }

    reconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            console.log(`尝试重连 (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
            
            setTimeout(() => {
                this.connect();
            }, this.reconnectInterval);
        } else {
            console.error('重连失败，已达到最大重试次数');
        }
    }

    disconnect() {
        this.stopHeartbeat();
        if (this.ws) {
            this.ws.close(1000, '用户主动断开');
        }
    }
}

// 使用示例
const userId = 123; // 从登录状态获取
const notificationWS = new NotificationWebSocket(userId);

// 页面加载时连接
window.addEventListener('load', () => {
    notificationWS.connect();
});

// 页面卸载时断开连接
window.addEventListener('beforeunload', () => {
    notificationWS.disconnect();
});

// 监听通知事件
window.addEventListener('newNotification', (event) => {
    console.log('新通知事件:', event.detail);
    // 在这里处理新通知，如更新UI、播放声音等
});
```

### 2. Vue 3 集成

```vue
<template>
  <div class="notification-system">
    <!-- 通知图标 -->
    <div class="notification-icon" @click="toggleNotificationPanel">
      <i class="bell-icon"></i>
      <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }}</span>
    </div>
    
    <!-- 连接状态指示器 -->
    <div class="connection-status" :class="{ connected: isConnected }">
      {{ isConnected ? '已连接' : '未连接' }}
    </div>
    
    <!-- 通知面板 -->
    <div v-if="showNotificationPanel" class="notification-panel">
      <div class="notification-header">
        <h3>通知</h3>
        <button @click="markAllAsRead">全部已读</button>
      </div>
      <div class="notification-list">
        <div 
          v-for="notification in notifications" 
          :key="notification.id"
          class="notification-item"
          :class="{ unread: !notification.isRead }"
        >
          <div class="notification-content">
            <h4>{{ notification.title }}</h4>
            <p>{{ notification.content }}</p>
            <span class="notification-time">{{ formatTime(notification.createTime) }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 实时通知弹窗 -->
    <div v-if="realtimeNotification" class="realtime-notification">
      <div class="notification-toast">
        <h4>{{ realtimeNotification.title }}</h4>
        <p>{{ realtimeNotification.content }}</p>
        <button @click="closeRealtimeNotification">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const ws = ref(null)
const isConnected = ref(false)
const unreadCount = ref(0)
const notifications = ref([])
const showNotificationPanel = ref(false)
const realtimeNotification = ref(null)

// 连接WebSocket
const connectWebSocket = () => {
  if (!userStore.user?.id) return
  
  const wsUrl = `ws://localhost:8080/ws/notification?userId=${userStore.user.id}`
  ws.value = new WebSocket(wsUrl)
  
  ws.value.onopen = () => {
    console.log('WebSocket连接成功')
    isConnected.value = true
  }
  
  ws.value.onmessage = (event) => {
    const message = JSON.parse(event.data)
    handleWebSocketMessage(message)
  }
  
  ws.value.onclose = () => {
    console.log('WebSocket连接关闭')
    isConnected.value = false
    // 尝试重连
    setTimeout(connectWebSocket, 3000)
  }
  
  ws.value.onerror = (error) => {
    console.error('WebSocket错误:', error)
    isConnected.value = false
  }
}

// 处理WebSocket消息
const handleWebSocketMessage = (message) => {
  switch (message.type) {
    case 'NOTIFICATION':
      handleNewNotification(message.data)
      break
    case 'BROADCAST':
      handleBroadcast(message.data)
      break
  }
}

// 处理新通知
const handleNewNotification = (notification) => {
  // 添加到通知列表
  notifications.value.unshift(notification)
  
  // 更新未读数量
  if (!notification.isRead) {
    unreadCount.value++
  }
  
  // 显示实时通知
  showRealtimeNotification(notification)
  
  // 浏览器通知
  if (Notification.permission === 'granted') {
    new Notification(notification.title, {
      body: notification.content,
      icon: '/favicon.ico'
    })
  }
}

// 显示实时通知弹窗
const showRealtimeNotification = (notification) => {
  realtimeNotification.value = notification
  // 3秒后自动关闭
  setTimeout(() => {
    realtimeNotification.value = null
  }, 3000)
}

// 关闭实时通知
const closeRealtimeNotification = () => {
  realtimeNotification.value = null
}

// 处理系统广播
const handleBroadcast = (broadcast) => {
  alert(`系统消息: ${broadcast.message}`)
}

// 切换通知面板
const toggleNotificationPanel = () => {
  showNotificationPanel.value = !showNotificationPanel.value
  if (showNotificationPanel.value) {
    loadNotifications()
  }
}

// 加载通知列表
const loadNotifications = async () => {
  try {
    const response = await fetch('/api/notification/list', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify({
        current: 1,
        pageSize: 20
      })
    })
    
    const data = await response.json()
    if (data.code === 0) {
      notifications.value = data.data.records
      // 计算未读数量
      unreadCount.value = notifications.value.filter(n => !n.isRead).length
    }
  } catch (error) {
    console.error('加载通知失败:', error)
  }
}

// 标记全部已读
const markAllAsRead = async () => {
  try {
    const response = await fetch('/api/notification/read/all', {
      method: 'POST',
      credentials: 'include'
    })
    
    const data = await response.json()
    if (data.code === 0) {
      notifications.value.forEach(n => n.isRead = 1)
      unreadCount.value = 0
    }
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

// 格式化时间
const formatTime = (time) => {
  return new Date(time).toLocaleString()
}

// 请求通知权限
const requestNotificationPermission = () => {
  if ('Notification' in window && Notification.permission === 'default') {
    Notification.requestPermission()
  }
}

onMounted(() => {
  requestNotificationPermission()
  connectWebSocket()
  loadNotifications()
})

onUnmounted(() => {
  if (ws.value) {
    ws.value.close()
  }
})
</script>

<style scoped>
.notification-system {
  position: relative;
}

.notification-icon {
  position: relative;
  cursor: pointer;
  padding: 8px;
}

.unread-badge {
  position: absolute;
  top: 0;
  right: 0;
  background: #ff4757;
  color: white;
  border-radius: 50%;
  padding: 2px 6px;
  font-size: 12px;
  min-width: 18px;
  text-align: center;
}

.connection-status {
  font-size: 12px;
  color: #ff4757;
}

.connection-status.connected {
  color: #2ed573;
}

.notification-panel {
  position: absolute;
  top: 100%;
  right: 0;
  width: 300px;
  max-height: 400px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  z-index: 1000;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #eee;
}

.notification-list {
  max-height: 300px;
  overflow-y: auto;
}

.notification-item {
  padding: 12px;
  border-bottom: 1px solid #eee;
}

.notification-item.unread {
  background: #f8f9fa;
}

.realtime-notification {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 2000;
}

.notification-toast {
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
  min-width: 250px;
}
</style>
```

## 消息格式

### WebSocket 消息格式

```json
{
  "type": "NOTIFICATION|BROADCAST|CONNECTED|PONG|ERROR",
  "message": "消息描述",
  "data": {
    // 具体数据内容
  },
  "timestamp": 1640995200000
}
```

### 通知数据格式

```json
{
  "id": 123,
  "userId": 456,
  "senderId": 789,
  "type": "LIKE",
  "title": "收到新的点赞",
  "content": "张三 点赞了你的文章《Spring Boot实战》",
  "relatedId": 100,
  "relatedType": "BLOG",
  "isRead": 0,
  "createTime": "2024-01-01 12:00:00",
  "sender": {
    "id": 789,
    "username": "zhangsan",
    "displayName": "张三",
    "avatarUrl": "https://example.com/avatar.jpg"
  }
}
```

## 部署注意事项

### 1. 生产环境配置

```java
// WebSocketConfig.java
registry.addHandler(notificationWebSocketHandler, "/ws/notification")
        .setAllowedOrigins("https://yourdomain.com") // 配置允许的域名
        .withSockJS();
```

### 2. 负载均衡

如果使用多个服务器实例，需要考虑 WebSocket 会话的粘性会话（Sticky Session）或使用 Redis 等外部存储来共享会话信息。

### 3. 监控和日志

- 监控 WebSocket 连接数量
- 记录连接建立和断开日志
- 监控消息发送成功率

## 测试

### 1. 连接测试

```bash
# 使用 wscat 测试 WebSocket 连接
npm install -g wscat
wscat -c "ws://localhost:8080/ws/notification?userId=123"
```

### 2. 功能测试

1. 登录系统获取用户ID
2. 建立 WebSocket 连接
3. 在另一个浏览器中点赞该用户的文章
4. 观察是否收到实时通知

## 故障排除

### 常见问题

1. **连接失败**
   - 检查用户ID是否正确
   - 确认用户是否存在
   - 检查网络连接

2. **消息接收不到**
   - 检查 WebSocket 连接状态
   - 确认 Pulsar 消息队列正常工作
   - 查看服务器日志

3. **频繁重连**
   - 检查服务器稳定性
   - 调整心跳间隔
   - 检查防火墙设置

通过以上实时通知系统，用户可以立即收到各种类型的通知，大大提升了用户体验和系统的实时性。 