# SomeDataprocSparkJob
Connect a Dataproc Spark job to Cloud SQL using the Cloud SQL proxy.
You must have the Cloud SQL instance running already.

1. Createa a Dataproc Cluster with the Cloud SQL proxy:
```
gcloud config set project *YOUR_PROJECT*
CLUSTER_NAME=tests-cluster
REGION=us-central1
PROJECT_ID=*YOUR_PROJECT_ID*
INSTANCE_NAME=*YOUR_INSTANCE_NAME*
      
gcloud dataproc clusters create ${CLUSTER_NAME} \
    --region ${REGION} \
    --scopes sql-admin \
    --initialization-actions gs://goog-dataproc-initialization-actions-${REGION}/cloud-sql-proxy/cloud-sql-proxy.sh \
    --metadata "enable-cloud-sql-hive-metastore=false" \
    --metadata "additional-cloud-sql-instances=${PROJECT_ID}:${REGION}:${INSTANCE_NAME}" \
    --zone=us-central1-c \
    --master-boot-disk-size=15GB \
    --master-boot-disk-type=pd-ssd \
    --master-machine-type n2-standard-4 \
    --single-node \
    --image-version 1.4
```
2. While the cluster is created you can build the jar we are going to deploy: 

``` 
sbt assembly 
```

3. Submit the Spark job on "dev" mode:
```
gcloud dataproc jobs submit spark \
   --region ${REGION} \
   --cluster=${CLUSTER_NAME} \
   --jars=./target/scala-2.11/SomeDataprocSparkJob-assembly-1.0.jar \
   --class=dev.ancor.somedataprocsparkjob.SomeSparkJob \
   --properties=spark.driver.extraClassPath=SomeDataprocSparkJob-assembly-1.0.jar \
   -- dev
```
It should work fine.

4. Submit the Spark job on "prod" mode.
```
gcloud dataproc jobs submit spark \
   --region ${REGION} \
   --cluster=${CLUSTER_NAME} \
   --jars=./target/scala-2.11/SomeDataprocSparkJob-assembly-1.0.jar \
   --class=dev.ancor.somedataprocsparkjob.SomeSparkJob \
   --properties=spark.driver.extraClassPath=SomeDataprocSparkJob-assembly-1.0.jar \
   -- prod
```
It returns an exception:
```
Exception in thread "main" java.lang.NoSuchFieldError: ASCII
        at org.apache.hadoop.yarn.api.records.impl.pb.ApplicationSubmissionContextPBImpl.checkTags(ApplicationSubmissionContextPBImpl.java:287)
        at org.apache.hadoop.yarn.api.records.impl.pb.ApplicationSubmissionContextPBImpl.setApplicationTags(ApplicationSubmissionContextPBImpl.java:302)
        at org.apache.spark.deploy.yarn.Client$$anonfun$createApplicationSubmissionContext$2.apply(Client.scala:245)
        at org.apache.spark.deploy.yarn.Client$$anonfun$createApplicationSubmissionContext$2.apply(Client.scala:244)
        at scala.Option.foreach(Option.scala:257)
        at org.apache.spark.deploy.yarn.Client.createApplicationSubmissionContext(Client.scala:244)
        at org.apache.spark.deploy.yarn.Client.submitApplication(Client.scala:180)
        at org.apache.spark.scheduler.cluster.YarnClientSchedulerBackend.start(YarnClientSchedulerBackend.scala:57)
        at org.apache.spark.scheduler.TaskSchedulerImpl.start(TaskSchedulerImpl.scala:183)
        at org.apache.spark.SparkContext.<init>(SparkContext.scala:501)
        at org.apache.spark.SparkContext$.getOrCreate(SparkContext.scala:2520)
        at org.apache.spark.sql.SparkSession$Builder$$anonfun$7.apply(SparkSession.scala:935)
        at org.apache.spark.sql.SparkSession$Builder$$anonfun$7.apply(SparkSession.scala:926)
        at scala.Option.getOrElse(Option.scala:121)
        at org.apache.spark.sql.SparkSession$Builder.getOrCreate(SparkSession.scala:926)
...
```
