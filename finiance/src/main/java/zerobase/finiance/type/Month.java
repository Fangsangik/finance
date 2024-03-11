package zerobase.finiance.type;

public enum Month {

    JAN("Jan", 1),
    FEB("Feb", 2),
    MAR("Mar", 3),
    APR("Apr", 4),
    MAY("May", 5),
    JUN("JUN", 6),
    JUL("Jul", 7),
    AUG("Aug", 8),
    SEP("Sep", 9),
    OCT("Oct", 10),
    NOV("Nov", 11),
    DEC("Dec", 12);

    private final String s;
    private final int number;

    Month(String s, int number) {
        this.s = s;
        this.number = number;
    }

    //s 문자열을 받으면 그에 해당하는 숫자값
    public static int strToNumber(String s){
        for (var m:
             Month.values()) {
            if (m.s.equals(s)){
                return m.number;
            }
        }

        return -1;
    }
}

//메핑 되기 위한 값, 문자열과 문자열에 해당하는 숫자 값