#Author: Harshali Singh
#!/bin/bash
# Spawn N instances

if [ -z "$1" ]; then 
	echo "Usage: ./start.sh N<number of nodes>"

else
	#create instances
	aws ec2 run-instances --image-id ami-c229c0a2 --iam-instance-profile Name="s3access" --count $1 --instance-type t2.medium --key-name EC2_KP --security-groups my-sg

	echo "Waiting for instances to start and do ssh on it..."
	sleep 90

	aws ec2 describe-instances --filters "Name=instance-type,Values=t2.medium" | jq -r ".Reservations[].Instances[].NetworkInterfaces[0].Association.PublicIp" > iplist.txt

	#fetch publicDNS of all instances and store them in an array.
	aws ec2 describe-instances --filters "Name=instance-type,Values=t2.medium" | jq -r ".Reservations[].Instances[].PublicDnsName" > dns.txt

	 publicDNSStr=$(aws ec2 describe-instances --filters "Name=instance-type,Values=t2.medium" | jq -r .Reservations | jq .[].Instances | jq .[].PublicDnsName | sed 's/"//g')
	 str=$(echo $publicDNSStr)
	 IFS=' '
	 publicdnsarr=($str)
	 dns_len=${#publicdnsarr[@]}

	#ssh to and transfer files to master instance.
	echo "Attempting ssh to master " ${publicdnsarr[0]}
	scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i EC2_KP.pem setup.sh ec2-user@${publicdnsarr[0]}:~
	ssh -i EC2_KP.pem -o StrictHostKeyChecking=no ec2-user@${publicdnsarr[0]} "chmod 777 setup.sh; sh setup.sh"

	#ssh to and transfer files to all slaves instances.
	for (( i=1; i<${dns_len}; i++ ));
	do
		echo "Attempting ssh to slaves" ${publicdnsarr[i]}
		scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i EC2_KP.pem setup.sh ec2-user@${publicdnsarr[i]}:~
		ssh -i EC2_KP.pem -o StrictHostKeyChecking=no ec2-user@${publicdnsarr[i]} "chmod 777 setup.sh; sh setup.sh"
	done
fi

