#!/bin/bash
SERVER_BIN=`dirname "$0"`
SERVER_HOME=`cd "$SERVER_BIN/.." >/dev/null; pwd`
echo "home:${SERVER_HOME}"
SERVER_PID_DIR="${SERVER_HOME}/run"
SERVER_LOG_DIR="${SERVER_HOME}/logs"

SERVER_CONF_DIR="file:${SERVER_HOME}/config"

SERVER_CONF_FILE="${SERVER_CONF_DIR}/config.properties"
SERVER_LIBS="${SERVER_HOME}/lib/*"
STDOUT_FILE=${SERVER_HOME}/logs/server-start.log
#SERVER_LIBS=`find ${SERVER_HOME}/lib -name *.jar | xargs | sed "s/ /:/g"`


DEPLOY_DIR="common_server_1.0"

# make log dir
if [ ! -d "${SERVER_LOG_DIR}" ]; then
   mkdir "${SERVER_LOG_DIR}"
fi

check_is_running() {
    PIDS=`ps -f | grep java | grep "${DEPLOY_DIR}" |awk '{print $2}'`
    if [ -n "$PIDS" ]; then
        echo "ERROR: The ${DEPLOY_DIR} already started!"
        echo "PID: $PIDS"
        exit 1
    fi
}

run() {
    SERVER_CLASSPATH="${SERVER_CONF_DIR}:${SERVER_LIBS}:${SERVER_HOME}/lib/CommonPlatformServer-1.0.jar}"
    SERVER_CONF_PARAMS="-Dconf_dir=${SERVER_CONF_DIR}"
    echo $SERVER_CLASSPATH
    main_method='com.yonyou.datafin.netty.server.SpringApplication'
    run_server_cmd="java -cp '${SERVER_CLASSPATH}' ${SERVER_CONF_PARAMS} ${main_method}"
    #$run_server_cmd
    nohup $run_server_cmd > $STDOUT_FILE 2>&1 &
}

running_success() {
    COUNT=0
    while [ $COUNT -lt 1 ]; do
        echo -e ".\c"
        sleep 1
        COUNT=`ps -f | grep java | grep "${DEPLOY_DIR}" | awk '{print $2}' | wc -l`
        if [ $COUNT -gt 0 ]; then
            break
        fi
    done

    echo "OK!"
    PIDS=`ps -f | grep java | grep "${DEPLOY_DIR}" | awk '{print $2}'`
    echo "PID: $PIDS"
    echo "STDOUT: $STDOUT_FILE"


}

check_is_running
run
running_success

