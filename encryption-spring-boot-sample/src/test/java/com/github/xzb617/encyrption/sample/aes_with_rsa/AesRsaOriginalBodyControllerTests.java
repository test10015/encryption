package com.github.xzb617.encyrption.sample.aes_with_rsa;

import com.alibaba.fastjson.JSON;
import com.github.xzb617.encryption.autoconfigure.serializer.EncryptionJsonSerializer;
import com.github.xzb617.encryption.autoconfigure.utils.RsaUtil;
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
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Body测试
 *
 * @author xzb617
 * @date 2022/5/11 14:39
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AesRsaOriginalBodyControllerTests {
    public static final String UTF_8 = "UTF-8";
    private MockMvc mockMvc;

    @Resource
    private WebApplicationContext webApplicationContext;
    @Resource
    private EncryptionJsonSerializer jsonSerializer;


    @Before
    public void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    static Cipher enAes;
//    static Cipher deAes;
//    static Cipher publicEn;
    static Cipher privateEn;
    /**
     * AES 秘钥
     */
    public static final String SECRET_VALUE = "ABCDEFGHIJKL0123";
    public static final String IV = "1234567890123456";

    /**
     * 公钥
     */
    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5jqX3qcr0xHfYvb1xh7Th4BzOg8hFChgpp4Zcb1LTcIXiCN1FYFpZkMHbwtZzRY4M+ZLSMAKx3ExNc1XLAQsoErHIzt3RXr1PoOt+vN/YeV7r2D55LOJh3HJBT1xhf2B9Q/EAQfnYoMQ68o5NV34m3nMPtRFa5D01b9DD8i/xTAfUfWXKJ4XklFxoWQ6f3ltguZlqfsNeXdwmbcukm9NVY83/R8KJNtkelqMDYkEB0nPvM+Jxjp+BJ54Y6cG904V57Tswv/7OZg0AWOqt3TzLK3gBbpYINLIgxotwY0lFwz3NfJyYs9xmp4ZM3/+wlgbbGywz3K+9MDOziwN/vWuIwIDAQAB";

    /**
     * 私钥
     */
    public static final String PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDmOpfepyvTEd9i9vXGHtOHgHM6DyEUKGCmnhlxvUtNwheII3UVgWlmQwdvC1nNFjgz5ktIwArHcTE1zVcsBCygSscjO3dFevU+g636839h5XuvYPnks4mHcckFPXGF/YH1D8QBB+digxDryjk1Xfibecw+1EVrkPTVv0MPyL/FMB9R9ZconheSUXGhZDp/eW2C5mWp+w15d3CZty6Sb01Vjzf9Hwok22R6WowNiQQHSc+8z4nGOn4Ennhjpwb3ThXntOzC//s5mDQBY6q3dPMsreAFulgg0siDGi3BjSUXDPc18nJiz3Ganhkzf/7CWBtsbLDPcr70wM7OLA3+9a4jAgMBAAECggEABRDQ+qMvAavpAGJysfNHsDmRT3u5vJnO1puv76K8n29f2Sz+jISWbxuWdEkJpsuQXQP6MBWZpx3EeWyVOHC3EqfSjeHnE+5Kqx92moj1CpBkEk3N8cxJgGNuiuD5OHuFeoDoTSWBX9eGbcm7TINOzwz1A4TkKiO8X1+f+B7wqUQFHN7gzVzPp8bKlM/YnY9f7zR8/dXpi8fpu3hW1U75OF7nxZCxfEN6YvSbkzCLUuOqnTSLm4kx/A7QVD/wXZlv08AmFlD5l1k+G+uzzHi+5+LaZRKUCnAt0I3wzNDim1O4ytQs1q5Sh0cc5vDsxF4R8PyO3RPTVQ/r8x9JXaYgUQKBgQD3qUzDis92xg3vCPWbAQ2NRPklzpQw3rFhobTiRafRHXVegwOLKG7tQ95s6KAmgLY+UzkQATXg5XgFJ6b4S9xBCJvJN/o1k9+y+PlOVjMddnL7ZcOuA0nEMY2lj+mmAFul7TQGsndXFnjPwTXDpL+EvbFjChMSEkqiWmCVtrRpTQKBgQDt+wkbS9s6ymi6aSIH/NNJ8pQYCFWSzlqPnGPi6K+aKSqDHP+dWf03OYD/lGDJCFp9urD2BurHgbc/NNn01QWjqXzwr8KugsbNhRNrneBNnFpxmQtqYPVEGX4Wy2/Cg0iNjAiwEFwmTeKA05uXjrZb6N+L+LxGHS3qlJib+js9LwKBgA0b9AlBtruVvjUR51Y+FwaMSRfcOHHhx8fgNF/pyflCsuy+yJg8GqsKdaKUKa4AECV6aEHVnpF59AFp2Oe5tD3pA74B7YeafTPPA/tGiswbcfimqVXzrJrq+JFz7a0wxakhxig4mCKO+PQeSQdDGDQhilswtFO3jiXL3OLv2drdAoGACKRxNDiRAZWQMBTZU4ju82SH1EeZM/eiekyno/nnRqXwEUrgTYqTE4pXEPEGgsZ24tIA0y51IEGpsfXtZGLIDaV+EA+R9lxxc809Y08ccjUXY8C3FWnn/k0esx04Ncwmul03g41Ui1+QtjT5FYvtO3E9jQu/apxsqnQzBpcTx6kCgYB3HlWpJzevkl2m0I+YlxDHPR0ec/MBM/cr7x3ebjm+2sbYmPkza+AfclZ1mc5yxza/WDixLbsT3TrffYV7zKHP5Ggq0k5jZlIiVkU9GVUIb0BApyMNXCvEmV9esQcmIKzozxaC3EqhM0cy4j8Vi2j5CvZmlwMvBeOSHjeT+MSpTw==";

    /**
     * secret key header name
     */
    public static final String SECRET_KEY_IN_HEADER = "txdata-secret";


    static {
        try {
            enAes = getCBCModeCipherInstance("AES", "AES/CBC/PKCS5Padding", SECRET_VALUE, IV, Cipher.ENCRYPT_MODE);
//            deAes = getCBCModeCipherInstance("AES", "AES/CBC/PKCS5Padding", SECRET_VALUE, IV, Cipher.DECRYPT_MODE);
//            publicEn = RsaUtil.getCipher(Cipher.DECRYPT_MODE, RsaUtil.string2PublicKey(PUBLIC_KEY));
            privateEn = RsaUtil.getCipher(Cipher.ENCRYPT_MODE, RsaUtil.string2PrivateKey(PRIVATE_KEY));
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


    private String encryptBody(String plainText) throws Exception {
        return Base64.encodeBase64String(enAes.doFinal(plainText.getBytes(UTF_8)));
    }

    private static String encryptHeader() throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return Base64.encodeBase64String(privateEn.doFinal(SECRET_VALUE.getBytes(UTF_8)));
    }

    private String getSerializeModelEntity() {
        ModelEntity entity = new ModelEntity();
        entity.setIntKey(1);
        entity.setLongKey(1658613L);
        entity.setStrKey("这是字符串参数");
        entity.setDateKey(new Date());
        return jsonSerializer.serialize(entity);
    }


    @Test
    public void mock() throws Exception {
        String jsonEntity = getSerializeModelEntity();

        String secretValueInValue = encryptHeader();
        String encryptedBody = encryptBody(jsonEntity);

        // 模拟请求
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders
                .post("/body/index")
                .characterEncoding(UTF_8)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(SECRET_KEY_IN_HEADER, secretValueInValue)
                .content(encryptedBody)
        );

        // 解决返回值中文乱码问题
        actions.andReturn().getResponse().setCharacterEncoding("utf-8");
        MvcResult mvcResult = actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String data = JSON.parseObject(response.getContentAsString()).getString("data");

        String responseSecretKey = response.getHeader(SECRET_KEY_IN_HEADER);
        String responseSecret = new String(privateEn.doFinal(Base64.decodeBase64(responseSecretKey)));

        Cipher responseAesDe = getCBCModeCipherInstance("AES", "AES/CBC/PKCS5Padding", responseSecret, IV, Cipher.DECRYPT_MODE);

        String body = new String(responseAesDe.doFinal(Base64.decodeBase64(data)));

        System.out.println("响应:" + body);
    }


}
