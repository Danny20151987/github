package com.constellation.cancer.commons;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/2 5:38 PM
 */
public class Constants {


    public static class ResponseType {
        public static final String SUCCESS = "S";
        public static final String EXCEPTION = "E";
        public static final String FAULT = "F";
        public static final String INCONFORMITY = "I";

    }

    public static class ResponseCode {
        public static final String SUCCESS = "00000000";
        public static final String SUCCESS_MSG = "交易成功";
        public static final String EXCEPTION = "99999999";
        public static final String CONNECT_ERROR = "DAPGWY01";
        public static final String CONNECT_ERROR_MSG = "通讯异常";
        public static final String RPC_TIMEOUT = "PRPCCN01";
        public static final String PRP_EXCEPTION = "PRPCCN02";
        public static final String PRP_OTHEREXCEPTION = "PRPCCN03";
        public static final String RPC_NOT_STARTED = "PRPCCN04";
        public static final String DATA_ACCESS_EXCEPTION = "PDBA0001";

    }

    public static class ServiceControl {
        public static final String PRINT_ARGUMENT = "IS_PRINTARGUMENT";
        public static final String TRANS_RECORED = "IS_TRANSRECORED";
        public static final String TRACE_ENABLE = "IS_TRACE_ENABLE";

    }

    public static class MDC {
        public static final String ID = "_ID_";
        public static final String STEP = "_STEP_";

    }


}
