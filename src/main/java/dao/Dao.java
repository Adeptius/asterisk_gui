package dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;
import javafx.Gui;
import json.*;
import model.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Dao {

//            public static final String IP = "http://cstat.nextel.com.ua/tracking";
    public static final String IP = "http://localhost:8080/tracking";
    public static final String ADMIN_PASS = "pthy0eds";
    //TODO вернуть адрес

    public static HashMap<String, String> settings = new HashMap<>();

    public static HashMap<String, String> hashes = new HashMap<>();

    public static void updateHashes() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("adminPassword", ADMIN_PASS);
        String response = sendPost(IP + "/admin/getTokens", map, false, "");
        Type listType = new TypeToken<HashMap<String, String>>() {
        }.getType();
        hashes = new Gson().fromJson(response, listType);
    }

    /**
     * User CRUD
     */
    public static String createNewUser(JsonUser user) throws Exception {
        return sendJsonObject(IP + "/user/add", user, "");
    }

    public static String editUser(JsonUser user) throws Exception {
        return sendJsonObject(IP + "/user/set", user, hashes.get(user.getLogin()));
    }

    public static User getUserByName(String selectedUser) throws Exception {
        String response = sendPost(IP + "/user/get", null, false, hashes.get(selectedUser));
        User user = new Gson().fromJson(response, User.class);
        user.setLogin(selectedUser);
        return user;
    }

    public static String removeUser(User user) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("username", user.getLogin());
        map.put("adminPassword", ADMIN_PASS);
        return sendPost(IP + "/user/remove", map);
    }

    /**
     * Tracking
     */

    public static String addTracking(User user, JsonTracking tracking) throws Exception {
        return sendJsonObject(IP + "/tracking/add", tracking, hashes.get(user.getLogin()));
    }

    public static String setTracking(User user, JsonTracking tracking) throws Exception {
        return sendJsonObject(IP + "/tracking/set", tracking, hashes.get(user.getLogin()));
    }

    public static String setTrackingNumberCount(User user, JsonTracking tracking) throws Exception {
        return sendJsonObject(IP + "/tracking/setNumberCount", tracking, hashes.get(user.getLogin()));
    }

    public static String removeTracking(User user) throws Exception {
        return sendPost(IP + "/tracking/remove", null, false, hashes.get(user.getLogin()));
    }

    public static List<OuterPhone> getOuterPhones(User user,String site) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("sitename", site);
        String result =  sendPost(IP + "/phones/get", map, false, hashes.get(user.getLogin()));
        Type listType = new TypeToken<List<OuterPhone>>() {
        }.getType();
        List<OuterPhone> phones = new Gson().fromJson(result, listType);
        return phones;
    }



    /**
     * Telephony
     */

    public static String addTelephony(User user) throws Exception {
        return sendJsonObject(IP + "/telephony/add", null, hashes.get(user.getLogin()));
    }

    public static String setTelephonyNumberCount(User user, JsonTelephony telephony) throws Exception {
        return sendJsonObject(IP + "/telephony/setNumberCount", telephony, hashes.get(user.getLogin()));
    }

    public static String removeTelephony(User user) throws Exception {
        return sendPost(IP + "/telephony/remove", null, false, hashes.get(user.getLogin()));
    }

    /**
     * AMOcrm
     */

    public static String setAmoAccount(User user, JsonAmoForController amoAccount) throws Exception {
        return sendJsonObject(IP + "/amo/set", amoAccount, hashes.get(user.getLogin()));
    }

    public static String testAmoAccount(User user) throws Exception {
        return sendJsonObject(IP + "/amo/test", "", hashes.get(user.getLogin()));
    }

    public static String removeAmoAccount(User user) throws Exception {
        return sendJsonObject(IP + "/amo/remove", "", hashes.get(user.getLogin()));
    }

    public static HashMap<String, String> getAmoBindings(User user) throws Exception {
        String response = sendPost(IP + "/amo/getBindings", null, false, hashes.get(user.getLogin()));
        return new Gson().fromJson(response, new TypeToken<HashMap<String, String>>() {}.getType());
    }

    public static String setAmoBindings(User user, HashMap<String, String> map) throws Exception {
        return sendJsonObject(IP + "/amo/setBindings", map, hashes.get(user.getLogin()));
    }


    /**
     * Roistat
     */

    public static String setRoistatAccount(User user, JsonRoistatForController roistatAccount) throws Exception {
        return sendJsonObject(IP + "/roistat/set", roistatAccount, hashes.get(user.getLogin()));
    }

    public static String testRoistatAccount(User user) throws Exception {
        return sendJsonObject(IP + "/roistat/test", "", hashes.get(user.getLogin()));
    }

    public static String removeRoistatAccount(User user) throws Exception {
        return sendJsonObject(IP + "/roistat/remove", "", hashes.get(user.getLogin()));
    }


    /**
     * BlackList Tracking
     */

    public static void addIpToBlackList(String ip, User user) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("ip", ip);
            sendPost(IP + "/blacklist/add", map, false, hashes.get(user.getLogin()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeIpFromBlackList(String ip, User user) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("ip", ip);
            sendPost(IP + "/blacklist/remove", map, false, hashes.get(user.getLogin()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<JsonSipAndPass> getPasswords(User user) throws Exception {
        String result = sendPost(IP + "/telephony/getSipPasswords", null, false, hashes.get(user.getLogin()));
        Type listType = new TypeToken<HashMap<String, String>>() {
        }.getType();
        HashMap<String, String> resultMap = new Gson().fromJson(result, listType);
        ArrayList<JsonSipAndPass> sips = new ArrayList<>();
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            sips.add(new JsonSipAndPass(entry.getKey(), entry.getValue()));
        }
        return sips;
    }


    /**
     * Сценарии
     */

    public static String setScenario(User user, JsonScenario scenario) throws Exception {
        return sendJsonObject(IP + "/scenario/set", scenario, hashes.get(user.getLogin()));
    }

    public static List<Scenario> getScenarios(User user) throws Exception {
        String responce = sendJsonObject(IP + "/scenario/get", null, hashes.get(user.getLogin()));
        Type listType = new TypeToken<List<Scenario>>() {
        }.getType();
        List<Scenario> list = new Gson().fromJson(responce, listType);
        return list;
    }

    public static String activateScenario(User user, int scenarioId) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", "" + scenarioId);
        return sendPost(IP + "/scenario/activate", map, false, hashes.get(user.getLogin()));
    }

    public static String deactivateScenario(User user, int scenarioId) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", "" + scenarioId);
        return sendPost(IP + "/scenario/deactivate", map, false, hashes.get(user.getLogin()));
    }

    public static String removeScenario(User user, int scenarioId) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", "" + scenarioId);
        return sendPost(IP + "/scenario/remove", map, false, hashes.get(user.getLogin()));
    }


    /**
     * Admin
     */

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


    public static void loadSettings() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("adminPassword", ADMIN_PASS);
        String result = sendPost(IP + "/admin/getAllSettings", map);
        Type listType = new TypeToken<HashMap<String, String>>() {
        }.getType();
        settings = new Gson().fromJson(result, listType);
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


    public static ArrayList<Call> getHistory(User user, String from, String to, String direction) throws Exception {
        JsonHistoryQuery query = new JsonHistoryQuery(from, to, direction);
        Type listType = new TypeToken<ArrayList<Call>>() {
        }.getType();
        String response = sendJsonObject(IP + "/history/get", query, hashes.get(user.getLogin()));
        return new Gson().fromJson(response, listType);
    }

    public static String getScriptForUser(User user, String sitename) throws Exception {
        return sendPost(IP + "/script/get/"+sitename, null, false, hashes.get(user.getLogin()));
    }


    public static List<String> getAvailableNumbers(User user) {
        try {
            String result = sendPost(IP + "/rules/getAvailableNumbers", null, false, hashes.get(user.getLogin()));
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            return new Gson().fromJson(result, listType);
        } catch (Exception e) {
            return new LinkedList<>();
        }
    }

    public static List<String> getMelodies() {
        try {
            String response = sendPost(IP + "/rules/getMelodies", null);
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            return new Gson().fromJson(response, listType);
        } catch (Exception e) {
            ArrayList<String> list = new ArrayList<>();
            list.add("none");
            return list;
        }
    }

    public static JsonNumbersCount getNumbersCount() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("adminPassword", ADMIN_PASS);
        String response = sendPost(IP + "/admin/getNumbersCount", map);
        return new Gson().fromJson(response, JsonNumbersCount.class);
    }


    public static String sendPost(String url, HashMap<String, String> jSonQuery) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        String urlParameters = "";
        if (jSonQuery != null) {
            Object[] keys = jSonQuery.keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                String key = (String) keys[i];
                urlParameters += key + "=" + jSonQuery.get(key);
                if (!(i == keys.length - 1)) urlParameters += "&";
            }
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//            System.out.println("Передаю параметры: " + urlParameters);
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String result = in.readLine();
//        result = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(result);
        byte[] bytes = result.getBytes();
        result = new String(bytes, "UTF-8");
        System.out.println("Ответ: " + result);
        in.close();
        return result;
    }


    static String sendPostParams(String url, String authorization, String... params) throws Exception {
        HttpRequestWithBody request = Unirest.post(url).header("authorization", authorization);

        for (int i = 0; i < 0; i=i+2) {
            request.field(params[i], params[i+1]);
        }

        HttpResponse<String> stringHttpResponse = request.asString();

        String result = stringHttpResponse.getBody();
        System.out.println("Ответ: " + result);
        return result;
    }

    static String sendJsonObject(String url, Object object, String authorization) throws Exception {
        String body = new Gson().toJson(object);
        System.out.println("Отправляю: " + body);
        HttpResponse<String> response = Unirest
                .post(url)
                .header("content-type", "application/json")
                .header("authorization", authorization)
                .body(body)
                .asString();

        String result = response.getBody();
        System.out.println("Ответ: " + result);
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
//            System.out.println("Передаю параметры: " + urlParameters);
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
