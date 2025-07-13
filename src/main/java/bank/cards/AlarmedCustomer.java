package bank.cards;

public class AlarmedCustomer {
    public final String id;
    public final String account;

    public AlarmedCustomer(){
        id="";
        account="";
    }

    public AlarmedCustomer(String s){
        String[] split= s.split(",");
        id = split[0];
        account = split[1];
    }

    @Override
    public String toString() {
        return "bank.fraud.cards.AlarmedCustomer{" +
                "id='" + id + '\'' +
                ", account='" + account + '\'' +
                '}';
    }
}
