## start jar with port and classpath
# start.sh
nohup java -classpath ./smt-data-sync_lib/*.jar -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=18891  -jar smt-data-sync.jar > ./nohup.log 2>&1 &

## stop java process
# stop.sh
kill -9 $(ps -ef|grep smt-data-sync.jar|gawk '$0 !~/grep/ {print $2}' |tr -s '\n' ' ')
 
# restart java process if you need
pid=`ps -ef|grep smt-data-sync.jar|gawk '$0 !~/grep/ {print $2}' |tr -s '\n' ' '`
if [ -n "$pid" ]; then
    kill -9 $pid
fi
nohup java -jar smt-data-sync.jar > ./nohup.log 2>&1 &
