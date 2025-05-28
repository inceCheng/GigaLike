# WebSocket 监控工具使用指南

## 概述

GigaLike 提供了完整的 WebSocket 连接监控功能，管理员可以实时查看连接状态、用户信息、历史记录等，并进行连接管理操作。

## 监控功能

### 1. 在线用户监控

**接口：** `GET /api/websocket/monitor/online-users`

**权限：** 仅管理员

**响应示例：**
```json
{
  "code": 0,
  "data": [
    {
      "userId": 123,
      "username": "zhangsan",
      "displayName": "张三",
      "connectTime": "2024-01-01T10:00:00",
      "lastHeartbeat": "2024-01-01T10:30:00",
      "remoteAddress": "192.168.1.100:52143",
      "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
      "messagesSent": 15,
      "messagesReceived": 8
    }
  ],
  "message": "ok"
}
```

### 2. 连接统计信息

**接口：** `GET /api/websocket/monitor/stats`

**权限：** 仅管理员

**响应示例：**
```json
{
  "code": 0,
  "data": {
    "currentConnections": 25,
    "totalConnections": 1250,
    "totalMessagesSent": 15680,
    "totalMessagesReceived": 8420,
    "connectionHistory": 856,
    "activeConnections": 24,
    "inactiveConnections": 1
  },
  "message": "ok"
}
```

### 3. 用户连接详情

**接口：** `GET /api/websocket/monitor/user/{userId}`

**权限：** 仅管理员

**响应示例：**
```json
{
  "code": 0,
  "data": {
    "userId": 123,
    "username": "zhangsan",
    "displayName": "张三",
    "connectTime": "2024-01-01T10:00:00",
    "lastHeartbeat": "2024-01-01T10:30:00",
    "remoteAddress": "192.168.1.100:52143",
    "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
    "messagesSent": 15,
    "messagesReceived": 8,
    "isOnline": true,
    "sessionId": "ws-session-abc123",
    "isOpen": true
  },
  "message": "ok"
}
```

### 4. 连接历史记录

**接口：** `GET /api/websocket/monitor/history?limit=100`

**权限：** 仅管理员

**响应示例：**
```json
{
  "code": 0,
  "data": [
    {
      "userId": 123,
      "username": "zhangsan",
      "action": "CONNECT",
      "timestamp": "2024-01-01T10:00:00",
      "details": "用户建立WebSocket连接",
      "remoteAddress": "192.168.1.100:52143"
    },
    {
      "userId": 456,
      "username": "lisi",
      "action": "DISCONNECT",
      "timestamp": "2024-01-01T09:45:00",
      "details": "连接关闭: 1000 Normal closure",
      "remoteAddress": "192.168.1.101:52144"
    }
  ],
  "message": "ok"
}
```

### 5. 实时监控数据

**接口：** `GET /api/websocket/monitor/realtime`

**权限：** 仅管理员

**响应示例：**
```json
{
  "code": 0,
  "data": {
    "currentConnections": 25,
    "totalConnections": 1250,
    "totalMessagesSent": 15680,
    "totalMessagesReceived": 8420,
    "activeConnections": 24,
    "inactiveConnections": 1,
    "onlineUserCount": 25,
    "onlineUserIds": [123, 456, 789]
  },
  "message": "ok"
}
```

## 管理功能

### 1. 强制断开连接

**接口：** `POST /api/websocket/monitor/disconnect/{userId}?reason=违规操作`

**权限：** 仅管理员

**用途：** 管理员可以强制断开指定用户的 WebSocket 连接

**响应示例：**
```json
{
  "code": 0,
  "data": true,
  "message": "ok"
}
```

### 2. 清理无效连接

**接口：** `POST /api/websocket/monitor/cleanup`

**权限：** 仅管理员

**用途：** 清理已断开但未正确移除的连接

**响应示例：**
```json
{
  "code": 0,
  "data": 3,
  "message": "ok"
}
```

## 前端监控界面示例

### 1. Vue 3 监控组件

```vue
<template>
  <div class="websocket-monitor">
    <div class="monitor-header">
      <h2>WebSocket 连接监控</h2>
      <div class="actions">
        <button @click="refreshData">刷新</button>
        <button @click="cleanupConnections">清理无效连接</button>
      </div>
    </div>
    
    <!-- 统计信息 -->
    <div class="stats-grid">
      <div class="stat-card">
        <h3>当前连接</h3>
        <div class="stat-value">{{ stats.currentConnections }}</div>
      </div>
      <div class="stat-card">
        <h3>总连接数</h3>
        <div class="stat-value">{{ stats.totalConnections }}</div>
      </div>
      <div class="stat-card">
        <h3>发送消息</h3>
        <div class="stat-value">{{ stats.totalMessagesSent }}</div>
      </div>
      <div class="stat-card">
        <h3>接收消息</h3>
        <div class="stat-value">{{ stats.totalMessagesReceived }}</div>
      </div>
    </div>
    
    <!-- 在线用户列表 -->
    <div class="online-users">
      <h3>在线用户 ({{ onlineUsers.length }})</h3>
      <div class="user-table">
        <table>
          <thead>
            <tr>
              <th>用户ID</th>
              <th>用户名</th>
              <th>显示名</th>
              <th>连接时间</th>
              <th>最后心跳</th>
              <th>IP地址</th>
              <th>发送/接收</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in onlineUsers" :key="user.userId">
              <td>{{ user.userId }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.displayName }}</td>
              <td>{{ formatTime(user.connectTime) }}</td>
              <td>{{ formatTime(user.lastHeartbeat) }}</td>
              <td>{{ user.remoteAddress }}</td>
              <td>{{ user.messagesSent }}/{{ user.messagesReceived }}</td>
              <td>
                <button @click="disconnectUser(user.userId)" class="disconnect-btn">
                  断开连接
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    
    <!-- 连接历史 -->
    <div class="connection-history">
      <h3>连接历史</h3>
      <div class="history-table">
        <table>
          <thead>
            <tr>
              <th>时间</th>
              <th>用户</th>
              <th>动作</th>
              <th>详情</th>
              <th>IP地址</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="record in connectionHistory" :key="record.timestamp" 
                :class="{'connect': record.action === 'CONNECT', 'disconnect': record.action === 'DISCONNECT', 'error': record.action === 'ERROR'}">
              <td>{{ formatTime(record.timestamp) }}</td>
              <td>{{ record.username }} ({{ record.userId }})</td>
              <td>{{ record.action }}</td>
              <td>{{ record.details }}</td>
              <td>{{ record.remoteAddress }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const stats = ref({})
const onlineUsers = ref([])
const connectionHistory = ref([])

let refreshTimer = null

// 加载数据
const loadData = async () => {
  try {
    // 加载统计信息
    const statsResponse = await fetch('/api/websocket/monitor/stats', {
      credentials: 'include'
    })
    if (statsResponse.ok) {
      const statsData = await statsResponse.json()
      stats.value = statsData.data
    }
    
    // 加载在线用户
    const usersResponse = await fetch('/api/websocket/monitor/online-users', {
      credentials: 'include'
    })
    if (usersResponse.ok) {
      const usersData = await usersResponse.json()
      onlineUsers.value = usersData.data
    }
    
    // 加载连接历史
    const historyResponse = await fetch('/api/websocket/monitor/history?limit=50', {
      credentials: 'include'
    })
    if (historyResponse.ok) {
      const historyData = await historyResponse.json()
      connectionHistory.value = historyData.data
    }
    
  } catch (error) {
    console.error('加载监控数据失败:', error)
  }
}

// 刷新数据
const refreshData = () => {
  loadData()
}

// 断开用户连接
const disconnectUser = async (userId) => {
  if (confirm(`确定要断开用户 ${userId} 的连接吗？`)) {
    try {
      const response = await fetch(`/api/websocket/monitor/disconnect/${userId}?reason=管理员操作`, {
        method: 'POST',
        credentials: 'include'
      })
      
      if (response.ok) {
        alert('用户连接已断开')
        refreshData()
      } else {
        alert('断开连接失败')
      }
    } catch (error) {
      console.error('断开连接失败:', error)
      alert('操作失败')
    }
  }
}

// 清理无效连接
const cleanupConnections = async () => {
  try {
    const response = await fetch('/api/websocket/monitor/cleanup', {
      method: 'POST',
      credentials: 'include'
    })
    
    if (response.ok) {
      const result = await response.json()
      alert(`已清理 ${result.data} 个无效连接`)
      refreshData()
    } else {
      alert('清理失败')
    }
  } catch (error) {
    console.error('清理失败:', error)
    alert('操作失败')
  }
}

// 格式化时间
const formatTime = (timeStr) => {
  return new Date(timeStr).toLocaleString()
}

onMounted(() => {
  loadData()
  // 每30秒自动刷新
  refreshTimer = setInterval(loadData, 30000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped>
.websocket-monitor {
  padding: 20px;
}

.monitor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.actions button {
  margin-left: 10px;
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
}

.actions button:hover {
  background: #f5f5f5;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
}

.stat-card h3 {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 14px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.online-users, .connection-history {
  margin-bottom: 30px;
}

.user-table, .history-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background: #f8f9fa;
  font-weight: 600;
}

.disconnect-btn {
  padding: 4px 8px;
  border: 1px solid #dc3545;
  border-radius: 4px;
  background: #dc3545;
  color: white;
  cursor: pointer;
  font-size: 12px;
}

.disconnect-btn:hover {
  background: #c82333;
}

.connect {
  color: #28a745;
}

.disconnect {
  color: #6c757d;
}

.error {
  color: #dc3545;
}
</style>
```

### 2. 实时监控仪表板

```javascript
// 实时监控类
class WebSocketMonitor {
    constructor() {
        this.refreshInterval = 5000; // 5秒刷新
        this.timer = null;
    }
    
    // 开始监控
    start() {
        this.loadRealtimeData();
        this.timer = setInterval(() => {
            this.loadRealtimeData();
        }, this.refreshInterval);
    }
    
    // 停止监控
    stop() {
        if (this.timer) {
            clearInterval(this.timer);
            this.timer = null;
        }
    }
    
    // 加载实时数据
    async loadRealtimeData() {
        try {
            const response = await fetch('/api/websocket/monitor/realtime', {
                credentials: 'include'
            });
            
            if (response.ok) {
                const data = await response.json();
                this.updateDashboard(data.data);
            }
        } catch (error) {
            console.error('加载实时数据失败:', error);
        }
    }
    
    // 更新仪表板
    updateDashboard(data) {
        // 更新连接数
        document.getElementById('current-connections').textContent = data.currentConnections;
        document.getElementById('active-connections').textContent = data.activeConnections;
        document.getElementById('total-messages-sent').textContent = data.totalMessagesSent;
        document.getElementById('total-messages-received').textContent = data.totalMessagesReceived;
        
        // 更新在线用户列表
        const usersList = document.getElementById('online-users-list');
        usersList.innerHTML = data.onlineUserIds.map(id => `<span class="user-id">${id}</span>`).join(' ');
        
        // 更新时间戳
        document.getElementById('last-update').textContent = new Date().toLocaleTimeString();
    }
}

// 使用监控
const monitor = new WebSocketMonitor();
monitor.start();

// 页面卸载时停止监控
window.addEventListener('beforeunload', () => {
    monitor.stop();
});
```

## 日志监控

WebSocket 处理器会自动记录以下日志：

- 用户连接/断开
- 消息发送/接收
- 错误和异常
- 管理员操作

日志级别配置：
```yaml
logging:
  level:
    com.ince.gigalike.websocket: DEBUG
```

## 性能监控

### 内存使用

- 连接信息存储在内存中
- 历史记录限制为1000条
- 定期清理无效连接

### 监控指标

- 当前连接数
- 总连接数
- 消息发送/接收统计
- 连接持续时间
- 心跳频率

## 故障排除

### 常见问题

1. **连接数不准确**
   - 调用清理接口：`POST /api/websocket/monitor/cleanup`
   - 检查应用日志

2. **内存占用过高**
   - 检查连接数是否异常
   - 重启应用清理缓存

3. **监控接口访问失败**
   - 确认管理员权限
   - 检查接口地址

### 监控命令行工具

```bash
# 查看当前连接数
curl -H "Cookie: your-session" http://localhost:8080/api/websocket/monitor/stats

# 清理无效连接
curl -X POST -H "Cookie: your-session" http://localhost:8080/api/websocket/monitor/cleanup

# 断开指定用户
curl -X POST -H "Cookie: your-session" "http://localhost:8080/api/websocket/monitor/disconnect/123?reason=测试"
```

通过这些监控工具，管理员可以全面了解和管理 WebSocket 连接状态，确保系统的稳定运行。 