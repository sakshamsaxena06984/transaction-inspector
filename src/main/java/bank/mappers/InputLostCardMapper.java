package bank.mappers;

import bank.cards.LostCard;
import org.apache.flink.api.common.functions.MapFunction;

public class InputLostCardMapper implements MapFunction<String, LostCard>{
    @Override
    public LostCard map(String s) throws Exception {
        return new LostCard(s);
    }
}
