#!/bin/sh

ROOT="/home/cis455/MiniGoogle/EMR"
CLI=$ROOT"/bin"
BUCKET="s3://cis555-project-test"
USER="wuyangjack1991@gmail.com"

case "$1" in
	JobSample)
		cd $CLI
		./elastic-mapreduce --create --name "Test custom JAR" \
		  	--jar s3n://elasticmapreduce/samples/cloudburst/cloudburst.jar \
		    --arg s3n://elasticmapreduce/samples/cloudburst/input/s_suis.br \
		    --arg s3n://elasticmapreduce/samples/cloudburst/input/100k.br \
		    --arg $BUCKET"/cloud" \
		    --arg 36 --arg 3 --arg 0 --arg 1 --arg 240 --arg 48 --arg 24 \
		    --arg 24 --arg 128 --arg 16
		;;
	JobPageRank)
		cd $CLI
		JAR=project-pagerank.jar
		CLASS="cis455.project.PageRankDriver"
		INPUT=$BUCKET"/input"
		OUTPUT=$BUCKET"/output"
		ITERATION=3
		MODE="emr"
		./elastic-mapreduce --create --name "Test PageRank JAR" \
		--jar $BUCKET"/"$JAR \
		--arg $CLASS \
		--arg $MODE \
		--arg $INPUT \
		--arg $OUTPUT \
		--arg $ITERATION
		;;
	JobCrawler)
		cd $CLI
		JAR=HadoopCrawler.jar
		CLASS="hadoopcrawler.CrawlerDriver"
		INPUT=$BUCKET"/data/level/urlfile"
		OUTPUT=$BUCKET"/data/level"
		DATABASE="berkeleydb"
		SIZE=10
		./elastic-mapreduce --create --name "Test Crawler JAR" \
		--jar $BUCKET"/"$JAR \
		--arg $CLASS \
		--arg $INPUT \
		--arg $OUTPUT \
		--arg $DATABASE \
		--arg $SIZE
		;;
	DeleteFile)
		aws s3 rm $BUCKET/$2
		;;
	DeleteFolder)
		aws s3 rm $BUCKET/$2 --recursive
		;;
	ListBuckets)
		aws s3 ls
		;;
	ListBucket)
		aws s3 ls $BUCKET
		;;
	Upload)
		aws s3 cp $2 $BUCKET"/" --grants full=emailaddress=$USER
		;;
	Verify)
		cd $CLI
		./elastic-mapreduce --version
		;;
	*)
		;;
esac
