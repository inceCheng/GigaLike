# OpenAPI definition


**简介**:OpenAPI definition


**HOST**:http://localhost:8123/api


**联系人**:


**Version**:v0


**接口路径**:/api/v3/api-docs/default


[TOC]






# 博客管理


## 创建博客


**接口地址**:`/api/blog/create`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>用户创建新的博客文章，可以添加话题标签</p>



**请求示例**:


```javascript
{
  "title": "",
  "coverImg": "",
  "content": "",
  "topicNames": []
}
```


**请求参数**:


| 参数名称               | 参数说明          | 请求类型 | 是否必须 | 数据类型          | schema            |
| ---------------------- | ----------------- | -------- | -------- | ----------------- | ----------------- |
| blogCreateRequest      | BlogCreateRequest | body     | true     | BlogCreateRequest | BlogCreateRequest |
| &emsp;&emsp;title      |                   |          | true     | string            |                   |
| &emsp;&emsp;coverImg   |                   |          | false    | string            |                   |
| &emsp;&emsp;content    |                   |          | true     | string            |                   |
| &emsp;&emsp;topicNames |                   |          | false    | array             | string            |


**响应状态**:


| 状态码 | 说明 | schema           |
| ------ | ---- | ---------------- |
| 200    | OK   | BaseResponseLong |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     |          | integer(int32) | integer(int32) |
| data     |          | integer(int64) | integer(int64) |
| message  |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": 0,
	"message": ""
}
```


## 获取博客详情


**接口地址**:`/api/blog/get`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据博客ID获取博客详细信息，包含话题标签</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型 | 是否必须 | 数据类型       | schema |
| -------- | -------- | -------- | -------- | -------------- | ------ |
| blogId   |          | query    | true     | integer(int64) |        |


**响应状态**:


| 状态码 | 说明 | schema             |
| ------ | ---- | ------------------ |
| 200    | OK   | BaseResponseBlogVO |


**响应参数**:


| 参数名称                                                | 参数说明 | 类型              | schema         |
| ------------------------------------------------------- | -------- | ----------------- | -------------- |
| code                                                    |          | integer(int32)    | integer(int32) |
| data                                                    |          | BlogVO            | BlogVO         |
| &emsp;&emsp;id                                          |          | integer(int64)    |                |
| &emsp;&emsp;title                                       |          | string            |                |
| &emsp;&emsp;coverImg                                    |          | string            |                |
| &emsp;&emsp;content                                     |          | string            |                |
| &emsp;&emsp;thumbCount                                  |          | integer(int32)    |                |
| &emsp;&emsp;createTime                                  |          | string(date-time) |                |
| &emsp;&emsp;hasThumb                                    |          | boolean           |                |
| &emsp;&emsp;author                                      |          | UserVO            | UserVO         |
| &emsp;&emsp;&emsp;&emsp;id                              |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;username                        |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;email                           |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;displayName                     |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;avatarUrl                       |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;bio                             |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;status                          |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;emailVerified                   |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;role                            |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;locale                          |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;timezone                        |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;lastLoginIpLocation             |          | string            |                |
| &emsp;&emsp;topics                                      |          | array             | TopicVO        |
| &emsp;&emsp;&emsp;&emsp;id                              |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;name                            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;description                     |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;coverImage                      |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;color                           |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;status                          |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;postCount                       |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;followCount                     |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;isOfficial                      |          | boolean           |                |
| &emsp;&emsp;&emsp;&emsp;isFollowed                      |          | boolean           |                |
| &emsp;&emsp;&emsp;&emsp;createTime                      |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;creator                         |          | UserVO            | UserVO         |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;id                  |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;username            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;email               |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;displayName         |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;avatarUrl           |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;bio                 |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;status              |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;emailVerified       |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;role                |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;locale              |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;timezone            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;lastLoginIpLocation |          | string            |                |
| message                                                 |          | string            |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": {
		"id": 0,
		"title": "",
		"coverImg": "",
		"content": "",
		"thumbCount": 0,
		"createTime": "",
		"hasThumb": true,
		"author": {
			"id": 0,
			"username": "",
			"email": "",
			"displayName": "",
			"avatarUrl": "",
			"bio": "",
			"status": {},
			"emailVerified": 0,
			"role": {},
			"locale": "",
			"timezone": "",
			"lastLoginIpLocation": ""
		},
		"topics": [
			{
				"id": 0,
				"name": "",
				"description": "",
				"coverImage": "",
				"color": "",
				"status": "",
				"postCount": 0,
				"followCount": 0,
				"isOfficial": true,
				"isFollowed": true,
				"createTime": "",
				"creator": {
					"id": 0,
					"username": "",
					"email": "",
					"displayName": "",
					"avatarUrl": "",
					"bio": "",
					"status": {},
					"emailVerified": 0,
					"role": {},
					"locale": "",
					"timezone": "",
					"lastLoginIpLocation": ""
				}
			}
		]
	},
	"message": ""
}
```


## 获取博客列表


**接口地址**:`/api/blog/list`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取所有博客列表，包含话题标签信息</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema                 |
| ------ | ---- | ---------------------- |
| 200    | OK   | BaseResponseListBlogVO |


**响应参数**:


| 参数名称                                                | 参数说明 | 类型              | schema         |
| ------------------------------------------------------- | -------- | ----------------- | -------------- |
| code                                                    |          | integer(int32)    | integer(int32) |
| data                                                    |          | array             | BlogVO         |
| &emsp;&emsp;id                                          |          | integer(int64)    |                |
| &emsp;&emsp;title                                       |          | string            |                |
| &emsp;&emsp;coverImg                                    |          | string            |                |
| &emsp;&emsp;content                                     |          | string            |                |
| &emsp;&emsp;thumbCount                                  |          | integer(int32)    |                |
| &emsp;&emsp;createTime                                  |          | string(date-time) |                |
| &emsp;&emsp;hasThumb                                    |          | boolean           |                |
| &emsp;&emsp;author                                      |          | UserVO            | UserVO         |
| &emsp;&emsp;&emsp;&emsp;id                              |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;username                        |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;email                           |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;displayName                     |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;avatarUrl                       |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;bio                             |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;status                          |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;emailVerified                   |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;role                            |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;locale                          |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;timezone                        |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;lastLoginIpLocation             |          | string            |                |
| &emsp;&emsp;topics                                      |          | array             | TopicVO        |
| &emsp;&emsp;&emsp;&emsp;id                              |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;name                            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;description                     |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;coverImage                      |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;color                           |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;status                          |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;postCount                       |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;followCount                     |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;isOfficial                      |          | boolean           |                |
| &emsp;&emsp;&emsp;&emsp;isFollowed                      |          | boolean           |                |
| &emsp;&emsp;&emsp;&emsp;createTime                      |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;creator                         |          | UserVO            | UserVO         |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;id                  |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;username            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;email               |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;displayName         |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;avatarUrl           |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;bio                 |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;status              |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;emailVerified       |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;role                |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;locale              |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;timezone            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;lastLoginIpLocation |          | string            |                |
| message                                                 |          | string            |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": [
		{
			"id": 0,
			"title": "",
			"coverImg": "",
			"content": "",
			"thumbCount": 0,
			"createTime": "",
			"hasThumb": true,
			"author": {
				"id": 0,
				"username": "",
				"email": "",
				"displayName": "",
				"avatarUrl": "",
				"bio": "",
				"status": {},
				"emailVerified": 0,
				"role": {},
				"locale": "",
				"timezone": "",
				"lastLoginIpLocation": ""
			},
			"topics": [
				{
					"id": 0,
					"name": "",
					"description": "",
					"coverImage": "",
					"color": "",
					"status": "",
					"postCount": 0,
					"followCount": 0,
					"isOfficial": true,
					"isFollowed": true,
					"createTime": "",
					"creator": {
						"id": 0,
						"username": "",
						"email": "",
						"displayName": "",
						"avatarUrl": "",
						"bio": "",
						"status": {},
						"emailVerified": 0,
						"role": {},
						"locale": "",
						"timezone": "",
						"lastLoginIpLocation": ""
					}
				}
			]
		}
	],
	"message": ""
}
```


## 更新博客话题


**接口地址**:`/api/blog/topics/update`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>更新指定博客的话题标签，支持话题名称，不存在的话题会自动创建</p>



**请求示例**:


```javascript
[]
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型 | 是否必须 | 数据类型       | schema |
| -------- | -------- | -------- | -------- | -------------- | ------ |
| blogId   |          | query    | true     | integer(int64) |        |
| strings  | string   | body     | true     | array          |        |


**响应状态**:


| 状态码 | 说明 | schema              |
| ------ | ---- | ------------------- |
| 200    | OK   | BaseResponseBoolean |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     |          | integer(int32) | integer(int32) |
| data     |          | boolean        |                |
| message  |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": true,
	"message": ""
}
```


# 测试接口


## 管理员接口


**接口地址**:`/api/test/admin`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>需要管理员权限才能访问</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema             |
| ------ | ---- | ------------------ |
| 200    | OK   | BaseResponseString |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     |          | integer(int32) | integer(int32) |
| data     |          | string         |                |
| message  |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": "",
	"message": ""
}
```


## 编辑接口


**接口地址**:`/api/test/editor`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>需要编辑或管理员权限才能访问</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema             |
| ------ | ---- | ------------------ |
| 200    | OK   | BaseResponseString |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     |          | integer(int32) | integer(int32) |
| data     |          | string         |                |
| message  |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": "",
	"message": ""
}
```


## 私有接口


**接口地址**:`/api/test/private`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>需要登录才能访问</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema             |
| ------ | ---- | ------------------ |
| 200    | OK   | BaseResponseString |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     |          | integer(int32) | integer(int32) |
| data     |          | string         |                |
| message  |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": "",
	"message": ""
}
```


## 公开接口


**接口地址**:`/api/test/public`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>无需登录即可访问</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema             |
| ------ | ---- | ------------------ |
| 200    | OK   | BaseResponseString |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     |          | integer(int32) | integer(int32) |
| data     |          | string         |                |
| message  |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": "",
	"message": ""
}
```


# 话题管理


## 获取博客的话题


**接口地址**:`/api/topic/blog`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据博客ID获取关联的话题列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型 | 是否必须 | 数据类型       | schema |
| -------- | -------- | -------- | -------- | -------------- | ------ |
| blogId   |          | query    | true     | integer(int64) |        |


**响应状态**:


| 状态码 | 说明 | schema                  |
| ------ | ---- | ----------------------- |
| 200    | OK   | BaseResponseListTopicVO |


**响应参数**:


| 参数名称                                    | 参数说明 | 类型              | schema         |
| ------------------------------------------- | -------- | ----------------- | -------------- |
| code                                        |          | integer(int32)    | integer(int32) |
| data                                        |          | array             | TopicVO        |
| &emsp;&emsp;id                              |          | integer(int64)    |                |
| &emsp;&emsp;name                            |          | string            |                |
| &emsp;&emsp;description                     |          | string            |                |
| &emsp;&emsp;coverImage                      |          | string            |                |
| &emsp;&emsp;color                           |          | string            |                |
| &emsp;&emsp;status                          |          | string            |                |
| &emsp;&emsp;postCount                       |          | integer(int32)    |                |
| &emsp;&emsp;followCount                     |          | integer(int32)    |                |
| &emsp;&emsp;isOfficial                      |          | boolean           |                |
| &emsp;&emsp;isFollowed                      |          | boolean           |                |
| &emsp;&emsp;createTime                      |          | string(date-time) |                |
| &emsp;&emsp;creator                         |          | UserVO            | UserVO         |
| &emsp;&emsp;&emsp;&emsp;id                  |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;username            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;email               |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;displayName         |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;avatarUrl           |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;bio                 |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;status              |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;emailVerified       |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;role                |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;locale              |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;timezone            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;lastLoginIpLocation |          | string            |                |
| message                                     |          | string            |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": [
		{
			"id": 0,
			"name": "",
			"description": "",
			"coverImage": "",
			"color": "",
			"status": "",
			"postCount": 0,
			"followCount": 0,
			"isOfficial": true,
			"isFollowed": true,
			"createTime": "",
			"creator": {
				"id": 0,
				"username": "",
				"email": "",
				"displayName": "",
				"avatarUrl": "",
				"bio": "",
				"status": {},
				"emailVerified": 0,
				"role": {},
				"locale": "",
				"timezone": "",
				"lastLoginIpLocation": ""
			}
		}
	],
	"message": ""
}
```


## 创建话题


**接口地址**:`/api/topic/create`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>用户可以创建新的话题</p>



**请求示例**:


```javascript
{
  "name": "",
  "description": "",
  "coverImage": "",
  "color": ""
}
```


**请求参数**:


| 参数名称                | 参数说明           | 请求类型 | 是否必须 | 数据类型           | schema             |
| ----------------------- | ------------------ | -------- | -------- | ------------------ | ------------------ |
| topicCreateRequest      | TopicCreateRequest | body     | true     | TopicCreateRequest | TopicCreateRequest |
| &emsp;&emsp;name        |                    |          | true     | string             |                    |
| &emsp;&emsp;description |                    |          | false    | string             |                    |
| &emsp;&emsp;coverImage  |                    |          | false    | string             |                    |
| &emsp;&emsp;color       |                    |          | false    | string             |                    |


**响应状态**:


| 状态码 | 说明 | schema           |
| ------ | ---- | ---------------- |
| 200    | OK   | BaseResponseLong |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     |          | integer(int32) | integer(int32) |
| data     |          | integer(int64) | integer(int64) |
| message  |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": 0,
	"message": ""
}
```


## 关注话题


**接口地址**:`/api/topic/follow`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>用户关注指定话题</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型 | 是否必须 | 数据类型       | schema |
| -------- | -------- | -------- | -------- | -------------- | ------ |
| topicId  |          | query    | true     | integer(int64) |        |


**响应状态**:


| 状态码 | 说明 | schema              |
| ------ | ---- | ------------------- |
| 200    | OK   | BaseResponseBoolean |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     |          | integer(int32) | integer(int32) |
| data     |          | boolean        |                |
| message  |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": true,
	"message": ""
}
```


## 获取关注的话题


**接口地址**:`/api/topic/followed`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取当前用户关注的话题列表</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema                  |
| ------ | ---- | ----------------------- |
| 200    | OK   | BaseResponseListTopicVO |


**响应参数**:


| 参数名称                                    | 参数说明 | 类型              | schema         |
| ------------------------------------------- | -------- | ----------------- | -------------- |
| code                                        |          | integer(int32)    | integer(int32) |
| data                                        |          | array             | TopicVO        |
| &emsp;&emsp;id                              |          | integer(int64)    |                |
| &emsp;&emsp;name                            |          | string            |                |
| &emsp;&emsp;description                     |          | string            |                |
| &emsp;&emsp;coverImage                      |          | string            |                |
| &emsp;&emsp;color                           |          | string            |                |
| &emsp;&emsp;status                          |          | string            |                |
| &emsp;&emsp;postCount                       |          | integer(int32)    |                |
| &emsp;&emsp;followCount                     |          | integer(int32)    |                |
| &emsp;&emsp;isOfficial                      |          | boolean           |                |
| &emsp;&emsp;isFollowed                      |          | boolean           |                |
| &emsp;&emsp;createTime                      |          | string(date-time) |                |
| &emsp;&emsp;creator                         |          | UserVO            | UserVO         |
| &emsp;&emsp;&emsp;&emsp;id                  |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;username            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;email               |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;displayName         |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;avatarUrl           |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;bio                 |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;status              |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;emailVerified       |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;role                |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;locale              |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;timezone            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;lastLoginIpLocation |          | string            |                |
| message                                     |          | string            |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": [
		{
			"id": 0,
			"name": "",
			"description": "",
			"coverImage": "",
			"color": "",
			"status": "",
			"postCount": 0,
			"followCount": 0,
			"isOfficial": true,
			"isFollowed": true,
			"createTime": "",
			"creator": {
				"id": 0,
				"username": "",
				"email": "",
				"displayName": "",
				"avatarUrl": "",
				"bio": "",
				"status": {},
				"emailVerified": 0,
				"role": {},
				"locale": "",
				"timezone": "",
				"lastLoginIpLocation": ""
			}
		}
	],
	"message": ""
}
```


## 获取话题详情


**接口地址**:`/api/topic/get`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据话题ID获取话题详细信息</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型 | 是否必须 | 数据类型       | schema |
| -------- | -------- | -------- | -------- | -------------- | ------ |
| topicId  |          | query    | true     | integer(int64) |        |


**响应状态**:


| 状态码 | 说明 | schema              |
| ------ | ---- | ------------------- |
| 200    | OK   | BaseResponseTopicVO |


**响应参数**:


| 参数名称                                    | 参数说明 | 类型              | schema         |
| ------------------------------------------- | -------- | ----------------- | -------------- |
| code                                        |          | integer(int32)    | integer(int32) |
| data                                        |          | TopicVO           | TopicVO        |
| &emsp;&emsp;id                              |          | integer(int64)    |                |
| &emsp;&emsp;name                            |          | string            |                |
| &emsp;&emsp;description                     |          | string            |                |
| &emsp;&emsp;coverImage                      |          | string            |                |
| &emsp;&emsp;color                           |          | string            |                |
| &emsp;&emsp;status                          |          | string            |                |
| &emsp;&emsp;postCount                       |          | integer(int32)    |                |
| &emsp;&emsp;followCount                     |          | integer(int32)    |                |
| &emsp;&emsp;isOfficial                      |          | boolean           |                |
| &emsp;&emsp;isFollowed                      |          | boolean           |                |
| &emsp;&emsp;createTime                      |          | string(date-time) |                |
| &emsp;&emsp;creator                         |          | UserVO            | UserVO         |
| &emsp;&emsp;&emsp;&emsp;id                  |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;username            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;email               |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;displayName         |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;avatarUrl           |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;bio                 |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;status              |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;emailVerified       |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;role                |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;locale              |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;timezone            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;lastLoginIpLocation |          | string            |                |
| message                                     |          | string            |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": {
		"id": 0,
		"name": "",
		"description": "",
		"coverImage": "",
		"color": "",
		"status": "",
		"postCount": 0,
		"followCount": 0,
		"isOfficial": true,
		"isFollowed": true,
		"createTime": "",
		"creator": {
			"id": 0,
			"username": "",
			"email": "",
			"displayName": "",
			"avatarUrl": "",
			"bio": "",
			"status": {},
			"emailVerified": 0,
			"role": {},
			"locale": "",
			"timezone": "",
			"lastLoginIpLocation": ""
		}
	},
	"message": ""
}
```


## 获取热门话题


**接口地址**:`/api/topic/hot`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取热门话题列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型 | 是否必须 | 数据类型       | schema |
| -------- | -------- | -------- | -------- | -------------- | ------ |
| limit    |          | query    | false    | integer(int32) |        |


**响应状态**:


| 状态码 | 说明 | schema                  |
| ------ | ---- | ----------------------- |
| 200    | OK   | BaseResponseListTopicVO |


**响应参数**:


| 参数名称                                    | 参数说明 | 类型              | schema         |
| ------------------------------------------- | -------- | ----------------- | -------------- |
| code                                        |          | integer(int32)    | integer(int32) |
| data                                        |          | array             | TopicVO        |
| &emsp;&emsp;id                              |          | integer(int64)    |                |
| &emsp;&emsp;name                            |          | string            |                |
| &emsp;&emsp;description                     |          | string            |                |
| &emsp;&emsp;coverImage                      |          | string            |                |
| &emsp;&emsp;color                           |          | string            |                |
| &emsp;&emsp;status                          |          | string            |                |
| &emsp;&emsp;postCount                       |          | integer(int32)    |                |
| &emsp;&emsp;followCount                     |          | integer(int32)    |                |
| &emsp;&emsp;isOfficial                      |          | boolean           |                |
| &emsp;&emsp;isFollowed                      |          | boolean           |                |
| &emsp;&emsp;createTime                      |          | string(date-time) |                |
| &emsp;&emsp;creator                         |          | UserVO            | UserVO         |
| &emsp;&emsp;&emsp;&emsp;id                  |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;username            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;email               |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;displayName         |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;avatarUrl           |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;bio                 |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;status              |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;emailVerified       |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;role                |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;locale              |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;timezone            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;lastLoginIpLocation |          | string            |                |
| message                                     |          | string            |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": [
		{
			"id": 0,
			"name": "",
			"description": "",
			"coverImage": "",
			"color": "",
			"status": "",
			"postCount": 0,
			"followCount": 0,
			"isOfficial": true,
			"isFollowed": true,
			"createTime": "",
			"creator": {
				"id": 0,
				"username": "",
				"email": "",
				"displayName": "",
				"avatarUrl": "",
				"bio": "",
				"status": {},
				"emailVerified": 0,
				"role": {},
				"locale": "",
				"timezone": "",
				"lastLoginIpLocation": ""
			}
		}
	],
	"message": ""
}
```


## 分页查询话题


**接口地址**:`/api/topic/list`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>根据条件分页查询话题列表</p>



**请求示例**:


```javascript
{
  "name": "",
  "status": "",
  "isOfficial": true,
  "sortField": "",
  "sortOrder": "",
  "current": 0,
  "pageSize": 0
}
```


**请求参数**:


| 参数名称               | 参数说明          | 请求类型 | 是否必须 | 数据类型          | schema            |
| ---------------------- | ----------------- | -------- | -------- | ----------------- | ----------------- |
| topicQueryRequest      | TopicQueryRequest | body     | true     | TopicQueryRequest | TopicQueryRequest |
| &emsp;&emsp;name       |                   |          | false    | string            |                   |
| &emsp;&emsp;status     |                   |          | false    | string            |                   |
| &emsp;&emsp;isOfficial |                   |          | false    | boolean           |                   |
| &emsp;&emsp;sortField  |                   |          | false    | string            |                   |
| &emsp;&emsp;sortOrder  |                   |          | false    | string            |                   |
| &emsp;&emsp;current    |                   |          | false    | integer(int64)    |                   |
| &emsp;&emsp;pageSize   |                   |          | false    | integer(int64)    |                   |


**响应状态**:


| 状态码 | 说明 | schema                   |
| ------ | ---- | ------------------------ |
| 200    | OK   | BaseResponseIPageTopicVO |


**响应参数**:


| 参数名称                                                | 参数说明 | 类型           | schema         |
| ------------------------------------------------------- | -------- | -------------- | -------------- |
| code                                                    |          | integer(int32) | integer(int32) |
| data                                                    |          | IPageTopicVO   | IPageTopicVO   |
| &emsp;&emsp;size                                        |          | integer(int64) |                |
| &emsp;&emsp;records                                     |          | array          | TopicVO        |
| &emsp;&emsp;&emsp;&emsp;id                              |          | integer        |                |
| &emsp;&emsp;&emsp;&emsp;name                            |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;description                     |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;coverImage                      |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;color                           |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;status                          |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;postCount                       |          | integer        |                |
| &emsp;&emsp;&emsp;&emsp;followCount                     |          | integer        |                |
| &emsp;&emsp;&emsp;&emsp;isOfficial                      |          | boolean        |                |
| &emsp;&emsp;&emsp;&emsp;isFollowed                      |          | boolean        |                |
| &emsp;&emsp;&emsp;&emsp;createTime                      |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;creator                         |          | UserVO         | UserVO         |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;id                  |          | integer        |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;username            |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;email               |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;displayName         |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;avatarUrl           |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;bio                 |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;status              |          | object         |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;emailVerified       |          | integer        |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;role                |          | object         |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;locale              |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;timezone            |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;lastLoginIpLocation |          | string         |                |
| &emsp;&emsp;current                                     |          | integer(int64) |                |
| &emsp;&emsp;total                                       |          | integer(int64) |                |
| &emsp;&emsp;pages                                       |          | integer(int64) |                |
| message                                                 |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": {
		"size": 0,
		"records": [
			{
				"id": 0,
				"name": "",
				"description": "",
				"coverImage": "",
				"color": "",
				"status": "",
				"postCount": 0,
				"followCount": 0,
				"isOfficial": true,
				"isFollowed": true,
				"createTime": "",
				"creator": {
					"id": 0,
					"username": "",
					"email": "",
					"displayName": "",
					"avatarUrl": "",
					"bio": "",
					"status": {},
					"emailVerified": 0,
					"role": {},
					"locale": "",
					"timezone": "",
					"lastLoginIpLocation": ""
				}
			}
		],
		"current": 0,
		"total": 0,
		"pages": 0
	},
	"message": ""
}
```


## 搜索话题


**接口地址**:`/api/topic/search`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据关键词搜索话题</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型 | 是否必须 | 数据类型 | schema |
| -------- | -------- | -------- | -------- | -------- | ------ |
| keyword  |          | query    | true     | string   |        |


**响应状态**:


| 状态码 | 说明 | schema                  |
| ------ | ---- | ----------------------- |
| 200    | OK   | BaseResponseListTopicVO |


**响应参数**:


| 参数名称                                    | 参数说明 | 类型              | schema         |
| ------------------------------------------- | -------- | ----------------- | -------------- |
| code                                        |          | integer(int32)    | integer(int32) |
| data                                        |          | array             | TopicVO        |
| &emsp;&emsp;id                              |          | integer(int64)    |                |
| &emsp;&emsp;name                            |          | string            |                |
| &emsp;&emsp;description                     |          | string            |                |
| &emsp;&emsp;coverImage                      |          | string            |                |
| &emsp;&emsp;color                           |          | string            |                |
| &emsp;&emsp;status                          |          | string            |                |
| &emsp;&emsp;postCount                       |          | integer(int32)    |                |
| &emsp;&emsp;followCount                     |          | integer(int32)    |                |
| &emsp;&emsp;isOfficial                      |          | boolean           |                |
| &emsp;&emsp;isFollowed                      |          | boolean           |                |
| &emsp;&emsp;createTime                      |          | string(date-time) |                |
| &emsp;&emsp;creator                         |          | UserVO            | UserVO         |
| &emsp;&emsp;&emsp;&emsp;id                  |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;username            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;email               |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;displayName         |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;avatarUrl           |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;bio                 |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;status              |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;emailVerified       |          | integer           |                |
| &emsp;&emsp;&emsp;&emsp;role                |          | object            |                |
| &emsp;&emsp;&emsp;&emsp;locale              |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;timezone            |          | string            |                |
| &emsp;&emsp;&emsp;&emsp;lastLoginIpLocation |          | string            |                |
| message                                     |          | string            |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": [
		{
			"id": 0,
			"name": "",
			"description": "",
			"coverImage": "",
			"color": "",
			"status": "",
			"postCount": 0,
			"followCount": 0,
			"isOfficial": true,
			"isFollowed": true,
			"createTime": "",
			"creator": {
				"id": 0,
				"username": "",
				"email": "",
				"displayName": "",
				"avatarUrl": "",
				"bio": "",
				"status": {},
				"emailVerified": 0,
				"role": {},
				"locale": "",
				"timezone": "",
				"lastLoginIpLocation": ""
			}
		}
	],
	"message": ""
}
```


## 取消关注话题


**接口地址**:`/api/topic/unfollow`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>用户取消关注指定话题</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型 | 是否必须 | 数据类型       | schema |
| -------- | -------- | -------- | -------- | -------------- | ------ |
| topicId  |          | query    | true     | integer(int64) |        |


**响应状态**:


| 状态码 | 说明 | schema              |
| ------ | ---- | ------------------- |
| 200    | OK   | BaseResponseBoolean |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     |          | integer(int32) | integer(int32) |
| data     |          | boolean        |                |
| message  |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": true,
	"message": ""
}
```


# health-controller


## health


**接口地址**:`/api/health`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema             |
| ------ | ---- | ------------------ |
| 200    | OK   | BaseResponseString |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     |          | integer(int32) | integer(int32) |
| data     |          | string         |                |
| message  |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": "",
	"message": ""
}
```


# thumb-controller


## doThumb


**接口地址**:`/api/thumb/do`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "blogId": 0
}
```


**请求参数**:


| 参数名称           | 参数说明       | 请求类型 | 是否必须 | 数据类型       | schema         |
| ------------------ | -------------- | -------- | -------- | -------------- | -------------- |
| doThumbRequest     | DoThumbRequest | body     | true     | DoThumbRequest | DoThumbRequest |
| &emsp;&emsp;blogId |                |          | false    | integer(int64) |                |


**响应状态**:


| 状态码 | 说明 | schema              |
| ------ | ---- | ------------------- |
| 200    | OK   | BaseResponseBoolean |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     |          | integer(int32) | integer(int32) |
| data     |          | boolean        |                |
| message  |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": true,
	"message": ""
}
```


## undoThumb


**接口地址**:`/api/thumb/undo`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "blogId": 0
}
```


**请求参数**:


| 参数名称           | 参数说明       | 请求类型 | 是否必须 | 数据类型       | schema         |
| ------------------ | -------------- | -------- | -------- | -------------- | -------------- |
| doThumbRequest     | DoThumbRequest | body     | true     | DoThumbRequest | DoThumbRequest |
| &emsp;&emsp;blogId |                |          | false    | integer(int64) |                |


**响应状态**:


| 状态码 | 说明 | schema              |
| ------ | ---- | ------------------- |
| 200    | OK   | BaseResponseBoolean |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     |          | integer(int32) | integer(int32) |
| data     |          | boolean        |                |
| message  |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": true,
	"message": ""
}
```


# user-controller


## getCurrentUserBlogs


**接口地址**:`/api/user/blogs`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型 | 是否必须 | 数据类型       | schema |
| -------- | -------- | -------- | -------- | -------------- | ------ |
| current  |          | query    | false    | integer(int64) |        |
| pageSize |          | query    | false    | integer(int64) |        |


**响应状态**:


| 状态码 | 说明 | schema               |
| ------ | ---- | -------------------- |
| 200    | OK   | BaseResponsePageBlog |


**响应参数**:


| 参数名称                           | 参数说明 | 类型           | schema         |
| ---------------------------------- | -------- | -------------- | -------------- |
| code                               |          | integer(int32) | integer(int32) |
| data                               |          | PageBlog       | PageBlog       |
| &emsp;&emsp;records                |          | array          | Blog           |
| &emsp;&emsp;&emsp;&emsp;id         |          | integer        |                |
| &emsp;&emsp;&emsp;&emsp;userid     |          | integer        |                |
| &emsp;&emsp;&emsp;&emsp;title      |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;coverImg   |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;content    |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;thumbCount |          | integer        |                |
| &emsp;&emsp;&emsp;&emsp;createTime |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;updateTime |          | string         |                |
| &emsp;&emsp;total                  |          | integer(int64) |                |
| &emsp;&emsp;size                   |          | integer(int64) |                |
| &emsp;&emsp;current                |          | integer(int64) |                |
| &emsp;&emsp;orders                 |          | array          | OrderItem      |
| &emsp;&emsp;&emsp;&emsp;column     |          | string         |                |
| &emsp;&emsp;&emsp;&emsp;asc        |          | boolean        |                |
| &emsp;&emsp;optimizeCountSql       |          | PageBlog       | PageBlog       |
| &emsp;&emsp;searchCount            |          | PageBlog       | PageBlog       |
| &emsp;&emsp;optimizeJoinOfCountSql |          | boolean        |                |
| &emsp;&emsp;maxLimit               |          | integer(int64) |                |
| &emsp;&emsp;countId                |          | string         |                |
| &emsp;&emsp;pages                  |          | integer(int64) |                |
| message                            |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": {
		"records": [
			{
				"id": 0,
				"userid": 0,
				"title": "",
				"coverImg": "",
				"content": "",
				"thumbCount": 0,
				"createTime": "",
				"updateTime": ""
			}
		],
		"total": 0,
		"size": 0,
		"current": 0,
		"orders": [
			{
				"column": "",
				"asc": true
			}
		],
		"optimizeCountSql": {},
		"searchCount": {},
		"optimizeJoinOfCountSql": true,
		"maxLimit": 0,
		"countId": "",
		"pages": 0
	},
	"message": ""
}
```


## getCurrentUser


**接口地址**:`/api/user/current`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema             |
| ------ | ---- | ------------------ |
| 200    | OK   | BaseResponseUserVO |


**响应参数**:


| 参数名称                        | 参数说明 | 类型           | schema         |
| ------------------------------- | -------- | -------------- | -------------- |
| code                            |          | integer(int32) | integer(int32) |
| data                            |          | UserVO         | UserVO         |
| &emsp;&emsp;id                  |          | integer(int64) |                |
| &emsp;&emsp;username            |          | string         |                |
| &emsp;&emsp;email               |          | string         |                |
| &emsp;&emsp;displayName         |          | string         |                |
| &emsp;&emsp;avatarUrl           |          | string         |                |
| &emsp;&emsp;bio                 |          | string         |                |
| &emsp;&emsp;status              |          | object         |                |
| &emsp;&emsp;emailVerified       |          | integer(int32) |                |
| &emsp;&emsp;role                |          | object         |                |
| &emsp;&emsp;locale              |          | string         |                |
| &emsp;&emsp;timezone            |          | string         |                |
| &emsp;&emsp;lastLoginIpLocation |          | string         |                |
| message                         |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": {
		"id": 0,
		"username": "",
		"email": "",
		"displayName": "",
		"avatarUrl": "",
		"bio": "",
		"status": {},
		"emailVerified": 0,
		"role": {},
		"locale": "",
		"timezone": "",
		"lastLoginIpLocation": ""
	},
	"message": ""
}
```


## userLogin


**接口地址**:`/api/user/login`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "username": "",
  "password": ""
}
```


**请求参数**:


| 参数名称             | 参数说明         | 请求类型 | 是否必须 | 数据类型         | schema           |
| -------------------- | ---------------- | -------- | -------- | ---------------- | ---------------- |
| userLoginRequest     | UserLoginRequest | body     | true     | UserLoginRequest | UserLoginRequest |
| &emsp;&emsp;username |                  |          | true     | string           |                  |
| &emsp;&emsp;password |                  |          | true     | string           |                  |


**响应状态**:


| 状态码 | 说明 | schema           |
| ------ | ---- | ---------------- |
| 200    | OK   | BaseResponseUser |


**响应参数**:


| 参数名称                        | 参数说明 | 类型              | schema         |
| ------------------------------- | -------- | ----------------- | -------------- |
| code                            |          | integer(int32)    | integer(int32) |
| data                            |          | User              | User           |
| &emsp;&emsp;id                  |          | integer(int64)    |                |
| &emsp;&emsp;username            |          | string            |                |
| &emsp;&emsp;password            |          | string            |                |
| &emsp;&emsp;email               |          | string            |                |
| &emsp;&emsp;displayName         |          | string            |                |
| &emsp;&emsp;avatarUrl           |          | string            |                |
| &emsp;&emsp;bio                 |          | string            |                |
| &emsp;&emsp;status              |          | object            |                |
| &emsp;&emsp;emailVerified       |          | integer(int32)    |                |
| &emsp;&emsp;role                |          | object            |                |
| &emsp;&emsp;locale              |          | string            |                |
| &emsp;&emsp;timezone            |          | string            |                |
| &emsp;&emsp;createTime          |          | string(date-time) |                |
| &emsp;&emsp;updateTime          |          | string(date-time) |                |
| &emsp;&emsp;lastLoginAt         |          | string(date-time) |                |
| &emsp;&emsp;socialProvider      |          | string            |                |
| &emsp;&emsp;socialId            |          | string            |                |
| &emsp;&emsp;metadata            |          | object            |                |
| &emsp;&emsp;lastLoginIp         |          | string            |                |
| &emsp;&emsp;lastLoginIpLocation |          | string            |                |
| message                         |          | string            |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": {
		"id": 0,
		"username": "",
		"password": "",
		"email": "",
		"displayName": "",
		"avatarUrl": "",
		"bio": "",
		"status": {},
		"emailVerified": 0,
		"role": {},
		"locale": "",
		"timezone": "",
		"createTime": "",
		"updateTime": "",
		"lastLoginAt": "",
		"socialProvider": "",
		"socialId": "",
		"metadata": {},
		"lastLoginIp": "",
		"lastLoginIpLocation": ""
	},
	"message": ""
}
```


## logout


**接口地址**:`/api/user/logout`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema             |
| ------ | ---- | ------------------ |
| 200    | OK   | BaseResponseString |


**响应参数**:


| 参数名称 | 参数说明 | 类型           | schema         |
| -------- | -------- | -------------- | -------------- |
| code     |          | integer(int32) | integer(int32) |
| data     |          | string         |                |
| message  |          | string         |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": "",
	"message": ""
}
```


## userRegister


**接口地址**:`/api/user/register`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "username": "",
  "password": "",
  "confirmPassword": ""
}
```


**请求参数**:


| 参数名称                    | 参数说明            | 请求类型 | 是否必须 | 数据类型            | schema              |
| --------------------------- | ------------------- | -------- | -------- | ------------------- | ------------------- |
| userRegisterRequest         | UserRegisterRequest | body     | true     | UserRegisterRequest | UserRegisterRequest |
| &emsp;&emsp;username        |                     |          | true     | string              |                     |
| &emsp;&emsp;password        |                     |          | true     | string              |                     |
| &emsp;&emsp;confirmPassword |                     |          | true     | string              |                     |


**响应状态**:


| 状态码 | 说明 | schema           |
| ------ | ---- | ---------------- |
| 200    | OK   | BaseResponseUser |


**响应参数**:


| 参数名称                        | 参数说明 | 类型              | schema         |
| ------------------------------- | -------- | ----------------- | -------------- |
| code                            |          | integer(int32)    | integer(int32) |
| data                            |          | User              | User           |
| &emsp;&emsp;id                  |          | integer(int64)    |                |
| &emsp;&emsp;username            |          | string            |                |
| &emsp;&emsp;password            |          | string            |                |
| &emsp;&emsp;email               |          | string            |                |
| &emsp;&emsp;displayName         |          | string            |                |
| &emsp;&emsp;avatarUrl           |          | string            |                |
| &emsp;&emsp;bio                 |          | string            |                |
| &emsp;&emsp;status              |          | object            |                |
| &emsp;&emsp;emailVerified       |          | integer(int32)    |                |
| &emsp;&emsp;role                |          | object            |                |
| &emsp;&emsp;locale              |          | string            |                |
| &emsp;&emsp;timezone            |          | string            |                |
| &emsp;&emsp;createTime          |          | string(date-time) |                |
| &emsp;&emsp;updateTime          |          | string(date-time) |                |
| &emsp;&emsp;lastLoginAt         |          | string(date-time) |                |
| &emsp;&emsp;socialProvider      |          | string            |                |
| &emsp;&emsp;socialId            |          | string            |                |
| &emsp;&emsp;metadata            |          | object            |                |
| &emsp;&emsp;lastLoginIp         |          | string            |                |
| &emsp;&emsp;lastLoginIpLocation |          | string            |                |
| message                         |          | string            |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": {
		"id": 0,
		"username": "",
		"password": "",
		"email": "",
		"displayName": "",
		"avatarUrl": "",
		"bio": "",
		"status": {},
		"emailVerified": 0,
		"role": {},
		"locale": "",
		"timezone": "",
		"createTime": "",
		"updateTime": "",
		"lastLoginAt": "",
		"socialProvider": "",
		"socialId": "",
		"metadata": {},
		"lastLoginIp": "",
		"lastLoginIpLocation": ""
	},
	"message": ""
}
```


## updateUserInfo


**接口地址**:`/api/user/update`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "displayName": "",
  "avatarUrl": "",
  "bio": "",
  "email": ""
}
```


**请求参数**:


| 参数名称                | 参数说明          | 请求类型 | 是否必须 | 数据类型          | schema            |
| ----------------------- | ----------------- | -------- | -------- | ----------------- | ----------------- |
| userUpdateRequest       | UserUpdateRequest | body     | true     | UserUpdateRequest | UserUpdateRequest |
| &emsp;&emsp;displayName |                   |          | false    | string            |                   |
| &emsp;&emsp;avatarUrl   |                   |          | false    | string            |                   |
| &emsp;&emsp;bio         |                   |          | false    | string            |                   |
| &emsp;&emsp;email       |                   |          | false    | string            |                   |


**响应状态**:


| 状态码 | 说明 | schema           |
| ------ | ---- | ---------------- |
| 200    | OK   | BaseResponseUser |


**响应参数**:


| 参数名称                        | 参数说明 | 类型              | schema         |
| ------------------------------- | -------- | ----------------- | -------------- |
| code                            |          | integer(int32)    | integer(int32) |
| data                            |          | User              | User           |
| &emsp;&emsp;id                  |          | integer(int64)    |                |
| &emsp;&emsp;username            |          | string            |                |
| &emsp;&emsp;password            |          | string            |                |
| &emsp;&emsp;email               |          | string            |                |
| &emsp;&emsp;displayName         |          | string            |                |
| &emsp;&emsp;avatarUrl           |          | string            |                |
| &emsp;&emsp;bio                 |          | string            |                |
| &emsp;&emsp;status              |          | object            |                |
| &emsp;&emsp;emailVerified       |          | integer(int32)    |                |
| &emsp;&emsp;role                |          | object            |                |
| &emsp;&emsp;locale              |          | string            |                |
| &emsp;&emsp;timezone            |          | string            |                |
| &emsp;&emsp;createTime          |          | string(date-time) |                |
| &emsp;&emsp;updateTime          |          | string(date-time) |                |
| &emsp;&emsp;lastLoginAt         |          | string(date-time) |                |
| &emsp;&emsp;socialProvider      |          | string            |                |
| &emsp;&emsp;socialId            |          | string            |                |
| &emsp;&emsp;metadata            |          | object            |                |
| &emsp;&emsp;lastLoginIp         |          | string            |                |
| &emsp;&emsp;lastLoginIpLocation |          | string            |                |
| message                         |          | string            |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": {
		"id": 0,
		"username": "",
		"password": "",
		"email": "",
		"displayName": "",
		"avatarUrl": "",
		"bio": "",
		"status": {},
		"emailVerified": 0,
		"role": {},
		"locale": "",
		"timezone": "",
		"createTime": "",
		"updateTime": "",
		"lastLoginAt": "",
		"socialProvider": "",
		"socialId": "",
		"metadata": {},
		"lastLoginIp": "",
		"lastLoginIpLocation": ""
	},
	"message": ""
}
```


## updatePassword


**接口地址**:`/api/user/update/password`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "oldPassword": "",
  "newPassword": "",
  "confirmPassword": ""
}
```


**请求参数**:


| 参数名称                    | 参数说明                  | 请求类型 | 是否必须 | 数据类型                  | schema                    |
| --------------------------- | ------------------------- | -------- | -------- | ------------------------- | ------------------------- |
| userUpdatePasswordRequest   | UserUpdatePasswordRequest | body     | true     | UserUpdatePasswordRequest | UserUpdatePasswordRequest |
| &emsp;&emsp;oldPassword     |                           |          | true     | string                    |                           |
| &emsp;&emsp;newPassword     |                           |          | true     | string                    |                           |
| &emsp;&emsp;confirmPassword |                           |          | true     | string                    |                           |


**响应状态**:


| 状态码 | 说明 | schema           |
| ------ | ---- | ---------------- |
| 200    | OK   | BaseResponseUser |


**响应参数**:


| 参数名称                        | 参数说明 | 类型              | schema         |
| ------------------------------- | -------- | ----------------- | -------------- |
| code                            |          | integer(int32)    | integer(int32) |
| data                            |          | User              | User           |
| &emsp;&emsp;id                  |          | integer(int64)    |                |
| &emsp;&emsp;username            |          | string            |                |
| &emsp;&emsp;password            |          | string            |                |
| &emsp;&emsp;email               |          | string            |                |
| &emsp;&emsp;displayName         |          | string            |                |
| &emsp;&emsp;avatarUrl           |          | string            |                |
| &emsp;&emsp;bio                 |          | string            |                |
| &emsp;&emsp;status              |          | object            |                |
| &emsp;&emsp;emailVerified       |          | integer(int32)    |                |
| &emsp;&emsp;role                |          | object            |                |
| &emsp;&emsp;locale              |          | string            |                |
| &emsp;&emsp;timezone            |          | string            |                |
| &emsp;&emsp;createTime          |          | string(date-time) |                |
| &emsp;&emsp;updateTime          |          | string(date-time) |                |
| &emsp;&emsp;lastLoginAt         |          | string(date-time) |                |
| &emsp;&emsp;socialProvider      |          | string            |                |
| &emsp;&emsp;socialId            |          | string            |                |
| &emsp;&emsp;metadata            |          | object            |                |
| &emsp;&emsp;lastLoginIp         |          | string            |                |
| &emsp;&emsp;lastLoginIpLocation |          | string            |                |
| message                         |          | string            |                |


**响应示例**:

```javascript
{
	"code": 0,
	"data": {
		"id": 0,
		"username": "",
		"password": "",
		"email": "",
		"displayName": "",
		"avatarUrl": "",
		"bio": "",
		"status": {},
		"emailVerified": 0,
		"role": {},
		"locale": "",
		"timezone": "",
		"createTime": "",
		"updateTime": "",
		"lastLoginAt": "",
		"socialProvider": "",
		"socialId": "",
		"metadata": {},
		"lastLoginIp": "",
		"lastLoginIpLocation": ""
	},
	"message": ""
}
```