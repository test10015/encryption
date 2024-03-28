package com.github.xzb617.encyrption.sample.aes;

import com.alibaba.fastjson.JSON;
import com.github.xzb617.encryption.autoconfigure.serializer.EncryptionJsonSerializer;
import com.github.xzb617.encyrption.sample.dto.ModelEntity;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
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
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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
public class AesOriginalBodyControllerTests {

    public static final String UTF_8 = "UTF-8";

    private MockMvc mockMvc;

    private Cipher enAes;

    private Cipher deAes;


    @Resource
    private WebApplicationContext webApplicationContext;
    @Resource
    private EncryptionJsonSerializer jsonSerializer;

    @Before
    public void init() {
        // 实例化
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        try {
            this.enAes = getCBCModeCipherInstance("AES", "AES/CBC/PKCS5Padding", "ABCDEFGHIJKL0123", "1234567890123456", Cipher.ENCRYPT_MODE);
            this.deAes = getCBCModeCipherInstance("AES", "AES/CBC/PKCS5Padding", "ABCDEFGHIJKL0123", "1234567890123456", Cipher.DECRYPT_MODE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Cipher getCBCModeCipherInstance(String alg, String padding, String secret, String iv, int cipherMode) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), alg);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        Cipher cipher = Cipher.getInstance(padding);
        cipher.init(cipherMode, secretKeySpec, ivParameterSpec);
        return cipher;
    }


    @Test
    public void mock() throws Exception {
        // 生成加密后的值
//        String jsonData = serializeModelEntity();
        String jsonData = "{\"longKey\":1658613,\"strKey\":\"这是字符串参数\",\"intKey\":1,\"dateKey\":\"2024-03-20 10:32:26\"}";
//        String content  = Base64.        return Base64.encodeBase64String(enAes.doFinal(plainText.getBytes(UTF_8)));(enAes.doFinal(jsonData.getBytes(UTF_8)));
        String content  = Base64.encodeBase64String(enAes.doFinal(jsonData.getBytes(UTF_8)));

        // 模拟请求
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders
                .post("/body/index")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(content)
        );

        // 解决返回值中文乱码问题
        actions.andReturn().getResponse().setCharacterEncoding("utf-8");
        MvcResult mvcResult = actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String data = JSON.parseObject(response.getContentAsString()).getString("data");

        String body = new String(deAes.doFinal(Base64.decodeBase64(data)));

        System.out.println("响应:" + body);
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
