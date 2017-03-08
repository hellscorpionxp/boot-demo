package com.example;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BootDemoApplication.class)
@WebAppConfiguration
@Transactional
public class BootDemoApplicationTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private String expectedJson;

    @Before
    public void setUp() throws JsonProcessingException {
        // Person p1 = new Person(null, "wy", 30, null);
        // Person p2 = new Person(null, "example", 31, null);
        // personRepository.save(p1);
        // personRepository.save(p2);
        expectedJson = obj2Json(personRepository.findAll());
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testPersonController() throws Exception {
        String uri = "/person";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();

        Assert.assertEquals("错误，正确的返回值为200", 200, status);
        Assert.assertEquals("错误，返回值与预期值不一致", expectedJson, content);
    }

    private String obj2Json(Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }

}
