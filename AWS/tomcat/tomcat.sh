# !/bin/sh

# Define CIS455_USER first
# export CIS455_USER="cis455/ec2-user"
echo "User: "$CIS455_USER

ROOT=/home/$CIS455_USER
SOURCE=$ROOT/MiniGoogle/AWS/tomcat
TOMCAT=$ROOT/tomcat

echo "Delete old"
sh $TOMCAT/bin/shutdown.sh
sudo rm -rf $TOMCAT

echo "Install new"
cd $ROOT
echo $SOURCE/jakarta-tomcat-5.5.9.tar.gz
tar xvfz $SOURCE/jakarta-tomcat-5.5.9.tar.gz
rm -rf ./tmp
mv $ROOT/jakarta-tomcat-5.5.9 $TOMCAT
cp $SOURCE/tomcat-users.xml $TOMCAT/conf/
sh $TOMCAT/bin/startup.sh
#netstat -tulpn if necessary
