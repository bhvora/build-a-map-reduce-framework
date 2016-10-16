#Author: Harshali Singh
#!/bin/bash

job=$1
input=$2
output=$3
mode=$4

if [ "$#" -lt 4	 ]; then 
	echo "Usage: ./my-mapreduce.sh <job.jar> <input> <output> <mode-local/ec2>"
else

#Execute job.jar in local
java -jar $1 $2 $3 $4
fi

