package com.test;

import org.springframework.util.Base64Utils;

import javax.xml.bind.DatatypeConverter;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/1 5:58 PM
 */
public class Test {

    public static void main(String[] args) throws Exception{
        String desKey ="11111111111111111111111111111111";
        String unpublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCq1YK3f3IXnqyXD1bXBxclOh/E9CNcRPdXNeX/QpZewNRnaTqTJ1B+JzJyVumhu9DIlEPvdF7OEYky7y5xEx6RNqUnCUY7sLO9O5wJ70G2k25VzZO0jtDssxXnwpwR9s37W6qCSBpw2/0v686L2oL2LIz8Q8d0K6wayOACzGp1hQIDAQAB";
        String result = Base64Utils.encodeToString(ShareUserUtil.encryptRSA(unpublicKey,ShareUserUtil.hexToBytes(desKey)));
        System.out.println(result);

        String prikey="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKrVgrd/cheerJcPVtcHFyU6H8T0I1xE91c15f9Cll7A1GdpOpMnUH4nMnJW6aG70MiUQ+90Xs4RiTLvLnETHpE2pScJRjuws707nAnvQbaTblXNk7SO0OyzFefCnBH2zftbqoJIGnDb/S/rzovagvYsjPxDx3QrrBrI4ALManWFAgMBAAECgYARpbC1J/MvQXLuv+TmnZLkg5aYj+e/Nliep7C0p7pE24KVlCc64ErOXVo2uYe072Pn9Hj4GXtZqjIUEuUnRSRZDEhqBVZ+s9lf8qhrRtM4XcF9CtDXvTdMI8yqJeQsdMn2A6uQyplks7ipaLPdOP+s2fbXvf7SRhgQ7j//hxslgQJBAO6xltqnPksfC7t3RKytQHFVJ/9+vVV495hHVWvBfToBfFeMqFRygm3m7nYjELoiVIhe3dI2g2uter7GUDbxg1UCQQC3OF98uZv5qDaHUHxiLG2gQD+Q50P75QcQCZAzX0tq9Cuc0zB2tkBeTga4jWJJtj9qHA4Tih+uIjUDowvIa4lxAkAVmfbIBPirPb0HEy0g33EEkKtVAPhLgu4NVu9RGCqoFDhXiAA33EEXhx2vjU7XOYIi8Urr2kkcdo+0MihSQJsdAkBWH6OS51SQGCf5lzXAmA235Dt9C5iDUXbubPBhlYvH7JqLjyIayzSan6a9HYh0CbzpTeK4AJc/rUiqOvwQ66uhAkAlxAGKhSZNd3cZnXeEWhzbRlgUDiVUGWcalfyVj5vmYfU0Y2FnLyJ/oBuq0Z+173nb9iO8ZI+DgXZSgnBYkxEk";

        System.out.println(ShareUserUtil.bytesToHex(ShareUserUtil.decryptRSA(prikey, DatatypeConverter.parseBase64Binary(result))));


    }
}
