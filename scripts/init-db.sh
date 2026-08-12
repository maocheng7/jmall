#!/usr/bin/env bash
# ============================================================
# J-Mall 数据库初始化：建库 + 建表（幂等可重复执行）
# 依赖：mysql 客户端（本机或容器内均可）
# 用法：
#   ./scripts/init-db.sh                     # 默认 127.0.0.1:3306 root/root
#   MYSQL_HOST=db MYSQL_PASSWORD=xxx ./scripts/init-db.sh
#   docker exec -i jmall-mysql bash /docker-entrypoint-initdb.d/../scripts/init-db.sh
# ============================================================
set -euo pipefail

MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-root}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_DIR="$(cd "${SCRIPT_DIR}/../sql" && pwd)"

MYSQL_CMD="mysql -h${MYSQL_HOST} -P${MYSQL_PORT} -u${MYSQL_USER} -p${MYSQL_PASSWORD} --default-character-set=utf8mb4"

echo "==> J-Mall 数据库初始化"
echo "    MySQL: ${MYSQL_HOST}:${MYSQL_PORT}  用户: ${MYSQL_USER}"
echo "    SQL 目录: ${SQL_DIR}"

# 1) 建库
echo "==> [1/2] 创建数据库 ..."
${MYSQL_CMD} < "${SQL_DIR}/00_database.sql"
echo "    11 个库创建完成"

# 2) 按序号建表
echo "==> [2/2] 导入建表脚本 ..."
for sql_file in "${SQL_DIR}"/[0-9][0-9]_*.sql; do
  fname="$(basename "${sql_file}")"
  db="jmall_$(echo "${fname}" | sed -E 's/^([0-9]+)_jmall_(.*)\.sql$/\2/')"
  echo "    -> ${fname}  =>  库 ${db}"
  ${MYSQL_CMD} "${db}" < "${sql_file}"
done

echo "==> 完成！数据库就绪。"
echo "    可用库："
${MYSQL_CMD} -e "SHOW DATABASES LIKE 'jmall_%';"
