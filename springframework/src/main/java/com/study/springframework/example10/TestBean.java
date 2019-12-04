package com.study.springframework.example10;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/24 11:57 AM
 */
public class TestBean{
    private String testStr = "testStr";

    public void test(){
        System.out.println(testStr);
//        throw new Exception();
    }

    public String getTestStr() {
        return testStr;
    }

    public void setTestStr(String testStr) {
        this.testStr = testStr;
    }
}
