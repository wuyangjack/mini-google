# !/bin/sh

ROOT=/home/ec2-user
SOURCE=$ROOT/AWS/tomcat
TOMCAT=$ROOT/tomcat

echo "Delete old"
sh $TOMCAT/bin/shutdown.sh
sudo rm -rf $TOMCAT

echo "Install new"
cd $ROOT
tar xvfz $SOURCE/jakarta-tomcat-5.5.9.tar.gz &> tmp
rm -rf tmp
mv $ROOT/jakarta-tomcat-5.5.9 $TOMCAT
cp $SOURCE/tomcat-users.xml $TOMCAT/conf/
sh $TOMCAT/bin/startup.sh
#netstat -tulpn if necessary
