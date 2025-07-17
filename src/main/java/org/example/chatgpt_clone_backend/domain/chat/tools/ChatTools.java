package org.example.chatgpt_clone_backend.domain.chat.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class ChatTools {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ChatTools(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // 세분화 시켜야함 합치면 오동작
    @Tool(description = "exchange rate : KRW <-> USD")
    public Map<String, String> rateKrwUsdExchangeTool() {

        String URL = "https://m.search.naver.com/p/csearch/content/qapirender.nhn?key=calculator&pkid=141&q=%ED%99%98%EC%9C%88&where=m&u1=keb&u6=standardUnit&u7=0&u3=USD&u4=KRW&u8=down&u2=1";

        try {
            String response = restTemplate.getForObject(URL, String.class);
            JsonNode root = objectMapper.readTree(response);

            JsonNode countryArray = root.get("country");
            if (countryArray != null && countryArray.size() >= 2) {
                String usd = countryArray.get(0).get("subValue").asText();
                String krw = countryArray.get(1).get("subValue").asText();
                return Map.of("usd", usd,  "krw", krw);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Map.of("usd", "N/A",  "krw", "N/A");
    }

}
