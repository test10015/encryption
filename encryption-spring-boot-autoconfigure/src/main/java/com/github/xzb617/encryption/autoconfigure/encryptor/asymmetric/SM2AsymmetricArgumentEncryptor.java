package com.github.xzb617.encryption.autoconfigure.encryptor.asymmetric;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.github.xzb617.encryption.autoconfigure.constant.AsymmetricConfigKey;
import com.github.xzb617.encryption.autoconfigure.encryptor.AbstractArgumentEncryptor;
import com.github.xzb617.encryption.autoconfigure.envirs.AlgorithmEnvironments;
import com.github.xzb617.encryption.autoconfigure.envirs.RequestHeaders;
import com.github.xzb617.encryption.autoconfigure.envirs.ResponseHeaders;
import org.apache.commons.codec.binary.Base64;

/**
 * @date 2024/03/26 16:29
 **/
public class SM2AsymmetricArgumentEncryptor extends AbstractArgumentEncryptor {

    private SM2 sm2;

    @Override
    protected void initConfigRespective(AlgorithmEnvironments environments) throws Exception {
        String strPublicKey = environments.getAlgorithmConfigElseThrow(AsymmetricConfigKey.PUBLIC_KEY);
        String strPrivateKey = environments.getAlgorithmConfigElseThrow(AsymmetricConfigKey.PRIVATE_KEY);
        // 每个算法内部配置
        this.sm2 = SmUtil.sm2(Base64.decodeBase64(strPrivateKey), Base64.decodeBase64(strPublicKey));
    }

    @Override
    protected String encryptInternal(String plainText, ResponseHeaders responseHeaders) throws Exception {
        return sm2.encryptBase64(plainText, KeyType.PublicKey);
//        byte[] var1 = plainText.getBytes(charset);
//        byte[] encrypt = sm2.encrypt(var1, KeyType.PublicKey);
//        return Base64.encodeBase64String(encrypt);
    }

    @Override
    protected String decryptInternal(String cipherText, RequestHeaders requestHeaders) throws Exception {
        byte[] var1 = Base64.decodeBase64(cipherText);
        byte[] decrypt = sm2.decrypt(var1, KeyType.PrivateKey);
        return StrUtil.utf8Str(decrypt);
    }
}
