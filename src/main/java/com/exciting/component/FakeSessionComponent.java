package com.exciting.component;

import com.exciting.entity.FakeSession;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class FakeSessionComponent {
    private volatile static Map<String,FakeSession> session =new HashMap<>();

    public void setAttribute(String key, String value, long expireSecond) {
        clean();
        synchronized (FakeSessionComponent.class) {
            LocalDateTime nowTime = LocalDateTime.now();
            FakeSession fakeSession = new FakeSession();
            fakeSession.setCreateTime(nowTime);
            fakeSession.setExpireTime(nowTime.plusSeconds(expireSecond));
            fakeSession.setSessionValue(value);
            session.put(key,fakeSession);
        }

    }

    public String getAttribute(String key) {
        FakeSession fakeSession = session.get(key);
        if( fakeSession==null || LocalDateTime.now().compareTo(fakeSession.getExpireTime()) > 0 ){
            return null;
        }else{
            return fakeSession.getSessionValue();
        }

    }

    public void clean(){
        synchronized (FakeSessionComponent.class) {
            Iterator it = session.keySet().iterator();
            while (it.hasNext()) {
                FakeSession next = (FakeSession) it.next();
                if (LocalDateTime.now().compareTo(next.getExpireTime()) > 0) {
                    it.remove();
                }

            }
        }
    }
}