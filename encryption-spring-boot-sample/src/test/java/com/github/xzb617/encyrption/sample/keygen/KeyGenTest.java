package com.github.xzb617.encyrption.sample.keygen;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;

/**
 * @date 2024/03/26 16:41
 **/
public class KeyGenTest {


    @Test
    public void sm2(){
        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        byte[] privateKey = pair.getPrivate().getEncoded();
        byte[] publicKey = pair.getPublic().getEncoded();

        String s = Base64.encodeBase64String(privateKey);
        String s2 = Base64.encodeBase64String(publicKey);

        System.out.println(s);
        System.out.println(s2);
    }

    @Test
    public void aes(){
//        SecretKey secretKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), 128);
//        System.out.println(Base64.encodeBase64String(secretKey.getEncoded()));

        System.out.println(RandomUtil.randomString(16));
        System.out.println(RandomUtil.randomString(16));

    }

}
