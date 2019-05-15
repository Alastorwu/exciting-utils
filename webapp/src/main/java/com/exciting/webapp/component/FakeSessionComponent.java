package com.exciting.webapp.component;

import com.exciting.common.entity.FakeSession;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FakeSessionComponent {
    private volatile static Map<String, FakeSession> session =new ConcurrentHashMap<>();

    public void setAttribute(String key, String value, long expireSecond) {
        clean();
        LocalDateTime nowTime = LocalDateTime.now();
        FakeSession fakeSession = new FakeSession();
        fakeSession.setCreateTime(nowTime);
        fakeSession.setExpireTime(nowTime.plusSeconds(expireSecond));
        fakeSession.setSessionValue(value);
        session.put(key,fakeSession);

    }

    public String getAttribute(String key) {
        FakeSession fakeSession = session.get(key);
        if( fakeSession==null || LocalDateTime.now().compareTo(fakeSession.getExpireTime()) > 0 ){
            return null;
        }else{
            return fakeSession.getSessionValue();
        }

    }

    private void clean(){
        session.entrySet().removeIf(item ->
                LocalDateTime.now().compareTo(item.getValue().getExpireTime()) > 0);
    }
}
