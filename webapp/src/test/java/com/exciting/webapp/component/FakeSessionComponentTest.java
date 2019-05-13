package com.exciting.webapp.component;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FakeSessionComponentTest {

    @Resource
    private FakeSessionComponent fakeSessionComponent;

    @Before
    public void setUp() throws Exception {
        String key = "testSession";
        fakeSessionComponent.setAttribute(key,"test",30L);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void setAttribute() {
        String key = "testSession";
        fakeSessionComponent.setAttribute(key,"test",30L);
    }

    @Test
    public void getAttribute() {
        String testSession = fakeSessionComponent.getAttribute("testSession");
        System.out.println(testSession);
    }

    @Test
    public void clean() {
    }
}