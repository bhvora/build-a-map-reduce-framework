#Author: Vishal Mehta , Harshali Singh

mode:
	chmod 777 my-mapreduce.sh
	chmod 777 script.sh
	chmod 777 start-cluster.sh
	chmod 777 stop-cluster.sh
	chmod 777 setup.sh

master:
	(cd MapReduce-Master && make jar)
	cp MapReduce-Master/Master.jar Master.jar 

wordcountjob:
	(cd Job && make jar)
	cp Job/Job.jar WordCount.jar 

wordmedianjob:
	(cd Job && make jar)
	cp Job/Job.jar WordMedian.jar 

clusterjob:
	(cd Job && make jar)
	cp Job/Job.jar Cluster.jar 

missedjob:
	(cd Job && make jar)
	cp Job/Job.jar Missed.jar 

clean:
	rm -rf error.txt
	rm -rf console.txt
	rm -rf *.jar
	rm -rf tempData
	rm -rf iplist.txt
	rm -rf dns.txt

kill:
	fuser -k 4002/tcp
