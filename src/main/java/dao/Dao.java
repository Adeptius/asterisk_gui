package dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.Gui;
import json.JsonHistoryQuery;
import model.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Dao {

    //            public static final String IP = "http://194.44.37.30/tracking";
    public static final String IP = "http://localhost:8080/tracking";
    public static final String ADMIN_PASS = "pthy0eds";
    //TODO вернуть адрес


    public static HashMap<String, String> hashes = new HashMap<>();

    public static void updateHashes() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("adminPassword", ADMIN_PASS);
        String response = sendPost(IP + "/admin/getHash", map, false, "");
        Type listType = new TypeToken<HashMap<String, String>>() {
        }.getType();
        hashes = new Gson().fromJson(response, listType);
    }

    public static String createNewUser(User user) throws Exception {
        return sendJsonObject(IP + "/addUser", user, "");
    }

    public static User getUserByName(String selectedUser) throws Exception {
        String response = sendPost(IP + "/user/getUser", null, false, hashes.get(selectedUser));
        return new Gson().fromJson(response, User.class);
    }

    public static String setTracking(User user, Tracking tracking) throws Exception {
        return sendJsonObject(IP + "/user/setTracking", tracking, hashes.get(user.getLogin()));
    }

    public static String removeTracking(User user) throws Exception {
        return sendPost(IP + "/user/removeTracking", null, false, hashes.get(user.getLogin()));
    }

    public static String setTelephony(User user, Telephony telephony) throws Exception {
        return sendJsonObject(IP + "/user/setTelephony", telephony, hashes.get(user.getLogin()));
    }

    public static String removeTelephony(User user) throws Exception {
        return sendPost(IP + "/user/removeTelephony", null, false, hashes.get(user.getLogin()));
    }

    public static String removeUser(User user) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("username", user.getLogin());
        map.put("adminPassword", ADMIN_PASS);
        return sendPost(IP + "/admin/removeUser", map);
    }

    public static ArrayList<Call> getHistory(User user, String from, String to, String direction) throws Exception {
        JsonHistoryQuery query = new JsonHistoryQuery(from, to, direction);
        Type listType = new TypeToken<ArrayList<Call>>() {
        }.getType();
        String response = sendJsonObject(IP + "/user/getHistory", query, hashes.get(user.getLogin()));
        return new Gson().fromJson(response, listType);

    }

    public static String getScriptForUser(User user) throws Exception{
        return sendPost(IP + "/script/get", null, false, hashes.get(user.getLogin()));
    }

    public static ArrayList<JsonSipAndPass> getPasswords(User user) throws Exception {
        String result = sendPost(IP + "/user/sipPasswords", null, false, hashes.get(user.getLogin()));
        Type listType = new TypeToken<HashMap<String, String>>() {
        }.getType();
        HashMap<String, String> resultMap = new Gson().fromJson(result, listType);
        ArrayList<JsonSipAndPass> sips = new ArrayList<>();
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            sips.add(new JsonSipAndPass(entry.getKey(), entry.getValue()));
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
            return sendJsonObject(IP + "/rules/update", rules, hashes.get(name));
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


    public static ArrayList<String> getListOfCustomers() {
        try {
            String url = "/admin/getAllUsers";
            HashMap<String, String> map = new HashMap<>();
            map.put("adminPassword", ADMIN_PASS);
            String response = sendPost(IP + url, map);

            Type listType = new TypeToken<ArrayList<String>>() {
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


    static String sendJsonObject(String url, Object object, String authorization) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("authorization", authorization);
        String urlParameters = new Gson().toJson(object);
        con.setRequestProperty("Content-Type", "application/json");
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

    static String sendPost(String url, HashMap<String, String> jSonQuery, boolean itsJson, String authorization) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", authorization);
        if (jSonQuery != null) {
            String urlParameters = "";
            if (itsJson) {
                con.setRequestProperty("Content-Type", "application/json");
                urlParameters = new Gson().toJson(jSonQuery);
            } else {
                Object[] keys = jSonQuery.keySet().toArray();
                for (int i = 0; i < keys.length; i++) {
                    String key = (String) keys[i];
                    urlParameters += key + "=" + jSonQuery.get(key);
                    if (!(i == keys.length - 1)) urlParameters += "&";
                }
            }
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            System.out.println("Передаю параметры: " + urlParameters);
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String result = in.readLine();
//        result = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(result);
        System.out.println("Ответ: " + result);
        in.close();
        return result;
    }


}
