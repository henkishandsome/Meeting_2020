package com.example.meeting2020.HttpConnect;

//import com.alibaba.fastjson.JSONObject;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    public static String BASE_URL = "http://192.168.43.188:8082/bird/";

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }


    public static void sendOkHttpRequestJson(String address, JSONObject json, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String JSON = json.toString();
        MediaType mediaType = MediaType.Companion.parse("application/json;charset=UTF-8");
        RequestBody requestBody = RequestBody.Companion.create(JSON, mediaType);
        Request request = new Request
                .Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


    public static void sendOkHttpRequestText(String address, Map<String, String> parms, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (parms != null) {
            for (Map.Entry<String, String> entry : parms.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        RequestBody requestBody = builder
                .setType(MultipartBody.FORM)
                .build();

        Request request = new Request
                .Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
    /*
     * urlStr:网址
     * parms：提交数据
     * return:网页源码
     * */
//    public static  String getContextByHttp(String urlStr, JSONObject jsonObject){
//        StringBuilder sb = new StringBuilder();
//        try{
//            URL url = new URL(urlStr);
//            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setReadTimeout(5000);
//            connection.setConnectTimeout(5000);
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//            connection.setRequestProperty("ser-Agent", "Fiddler");
//            connection.setRequestProperty("Content-Type","application/json");
//            connection.setInstanceFollowRedirects(true);
//            OutputStream outputStream = connection.getOutputStream();
//            outputStream.write(String.valueOf(jsonObject).getBytes());
//            outputStream.close();
//            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String temp;
//                while((temp = reader.readLine()) != null){
//                    sb.append(temp);
//                }
//                reader.close();
//            }else{
//                return "connection error:" + connection.getResponseCode();
//            }
//
//            connection.disconnect();
//        }catch (Exception e){
//            return e.toString();
//        }
//        return sb.toString();
//    }

    /**
     * 将map转换成key1=value1&key2=value2的形式
     * @return
     * @throws UnsupportedEncodingException
     */
   /* private static String getStringFromOutput(Map<String,String> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for(Map.Entry<String,String> entry:map.entrySet()){
            if(isFirst)
                isFirst = false;
            else
                sb.append("&");
            sb.append(URLEncoder.encode(entry.getKey(),"UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue(),"UTF-8"));
        }
        return sb.toString();
    }*/

//    public static  String getContextByHttp1(String urlStr, Map<String,String> parms){
//        StringBuilder sb = new StringBuilder();
//        try{
//            URL url = new URL(urlStr);
//            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setReadTimeout(5000);
//            connection.setConnectTimeout(5000);
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//            connection.setInstanceFollowRedirects(true);
//
//            OutputStream outputStream = connection.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
//            writer.write(getStringFromOutput(parms));
//
//            writer.flush();
//            writer.close();
//            outputStream.close();
//
//            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String temp;
//                while((temp = reader.readLine()) != null){
//                    sb.append(temp);
//                }
//                reader.close();
//            }else{
//                return "connection error:" + connection.getResponseCode();
//            }
//
//            connection.disconnect();
//        }catch (Exception e){
//            return e.toString();
//        }
//        return sb.toString();
//    }

//    public static  String getContextByHttptest(String urlStr, Map<String,String> parms){
//        StringBuilder sb = new StringBuilder();
//        try{
//            URL url = new URL(urlStr);
//            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setReadTimeout(5000);
//            connection.setConnectTimeout(5000);
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//            connection.setInstanceFollowRedirects(true);
//
//            OutputStream outputStream = connection.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
//            writer.write(getStringFromOutput(parms));
//
//            writer.flush();
//            writer.close();
//            outputStream.close();
//
//            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String temp;
//                while((temp = reader.readLine()) != null){
//                    sb.append(temp);
//                }
//                reader.close();
//            }else{
//                return "connection error:" + connection.getResponseCode();
//            }
//
//            connection.disconnect();
//        }catch (Exception e){
//            return e.toString();
//        }
//        return sb.toString();
//    }
}
