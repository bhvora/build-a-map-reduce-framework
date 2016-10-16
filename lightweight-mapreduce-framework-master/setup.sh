#Author: Harshali Singh
#!/bin/bash
#Set up and install required packages on instances and configure aws

AWS_ACCESS_KEY=""
AWS_SECRET_KEY=""
REGION=""

sudo yum install java-1.7.0-openjdk java-1.7.0-openjdk-devel -y; 

export JAVA_HOME=/usr/lib/jvm/jre-1.7.0-openjdk.x86_64

export PATH=$PATH:$JAVA_HOME/bin 

wget http://s3.amazonaws.com/ec2-downloads/ec2-api-tools.zip

sudo mkdir /usr/local/ec2

sudo unzip ec2-api-tools.zip -d /usr/local/ec2

export EC2_HOME=/usr/local/ec2/ec2-api-tools-1.7.0.0

export PATH=$PATH:$EC2_HOME/bin 

#install aws cli
#sudo apt-get install awscli

#automate aws configure
echo "${AWS_ACCESS_KEY}
${AWS_SECRET_KEY}
${REGION}
json" | aws configure

