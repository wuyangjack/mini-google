#!/bin/sh

ROOT="/home/cis455/MiniGoogle/StorageQuery"
DATABASE="/home/cis455/database"

case "$1" in
	Upload)
		cd $ROOT
		ant clean
		ant
		cp $ROOT/storage.war /home/cis455/tomcat/webapps/
		;;
	DBInit)
		rm -rf $DATABASE
		mkdir $DATABASE
		;;
	DBDump)
		cd $ROOT
		ant clean
		ant
		CLASSPATH="$ROOT/storage.jar:$ROOT/lib/*"
		APP="cis455.project.storage.StorageDumper"
		SOURCE="$ROOT/sample/pagerank"
		NODES=2
		INDEX=1
		TABLE="pagerank"
		java -cp $CLASSPATH $APP $SOURCE $DATABASE $NODES $INDEX $TABLE
		;;
	*)
		echo "Unknown mode"
		;;
esac