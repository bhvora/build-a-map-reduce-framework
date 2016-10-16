# SimpleMapReduce
A map-reduce framework that allows distributed processing of large datasets in parallel across a distributed cluster of AWS EC2
instances or on a single computer using all of its available CPU cores.

Please refer to Report.pdf file to go through the description of entire project.

Instructions to set up and run the program.

*********************************************************************
PRE-REQUISITES:

1. Install jq. 
	sudo apt-get install jq.

2. A linux environment with working ssh in it.

3. Key-pair and Security group
  * Connecting to ec2 instances through ssh requires a key pair which can be created by Amazon aws CLI given below. Make sure to run 'chmod' command for key pair.  
  __aws ec2 create-key-pair --key-name EC2_KP chmod 400 EC2_KP__  
  * Create a security group which allows SSH to all the instances and set the rules in that SG that allows all incoming traffic for specified port. Use below Amazon aws CLI to create a security group:  
		__aws ec2 create-security-group --group-name my-sg --description "My security group"__  
	
4. Create an IAM role to allow access to S3 bucket from your ec2 instances using the AWS Console by follwoing the below steps.
	a. In the IAM console, in the navigation pane, choose Policies, and then choose Create Policy. (If a Get Started button appears, choose it, and then choose Create Policy.)
	b. Next to Create Your Own Policy, choose Select.
	c. In the Policy Name box, type 'S3-Permissions'.
	d. In the Policy Document box, paste the following after placing your <bucket_name>:
	```
		{
			"Version": "2012-10-17",
			"Statement": [
			{
				"Effect": "Allow",
				"Action": [
				"s3:ListBucket"
				],
				"Resource": [
				"arn:aws:s3:::<bucket_name>"
				]
				},
				{
					"Effect": "Allow",
					"Action": [
					"s3:*"
					],
					"Resource": [
					"arn:aws:s3:::<bucket_name>/*"
					]
				}
				]
			}
		}
	```

	e. Choose Create Policy.
	f. In the navigation pane, choose Roles, and then choose Create New Role	
	g. In the Role Name box, give the IAM instance profile a name like 's3access', and then choose Next Step.
	h. On the Select Role Type page, next to Amazon EC2, choose Select.
	i. On the Attach Policy page, select the box next to S3-Permissions, and then choose Next Step.
	j. Choose Create Role.


STEPS TO RUN THE PROGRAM on pseudo or ec2 instances:
*****************************************************************************************
NOTE: Make sure you have input files in the input folder.
      Include your AWS_ACCESS_KEY, AWS_SECRET_KEY and region in lines 5,6,7 of setup.sh file.
      MASTER CODE- MapReduce-Master folder
      SLAVE CODE- MapReduce-Slave folder
      Job Examples - Job folder
*******************************************************************************************

Run 'make mode' from the project directory.   
1. Navigate to MapReduce-Master folder  
2. make jar  
3. then, Navigate to MapReduce-Slave folder  
4. make jar  
5. then, Navigate to Job folder and copy Slave.jar to libs/ directory  
6. make jar  
7. You should have three jars at this point - Master.jar, Slave.jar, Job.jar (WordCount.jar) in the project directory.  
8. *./start-cluster.sh 2*  
9. *time ./my-mapreduce.sh job_jar input output mode*  
10. *./stop-cluster.sh*


Note: If you are not able to do ssh to any instance in one attempt, please try 3-4 times as your network connection might not be that strong.

