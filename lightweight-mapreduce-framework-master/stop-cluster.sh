#Author: Harshali Singh	
#!/bin/bash
# Stop N instances

#download s3 output


#fetch instance ids
instance_ids=`aws ec2 describe-instances --filters "Name=instance-type,Values=t2.medium" | jq -r ".Reservations[].Instances[].InstanceId"`
instance_array=( $instance_ids )

arr_len=${#instance_array[@]}
i=0
for (( i=0; i<${arr_len}; i++ ));
do
	aws ec2 terminate-instances --instance-ids ${instance_array[i]}
	""$((i+1))" nodes stopped."
done
	

