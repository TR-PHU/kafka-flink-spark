package demo.sparkstreaming;

import java.io.Console;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import demo.sparkstreaming.properties.SSKafkaProperties;
import kafka.tools.ConsoleProducer.ProducerConfig;
import scala.Tuple2;

public class StreamingConsumer {
	private static String inputTopic = "words";
	private static String outputTopic = "words-count";
	
	public static void main(String[] args) throws InterruptedException {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Spark Streaming Consumer");
		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(10));
		
		Map<String, Object> kafkaParams = new HashMap<String, Object>();
        kafkaParams.put("bootstrap.servers", "kafka:9094");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
		kafkaParams.put("key.serializer", StringSerializer.class);
		kafkaParams.put("value.serializer", StringSerializer.class);

        kafkaParams.put("group.id", "myGroupId");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

		// Define a list of Kafka topic to subscribe
		Collection<String> topics = Arrays.asList(inputTopic);
		
		// Create an input DStream which consume message from Kafka topics
		JavaInputDStream<ConsumerRecord<String, String>> stream;
		stream = KafkaUtils.createDirectStream(jssc, 
				LocationStrategies.PreferConsistent(), 
				ConsumerStrategies.Subscribe(topics, kafkaParams));
		
		JavaDStream<String> lines = stream.map((Function<ConsumerRecord<String,String>, String>) kafkaRecord -> kafkaRecord.value());
		
		// KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaParams);
		JavaPairDStream<String, Integer> wordsCount = lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator())
		.mapToPair(word -> new Tuple2<>(word, 1))
		.reduceByKey((a, b) -> (a + b));
		wordsCount.print();
		
		// Start the computation
        jssc.start();
        jssc.awaitTermination();
	}
}
