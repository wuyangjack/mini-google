#!/bin/sh

# Define CIS455_USER first
# export CIS455_USER="cis455/ec2-user"
echo "User: "$CIS455_USER

BASE=/home/$CIS455_USER
ROOT=$BASE"/MiniGoogle/StorageQuery"
DATABASE=$BASE"/database"
TOMCAT=$BASE"/tomcat"

# Data sources
SOURCE_PAGERANK="$ROOT/sample/pagerank"
SOURCE_META="$ROOT/sample/indexer/meta"
SOURCE_BODY="$ROOT/sample/indexer/body"
SOURCE_TITLE="$ROOT/sample/indexer/title"
SPLIT="_split"

# Tables
# Should be consistent with cis455.project.StorageGlobal
TABLE_PAGERANK="pagerank"
TABLE_META="freqmeta"
TABLE_BODY="freqbody"
TABLE_TITLE="freqtitle"

# Split function
fileSplit() { 
	echo "split folder: "$1
   	PREFIX="trunk_"
	i=0
	SOURCE=$1$SPLIT
	echo "split write to: "$SOURCE
	rm -rf $SOURCE
	mkdir $SOURCE
	for file in $1/*; do
		i=$((i + 1))
		echo "split file: "$file
		cd $SOURCE
		split -l $2 $file $PREFIX$i
	done
}

case "$1" in
	Upload)
		cd $ROOT
		ant clean
		ant
		sh $TOMCAT/bin/shutdown.sh
		sh $TOMCAT/bin/startup.sh
		WAR="storage.war"
		rm -rf $TOMCAT/webapps/$WAR
		cp $ROOT/$WAR $TOMCAT/webapps/
		;;
	DBInit)
		rm -rf $DATABASE
		mkdir $DATABASE
		;;
	DBSplit)
		fileSplit $SOURCE_PAGERANK 50000
		fileSplit $SOURCE_META 50000
		fileSplit $SOURCE_BODY 50000
		fileSplit $SOURCE_TITLE 50000
		;;
	DBDump)
		cd $ROOT
		ant clean
		ant
		CLASSPATH="$ROOT/storage.jar:$ROOT/lib/*"
		APP="cis455.project.storage.StorageDumper"
		NODES=$2
		INDEX=$3
		# Dump
		java -cp $CLASSPATH $APP $SOURCE_PAGERANK $DATABASE $NODES $INDEX $TABLE_PAGERANK
		java -cp $CLASSPATH $APP $SOURCE_META $DATABASE $NODES $INDEX $TABLE_META
		java -cp $CLASSPATH $APP $SOURCE_TITLE $DATABASE $NODES $INDEX $TABLE_TITLE
		#java -cp $CLASSPATH $APP $SOURCE_BODY $DATABASE $NODES $INDEX $TABLE_BODY
		;;
	*)
		echo "Unknown mode"
		;;
esac