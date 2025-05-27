# 验证码颜色配置指南

## 问题说明

Kaptcha验证码库的颜色配置只支持RGB数值格式，不支持颜色名称（如"blue", "red"等）。

### 错误配置示例
```java
// ❌ 错误：使用颜色名称
properties.setProperty("kaptcha.textproducer.font.color", "blue,red,green");
properties.setProperty("kaptcha.background.clear.from", "lightGray");
```

### 正确配置示例
```java
// ✅ 正确：使用RGB数值
properties.setProperty("kaptcha.textproducer.font.color", "0,0,255"); // 蓝色
properties.setProperty("kaptcha.background.clear.from", "211,211,211"); // 浅灰色
```

## 常用颜色RGB值对照表

### 基础颜色
| 颜色名称 | RGB值 | 十六进制 | 效果 |
|---------|-------|---------|------|
| 黑色 | 0,0,0 | #000000 | 最高对比度，推荐用于字体 |
| 白色 | 255,255,255 | #FFFFFF | 纯白背景 |
| 红色 | 255,0,0 | #FF0000 | 鲜艳红色 |
| 绿色 | 0,128,0 | #008000 | 标准绿色 |
| 蓝色 | 0,0,255 | #0000FF | 标准蓝色 |

### 深色系（推荐用于字体）
| 颜色名称 | RGB值 | 十六进制 | 适用场景 |
|---------|-------|---------|---------|
| 深蓝色 | 25,25,112 | #191970 | 字体颜色，高对比度 |
| 深红色 | 139,0,0 | #8B0000 | 字体颜色，醒目 |
| 深绿色 | 0,100,0 | #006400 | 字体颜色，护眼 |
| 深灰色 | 64,64,64 | #404040 | 字体颜色，柔和 |
| 棕色 | 139,69,19 | #8B4513 | 字体颜色，温和 |

### 浅色系（推荐用于背景）
| 颜色名称 | RGB值 | 十六进制 | 适用场景 |
|---------|-------|---------|---------|
| 浅灰色 | 211,211,211 | #D3D3D3 | 背景、干扰线 |
| 浅蓝色 | 173,216,230 | #ADD8E6 | 背景渐变 |
| 浅绿色 | 144,238,144 | #90EE90 | 背景渐变 |
| 浅黄色 | 255,255,224 | #FFFACD | 背景渐变 |
| 浅粉色 | 255,182,193 | #FFB6C1 | 背景渐变 |

### 边框颜色推荐
| 颜色名称 | RGB值 | 十六进制 | 视觉效果 |
|---------|-------|---------|---------|
| 标准蓝 | 0,0,255 | #0000FF | 经典、专业 |
| 深蓝 | 0,0,139 | #00008B | 稳重、商务 |
| 绿色 | 105,179,90 | #69B35A | 清新、自然 |
| 灰色 | 128,128,128 | #808080 | 中性、简约 |

## 配置示例

### 高对比度清晰配置
```java
// 字体：黑色
properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
// 背景：白色
properties.setProperty("kaptcha.background.clear.from", "255,255,255");
properties.setProperty("kaptcha.background.clear.to", "255,255,255");
// 边框：深灰色
properties.setProperty("kaptcha.border.color", "64,64,64");
```

### 蓝色主题配置
```java
// 字体：深蓝色
properties.setProperty("kaptcha.textproducer.font.color", "25,25,112");
// 背景：浅蓝到白色渐变
properties.setProperty("kaptcha.background.clear.from", "173,216,230");
properties.setProperty("kaptcha.background.clear.to", "255,255,255");
// 边框：标准蓝
properties.setProperty("kaptcha.border.color", "0,0,255");
```

### 绿色主题配置
```java
// 字体：深绿色
properties.setProperty("kaptcha.textproducer.font.color", "0,100,0");
// 背景：浅绿到白色渐变
properties.setProperty("kaptcha.background.clear.from", "144,238,144");
properties.setProperty("kaptcha.background.clear.to", "255,255,255");
// 边框：绿色
properties.setProperty("kaptcha.border.color", "105,179,90");
```

## 颜色选择建议

### 字体颜色选择
1. **优先使用深色**：确保与背景有足够对比度
2. **避免过于鲜艳**：减少视觉疲劳
3. **推荐颜色**：
   - 黑色 (0,0,0) - 最佳对比度
   - 深蓝 (25,25,112) - 专业感
   - 深灰 (64,64,64) - 柔和

### 背景颜色选择
1. **使用浅色**：为深色字体提供对比
2. **渐变效果**：from浅色到白色
3. **推荐组合**：
   - 浅灰到白色：211,211,211 → 255,255,255
   - 浅蓝到白色：173,216,230 → 255,255,255
   - 纯白背景：255,255,255 → 255,255,255

### 边框颜色选择
1. **与主题一致**：与字体或背景颜色协调
2. **适中对比度**：不要太突兀
3. **推荐颜色**：
   - 深灰 (128,128,128) - 中性
   - 深蓝 (0,0,139) - 商务
   - 绿色 (105,179,90) - 清新

## RGB转换工具

### 在线转换工具
- [RGB颜色对照表](https://www.rapidtables.com/web/color/RGB_Color.html)
- [颜色选择器](https://htmlcolorcodes.com/color-picker/)

### 手动转换
如果你有十六进制颜色值（如#FF0000），可以这样转换：
- #FF0000 = RGB(255,0,0)
- #00FF00 = RGB(0,255,0)
- #0000FF = RGB(0,0,255)

## 故障排除

### 常见错误
1. **NumberFormatException**: 使用了颜色名称而不是RGB值
2. **颜色显示异常**: RGB值超出0-255范围

### 解决方案
1. 检查所有颜色配置是否使用RGB格式
2. 确保RGB值在0-255范围内
3. 使用逗号分隔RGB值，不要有空格

### 测试配置
修改配置后，重启应用并访问验证码接口测试：
```bash
curl http://localhost:8123/api/captcha/generate
```

现在验证码颜色配置已修复，不会再出现颜色解析错误！ 