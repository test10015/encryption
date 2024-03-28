package com.github.xzb617.encyrption.sample;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @date 2024/03/22 16:14
 **/
public class DecryptionTest {
    static String secret = "NV5HSaXQ/AiF5Ca+jSiJlA==";

    static Cipher deAes = getCBCModeCipherInstance("AES", "AES/CBC/PKCS5Padding", secret, "1234567890123456", Cipher.DECRYPT_MODE);

    static Cipher enAes = getCBCModeCipherInstance("AES", "AES/CBC/PKCS5Padding", secret, "1234567890123456", Cipher.DECRYPT_MODE);

    private static Cipher getCBCModeCipherInstance(String alg, String padding, String secret, String iv, int cipherMode) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), alg);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(padding);
            cipher.init(cipherMode, secretKeySpec, ivParameterSpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        return cipher;
    }

    @Test
    public void decodeAes() throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException {
        String s = "baAgv5edMn5j1v8SGlyJZAx5rOTnmp7u0jDo3SAZc52hbqJ7kD/UIBGgCZYfGjdwSRDPvM24GTqE1N1cZN+hzXl94CYXSCXZy3ZsMbFp8UtzMBaljyVzqgfNxlbppAia";

        byte[] bytes = deAes.doFinal(Base64.decodeBase64(s));
        System.out.println(new String(bytes));

        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        System.out.println(cn.hutool.core.codec.Base64.encode(key));
    }


    @Test
    public void testEnAndDe(){


    }

    @Test
    public void testEnAndDeByHutool() {
        String content = "test中文";

        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();

        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, new String("NV5HSaXQ/AiF5Ca+jSiJlA==").getBytes());

        byte[] encrypt = aes.encrypt(content);
        byte[] decrypt = aes.decrypt(encrypt);

        String encryptHex = aes.encryptBase64(content);
        String decryptStr = aes.decryptStr(encryptHex);
    }


}
