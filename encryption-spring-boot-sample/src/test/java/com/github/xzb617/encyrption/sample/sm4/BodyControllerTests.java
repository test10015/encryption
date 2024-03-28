package com.github.xzb617.encyrption.sample.sm4;

import com.alibaba.fastjson.JSON;
import com.github.xzb617.encryption.autoconfigure.constant.Algorithm;
import com.github.xzb617.encryption.autoconfigure.encryptor.symmetric.Sm4ArgumentEncryptor;
import com.github.xzb617.encryption.autoconfigure.mock.MockEncryption;
import com.github.xzb617.encryption.autoconfigure.serializer.EncryptionJsonSerializer;
import com.github.xzb617.encyrption.sample.dto.ModelEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Body测试
 * @author xzb617
 * @date 2022/5/11 14:39
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class BodyControllerTests {

    private MockMvc mockMvc;
    private MockEncryption mockEncryption;

    @Resource
    private WebApplicationContext webApplicationContext;
    @Resource
    private EncryptionJsonSerializer jsonSerializer;

    @Before
    public void init() {
        // 实例化
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.mockEncryption = MockEncryption.configurableEnvironmentContextSetup(new Sm4ArgumentEncryptor(), (ConfigurableEnvironment) webApplicationContext.getEnvironment());
        // 判断是否为用例要求的算法
        if (!Algorithm.SM4.equals(this.mockEncryption.getAlgorithm())) {
            throw new IllegalArgumentException("本测试用例要求采用算法模式为 AES，您尚未配置该算法");
        }
    }

    @Test
    public void sm4Test() throws Exception {
        // 生成加密后的值
        String jsonData = serializeModelEntity();
        String content  = mockEncryption.encryptValue(jsonData);

        // 模拟请求
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders
                .post("/body/encryptByHeader")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("Encryption", "true")
                // 添加body
                .content(content)
        );

        // 解决返回值中文乱码问题
        actions.andReturn().getResponse().setCharacterEncoding("utf-8");
        MvcResult mvcResult = actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();

        Assert.assertEquals(jsonData, mockEncryption.decryptValue(JSON.parseObject(mvcResult.getResponse().getContentAsString()).getString("data")));
    }


    /**
     * 模拟一个请求实体的序列化字符串
     * @return
     */
    private String serializeModelEntity() {
        ModelEntity entity = new ModelEntity();
        entity.setIntKey(1);
        entity.setLongKey(1658613L);
        entity.setStrKey("这是字符串参数");
        entity.setDateKey(new Date());
        return jsonSerializer.serialize(entity);
    }
}