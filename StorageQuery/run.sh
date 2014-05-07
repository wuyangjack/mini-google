#!/bin/sh

# Define CIS455_USER, CIS455_NODES, CIS455_INDEX first
# e.g. export CIS455_USER="cis455/ec2-user"

BASE=/home/$CIS455_USER
PROJ=$BASE"/MiniGoogle"
ROOT=$PROJ"/StorageQuery"
UI=$PROJ"/UI"
SOURCE=$BASE"/data"
DATABASE=$BASE"/database"
TOMCAT=$BASE"/tomcat"

# Data sources
SOURCE_PAGERANK="$SOURCE/pagerank"
SOURCE_FREQMETA="$SOURCE/indexer/meta"
SOURCE_FREQBODY="$SOURCE/indexer/body"
SOURCE_FREQTITLE="$SOURCE/indexer/title"
SOURCE_MODMETA="$SOURCE/indexer/mod-meta"
SOURCE_MODBODY="$SOURCE/indexer/mod-body"
SOURCE_MODTITLE="$SOURCE/indexer/mod-title"
SOURCE_TITLE="$SOURCE/title"
SPLIT="_split"
CLASSPATH="$ROOT/storage.jar:$ROOT/lib/*"
DUMPER="cis455.project.storage.DumperDistributed"

# Tables
# Should be consistent with cis455.project.StorageGlobal
TABLE_PAGERANK="pagerank"
TABLE_FREQMETA="freqmeta"
TABLE_FREQBODY="freqbody"
TABLE_FREQTITLE="freqtitle"
TABLE_MODMETA="modmeta"
TABLE_MODBODY="modbody"
TABLE_MODTITLE="modtitle"
TABLE_TITLE="title"

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
	Commit)
		# Add master servlet
		cd $ROOT
		ant clean
		ant
		git add master.war
		git add worker.war
		git add storage.jar
		git add run.sh
		# Add ui servlet
		cd $UI
		ant clean
		ant
		git add ui.war
		# Add 
		git commit -m "Compile & deploy servlets."
		git push
		;;
	UnloadMaster)
		WAR="master"
		rm -rf $TOMCAT/webapps/$WAR
		rm -rf $TOMCAT/webapps/$WAR.war
		;;
	UnloadWorker)
		WAR="worker"
		rm -rf $TOMCAT/webapps/$WAR
		rm -rf $TOMCAT/webapps/$WAR.war
		;;
	UploadMaster)
		WAR="master"
		LOG=/tmp/QueryMaster.log
		cd $ROOT
		git pull
		rm -rf $LOG
		sh $TOMCAT/bin/shutdown.sh
		fuser -k 8080/tcp
		sh $TOMCAT/bin/startup.sh
		rm -rf $TOMCAT/webapps/$WAR
		rm -rf $TOMCAT/webapps/$WAR.war
		cp $ROOT/$WAR.war $TOMCAT/webapps/
		sleep 3
		tail -f $LOG
		;;
	UploadWorker)
		WAR="worker"
		LOG=/tmp/QueryWorker.log
		cd $ROOT
		git pull
		rm -rf $LOG
		sh $TOMCAT/bin/shutdown.sh
		fuser -k 8080/tcp
		sh $TOMCAT/bin/startup.sh
		rm -rf $TOMCAT/webapps/$WAR
		rm -rf $TOMCAT/webapps/$WAR.war
		cp $ROOT/$WAR.war $TOMCAT/webapps/
		sleep 3
		tail -f $LOG
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
	DumpFreqTitle)
		cd $ROOT
		THREADS=5
		KEY=1
		java -cp $CLASSPATH $DUMPER $SOURCE_FREQTITLE $DATABASE $CIS455_NODES $CIS455_INDEX $TABLE_FREQTITLE $THREADS $KEY
		;;
	DumpFreqMeta)
		cd $ROOT
		THREADS=5
		KEY=1
		java -cp $CLASSPATH $DUMPER $SOURCE_FREQMETA $DATABASE $CIS455_NODES $CIS455_INDEX $TABLE_FREQMETA $THREADS $KEY
		;;
	DumpFreqBody)
		cd $ROOT
		THREADS=5
		KEY=1
		java -cp $CLASSPATH $DUMPER $SOURCE_FREQBODY $DATABASE $CIS455_NODES $CIS455_INDEX $TABLE_FREQBODY $THREADS $KEY
		;;
	DumpModTitle)
		cd $ROOT
		THREADS=5
		KEY=0
		java -cp $CLASSPATH $DUMPER $SOURCE_MODTITLE $DATABASE $CIS455_NODES $CIS455_INDEX $TABLE_MODTITLE $THREADS $KEY
		;;
	DumpModMeta)
		cd $ROOT
		THREADS=5
		KEY=0
		java -cp $CLASSPATH $DUMPER $SOURCE_MODMETA $DATABASE $CIS455_NODES $CIS455_INDEX $TABLE_MODMETA $THREADS $KEY
		;;
	DumpModBody)
		cd $ROOT
		THREADS=5
		KEY=0
		java -cp $CLASSPATH $DUMPER $SOURCE_MODBODY $DATABASE $CIS455_NODES $CIS455_INDEX $TABLE_MODBODY $THREADS $KEY
		;;
	DumpTitle)
		cd $ROOT
		THREADS=1
		KEY=0
		java -cp $CLASSPATH $DUMPER $SOURCE_TITLE $DATABASE 1 0 $TABLE_TITLE $THREADS $KEY
		;;
	*)
		echo "Unknown mode"
		;;
esac