package model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.Gui;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Dao {

    //    public static final String IP = "http://194.44.37.30/tracking";
    public static final String IP = "http://localhost:8080/tracking";
    public static final String ADMIN_PASS = "pthy0eds";
    //TODO вернуть адрес

    public static TelephonyCustomer getTelephonyCustomerByName(String name) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("password", ADMIN_PASS);
            String response = sendPost(IP + "/status/telephonyinfo", map);
            return new Gson().fromJson(response, TelephonyCustomer.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new TelephonyCustomer("", "", "", "", new ArrayList<>(), new ArrayList<>());
        }
    }

    public static String saveRules(String name, List<Rule> rules) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("password", ADMIN_PASS);
            map.put("rules", new Gson().toJson(rules));
            String response = sendPost(IP + "/rules/update", map);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка отправки на сервер " + e;
        }
    }

    public static ArrayList<Rule> getRules(String name) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("password", ADMIN_PASS);
            String response = sendPost(IP + "/rules/get", map);
            Type listType = new TypeToken<ArrayList<Rule>>() {
            }.getType();
            ArrayList<Rule> rules = new Gson().fromJson(response, listType);
            return rules;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static ArrayList<History> getHistory(String site, String from, String to, String direction) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", site);
            map.put("dateFrom", from);
            map.put("dateTo", to);
            map.put("direction", direction);
            map.put("password", ADMIN_PASS);
            String response = sendPost(IP + "/status/history", map);
            History[] histories = new Gson().fromJson(response, History[].class);
            return new ArrayList<>(Arrays.asList(histories));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static String setSetting(String name, String value) {
        if (name.equals("ONLY_ACTIVE_SITE")) {
            Gui.onlyActiveSite = Boolean.parseBoolean(value);
            return "";
        }
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("value", value);
            map.put("adminPassword", ADMIN_PASS);
            String response = sendPost(IP + "/admin/setsetting", map);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка";
        }
    }

    public static boolean getSettingBoolean(String name) {
        return getSetting(name).equals("true");
    }

    public static String getSetting(String name) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("adminPassword", ADMIN_PASS);
            String response = sendPost(IP + "/admin/getsetting", map);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка";
        }
    }


    public static String getScriptForSite(String site) {
        site = site.replaceAll(" ", "%20");
        try {
            String response = getJsonFromUrl(IP + "/admin/script/" + site);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка: " + e.getMessage();
        }
    }

    public static String removeSite(String name) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("adminPassword", ADMIN_PASS);
        String response = sendPost(IP + "/admin/site/remove", map);
        return response;
    }

    public static String removeTelephony(String name) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("adminPassword", ADMIN_PASS);
        return sendPost(IP + "/admin/telephony/remove", map);
    }

    public static String addOrUpdate(TelephonyCustomer customer) throws Exception {
        String name = new Gson().toJson(customer);
        HashMap<String, String> map = new HashMap<>();
        map.put("adminPassword", ADMIN_PASS);
        map.put("customer", name);
        return sendPost(IP + "/admin/telephony/add", map);
    }

    public static String addOrUpdate(Site site) throws Exception {
        String name = site.getName();
        String standartNumber = site.getStandartNumber();
        String googleAnalyticsTrackingId = site.getGoogleAnalyticsTrackingId();
        String email = site.getMail();
        String phones = "";
        String blackIps = "";
        String password = site.getPassword();
        int timeToBlock = site.getTimeToBlock();

        List<Phone> phoneList = site.getPhones();
        for (int i = 0; i < phoneList.size(); i++) {
            if (i != 0) {
                phones += ",";
            }
            phones += phoneList.get(i).getNumber();
        }

        List<String> blackList = site.getBlackIps();
        for (int i = 0; i < blackList.size(); i++) {
            if (i != 0) {
                blackIps += ",";
            }
            blackIps += blackList.get(i);
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("standartNumber", standartNumber);
        map.put("googleAnalyticsTrackingId", googleAnalyticsTrackingId);
        map.put("email", email);
        map.put("phones", phones);
        map.put("blackIps", blackIps);
        map.put("password", password);
        map.put("adminPassword", ADMIN_PASS);
        map.put("timeToBlock", "" + timeToBlock);

        String response = sendPost(IP + "/admin/site/add", map);

        return response;
    }


    public static ArrayList<String> getLogs() {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("adminPassword", ADMIN_PASS);
            String response = sendPost(IP + "/admin/logs", map);
            String[] logs = new Gson().fromJson(response, String[].class);
            if (Gui.onlyActiveSite) {
                ArrayList<String> filteredLogs = new ArrayList<>();
                for (int i = 0; i < logs.length; i++) {
                    if (logs[i].startsWith(Gui.selectedSiteString)) {
                        filteredLogs.add(logs[i]);
                    }
                }
                return filteredLogs;
            } else {
                return new ArrayList<>(Arrays.asList(logs));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static ArrayList<String> getListOfSites() {
//        try {
//            String url = "/admin/getallsites";
//            HashMap<String, String> map = new HashMap<>();
//            map.put("password", ADMIN_PASS);
//            String response = sendPost(IP + url, map);
//            String[] sites = new Gson().fromJson(response, String[].class);
//            return new ArrayList<>(Arrays.asList(sites));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }


    public static ArrayList<CustomerGroup> getListOfCustomers() {
        try {
            String url = "/admin/getallcustomers";
            HashMap<String, String> map = new HashMap<>();
            map.put("password", ADMIN_PASS);
            String response = sendPost(IP + url, map);

            Type listType = new TypeToken<ArrayList<CustomerGroup>>() {
            }.getType();
            return new Gson().fromJson(response, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Site getSiteByName(String siteName) {
        siteName = siteName.replaceAll(" ", "%20");
        try {
            String url = "/status/siteinfo";

            HashMap<String, String> map = new HashMap<>();
            map.put("name", siteName);
            map.put("password", ADMIN_PASS);
            String response = sendPost(IP + url, map);

            return new Gson().fromJson(response, Site.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getJsonFromUrl(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String result = "";
        while (in.ready()) {
            result += in.readLine();
        }
//        result = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(result);
        byte[] bytes = result.getBytes();
        result = new String(bytes, "UTF-8");
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
        byte[] bytes = result.getBytes();
        result = new String(bytes, "UTF-8");
        System.out.println("Ответ: " + result);
        in.close();
        return result;
    }

}
