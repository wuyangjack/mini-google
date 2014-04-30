#!/bin/sh
mv $1/meta.txt $1/meta$2.txt
mv $1/pagelink.txt $1/pagelink$2.txt
mv $1/body.txt $1/body$2.txt
aws s3 cp $1/meta$2.txt s3://cis555-project-test/data/large/meta/ --grants full=emailaddress=wuyangjack1991@gmail.com
aws s3 cp $1/pagelink$2.txt s3://cis555-project-test/data/large/pagelink/ --grants full=emailaddress=wuyangjack1991@gmail.com
aws s3 cp $1/body$2.txt s3://cis555-project-test/data/large/body/ --grants full=emailaddress=wuyangjack1991@gmail.com