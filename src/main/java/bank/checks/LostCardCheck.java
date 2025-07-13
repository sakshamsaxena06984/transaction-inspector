package bank.checks;

import bank.cards.LostCard;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.io.IOException;
import java.util.Map;


public class LostCardCheck extends KeyedBroadcastProcessFunction<String, Tuple2<String, String>, LostCard, Tuple2<String, String>> {
    public static final  MapStateDescriptor<String, LostCard> lostCardStateDescriptor = new MapStateDescriptor<String, LostCard>("lost_cards", BasicTypeInfo.STRING_TYPE_INFO, Types.POJO(LostCard.class));


    @Override
    public void processElement(Tuple2<String, String> value, KeyedBroadcastProcessFunction<String, Tuple2<String, String>, LostCard, Tuple2<String, String>>.ReadOnlyContext ctx, Collector<Tuple2<String, String>> out) throws Exception {
        for (Map.Entry<String, LostCard> cardEntry : ctx.getBroadcastState(lostCardStateDescriptor).immutableEntries()){
            final String lostCardId = cardEntry.getKey();
            final LostCard card = cardEntry.getValue();

            // get card_id of current transaction
            final String cId = value.f1.split(",")[5];
            if (cId.equals(lostCardId)) {
                out.collect(new Tuple2 < String, String > ("__ALARM__", "Transaction: " + value + " issued via LOST card"));
            }
        }

    }

    @Override
    public void processBroadcastElement(LostCard card, KeyedBroadcastProcessFunction<String, Tuple2<String, String>, LostCard, Tuple2<String, String>>.Context ctx, Collector<Tuple2<String, String>> out) throws Exception {
        ctx.getBroadcastState(lostCardStateDescriptor).put(card.id, card);
    }
}