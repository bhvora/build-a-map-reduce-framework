#Author: Harshali Singh
#!/bin/bash
	
jarname=$1
mapper=$2
reducer=$3
input=$4
output=$5
mode=$6

if [ "$mode" = "local" ]; then
	map=$(echo ${mapper} | tr -d "'")
	reduce=$(echo ${reducer} | tr -d "'")
	java -jar Master.jar ${map} ${reduce} ${input} ${output} ${mode} &
	java -jar ${jarname} "127.0.1.1" 	
	#wait
else if [ "$mode" = "ec2" ]; then
	#Fetch IP lists
	ipLists=$(aws ec2 describe-instances --filters "Name=instance-type,Values=t2.medium" | jq -r ".Reservations[].Instances[].NetworkInterfaces[0].Association.PublicIp")
	str=$(echo $ipLists)
	IFS=' '
	ipLists=($str)

	#Fetch master's DNS and execute the Master that starts the server.
	line=$(head -n 1 dns.txt)
	echo "Transferring jar file to the server and starting the server..."
	scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i EC2_KP.pem Master.jar ec2-user@${line}:~
	ssh -i EC2_KP.pem -o StrictHostKeyChecking=no ec2-user@${line} "java -jar Master.jar ${mapper} ${reducer} ${input} ${output} ${mode}" 


	#Fetch other DNS(s) of slave machines and starts the Client  
	sed 1d $file | while read dns; do
		echo "Transferring jar file & starting the client ${dns}"
		scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i EC2_KP.pem Slave.jar ec2-user@${dns}:~
		ssh -i EC2_KP.pem -o StrictHostKeyChecking=no ec2-user@${dns} "java -jar ${jarname} ${ipLists[0]}" 
	done < dns.txt
	fi
fi


