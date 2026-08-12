#!/usr/bin/env bash
# ============================================================
# J-Mall 微服务启动脚本（本地 java -jar 方式）
# 用法：
#   ./scripts/start-services.sh min   # gateway + auth + user（最小联调）
#   ./scripts/start-services.sh all   # 全部 14 个服务
#   ./scripts/start-services.sh stop # 停止所有
# 依赖：已 mvn clean install -DskipTests 生成 target/*.jar
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
LOG_DIR="${PROJECT_DIR}/logs"
mkdir -p "${LOG_DIR}"

# 加载 .env
if [[ -f "${PROJECT_DIR}/.env" ]]; then
  set -a; source "${PROJECT_DIR}/.env"; set +a
fi

export JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-17-openjdk-amd64}"
export JAVA_OPTS="${JAVA_OPTS:--Xms128m -Xmx256m}"

MODE="${1:-min}"

stop_all() {
  echo "==> 停止所有 jmall 服务..."
  pkill -f 'jmall-.*-1.0.0.jar' 2>/dev/null || true
  sleep 2
  echo "    已停止"
  exit 0
}

[[ "${MODE}" == "stop" ]] && stop_all

# 服务列表：name => port
declare -A SERVICES=(
  [jmall-gateway]=8080
  [jmall-auth]=8101
  [jmall-user]=8102
  [jmall-product]=8103
  [jmall-search]=8104
  [jmall-cart]=8105
  [jmall-order]=8106
  [jmall-pay]=8107
  [jmall-stock]=8108
  [jmall-coupon]=8109
  [jmall-merchant]=8110
  [jmall-admin]=8111
  [jmall-message]=8112
  [jmall-logistics]=8113
)

# 启动顺序
if [[ "${MODE}" == "min" ]]; then
  ORDER=(jmall-gateway jmall-auth jmall-user)
elif [[ "${MODE}" == "all" ]]; then
  ORDER=(jmall-gateway jmall-auth jmall-user jmall-product jmall-stock jmall-order jmall-pay jmall-cart jmall-coupon jmall-merchant jmall-admin jmall-message jmall-logistics jmall-search)
else
  echo "用法: $0 {min|all|stop}"
  exit 1
fi

echo "==> 启动模式: ${MODE}  JAVA_OPTS=${JAVA_OPTS}"
echo "==> 日志目录: ${LOG_DIR}"

for svc in "${ORDER[@]}"; do
  jar="${PROJECT_DIR}/${svc}/target/${svc}-1.0.0.jar"
  if [[ ! -f "${jar}" ]]; then
    echo "    [跳过] ${svc}: 未找到 ${jar}（请先 mvn clean install -DskipTests）"
    continue
  fi
  port=${SERVICES[$svc]}
  echo "    [启动] ${svc}  端口 ${port}  日志 logs/${svc}.log"
  nohup java ${JAVA_OPTS} -jar "${jar}" \
    --MYSQL_HOST=${MYSQL_HOST:-127.0.0.1} \
    --MYSQL_PORT=${MYSQL_PORT:-3306} \
    --MYSQL_USER=${MYSQL_USER:-root} \
    --MYSQL_PASSWORD=${MYSQL_PASSWORD:-root} \
    --REDIS_HOST=${REDIS_HOST:-127.0.0.1} \
    --REDIS_PORT=${REDIS_PORT:-6379} \
    --REDIS_PASSWORD=${REDIS_PASSWORD:-} \
    --NACOS_ADDR=${NACOS_ADDR:-127.0.0.1:8848} \
    --NACOS_USERNAME=${NACOS_USERNAME:-nacos} \
    --NACOS_PASSWORD=${NACOS_PASSWORD:-nacos} \
    > "${LOG_DIR}/${svc}.log" 2>&1 &
  sleep 3
done

echo ""
echo "==> 已启动服务，查看日志：tail -f logs/${ORDER[0]}.log"
echo "==> 网关地址: http://127.0.0.1:8080"
