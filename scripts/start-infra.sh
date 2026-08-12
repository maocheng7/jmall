#!/usr/bin/env bash
# ============================================================
# J-Mall 本地开发环境一键启动（Docker 版）
# 用法：
#   ./scripts/start-infra.sh              # 仅核心（MySQL/Redis/Nacos）
#   ./scripts/start-infra.sh full         # 全量（含 MQ/ES/MinIO）
#   ./scripts/start-infra.sh down         # 停止并清理
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
PROFILE="${1:-core}"

cd "${PROJECT_DIR}"

# 确保 .env 存在
if [[ ! -f .env ]]; then
  echo "==> 未检测到 .env，从 .env.example 复制"
  cp .env.example .env
fi

case "${PROFILE}" in
  core)
    echo "==> 启动核心中间件（MySQL / Redis / Nacos）..."
    docker compose --profile core up -d
    echo "==> 等待 MySQL 就绪..."
    for i in $(seq 1 30); do
      if docker exec jmall-mysql mysqladmin ping -h 127.0.0.1 -proot &>/dev/null; then
        echo "    MySQL 已就绪"
        break
      fi
      sleep 2
    done
    echo "==> 导入数据库..."
    bash scripts/init-db.sh
    ;;
  full)
    echo "==> 启动全量中间件（MySQL / Redis / Nacos / RocketMQ / ES / MinIO）..."
    docker compose --profile full up -d
    ;;
  down)
    echo "==> 停止并清理容器与数据卷..."
    docker compose down -v
    exit 0
    ;;
  *)
    echo "用法: $0 {core|full|down}"
    exit 1
    ;;
esac

echo ""
echo "==> 中间件状态："
docker compose ps
echo ""
echo "==> 服务地址："
echo "    MySQL:    ${MYSQL_HOST:-127.0.0.1}:${MYSQL_PORT:-3306}"
echo "    Redis:    ${REDIS_HOST:-127.0.0.1}:${REDIS_PORT:-6379}"
echo "    Nacos:    http://${NACOS_ADDR:-127.0.0.1:8848}/nacos  (nacos/nacos)"
echo ""
echo "下一步：启动微服务"
echo "    ./scripts/start-services.sh min   # gateway + auth + user"
echo "    ./scripts/start-services.sh all  # 全部"
