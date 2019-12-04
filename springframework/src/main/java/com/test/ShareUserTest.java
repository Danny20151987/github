//import com.test.ShareUserUtil;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.springframework.util.Base64Utils;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by yezhangyuan on 2018-09-27.
// *
// * @author yezhangyuan
// */
//public class ShareUserTest {
//
//    //pm
//    private static String privateKey = "****";
//    //pm公钥
//    private static String unpublicKey = "***";
//
//    private static String bindCardUrl = "http://101.231.204.80:8086/app/access/bank/bindCard";
//
//    private static ObjectMapper objectMapper = new ObjectMapper();
//
//    public static void main(String[] args) throws Exception {
//
//        bindCard();
//
//    }
//
//    public static String sign() throws Exception {
//        String sn = "";
//        Map<String,String> map = new HashMap<>();
//        map.put("appId","5949221470");
//        map.put("indUsrId","hs12346");
//        map.put("nonceStr","78TaA2xIMAUDiotK");
//        map.put("chnl","1");
//        map.put("timestamp",("" + System.currentTimeMillis()/1000));
//
//        String waitForSign = MyHttpClient.coverMap2String(map);
//        System.out.println("待签名：" + waitForSign);
//
//        String sign = ShareUserUtil.sign(waitForSign, privateKey);
//        map.put("signature", sign);
//        return  sign;
//    }
//
//
//
//
//    public static void bindCard() throws Exception {
//        String sn = "";
//        Map<String,String> map = new HashMap<>();
//        map.put("appId","***");
//        map.put("userId","***");
//        map.put("realNm","***");//s
//        map.put("accType","01");//s
//        map.put("cardNo","***");//s
//        map.put("registerMobile","***");//s
//        map.put("reserveMobile","***");
//        map.put("certType","01");//s
//        map.put("certifId","***");//s
//        map.put("nonceStr",ShareUserUtil.createNonceStr(16));
//        map.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
//
//        String waitForSign = MyHttpClient.coverMap2String(map);
//        System.out.println("待签名：" + waitForSign);
//
//        String sign = ShareUserUtil.sign(waitForSign, bankPrivateKey);
//        map.put("signature", sign);
//
//        System.out.println("verify: " + ShareUserUtil.signValidate(waitForSign, sign, bankPublicKey));
//
//
//        String desKey = ShareUserUtil.get3Des();//3des密钥
//
//        map.put("accType",ShareUserUtil.getEncryptedValue("01",desKey));//s
//        map.put("cardNo",ShareUserUtil.getEncryptedValue("***",desKey));//s
//        map.put("reserveMobile",ShareUserUtil.getEncryptedValue("***",desKey));//s
//        map.put("registerMobile",ShareUserUtil.getEncryptedValue("***",desKey));//s
//        map.put("realNm",ShareUserUtil.getEncryptedValue("***",desKey));//s
//        map.put("certifId",ShareUserUtil.getEncryptedValue("***",desKey));//s
//        map.put("certType",ShareUserUtil.getEncryptedValue("01",desKey));//s
//
//        map.put("symmetricKey", Base64Utils.encodeToString(ShareUserUtil.encryptRSA(unpublicKey,ShareUserUtil.hexToBytes(desKey))));
//
//        System.out.println("symmetricKey:" + Base64Utils.encodeToString(ShareUserUtil.encryptRSA(unpublicKey,ShareUserUtil.hexToBytes(desKey))));
//        sendPost(bindCardUrl,map);
//
//    }
//
//    public static void getDecryptedValue() throws Exception {
//        System.out.println(ShareUserUtil.getDecryptedValue("tA398ODUSgsKIaLxAgB7hA==","aec761041f852916fdeff7ea91b591e0aec761041f852916"));
//    }
//
//    public static void getDecryptRSA() throws Exception {
//        System.out.println(Base64Utils.encodeToString(ShareUserUtil.decryptRSA(privateKey,ShareUserUtil.hexToBytes("UGaTs4GMd8OsBKx48gn0Cow6OmBKq9ARZzc+LMLOOiybcX0KYdOOoz7AVo963zCx6GDbyOrmXeRbtKIghoMvTpm42nvuTPIxrZ/KXHIRynm1hP1wT++o7zsRyPz5DAso7l8Jat4/ee+FG8TWcXhmbf9woPu7zSVKBaAp+HAHowE="))));
//
//    }
//
//    public static void decryptRSA() throws Exception {
//        String desKey = ShareUserUtil.get3Des();
//        System.out.println(desKey);
//        System.out.println(ShareUserUtil.getEncryptedValue("wade",desKey));
//        System.out.println(ShareUserUtil.getDecryptedValue("zEQMDyxcDWU=","d5191604388585156e67cbb3807a4c58d519160438858515"));
//    }
//    /**
//     * 发送請求
//     * @param map
//     * @return
//     * @throws IOException
//     */
//    public static String sendPost(String targetUrl,Map<String,String> map) throws IOException {
//        String sn = "";
//        MyHttpClient client = new MyHttpClient(targetUrl);
//        String params = objectMapper.writeValueAsString(map);
//        System.out.println("请求参数：:" + params);
//
//        String res = client.excuteResult(params);
//        System.out.println(res);
//        ShareUserResponse shareUserResponse = objectMapper.readValue(res, ShareUserResponse.class);
//        if(shareUserResponse.getResp().equals("00")){
//            if (shareUserResponse.getParams() != null){
//                sn = shareUserResponse.getParams().getSn();
//            }
//        }else{
//            System.out.println(shareUserResponse.getMsg());
//        }
//        return sn;
//    }
//
//}
