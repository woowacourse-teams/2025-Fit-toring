package fittoring.domain.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

public class Notification {

    @Getter
    private final Map<String, String> data;

    public Notification(String title, String body) {
        this.data = new HashMap<>();
        data.put("title", title);
        data.put("body", body);
    }

    public void putData(String key, String value) {
        data.put(key, value);
    }

    public void setImageNotificationBody(){
        data.put("title", "이미지를 보냈습니다.");
    }
}
