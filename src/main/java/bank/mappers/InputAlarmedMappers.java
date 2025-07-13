package bank.mappers;

import bank.cards.AlarmedCustomer;
import org.apache.flink.api.common.functions.MapFunction;

public class InputAlarmedMappers implements MapFunction<String, AlarmedCustomer> {
    @Override
    public AlarmedCustomer map(String s) throws Exception {
        return new AlarmedCustomer(s);
    }
}
