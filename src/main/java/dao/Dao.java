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
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Dao {

    public static final String IP = "https://cstat.nextel.com.ua:8443/tracking";
//    public static final String IP = "http://localhost:8080/tracking";
//    public static final String IP = "https://adeptius.pp.ua:8443/tracking";

    public static final String ADMIN_PASS = "csadmx84";
//    public static final String IP = "https://adeptius.pp.ua:8443/tracking";
    //TODO вернуть адрес

    public static HashMap<String, String> settings = new HashMap<>();

    public static HashMap<String, String> hashes = new HashMap<>();

    public static void updateHashes() throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("adminPassword", ADMIN_PASS);
        String response = sendPost("/admin/getTokens", map);
        Type listType = new TypeToken<HashMap<String, String>>() {
        }.getType();
        hashes = new Gson().fromJson(response, listType);
    }

    /**
     * User CRUD
     */
    public static String createNewUser(JsonUser user) throws Exception {
        return sendJsonObject("/user/add", user, "");
    }

    public static String registerNewUser(String login, String password, String email, String phone) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("login", login);
        map.put("password", password);
        map.put("email", email);
        map.put("phone", phone);
        return sendPost("/registration/register", map);
    }

    public static String editUser(JsonUser user) throws Exception {
        return sendJsonObject("/user/set", user, hashes.get(user.getLogin()));
    }

    public static String changePassword(String userName, String password) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("password", password);
        return sendPost("/user/setPassword", map, hashes.get(userName));
    }

    public static String recoverPassword(String userName) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userName", userName);
        return sendPost("/user/recoverPassword", map, hashes.get(userName));
    }

    public static User getUser(String userName) throws Exception {
        String response = sendPost("/user/get", null, hashes.get(userName));
        User user = new Gson().fromJson(response, User.class);
        return user;
    }

    public static String removeUser(User user) throws Exception {
        return sendJsonObject("/user/remove", null, hashes.get(user.getLogin()));
    }

    /**
     * Tracking
     */

    public static List<JsonSite> getSites(User user) throws Exception {
        Type listType = new TypeToken<List<JsonSite>>() {
        }.getType();
        String s = sendJsonObject("/sites/get", null, hashes.get(user.getLogin()));
        return new Gson().fromJson(s, listType);
    }

    public static String setSite(User user, JsonSite jsonSite) throws Exception {
        return sendJsonObject("/sites/set", jsonSite, hashes.get(user.getLogin()));
    }

    public static String removeSite(User user, String siteName) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("siteName", siteName);
        return sendPost("/sites/remove", map, hashes.get(user.getLogin()));
    }

    public static List<OuterPhone> getOuterPhones(User user, String site) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("siteName", site);
        String result = sendPost("/phones/getSiteOuter", map, hashes.get(user.getLogin()));
        Type listType = new TypeToken<List<OuterPhone>>() {
        }.getType();
        List<OuterPhone> phones = new Gson().fromJson(result, listType);
        return phones;
    }


    public static JsonInnerAndOuterPhones getInnerAndOuterPhones(User user) throws Exception {
        String result = sendPost("/phones/getAllPhones", null, hashes.get(user.getLogin()));
        return new Gson().fromJson(result, JsonInnerAndOuterPhones.class);
    }

    /**
     * Telephony
     */

    public static String setPhonesCount(User user, JsonPhoneCount jPhoneCount) throws Exception {
        return sendJsonObject("/phones/setNumberCount", jPhoneCount, hashes.get(user.getLogin()));
    }

    /**
     * AMOcrm
     */
    public static JsonAmoForController getAmoAccount(User user) throws Exception {
        String result = sendJsonObject("/amo/get", null, hashes.get(user.getLogin()));
        if (result.contains("User have not connected amo account")){
            throw new Exception("No amo acc");
        }
        return new Gson().fromJson(result, JsonAmoForController.class);
    }

    public static String setAmoAccount(User user, JsonAmoForController amoAccount) throws Exception {
        return sendJsonObject("/amo/set", amoAccount, hashes.get(user.getLogin()));
    }

    public static String testAmoAccount(User user) throws Exception {
        return sendJsonObject("/amo/test", "", hashes.get(user.getLogin()));
    }

    public static String removeAmoAccount(User user) throws Exception {
        return sendJsonObject("/amo/remove", "", hashes.get(user.getLogin()));
    }

    public static String setAmoBindings(User user, HashMap<String, String> map) throws Exception {
        return sendJsonObject("/amo/setBindings", map, hashes.get(user.getLogin()));
    }


    /**
     * Roistat
     */

    public static String getRoistatAccount(User user) throws Exception {
        return sendJsonObject("/roistat/get", null, hashes.get(user.getLogin()));
    }

    public static String setRoistatAccount(User user, JsonRoistatForController roistatAccount) throws Exception {
        return sendJsonObject("/roistat/set", roistatAccount, hashes.get(user.getLogin()));
    }

    public static String testRoistatAccount(User user) throws Exception {
        return sendJsonObject("/roistat/test", "", hashes.get(user.getLogin()));
    }

    public static String removeRoistatAccount(User user) throws Exception {
        return sendJsonObject("/roistat/remove", "", hashes.get(user.getLogin()));
    }

    /**
     * Melodies
     */
    public static List<JsonUserAudio> getUserAudio(User user) throws Exception {
        Type listType = new TypeToken<List<JsonUserAudio>>() {
        }.getType();
        return new Gson().fromJson(sendJsonObject("/audio/getList", "", hashes.get(user.getLogin())), listType);
    }

    public static String removeAudio(int id, User user) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", "" + id);
        return sendPost("/audio/remove", map, hashes.get(user.getLogin()));
    }

    public static ArrayList<JsonSipAndPass> getPasswords(User user) throws Exception {
        List<InnerPhone> innerPhones = getInnerAndOuterPhones(user).getInnerPhones();

        ArrayList<JsonSipAndPass> sips = new ArrayList<>();
        for (InnerPhone innerPhone : innerPhones) {
            sips.add(new JsonSipAndPass(innerPhone.getNumber(), innerPhone.getPass()));
        }
        return sips;
    }


    /**
     * Сценарии
     */

    public static String setScenario(User user, Scenario scenario) throws Exception {
        return sendJsonObject("/scenario/set", scenario, hashes.get(user.getLogin()));
    }

    public static List<Scenario> getScenarios(User user) throws Exception {
        String responce = sendJsonObject("/scenario/getAll", null, hashes.get(user.getLogin()));
        Type listType = new TypeToken<List<Scenario>>() {
        }.getType();
        List<Scenario> list = new Gson().fromJson(responce, listType);
        return list;
    }

    public static String removeScenario(User user, int scenarioId) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", "" + scenarioId);
        return sendPost("/scenario/remove", map, hashes.get(user.getLogin()));
    }

    public static JsonScenarioBindings getScenarioBindings(User user) throws Exception {
        String response = sendPost("/scenario/getBindings", null, hashes.get(user.getLogin()));
        return new Gson().fromJson(response, JsonScenarioBindings.class);
    }

    public static String setScenariosBindings(User user, HashMap<String, Integer> bindings) throws Exception {
        return sendJsonObject("/scenario/setBindings", bindings, hashes.get(user.getLogin()));
    }


    /**
     * Admin
     */

    public static ArrayList<String> getListOfCustomers() {
        try {
            String url = "/admin/getAllUsers";
            HashMap<String, Object> map = new HashMap<>();
            map.put("adminPassword", ADMIN_PASS);
            String response = sendPost(url, map);

            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            return new Gson().fromJson(response, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void loadSettings() throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("adminPassword", ADMIN_PASS);
        String result = sendPost("/admin/getAllSettings", map);
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
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("value", value);
            map.put("adminPassword", ADMIN_PASS);
            return sendPost("/admin/setSetting", map);
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка";
        }
    }

    public static String getHistory(User user, String from, String to, String direction, int limit, int offset) throws Exception {
        JsonHistoryQuery query = new JsonHistoryQuery(from, to, direction, limit, offset);
        return sendJsonObject("/history/get", query, hashes.get(user.getLogin()));
    }

    public static List<String> getMelodies() throws Exception {
        String response = sendPost("/scenario/getMelodies", null);
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(response, listType);
    }

    public static JsonNumbersCount getNumbersCount() throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("adminPassword", ADMIN_PASS);
        String response = sendPost("/admin/getNumbersCount", map);
        return new Gson().fromJson(response, JsonNumbersCount.class);
    }


    public static String sendMelodyFile(String filePath, String name, User user) throws Exception {
        HttpResponse<String> stringHttpResponse = Unirest.post(IP + "/audio/add")
                .header("Authorization", hashes.get(user.getLogin()))
                .field("file", new File(filePath))
                .field("name", name)
                .asString();
        return stringHttpResponse.getBody();
    }


    static String sendJsonObject(String relativeUrl, Object object, String authorization) throws Exception {
        String url = IP + relativeUrl;
        String body = new Gson().toJson(object);
        HttpResponse<String> response = Unirest
                .post(url)
                .header("content-type", "application/json")
                .header("authorization", authorization)
                .body(body)
                .asString();
        String result = response.getBody();
        stdout(relativeUrl, result);
        return result;
    }


    static String sendPost(String relativeUrl, HashMap<String, Object> query) throws Exception {
        return sendPost(relativeUrl, query, "");
    }

    static String sendPost(String relativeUrl, HashMap<String, Object> query, String authorization) throws Exception {
        String url = IP + relativeUrl;

        HttpRequestWithBody post = Unirest.post(url).header("Authorization", authorization);
        if (query != null) {
            post.fields(query);
        }

        String result = post.asString().getBody();
        stdout(relativeUrl, result);
        return result;
    }


    private static void stdout(String url, String response) {
        int neededLengh = 25;
        for (int i = url.length(); i < 25; i++) {
            url = url + " ";
        }
        System.out.println(url + ": " + response);
    }
}