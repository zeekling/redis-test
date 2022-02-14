#!/usr/bin/env bash

source ./exec_redis.sh

exec "127.0.0.1" "6379" commands/zset.txt

