package bank;


import bank.cards.AlarmedCustomer;
import bank.cards.LostCard;
import bank.checks.*;
import bank.mappers.ExcessiveTransactionMapper;
import bank.mappers.InputAlarmedMappers;
import bank.mappers.InputDataStreamMapper;
import bank.mappers.InputLostCardMapper;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bank {
    private static final Logger logger = LoggerFactory.getLogger(Bank.class);
    public static final MapStateDescriptor<String, AlarmedCustomer> alarmedCusStateDescriptor = new MapStateDescriptor<String, AlarmedCustomer>("alarmed_customers", BasicTypeInfo.STRING_TYPE_INFO, Types.POJO(AlarmedCustomer.class));
    public static final  MapStateDescriptor<String, LostCard> lostCardStateDescriptor = new MapStateDescriptor<String, LostCard>("lost_cards", BasicTypeInfo.STRING_TYPE_INFO, Types.POJO(LostCard.class));



    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        logger.info("Starting Bank Fraud Detection Flink job...");
        final ParameterTool param;
        try {
            param = ParameterTool.fromPropertiesFile("/Users/sakshamsaxena/koo/code/flink-tut/data-set-stream/src/main/resources/config_prod.properties");
            logger.info("Loaded configuration successfully.");
        } catch (Exception e) {
            logger.error("Failed to load configuration file.", e);
            throw e;
        }

        try {
            logger.info("Reading alarmed customers input...");
            DataStream<AlarmedCustomer> alarmedCustomers = env.readTextFile(param.get("input_alarmed"))
                    .map(new InputAlarmedMappers());
            BroadcastStream<AlarmedCustomer> alarmedCustomerBroadcast = alarmedCustomers.broadcast(alarmedCusStateDescriptor);

            logger.info("Reading lost cards input...");
            DataStream<LostCard> lostCards = env.readTextFile(param.get("input_lost_card"))
                    .map(new InputLostCardMapper());
            BroadcastStream<LostCard> lostCardBroadcast = lostCards.broadcast(lostCardStateDescriptor);

            logger.info("Reading transaction stream from socket...");
            SingleOutputStreamOperator<Tuple2<String, String>> data = env.socketTextStream("localhost", 9090)
                    .map(new InputDataStreamMapper());

            logger.info("Setting up fraud detection operators...");

            DataStream<Tuple2<String, String>> alarmedCusTransaction = data.keyBy(0)
                    .connect(alarmedCustomerBroadcast)
                    .process(new AlarmedCustCheck());

            DataStream<Tuple2<String, String>> lostCardTransactions = data.keyBy(0)
                    .connect(lostCardBroadcast)
                    .process(new LostCardCheck());

            DataStream<Tuple2<String, String>> excessiveTransactions = data
                    .map(new ExcessiveTransactionMapper()).keyBy(0)
                    .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                    .sum(2)
                    .flatMap(new FilterAndMapMoreThan10());

            DataStream<Tuple2<String, String>> cityChanged = data.keyBy(new KeySelector<Tuple2<String, String>, String>() {
                        @Override
                        public String getKey(Tuple2<String, String> value) throws Exception {
                            return value.f0;
                        }
                    })
                    .window(TumblingProcessingTimeWindows.of(Time.minutes(1)))
                    .process(new CityChange());

            DataStream<Tuple2<String, String>> allFlaggedTxn = alarmedCusTransaction
                    .union(lostCardTransactions, excessiveTransactions, cityChanged);

            logger.info("Writing fraud detection output to: {}", param.get("fraud_detection_output"));
            allFlaggedTxn.writeAsText(param.get("fraud_detection_output")).setParallelism(2);

            logger.info("Starting Flink job: Bank Fraud Detection");
            env.execute("Bank Fraud Detection");

        } catch (Exception e) {
            logger.error("Error occurred during stream execution", e);
            throw e;
        }

    }


}
