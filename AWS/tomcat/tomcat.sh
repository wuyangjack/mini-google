# !/bin/sh

ROOT=/home/cis455
TOMCAT=$ROOT/tomcat

echo "Delete old"
sh $TOMCAT/bin/shutdown.sh
sudo rm -rf $TOMCAT
#wget http://archive.apache.org/dist/jakarta/tomcat-5/v5.5.9/bin/jakarta-tomcat-5.5.9.tar.gz
echo "Install new"
tar xvfz $ROOT/jakarta-tomcat-5.5.9.tar.gz &> tmp
rm -rf tmp
mv $ROOT/jakarta-tomcat-5.5.9 $TOMCAT
cp $ROOT/tomcat-users.xml $TOMCAT/conf/
sh $TOMCAT/bin/startup.sh
#netstat -tulpn if necessary
