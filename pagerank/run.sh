#!/bin/sh

ROOT="/home/cloudera/MiniGoogle/pagerank"
CLASS=$ROOT"/class"
LOCALINPUT=$ROOT"/input"
LOCALOUTPUT=$ROOT"/output"
BASEDIR="/user/cloudera"
CLASSPATH="/usr/lib/hadoop/*:/usr/lib/hadoop/client-0.20/*:$ROOT/lib/*.jar"
JAR="project-pagerank.jar"

case "$1" in
	PageRank)
		cd $ROOT
		APP="PageRank"
		DIR=$BASEDIR/$APP
		INPUT=$DIR/input
		OUTPUT=$DIR/output
		ITERATION=3
		echo "Prepare hadoop fs:"
		hadoop fs -rm -r $DIR
		hadoop fs -mkdir $DIR $INPUT
		echo "Compile classes:"
		ant clean
		ant
		echo "Prepare input:"
		hadoop fs -put $LOCALINPUT/$APP/* $INPUT
		hadoop fs -ls $INPUT
		echo "Run job:"
		hadoop jar $JAR cis455.project.$APP"Driver" $INPUT $OUTPUT $ITERATION
		echo "Fetch output:"
		hadoop fs -ls $OUTPUT
		hadoop fs -cat $OUTPUT/part-r-00000 &> $LOCALOUTPUT/$APP.out
		;;
	Pack)
		ant pack
		;;
	*)
		;;
esac
