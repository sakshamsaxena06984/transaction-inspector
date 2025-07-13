package bank.mappers;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;

public class InputDataStreamMapper implements MapFunction<String, Tuple2<String, String>> {

    @Override
    public Tuple2<String, String> map(String s) throws Exception {
        String[] words = s.split(",");
        return new Tuple2<>(words[3], s);
    }
}
