# spark-streaming-kafka-demo
Demo Spark Streaming Consumer read data from kafka

Enable Kafka and create 1 topic with name "topic-1": 

```bash
bin/kafka-topics.sh --create --topic topic-1 --bootstrap-server localhost:9092
```

Create _producer_: 

```bash
bin/kafka-console-producer.sh --topic topic-1 --bootstrap-server localhost:9092
```

Build project to file `.jar`: 

```bash
mvn clean package
```

Run file `.jar` in Spark with `spark-submit`: 

```bash
spark-submit --class demo.sparkstreaming.StreamingConsumer target/SparkStreamingDemo-V1-jar-with-dependencies.jar
```
