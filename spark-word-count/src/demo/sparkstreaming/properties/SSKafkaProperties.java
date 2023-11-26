package demo.sparkstreaming.properties;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.StringDeserializer;

public class SSKafkaProperties {
	/**
	 * Define configuration kafka consumer with spark streaming
	 * @return
	 */
	public static Map<String, Object> getInstance() {
        Map<String, Object> kafkaParams = new HashMap<String, Object>();
        kafkaParams.put("bootstrap.servers", "localhost:9094");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "0");
        kafkaParams.put("auto.offset.reset", "earliest");
        kafkaParams.put("enable.auto.commit", false);
        
        return kafkaParams;
	}
}
