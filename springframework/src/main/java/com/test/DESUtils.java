package com.test;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.springframework.util.Base64Utils;

import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static com.test.ShareUserUtil.hexToBytes;


/**
 * @author cerofe
 * @version $Revision: 1.1 $ 建立日期 2013-12-11
 */
public class DESUtils
{
    private static final String KEY_ALGORITHM = "DESede";
    private static final String Algorithm = KEY_ALGORITHM + "/ECB/PKCS5Padding";

    /**
     * 生产3DES密钥
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String gen3DesKey(long seed) throws NoSuchAlgorithmException{
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(seed);
        keyGenerator.init(112, random);
        Key secretKey = keyGenerator.generateKey();

        byte[] key = new byte[24];
        System.arraycopy(secretKey.getEncoded(), 0, key, 0, 24);

        return DatatypeConverter.printHexBinary(key);
    }


    /**
     * 根据密钥返回密钥值
     * @param key
     * @return
     */
    public static String getKeyStr(Key key){
        return DatatypeConverter.printBase64Binary(key.getEncoded());
    }

    /**
     * 加密数据，返回base64结果
     * @param data
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String doEncrypt(byte[] data,Key key) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        Cipher cipher = Cipher.getInstance(Algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] result = cipher.doFinal(data);
        return DatatypeConverter.printBase64Binary(result);
    }

    /**
     * 将传入base64密文进行解密
     * @param data
     * @param key
     * @param charSet
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    public static String doDecrypt(String data,Key key,String charSet) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
        Cipher cipher = Cipher.getInstance(Algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte [] result = cipher.doFinal(DatatypeConverter.parseBase64Binary(data));
        return new String(result,charSet);
    }

    static class KeyGen {

        private static final int DEFAULT_LENGTH = 16;

        public static String keyGen( String hexKey ) {
            validateLength( hexKey.length() );
            hexKey = complement( hexKey );
            return hexKey;
        }

        private static void validateLength( int length ) throws IllegalArgumentException {
            if ( length % DEFAULT_LENGTH != 0 ) {
                throw new IllegalArgumentException( "Length is not a multiple of 8." );
            }
            if ( length / DEFAULT_LENGTH > 3 ) {
                throw new IllegalArgumentException( "Length too long" );
            }
        }

        private static String complement( String hexKey ) {
            final int length = hexKey.length();
            StringBuffer buffer = new StringBuffer( DEFAULT_LENGTH * 3 );
            if ( length == DEFAULT_LENGTH ) {
                buffer.append( hexKey ).append( hexKey ).append( hexKey );
            } else if ( length == DEFAULT_LENGTH * 2 ) {
                buffer.append( hexKey ).append( hexKey.subSequence( 0, DEFAULT_LENGTH ) );
            } else{
                buffer.append( hexKey );
            }
            return buffer.toString();
        }
    }




    public static void main(String []strs) throws Exception{

        String charset = "utf-8";
        String desKey = gen3DesKey(System.currentTimeMillis());
        System.out.println("java生成的3des秘钥为：" + desKey);

        byte [] key = hexToBytes(desKey);
        for(int i=0;i<key.length;i++){
            System.out.print(key[i]+" ");
        }
        System.out.println("");
        byte [] key2 = DatatypeConverter.parseHexBinary(desKey);
        for(int i=0;i<key2.length;i++){
            System.out.print(key2[i]+" ");
        }
        System.out.println("");

        String publickeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClHVqo2xvbR0Kp1L84KE8b3dwzVGJFxkuLk1TQB8TrtHU1opyVM5zNw8lnUXS848nKY0l0a3+OipCrk/PxbggBT18Fsytu/WUI006mmOBeYlo43ALDMB60yxdY9iJiia3AgidF9te9V+F5+DLoyXPHkW09MI/7i9FbagflepwvRwIDAQAB";

        byte[] keyBytes = DatatypeConverter.parseBase64Binary(publickeyStr);

        for(int i=0;i<keyBytes.length;i++){
            System.out.print(keyBytes[i]+" ");
        }
        System.out.println("");

        byte[] encodedKey = Base64Utils.decodeFromString(publickeyStr);
        for(int i=0;i<encodedKey.length;i++){
            System.out.print(encodedKey[i]+" ");
        }

        System.out.println("");

        byte [] xxx = ShareUserUtil.encryptRSA(publickeyStr,key2);

        System.out.println(DatatypeConverter.printBase64Binary(xxx));
        System.out.println(Base64Utils.encodeToString(xxx));

        byte []  yyy = ByteUtils.fromHexString(desKey);
        for(int i=0;i<yyy.length;i++){
            System.out.print(yyy[i]+" ");
        }
        System.out.println("");

        byte []  zzz = DatatypeConverter.parseHexBinary(desKey);
        for(int i=0;i<zzz.length;i++){
            System.out.print(zzz[i]+" ");
        }
        System.out.println("");

        System.out.println(DatatypeConverter.printBase64Binary(yyy));




//        String unPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClHVqo2xvbR0Kp1L84KE8b3dwzVGJFxkuLk1TQB8TrtHU1opyVM5zNw8lnUXS848nKY0l0a3+OipCrk/PxbggBT18Fsytu/WUI006mmOBeYlo43ALDMB60yxdY9iJiia3AgidF9te9V+F5+DLoyXPHkW09MI/7i9FbagflepwvRwIDAQAB";
//        System.out.println("公钥对3DE密钥进行加密：" +RSASignUtils.encrypt(desKey.getEncoded(), unPublicKey));
//
//	    //数据明文
//	    String data = "zhegsjs胡德健jf@##4sfjs";
//	    System.out.println("加密前明文：" + data);
//	    String result = doEncrypt(data.getBytes(charset),desKey);
//	    System.out.println("加密后结果：" + result);
//
//	    String deResult = doDecrypt(result,desKey,charset);
//	    System.out.println("解密后结果：" + deResult);

        //VAwNXptSIvcmhILknzhNZNsMTU/G94WebzT8bJGiU3ubCd8YU/ucRfx51Y91CpJzxjvEL9ecbNGlE2frWR+MAJp/EmBk/02KjuirBeEPivmgiYkxp8kVXHQtfqoLfJvbR+paOIwjyFcSEuwHoqmjRUNBu4ZJ2yDZIaTcTURKtDg=
    }
}

