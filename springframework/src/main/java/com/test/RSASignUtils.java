package com.test;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA签名验签处理工具类。
 * @author hdj
 * @since 2019/10/29
 */
public class RSASignUtils {

    private final static String KEY_ALGORITHM = "RSA";

    private final static String ALGORITHM = "SHA256WithRSA";

    private final static String PRIVATE_KEY = "privateKey";

    private final static String PUBLIC_KEY = "publicKey";

    private static final String Algorithm = KEY_ALGORITHM + "/ECB/PKCS1Padding";

    private final static int KEY_LENGTH = 1024;

    /** 日志记录。 */

    /**
     * RSA公钥字符串加签
     * @param publicKeyStr
     * @param data
     * @param sign
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static boolean verifyWithSha256(String publicKeyStr, byte[] data, String sign) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey publicKey = getPublickey(publicKeyStr);
        boolean correct = false;
        try {
            Signature st = Signature.getInstance(ALGORITHM);
            st.initVerify(publicKey);
            st.update(data);
            correct = st.verify(DatatypeConverter.parseBase64Binary(sign));
        } catch (Exception e) {
            throw new RuntimeException("SHA256WithRSA验证签名异常。");
        }
        return correct;
    }

    /**
     * 根据字符串获取公钥
     *
     * @param publicKeyStr
     * @return
     * @throws InvalidKeySpecException
     */
    public static PublicKey getPublickey(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = DatatypeConverter.parseBase64Binary(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 根据字符串获取私钥
     *
     * @param privateKeyStr
     * @return
     */
    public static PrivateKey getPrivateKey(String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = DatatypeConverter.parseBase64Binary(privateKeyStr);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        return privateKey;
    }


    /**
     * SHA256摘要，RSA私钥加签。
     * @param data 消息数据。
     * @return 签名字节数组。
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static String signWithSha256(String privateKeyStr, byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] signBytes = null;
        PrivateKey privateKey = getPrivateKey(privateKeyStr);
        try {
            Signature st = Signature.getInstance(ALGORITHM);
            st.initSign(privateKey);
            st.update(data);
            signBytes = st.sign();
        } catch (Exception e) {
            throw new RuntimeException("SHA256WithRSA加签异常。");
        }
        return DatatypeConverter.printBase64Binary(signBytes);
    }

    /**
     *  RSA公钥加密
     * @param str
     * @param publicKeyStr
     * @return
     * @throws Exception
     */
    public static String encrypt(byte [] str, String publicKeyStr) throws Exception{

        PublicKey publicKey = getPublickey(publicKeyStr);
        //RSA加密
        Cipher cipher = Cipher.getInstance(Algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        String outStr = DatatypeConverter.printBase64Binary(cipher.doFinal(str));
        return outStr;
    }


    /**
     * RSA私钥解密
     *
     * @param str
     *            加密字符串
     * @return 铭文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKeyStr) throws Exception{

        PrivateKey privateKey =  getPrivateKey(privateKeyStr);
        //64位解码加密后的字符串
        byte[] inputByte = DatatypeConverter.parseBase64Binary(str);
        //RSA解密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }


    public static void main(String [] strs) throws NoSuchAlgorithmException{
        Map<String, String> keyMap = getKeyPair() ;
        System.out.println("PublicKey---"+keyMap.get(PUBLIC_KEY));
        System.out.println("PublicKey---"+keyMap.get(PRIVATE_KEY));

    }

    private static Map<String, String> getKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = null;
        generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //初始化秘钥size，最小为512
        generator.initialize(KEY_LENGTH);
        KeyPair keyPair = generator.generateKeyPair();
        String privateKey = DatatypeConverter.printBase64Binary(keyPair.getPrivate().getEncoded());
        String publicKey = DatatypeConverter.printBase64Binary(keyPair.getPublic().getEncoded());
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put(PRIVATE_KEY, privateKey);
        keyMap.put(PUBLIC_KEY, publicKey);
        return keyMap;
    }

}
