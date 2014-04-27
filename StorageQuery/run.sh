#!/bin/sh

ROOT="/home/cis455/MiniGoogle/StorageQuery"

case "$1" in
	Upload)
		cd $ROOT
		ant clean
		ant
		cp $ROOT/storage.war /home/cis455/tomcat/webapps/
		;;
	*)
		echo "Unknown mode"
		;;
esac