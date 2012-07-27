#! /bin/sh


start(){
		RETVAL=0
		pslist=$(pgrep -u jboss)
		if [ ! -z "$pslist" ]; then
			echo "jboss already running, not starting."
			RETVAL=1
		else
		    echo "Starting jboss.."

		    # If using an SELinux system such as RHEL 4, use the command below
		    # instead of the "su":
		    # eval "runuser - jboss -c '/opt/jboss/current/bin/run.sh > /dev/null 2> /dev/null &'
		    # if the 'su -l ...' command fails (the -l flag is not recognized by my su cmd) try:
		    #   sudo -u jboss /opt/jboss/bin/run.sh > /dev/null 2> /dev/null &
		    su -l jboss -c '/opt/jboss/bin/run.sh -c default -b 0.0.0.0 > /dev/null 2> /dev/null &'
        fi
        return $RETVAL
}

stop(){
        echo "Stopping jboss.."

        # If using an SELinux system such as RHEL 4, use the command below
        # instead of the "su":
        # eval "runuser - jboss -c '/opt/jboss/current/bin/shutdown.sh -S &'
        # if the 'su -l ...' command fails try:
        #   sudo -u jboss /opt/jboss/bin/shutdown.sh -S &
        su -l jboss -c '/opt/jboss/bin/shutdown.sh -s 0.0.0.0 -S &'

	# Sleep every 10 seconds for up to 180 seconds, and check to see if
	# jboss is still running
	sleep=10
	RETVAL=1
	while [ $sleep -lt 180 -a $RETVAL -eq 1 ]; do
		sleep 5
		sleep=`expr $sleep + 5`
		pslist=$(pgrep -u jboss)
		if [ -z "$pslist" ]; then
			echo "Stopped successfully."
			RETVAL=0
		fi
	done
	return $RETVAL
}

restart(){
        stop
	if [ $? = 0 ]; then
		start
	else
		echo "Could not stop jboss server, check manually"
	fi
}




case "$1" in
  start)
        start
        ;;
  stop)
        stop
        ;;
  restart)
        restart
        ;;
  *)
        echo "Usage: jboss {start|stop|restart}"
        exit 1
esac

exit 0

