package dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.Gui;
import model.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Dao {

            public static final String IP = "http://194.44.37.30/tracking";
//    public static final String IP = "http://localhost:8080/tracking";
    public static final String ADMIN_PASS = "pthy0eds";
    //TODO вернуть адрес


    public static ArrayList<JsonSipAndPass> getPasswords(String name) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("password", ADMIN_PASS);
        String response = sendPost(IP + "/telephony/sipPasswords", map);
        Type listType = new TypeToken<HashMap<String, String>>() {
        }.getType();
        HashMap<String, String> resultMap = new Gson().fromJson(response, listType);
        ArrayList<JsonSipAndPass> sips = new ArrayList<>();
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            sips.add(new JsonSipAndPass(entry.getKey(),entry.getValue()));
        }
        return sips;
    }

    public static List<String> getMelodies() {
        try {
            String response = getJsonFromUrl(IP + "/status/getMelodies");
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            return new Gson().fromJson(response, listType);
        } catch (Exception e) {
            ArrayList<String> list = new ArrayList<>();
            list.add("none");
            return list;
        }
    }

    public static TelephonyCustomer getTelephonyCustomerByName(String name) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("password", ADMIN_PASS);
        String response = sendPost(IP + "/status/telephonyinfo", map);
        return new Gson().fromJson(response, TelephonyCustomer.class);
    }

    public static JsonNumbersCount getNumbersCount() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("adminPassword", ADMIN_PASS);
        String response = sendPost(IP + "/admin/getNumbersCount", map);
        return new Gson().fromJson(response, JsonNumbersCount.class);
    }

    public static String saveRules(String name, List<Rule> rules) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("password", ADMIN_PASS);
            map.put("rules", new Gson().toJson(rules));
            return sendPost(IP + "/rules/update", map);
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
            return new Gson().fromJson(response, listType);
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
            return sendPost(IP + "/admin/setsetting", map);
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
            return sendPost(IP + "/admin/getsetting", map);
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

    public static String removeCustomer(String name) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("adminPassword", ADMIN_PASS);
        return sendPost(IP + "/admin/userremove", map);
    }

    public static String addOrUpdate(TelephonyCustomer customer) throws Exception {
        String name = new Gson().toJson(customer);
        HashMap<String, String> map = new HashMap<>();
        map.put("adminPassword", ADMIN_PASS);
        map.put("telephonyCustomer", name);
        return sendPost(IP + "/admin/telephony/add", map);
    }

    public static String addOrUpdate(Site site) throws Exception {
        String name = new Gson().toJson(site);
        HashMap<String, String> map = new HashMap<>();
        map.put("adminPassword", ADMIN_PASS);
        map.put("siteCustomer", name);
        return sendPost(IP + "/admin/site/add", map);
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


    public static ArrayList<JsonCustomerGroup> getListOfCustomers() {
        try {
            String url = "/admin/getallcustomers";
            HashMap<String, String> map = new HashMap<>();
            map.put("adminPassword", ADMIN_PASS);
            String response = sendPost(IP + url, map);

            Type listType = new TypeToken<ArrayList<JsonCustomerGroup>>() {
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
