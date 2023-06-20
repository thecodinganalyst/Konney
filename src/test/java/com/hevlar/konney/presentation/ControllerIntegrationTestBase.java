package com.hevlar.konney.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class ControllerIntegrationTestBase<T> {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    protected MvcResult post(T t, String url) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(t)))
                .andReturn();
    }

    protected MvcResult postIfNotExist(T t, String postUrl, String getUrl) throws Exception {
        MvcResult result = get(getUrl);
        if(result.getResponse().getStatus() != HttpStatus.OK.value()){
            return post(t, postUrl);
        }
        return null;
    }

    protected MvcResult get(String url) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get(url)).andReturn();
    }

    protected MvcResult put(T t, String url) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(t)))
                .andReturn();
    }

    protected void assertHttpStatus(MvcResult result, HttpStatus status){
        assertThat(result.getResponse().getStatus(), is(status.value()));
    }

    protected T getResultObject(MvcResult result, Class<T> classRef ) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsString(), classRef);
    }

    protected List<T> getResultObjectList(MvcResult result, Class<T> classRef ) throws Exception {
        ObjectReader reader = objectMapper.readerForListOf(classRef);
        return reader.readValue(result.getResponse().getContentAsString());
    }



}
