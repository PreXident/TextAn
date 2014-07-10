#!/bin/bash
#Use #!/bin/sh in a OS different from Ubuntu. Old "sh" is not exist in Ubuntu.

#Start script for Textan server in Unix style (starts process in background).
#It can be used as Unix daemon.

#The program name
TEXTAN_SERVER_NAME="TextAn server"
#The PID file
TEXTAN_SERVER_PID="/tmp/textan_server.pid"
#The path to a directory with TextAn
TEXTAN_SERVER_PATH="."
#The relative path to the TextAn jar in a directory with TextAn (should not be changed)
TEXTAN_SERVER_JAR="TextAn-server.jar"

if [ "$#" -ge 1 ];then
	COMMAND="$1"
	shift
	PARAMS="$*"
fi

function checkPidFile (){
	if [ -f $TEXTAN_SERVER_PID ]; then
		read _PID < $TEXTAN_SERVER_PID
		if [ -d "/proc/$_PID" ]; then
			#pid exists and process running
			return 0
		else
			#pid file exists but the process is not running
			return 1
		fi
	else
		return 2
	fi		
}

function run() {
	rm -f stdout stderr
	nohup java -jar $TEXTAN_SERVER_JAR $PARAMS 1>stdout 2>stderr &
	echo $! > $TEXTAN_SERVER_PID
	echo "$TEXTAN_SERVER_NAME started."
}

function start(){
	echo "Starting $TEXTAN_SERVER_NAME..."
	
	checkPidFile
	RETVAL="$?"

	case $RETVAL in
	1)
		rm $TEXTAN_SERVER_PID
		echo "WARN: $TEXTAN_SERVER_NAME is not running, but PID file existed."
		run
		exit 0
		;;	
	2)
		run
		exit 0
		;;
	0)
		echo "$TEXTAN_SERVER_NAME is already running."
		exit 1
		;;
	esac
}

function stop (){
	checkPidFile
	RETVAL="$?"
	PID=$_PID

	case $RETVAL in
	0)
		read PID < $TEXTAN_SERVER_PID
		echo "$TEXTAN_SERVER_NAME stopping..."
		kill $PID
		echo "$TEXTAN_SERVER_NAME stopped."
		rm $TEXTAN_SERVER_PID
		exit 0
		;;
	1)
		rm $TEXTAN_SERVER_PID
		echo "WARN: $TEXTAN_SERVER_NAME is not running, but PID file existed."
		exit 2
		;;
	2)
		echo "$TEXTAN_SERVER_NAME is not running."
		exit 1
		;;
	esac
}

function status () {
	checkPidFile
	RETVAL="$?"
	case $RETVAL in
	0)
		echo "$TEXTAN_SERVER_NAME (PID $_PID) is running."
		exit 0
		;;
	1)
		echo "WARN: $TEXTAN_SERVER_NAME is not running, but pid file exist!"
		exit 2
		;;
	2)
		echo "$TEXTAN_SERVER_NAME is not running."
		exit 1
		;;
	esac		
}

cd $TEXTAN_SERVER_PATH

case $COMMAND in
start)
	start
	;;
stop)
	stop
	;;
restart)
	stop
	start
	;;
status)
	status 
	;;
*)
	echo "Usage: $0 {start|stop|restart|status}"
	exit 1
	;;
esac

