import java.util.GregorianCalendar;

public class ChartData {
    private GregorianCalendar date;
    private Number number;

    public ChartData(GregorianCalendar date, Number number) {
        this.date = date;
        this.number = number;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    public Number getNumber() {
        return number;
    }

    public void setNumber(Number number) {
        this.number = number;
    }
}
