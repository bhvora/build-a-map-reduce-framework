#Author: Harshali Singh
#!/bin/bash
# Configure aws on instances

AWS_ACCESS_KEY=""
AWS_SECRET_KEY=""
REGION=""

#automate aws configure
echo "${AWS_ACCESS_KEY}
${AWS_SECRET_KEY}
${REGION}
json" | aws configure
