package info.futureme.abs.example.conf;


/**
 * 
 * @author wujin
 * @version $Id: BizErrorCode.java, v 0.1 2016年1月21日 下午3:16:23 wujin Exp $
 */
public class BizErrorCode extends ErrorCode {
    
    public static class BizCategory extends ErrorCode.Catetory {
        public static final Catetory APPGW = new BizCategory("APPGW");
        public static final Catetory ITSMGW = new BizCategory("ITSMGW");
        public static final Catetory TRACER = new BizCategory("TRACER");
        
        public BizCategory(String name) {
            super(name);
        }
    }
    
    // app gateway error code
    public static final ErrorCode APPGW_COMM_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.APPGW, UNKNOWN, "App Gateway common error: ");
    public static final ErrorCode APPGW_TRACER_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.APPGW, 2, "App Gateway communicate with tracer error: ");
    public static final ErrorCode APPGW_ITSMGW_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.APPGW, 3, "App Gateway communicate with ITSM gateway error: ");
    public static final ErrorCode APPGW_MQ_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.APPGW, 4, "App Gateway MQ error: ");
    public static final ErrorCode APPGW_FILE_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.APPGW, 5, "App Gateway access file error: ");
    public static final ErrorCode APPGW_CACHE_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.APPGW, 6, "App Gateway access cache error: ");
    
    // trace error code
    public static final ErrorCode TRACER_COMM_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.TRACER, UNKNOWN, "Assign Tracer common error: ");
    public static final ErrorCode TRACER_ITSMGW_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.TRACER, 2, "Assign Tracer communicate with ITSM gateway error: ");
    public static final ErrorCode TRACER_WF_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.TRACER, 3, "Assign Tracer access workflow error: ");
    public static final ErrorCode TRACER_DB_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.TRACER, 4, "Assign Tracer access DB error: ");
    public static final ErrorCode TRACER_MQ_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.TRACER, 5, "Assign Tracer MQ error: ");
    public static final ErrorCode TRACER_CATCH_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.TRACER, 6, "Assign Tracer access cache error: ");
    public static final ErrorCode TRACER_EMAIL_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.TRACER, 7, "Assign Tracer send mail error: ");
    public static final ErrorCode TRACER_SMS_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.TRACER, 8, "Assign Tracer send SMS error: ");
    public static final ErrorCode TRACER_PUSHER_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.TRACER, 9, "Assign Tracer push message error: ");
    
    // itsm gateway error code
    public static final ErrorCode ITSMGW_COMM_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.ITSMGW, UNKNOWN, "ITSM Gateway common error: ");
    public static final ErrorCode ITSMGW_FROM_ITSM = new BizErrorCode(Severity.ERROR, BizCategory.ITSMGW, 2, "ITSM Gateway receive ITSM message error: ");
    public static final ErrorCode ITSMGW_TO_ITSM = new BizErrorCode(Severity.ERROR, BizCategory.ITSMGW, 3, "ITSM Gateway sync data to ITSM error: ");
    public static final ErrorCode ITSMGW_DB_ERROR = new BizErrorCode(Severity.ERROR, BizCategory.ITSMGW, 4, "ITSM Gateway access DB error: ");
    public static final ErrorCode ITSMGW_MQ_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.ITSMGW, 5, "ITSM Gateway MQ error: ");
    public static final ErrorCode ITSMGW_FILE_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.ITSMGW, 6, "ITSM Gateway access file error: ");
    public static final ErrorCode ITSMGW_CATCH_ERROR = new BizErrorCode(Severity.WARNING, BizCategory.ITSMGW, 7, "ITSM Gateway access cache error: ");
    
    /**
     * @param severity
     * @param category
     * @param sequence
     * @param reason
     */
    protected BizErrorCode(Severity severity, Catetory category, int sequence, String reasonFormat) {
        super(severity, category, sequence, reasonFormat);
    }
}
