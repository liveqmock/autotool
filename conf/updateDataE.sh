#!/bin/sh -e 
#kill dataenode pid 
ps -ef|grep DataENodeServer|grep -v grep|awk  '{print "kill -9 " $2}' |sh 
path=${1}; 
zipname=${2}; 
cd ${1}/ 
rm -rf ${1}/${2}/lib/dataE*.jar 
echo $zipname echo "unzip ${1}/${2}-all.zip"
unzip -o ${1}/${2}-all.zip; 
#cp ${1}/${2}/lib/${3}.jar  /${1}/${2}/lib/ 
sqlPath=${1}/${2}/AppSql.sql;
mysql -u root -h 127.0.0.1 -proot << EOF
source $sqlPath; 
EOF
/${1}/${2}/conf/startServer.sh;
