package repon.cse.kuetian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassTestObject {
    public  List<String> rawDateTimeList = new ArrayList<>();
    public  HashMap<String,String> dateTimeMap = new HashMap<>();
    public  HashMap<String,String> titleMap = new HashMap<>();
    public  HashMap<String,String> descriptionMap = new HashMap<>();

    public ClassTestObject( ) {
    }
    public ClassTestObject(List<String> rawDateTimeList, HashMap<String, String> dateTimeMap, HashMap<String, String> titleMap, HashMap<String, String> descriptionMap) {
        this.rawDateTimeList = rawDateTimeList;
        this.dateTimeMap = dateTimeMap;
        this.titleMap = titleMap;
        this.descriptionMap = descriptionMap;
    }
}
