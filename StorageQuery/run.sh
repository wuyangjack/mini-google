#!/bin/sh

# Define CIS455_USER, CIS455_NODES, CIS455_INDEX first
# e.g. export CIS455_USER="cis455/ec2-user"

BASE=/home/$CIS455_USER
ROOT=$BASE"/MiniGoogle/StorageQuery"
SOURCE=$BASE"/data"
DATABASE=$BASE"/database"
TOMCAT=$BASE"/tomcat"

# Data sources
SOURCE_PAGERANK="$SOURCE/pagerank"
SOURCE_META="$SOURCE/indexer/meta"
SOURCE_BODY="$SOURCE/indexer/body"
SOURCE_TITLE="$SOURCE/indexer/title"
SOURCE_MODMETA="$SOURCE/indexer/mod-meta"
SOURCE_MODBODY="$SOURCE/indexer/mod-body"
SOURCE_MODTITLE="$SOURCE/indexer/mod-title"
SPLIT="_split"
CLASSPATH="$ROOT/storage.jar:$ROOT/lib/*"
DUMPER="cis455.project.storage.StorageDumper"

# Tables
# Should be consistent with cis455.project.StorageGlobal
TABLE_PAGERANK="pagerank"
TABLE_META="freqmeta"
TABLE_BODY="freqbody"
TABLE_TITLE="freqtitle"
TABLE_MODMETA="modmeta"
TABLE_MODBODY="modbody"
TABLE_MODTITLE="modtitle"

WAR="storage.war"

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

echoConfig() {
	echo "User: "$CIS455_USER
	echo "Node count: "$CIS455_NODES
	echo "Node index: "$CIS455_INDEX
}

echoConfig

case "$1" in
	Unload)
		rm -rf $TOMCAT/webapps/$WAR
		;;
	Upload)
		cd $ROOT
		ant clean
		ant
		rm -rf /tmp/Query*.log
		sh $TOMCAT/bin/shutdown.sh
		sh $TOMCAT/bin/startup.sh
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
	Build)
		cd $ROOT
		ant clean
		ant
		;;
	DumpPageRank)
		cd $ROOT
		THREADS=5
		KEY=0
		java -cp $CLASSPATH $DUMPER $SOURCE_PAGERANK $DATABASE $CIS455_NODES $CIS455_INDEX $TABLE_PAGERANK $THREADS $KEY
		;;
	DumpTitle)
		cd $ROOT
		THREADS=5
		KEY=1
		java -cp $CLASSPATH $DUMPER $SOURCE_TITLE $DATABASE $CIS455_NODES $CIS455_INDEX $TABLE_TITLE $THREADS $KEY
		;;
	DumpMeta)
		cd $ROOT
		THREADS=5
		KEY=1
		java -cp $CLASSPATH $DUMPER $SOURCE_META $DATABASE $CIS455_NODES $CIS455_INDEX $TABLE_META $THREADS $KEY
		;;
	DumpBody)
		cd $ROOT
		THREADS=5
		KEY=1
		java -cp $CLASSPATH $DUMPER $SOURCE_BODY $DATABASE $CIS455_NODES $CIS455_INDEX $TABLE_META $THREADS $KEY
		;;
	DumpModTitle)
		cd $ROOT
		THREADS=5
		KEY=1
		java -cp $CLASSPATH $DUMPER $SOURCE_MODTITLE $DATABASE $CIS455_NODES $CIS455_INDEX $TABLE_MODTITLE $THREADS $KEY
		;;
	DumpModMeta)
		cd $ROOT
		THREADS=5
		KEY=1
		java -cp $CLASSPATH $DUMPER $SOURCE_MODMETA $DATABASE $CIS455_NODES $CIS455_INDEX $TABLE_MODMETA $THREADS $KEY
		;;
	DumpModBody)
		cd $ROOT
		THREADS=5
		KEY=1
		java -cp $CLASSPATH $APP $SOURCE_MODBODY $DATABASE $CIS455_NODES $CIS455_INDEX $TABLE_MODMETA $THREADS $KEY
		;;
	*)
		echo "Unknown mode"
		;;
esac