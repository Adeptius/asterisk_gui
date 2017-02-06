package model;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Dao {

private static final String IP = "http://localhost:8080";




    public static String setSetting(String name, String value) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("value", value);
            String response = sendPost(IP+"/admin/setsetting", map);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка";
        }
    }


    public static boolean getSettingBoolean(String name){
        return getSetting(name).equals("true");
    }

    public static String getSetting(String name) {
        try {
            String response = getJsonFromUrl(IP+"/admin/getsetting/"+name);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка";
        }
    }



    public static String getScriptForSite(String site) {
        site = site.replaceAll(" ", "%20");
        try {
            String response = getJsonFromUrl(IP+"/admin/script/"+site);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка: " + e.getMessage();
        }
    }

    public static String removeSite(String name) throws Exception{
        HashMap<String, String> map = new HashMap<>();
        map.put("name",name);
        String response = sendPost(IP+"/admin/site/remove",map);
        return response;
    }

    public static String addOrUpdate(Site site) throws Exception{
        String name = site.getName();
        String standartNumber = site.getStandartNumber();
        String googleAnalyticsTrackingId = site.getGoogleAnalyticsTrackingId();
        String email = site.getMail();
        String phones = "";
        String blackIps = "";

        List<Phone> phoneList = site.getPhones();
        for (int i = 0; i < phoneList.size(); i++) {
            if (i!=0){
                phones += ",";
            }
            phones += phoneList.get(i).getNumber();
        }

        List<String> blackList = site.getBlackIps();
        for (int i = 0; i < blackList.size(); i++) {
            if (i!=0){
                blackIps += ",";
            }
            blackIps += blackList.get(i);
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("name",name);
        map.put("standartNumber",standartNumber);
        map.put("googleAnalyticsTrackingId",googleAnalyticsTrackingId);
        map.put("email",email);
        map.put("phones",phones);
        map.put("blackIps",blackIps);

        String response = sendPost(IP+"/admin/site/add",map);

        return response;
    }


    public static ArrayList<String> getLogs() {
        try{
            String url = "/status/logs";
            String response = getJsonFromUrl(IP+url);
            String[] logs = new Gson().fromJson(response, String[].class);
            return new ArrayList<>(Arrays.asList(logs));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> getListOfSites() {
        try{
            String url = "/status/site/getall";
            String response = getJsonFromUrl(IP+url);
            String[] sites = new Gson().fromJson(response, String[].class);
            return new ArrayList<>(Arrays.asList(sites));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public static Site getSiteByName(String siteName) {
        siteName = siteName.replaceAll(" ", "%20");
        try{
            String url = "/status/site/"+siteName;
            String response = getJsonFromUrl(IP+url);
            return new Gson().fromJson(response, Site.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getJsonFromUrl(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String result = "";
        while (in.ready()){
            result += in.readLine();
        }
//        result = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(result);
        System.out.println("Получен Json: " + result);
        in.close();
        return result;
    }


    public static String sendPost(String url, HashMap<String, String> jSonQuery) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        String urlParameters = "";
        Object[] keys = jSonQuery.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            String key = (String) keys[i];
            urlParameters += key + "=" + jSonQuery.get(key);
            if (!(i == keys.length - 1)) urlParameters += "&";
        }
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        System.out.println("Передаю параметры: " + urlParameters);
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String result = in.readLine();
//        result = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(result);
        System.out.println("Ответ: " + result);
        in.close();
        return result;
    }

}
