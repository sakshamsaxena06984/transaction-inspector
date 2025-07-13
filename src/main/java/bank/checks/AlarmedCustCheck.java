package bank.checks;


import bank.cards.AlarmedCustomer;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;
import java.util.Map;


public class AlarmedCustCheck extends KeyedBroadcastProcessFunction<String, Tuple2<String, String>, AlarmedCustomer, Tuple2<String, String>> {

    public static final MapStateDescriptor<String, AlarmedCustomer> alarmedCusStateDescriptor = new MapStateDescriptor<String, AlarmedCustomer>("alarmed_customers", BasicTypeInfo.STRING_TYPE_INFO, Types.POJO(AlarmedCustomer.class));

    @Override
    public void processElement(Tuple2<String, String> value, KeyedBroadcastProcessFunction<String, Tuple2<String, String>, AlarmedCustomer, Tuple2<String, String>>.ReadOnlyContext ctx, Collector<Tuple2<String, String>> out) throws Exception {
        for (Map.Entry<String, AlarmedCustomer> custEntry: ctx.getBroadcastState(alarmedCusStateDescriptor).immutableEntries()){
            final String alarmedCusId = custEntry.getKey();
            final AlarmedCustomer cust = custEntry.getValue();

            final String tId = value.f1.split(",")[3];
            if (tId.equals(alarmedCusId)){
                out.collect(new Tuple2<>("___ALARM___","Transaction: "+ value +" is by an ALARMED customer"));
            }
        }
    }

    @Override
    public void processBroadcastElement(AlarmedCustomer cust, KeyedBroadcastProcessFunction<String, Tuple2<String, String>, AlarmedCustomer, Tuple2<String, String>>.Context ctx, Collector<Tuple2<String, String>> out) throws Exception {
        ctx.getBroadcastState(alarmedCusStateDescriptor).put(cust.id, cust);

    }
}