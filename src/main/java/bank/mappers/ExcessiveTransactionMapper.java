package bank.mappers;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;

public class ExcessiveTransactionMapper implements MapFunction<Tuple2<String, String>, Tuple3<String, String, Integer>> {
    @Override
    public Tuple3<String, String, Integer> map(Tuple2<String, String> value) throws Exception {
        return new Tuple3<String, String, Integer>(value.f0, value.f1, 1);
    }
}
