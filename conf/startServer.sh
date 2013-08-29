echo  $0
dird=`dirname $0`
cd $dird
CLASSPATH=$CLASSPATH
for x in ../lib/*.*
 do
  if [ "$x" != "lib/*.*" ] ; then
    if [ -z "$CLASSPATH" ] ; then
      CLASSPATH=$x
    else
      CLASSPATH="$CLASSPATH":$x
    fi
  fi
done
echo
echo "Using JAVA_HOME:       $JAVA_HOME"
echo "Using REPORT_HOME:     $REPORT_HOME"
echo "Using CLASSPATH:       $CLASSPATH"
echo
nohup "$JAVA_HOME"/bin/java  -Duser.home=. -classpath "$CLASSPATH" com.topinfo.AutoServer -Xms300m -Xmx 500m   &
