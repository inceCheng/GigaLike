#!/bin/bash

# 博客热门话题接口测试脚本
# 使用方法: ./test_hot_topics_api.sh

echo "🔥 博客热门话题接口测试"
echo "=========================="

# 设置API基础URL
BASE_URL="http://localhost:8123/api"
HOT_TOPICS_URL="${BASE_URL}/blog/hot-topics"

echo "📡 测试接口: ${HOT_TOPICS_URL}"
echo ""

# 测试1: 基本功能测试
echo "🧪 测试1: 基本功能测试"
echo "发送GET请求到热门话题接口..."

response=$(curl -s -w "\n%{http_code}" "${HOT_TOPICS_URL}")
http_code=$(echo "$response" | tail -n1)
response_body=$(echo "$response" | head -n -1)

echo "HTTP状态码: $http_code"

if [ "$http_code" = "200" ]; then
    echo "✅ 接口调用成功"
    
    # 检查响应格式
    echo ""
    echo "📋 响应内容:"
    echo "$response_body" | jq '.'
    
    # 验证响应结构
    echo ""
    echo "🔍 验证响应结构:"
    
    # 检查code字段
    code=$(echo "$response_body" | jq -r '.code')
    if [ "$code" = "0" ]; then
        echo "✅ code字段正确: $code"
    else
        echo "❌ code字段错误: $code"
    fi
    
    # 检查data字段是否为数组
    data_type=$(echo "$response_body" | jq -r '.data | type')
    if [ "$data_type" = "array" ]; then
        echo "✅ data字段类型正确: $data_type"
        
        # 检查数组长度
        data_length=$(echo "$response_body" | jq -r '.data | length')
        echo "📊 返回话题数量: $data_length"
        
        if [ "$data_length" -le "10" ] && [ "$data_length" -gt "0" ]; then
            echo "✅ 话题数量合理 (1-10个)"
        else
            echo "⚠️  话题数量异常: $data_length"
        fi
        
        # 检查第一个话题的字段
        if [ "$data_length" -gt "0" ]; then
            echo ""
            echo "🔍 检查第一个话题的字段:"
            
            first_topic=$(echo "$response_body" | jq -r '.data[0]')
            
            # 检查必要字段
            id=$(echo "$first_topic" | jq -r '.id')
            name=$(echo "$first_topic" | jq -r '.name')
            description=$(echo "$first_topic" | jq -r '.description')
            color=$(echo "$first_topic" | jq -r '.color')
            postCount=$(echo "$first_topic" | jq -r '.postCount')
            
            echo "  ID: $id"
            echo "  名称: $name"
            echo "  描述: $description"
            echo "  颜色: $color"
            echo "  帖子数: $postCount"
            
            if [ "$id" != "null" ] && [ "$name" != "null" ]; then
                echo "✅ 必要字段完整"
            else
                echo "❌ 缺少必要字段"
            fi
        fi
        
    else
        echo "❌ data字段类型错误: $data_type"
    fi
    
else
    echo "❌ 接口调用失败，HTTP状态码: $http_code"
    echo "响应内容: $response_body"
fi

echo ""
echo "=========================="

# 测试2: 性能测试
echo "🧪 测试2: 性能测试"
echo "测试接口响应时间..."

start_time=$(date +%s%N)
curl -s "${HOT_TOPICS_URL}" > /dev/null
end_time=$(date +%s%N)

duration=$((($end_time - $start_time) / 1000000))
echo "⏱️  响应时间: ${duration}ms"

if [ "$duration" -lt "1000" ]; then
    echo "✅ 响应时间良好 (<1秒)"
elif [ "$duration" -lt "3000" ]; then
    echo "⚠️  响应时间一般 (1-3秒)"
else
    echo "❌ 响应时间过慢 (>3秒)"
fi

echo ""
echo "=========================="

# 测试3: 并发测试
echo "🧪 测试3: 简单并发测试"
echo "发送5个并发请求..."

for i in {1..5}; do
    curl -s "${HOT_TOPICS_URL}" > /dev/null &
done

wait
echo "✅ 并发测试完成"

echo ""
echo "=========================="
echo "🎉 测试完成！"
echo ""
echo "💡 提示:"
echo "1. 确保项目已启动 (http://localhost:8123)"
echo "2. 确保数据库中有话题数据"
echo "3. 如需插入测试数据，请运行: sql/insert_new_topics.sql"
echo ""
echo "📚 相关文档:"
echo "- API文档: doc/default.md"
echo "- 功能说明: doc/BLOG_HOT_TOPICS_API.md" 