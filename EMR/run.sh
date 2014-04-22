#!/bin/sh

ROOT="/home/cis455/MiniGoogle/EMR"
CLI=$ROOT"/bin"
BUCKET="cis555-project-test"

case "$1" in
	SampleEMR)
		cd $CLI
		./elastic-mapreduce --create --name "Test custom JAR" \
		  	--jar s3n://elasticmapreduce/samples/cloudburst/cloudburst.jar \
		    --arg s3n://elasticmapreduce/samples/cloudburst/input/s_suis.br \
		    --arg s3n://elasticmapreduce/samples/cloudburst/input/100k.br \
		    --arg "s3n://"$BUCKET"/cloud" \
		    --arg 36 --arg 3 --arg 0 --arg 1 --arg 240 --arg 48 --arg 24 \
		    --arg 24 --arg 128 --arg 16
		;;
	*)
		;;
esac