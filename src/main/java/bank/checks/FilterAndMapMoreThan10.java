package bank.checks;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;

public class FilterAndMapMoreThan10 implements FlatMapFunction<Tuple3< String, String, Integer >, Tuple2< String, String >> {
    public void flatMap(Tuple3 < String, String, Integer > value, Collector< Tuple2 < String, String >> out) {
        if (value.f2 > 10) {
            out.collect(new Tuple2 < String, String > ("__ALARM__", value + " marked for >10 TXNs"));
        }
    }
}