package com.jaslou.streaming;

import com.jaslou.assigner.UserBehaviorTimeAssigner;
import com.jaslou.domin.UserBehavior;
import com.jaslou.domin.UserDeserializationSchema;
import com.jaslou.source.UserBehaviorSource;
import com.jaslou.util.PropertyUtil;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;


import java.util.Properties;

/**
 * send data to kafka
 */
public class UserDefineSourceKafkaProducer {

    public static final String KAFKA_TOPIC = "flink_topic";

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(1000L);
        env.setParallelism(4);

        Properties properties = PropertyUtil.getKafkaProperties();
        DataStreamSource<UserBehavior> userBehaviorDataStreamSource = env.addSource(new UserBehaviorSource());
        userBehaviorDataStreamSource.assignTimestampsAndWatermarks(new UserBehaviorTimeAssigner(Time.seconds(5)))
                .addSink(new FlinkKafkaProducer011<>(KAFKA_TOPIC, new UserDeserializationSchema(), properties));

        env.execute("The job with sending data to kafka Job ");
    }

}
