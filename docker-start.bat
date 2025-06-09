@echo off
chcp 65001 > nul

REM GigaLike Docker Compose 管理脚本 (Windows)

if "%1"=="start" goto start
if "%1"=="stop" goto stop
if "%1"=="restart" goto restart
if "%1"=="logs" goto logs
if "%1"=="build" goto build
if "%1"=="clean" goto clean
if "%1"=="status" goto status
goto help

:start
echo 启动 GigaLike 应用栈...
docker-compose up -d
echo 应用栈启动完成！
echo 访问地址：
echo   - 应用: http://localhost:8080
echo   - MySQL: localhost:3306
echo   - Redis: localhost:6379
echo   - Pulsar Admin: http://localhost:8080
echo   - Pulsar Broker: pulsar://localhost:6650
goto end

:stop
echo 停止 GigaLike 应用栈...
docker-compose down
echo 应用栈已停止！
goto end

:restart
echo 重启 GigaLike 应用栈...
docker-compose restart
echo 应用栈重启完成！
goto end

:logs
if "%2"=="" (
    echo 显示所有服务日志...
    docker-compose logs -f
) else (
    echo 显示 %2 服务日志...
    docker-compose logs -f %2
)
goto end

:build
echo 重新构建应用镜像...
docker-compose build --no-cache gigalike-app
echo 构建完成！
goto end

:clean
echo 清理 Docker 资源...
echo ⚠️  这将删除所有容器、网络和数据卷！
set /p confirm="确认继续？(y/N): "
if /i "%confirm%"=="y" (
    docker-compose down -v --rmi all
    echo 清理完成！
) else (
    echo 取消清理。
)
goto end

:status
echo GigaLike 应用栈状态：
docker-compose ps
goto end

:help
echo GigaLike Docker 管理脚本
echo 用法: %0 {start^|stop^|restart^|logs^|build^|clean^|status}
echo.
echo 命令说明：
echo   start   - 启动所有服务
echo   stop    - 停止所有服务
echo   restart - 重启所有服务
echo   logs    - 查看日志 (可指定服务名)
echo   build   - 重新构建应用镜像
echo   clean   - 清理所有 Docker 资源
echo   status  - 查看服务状态
echo.
echo 示例：
echo   %0 start              # 启动所有服务
echo   %0 logs gigalike-app  # 查看应用日志
echo   %0 logs mysql         # 查看 MySQL 日志

:end 