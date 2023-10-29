package com.kr.matitting.service;

import com.kr.matitting.exception.Map.MapException;
import com.kr.matitting.exception.Map.MapExceptionType;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

@Service
@Slf4j
public class MapService {
    @Value("${kakaoMap.key}")
    private String MAP_KEY;

 //좌표로 주소 변환하기
    public String coordToAddr(double longitude, double latitude) {
        String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x=" + longitude + "&y=" + latitude;
        //TODO: Optional 타입
        String addr = getRegionAddress(getJSONData(url));

        return addr;
    }

    /**
     * REST API로 통신하여 받은 JSON형태의 데이터를 String으로 받아오는 메소드
     */
    private String getJSONData(String apiUrl) {
        HttpURLConnection conn = null;
        StringBuffer response = new StringBuffer();
        int responseCode = 0;

        try {
            String auth = "KakaoAK " + MAP_KEY;

            //URL 설정
            URL url = new URL(apiUrl);

            conn = (HttpURLConnection) url.openConnection();
            //Request 형식 설정
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Requested-With", "curl");
            conn.setRequestProperty("Authorization", auth);

            //request에 JSON data 준비
            conn.setDoOutput(true);

            Charset charset = Charset.forName("UTF-8");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

        } catch (IOException e) {
            throw new MapException(MapExceptionType.FAILED_GET_DATA);
        }
        return response.toString();
    }

    /**
     * JSON형태의 String 데이터에서 주소값(address_name)만 받아오기
     */
    private static String getRegionAddress(String jsonString) {
        String value = "";
        if (!jsonString.startsWith("{") && !jsonString.endsWith("}")) {
            throw new MapException(MapExceptionType.INVALID_DATA);
        }

        JSONObject jObj = (JSONObject) JSONValue.parse(jsonString);
        JSONObject meta = (JSONObject) jObj.get("meta");
        int size = (int) meta.get("total_count");

        if (size > 0) {
            JSONArray jArray = (JSONArray) jObj.get("documents");
            JSONObject subJobj = (JSONObject) jArray.get(0);
            JSONObject roadAddress = (JSONObject) subJobj.get("road_address");

            if (roadAddress == null) {
                JSONObject subsubJobj = (JSONObject) subJobj.get("address");
                value = (String) subsubJobj.get("address_name");
            } else {
                value = (String) roadAddress.get("address_name");
            }

            if (value.equals("") || value == null) {
                subJobj = (JSONObject) jArray.get(1);
                subJobj = (JSONObject) subJobj.get("address");
                value = (String) subJobj.get("address_name");
            }
        }

        return value;
    }
}
