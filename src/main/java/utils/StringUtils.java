package utils;

import java.util.ArrayList;

public class StringUtils {



    public static String convertToStringWithN(ArrayList<String> list){
       String s = "";
        for (String s1 : list) {
            s+=s1 + "\n";
        }
        if (s.endsWith("\n")){
            s.substring(0,s.length()-1);
        }
        return s;
    }

    public static ArrayList<String> convertToList(String s){
        ArrayList<String> list = new ArrayList<>();
        s.replaceAll(" ", "").replaceAll("\t","");
        String[] phonesArr = s.split("\n");
        for (String s1 : phonesArr) {
            if (!s1.equals("\n"))
            list.add(s1);
        }
        return list;
    }


    public static String getStringedTime(long time){
        time = time /1000;

        int seconds = (int) (time%60);
        int minutes = (int) ((time/60)%60);
        int hours = (int) ((time/60)/60);

        String sec = String.valueOf(seconds).length() == 1 ? "0"+seconds : ""+seconds;

        String stringedTime = sec + "c";
        if (minutes != 0){
            String min = String.valueOf(minutes).length() == 1 ? "0"+minutes : ""+minutes;
            stringedTime = min+ "м " + stringedTime;
        }
        if (hours != 0){
            stringedTime = hours + "ч " + stringedTime;
        }

        return stringedTime;
    }

    public static String doTwoSymb(int i) {
        String s = String.valueOf(i);
        if (s.length() == 1) s = "0" + s;
        return s;
    }
}
