package bank.checks;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class CityChange extends ProcessWindowFunction<Tuple2<String, String>, Tuple2<String, String>, String, TimeWindow> {

    @Override
    public void process(String key, ProcessWindowFunction<Tuple2<String, String>, Tuple2<String, String>, String, TimeWindow>.Context context, Iterable<Tuple2<String, String>> input, Collector<Tuple2<String, String>> out) throws Exception {
        String lastCity = "";
        int changeCount = 0;
        for (Tuple2<String, String> ele : input){
            String city = ele.f1.split(",")[2].toLowerCase();
            if(lastCity.isEmpty()){
                lastCity = city;
            }else{
                if (!city.equals(lastCity)){
                    lastCity=city;
                    changeCount+=1;
                }
            }
            if(changeCount>=2){
                out.collect(new Tuple2<String, String>("___ALARM___", ele + " marked for FREQUENT city changes"));
            }
        }
    }
}