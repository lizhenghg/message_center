#!/bin/bash
ulimit -SHn 65535
pid=`ps -ef|grep mc_scheduler|grep -v grep|awk '{print $2}' `
if [ -z "${pid}" ]; then
    echo 'Starting the server of mc_center\n'
	nohup java -ea -XX:MetaspaceSize=512m -Xms3072M -Xmx3072M -Xss1024k -XX:+UseG1GC -jar mc_scheduler_dev_1.1.0.jar > bootdolog.log   2>&1 &
else
	echo "mc_center is running pid=$pid, now restart mc_center\n"
	kill -15 $pid
	sleep 3s
	nohup java -ea -XX:MetaspaceSize=512m -Xms3072M -Xmx3072M -Xss1024k -XX:+UseG1GC -jar mc_scheduler_dev_1.1.0.jar > bootdolog.log   2>&1 &
fi
