package info.futureme.abs.example.conf;

/**
 * Created by Jeffrey on 2016/3/23.
 */
/*
 * 
 * Copyright (c) 2015-2016 All Rights Reserved.
 *
 * Project Name:abs-core-util
 * Create Time: 2015-12-2 16:19:27
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Define common error code for framework.
 *
 * Code is a number consists of severity,category and sequence, format is,
 * severity(1-4)category(1-99)sequence(1 -999)
 * Other sub-system, module or component can exends class do define himself catetory and sequence
 *
 * Notion: don't override parent defined code!!!
 *
 * Following defined severity and category can't be replaced.
 *
 * severity: 1 - info, 2 - warning, 3 - error, 4 - critical
 * category: 1 - common, 2 - db, 3 - file, 4 - network, 5 - cache, 6 - mq, 7 - security
 *
 * @author wujin
 * @version v1.0
 */
public class ErrorCode {
    protected static final int UNKNOWN = 1;
    public static Map<Integer, ErrorCode> errorMap = new HashMap<>();
    public static class Catetory {
        private static ArrayList<Catetory> clist = new ArrayList<>();

        public static final Catetory NONE = new Catetory("NONE");
        public static final Catetory COMMON = new Catetory("COMMON");
        public static final Catetory DATABASE = new Catetory("DATABASE");
        public static final Catetory FILE = new Catetory("FILE");
        public static final Catetory NETWORK = new Catetory("NETWORK");
        public static final Catetory CACHE = new Catetory("CACHE");
        public static final Catetory MQ = new Catetory("MQ");
        public static final Catetory SECURITY = new Catetory("SECURITY");

        private String name;
        private int index;

        public Catetory(String name) {
            this.name = name;
            clist.add(this);
            index = clist.size();
        }

        public final String name() {
            return this.name;
        }

        public final int ordinal() {
            return index;
        }
    }

    // common error code
    public static final ErrorCode COMM_ERROR = new ErrorCode(Severity.WARNING, Catetory.COMMON, UNKNOWN, "Common Error: ");
    public static final ErrorCode COMM_PARAM_NULL = new ErrorCode(Severity.INFO, Catetory.COMMON, 2, "Input parameter is null: ");
    public static final ErrorCode COMM_PARAM_MISSED = new ErrorCode(Severity.INFO, Catetory.COMMON, 3, "Miss required parameter: ");
    public static final ErrorCode COMM_PARAM_FORMAT = new ErrorCode(Severity.INFO, Catetory.COMMON, 4, "Input parameter format is wrong: ");
    public static final ErrorCode COMM_PARAM_INVALID = new ErrorCode(Severity.INFO, Catetory.COMMON, 5, "Input parameter value is invalid: ");
    public static final ErrorCode COMM_FUNC_NOTFOUND = new ErrorCode(Severity.INFO, Catetory.COMMON, 6, "Not found required function:");
    public static final ErrorCode COMM_PARSE_FAILED = new ErrorCode(Severity.WARNING, Catetory.COMMON, 7, "Parse data failed:");
    public static final ErrorCode COMM_CREATE_FAILED = new ErrorCode(Severity.WARNING, Catetory.COMMON, 8, "Create object failed:");

    // common DB error code
    public static final ErrorCode DB_ERROR = new ErrorCode(Severity.WARNING, Catetory.DATABASE, UNKNOWN, "Database Error: ");
    public static final ErrorCode DB_ERROR_CONNECT = new ErrorCode(Severity.ERROR, Catetory.DATABASE, 2, "Failed to connect database:");
    public static final ErrorCode DB_QUERY_FAILED = new ErrorCode(Severity.WARNING, Catetory.DATABASE, 3, "Failed to execute select SQL:");
    public static final ErrorCode DB_INSERT_FAILED = new ErrorCode(Severity.WARNING, Catetory.DATABASE, 4, "Failed to execute insert SQL:");
    public static final ErrorCode DB_UPDATE_FAILED = new ErrorCode(Severity.WARNING, Catetory.DATABASE, 5, "Failed to execute update SQL:");
    public static final ErrorCode DB_DELETE_FAILED = new ErrorCode(Severity.WARNING, Catetory.DATABASE, 6, "Failed to execute delete SQL:");
    public static final ErrorCode DB_SCRIPT_FAILED = new ErrorCode(Severity.WARNING, Catetory.DATABASE, 7, "Failed to execute SQL script:");
    public static final ErrorCode DB_BATCH_FAILED = new ErrorCode(Severity.WARNING, Catetory.DATABASE, 8, "Failed to execute batch SQL:");
    public static final ErrorCode DB_TRANS_FAILED = new ErrorCode(Severity.WARNING, Catetory.DATABASE, 9, "Failed to commit data:");
    public static final ErrorCode DB_RECORD_NOTFOUND = new ErrorCode(Severity.INFO, Catetory.DATABASE, 10, "Not found required record:");

    // common cache error code
    public static final ErrorCode CACHE_ERROR = new ErrorCode(Severity.WARNING, Catetory.CACHE, UNKNOWN, "Cache Error: ");
    public static final ErrorCode CACHE_NOTFOUND = new ErrorCode(Severity.WARNING, Catetory.CACHE, 2, "Not found required cache: ");
    public static final ErrorCode CACHE_CREATE_FAILED = new ErrorCode(Severity.ERROR, Catetory.CACHE, 3, "Failed to create cache client: ");

    // common MQ error code
    public static final ErrorCode MQ_ERROR = new ErrorCode(Severity.WARNING, Catetory.MQ, UNKNOWN, "MQ Error: ");
    public static final ErrorCode MQ_WRONG_PROVIDER = new ErrorCode(Severity.INFO, Catetory.MQ, 2, "Unsupported MQ Porvider:");
    public static final ErrorCode MQ_PUBLISH_FAILED = new ErrorCode(Severity.WARNING, Catetory.MQ, 3, "Failed to publish message:");
    public static final ErrorCode MQ_DISPATCH_FAILED = new ErrorCode(Severity.WARNING, Catetory.MQ, 4, "Meet error when dispatch message:");
    public static final ErrorCode MQ_CLIENT_CREATED = new ErrorCode(Severity.ERROR, Catetory.MQ, 5, "Meet error when create MQ client:");
    public static final ErrorCode MQ_SERVER_CREATED = new ErrorCode(Severity.ERROR, Catetory.MQ, 6, "Meet error when create MQ server:");

    public static final ErrorCode NET_ERROR = new ErrorCode(Severity.WARNING, Catetory.NETWORK, UNKNOWN, "Network Error: ");
    public static final ErrorCode NET_CONNECTION_CLOSED = new ErrorCode(Severity.ERROR, Catetory.NETWORK, 2, "Connection closed with ");
    public static final ErrorCode NET_CONNECTION_TIMEOUT = new ErrorCode(Severity.ERROR, Catetory.NETWORK, 3, "Connection timeout with ");
    public static final ErrorCode NET_WRONG_URL = new ErrorCode(Severity.ERROR, Catetory.NETWORK, 4, "Invalid URL  ");
    public static final ErrorCode NET_READ_ERROR = new ErrorCode(Severity.ERROR, Catetory.NETWORK, 5, "Read data form network failed: ");
    public static final ErrorCode NET_WRITE_ERROR = new ErrorCode(Severity.ERROR, Catetory.NETWORK, 6, "Write data to network failed: ");
    public static final ErrorCode NET_FTP_COMMAND = new ErrorCode(Severity.ERROR, Catetory.NETWORK, 7, "Execute FTP command meet error: ");

    public static final ErrorCode FILE_ERROR = new ErrorCode(Severity.WARNING, Catetory.FILE, UNKNOWN, "File Error: ");
    public static final ErrorCode FILE_NOTFOUND = new ErrorCode(Severity.WARNING, Catetory.FILE, 2, "File not found: ");
    public static final ErrorCode FILE_PARSE_FAILED = new ErrorCode(Severity.WARNING, Catetory.FILE, 3, "Parse file meet error: ");
    public static final ErrorCode FILE_LOAD_FAILED = new ErrorCode(Severity.INFO, Catetory.FILE, 4, "Load file meet error: ");
    public static final ErrorCode FILE_READ_FAILED = new ErrorCode(Severity.INFO, Catetory.FILE, 5, "Read file meet error: ");
    public static final ErrorCode FILE_WRITE_FAILED = new ErrorCode(Severity.INFO, Catetory.FILE, 6, "Write file meet error: ");
    public static final ErrorCode FILE_CREATE_FAILED = new ErrorCode(Severity.INFO, Catetory.FILE, 7, "Create file or path meet error: ");

    public static final ErrorCode SECURITY_ERROR = new ErrorCode(Severity.INFO, Catetory.SECURITY, UNKNOWN, "Security Error: ");
    public static final ErrorCode SECURITY_AUTH_FAILED = new ErrorCode(Severity.INFO, Catetory.SECURITY, 2, "Authenticate Failed: ");
    public static final ErrorCode SECURITY_NO_ACCOUNT = new ErrorCode(Severity.INFO, Catetory.SECURITY, 3, "Not found Account: ");
    public static final ErrorCode SECURITY_WRONG_PWD = new ErrorCode(Severity.INFO, Catetory.SECURITY, 4, "Password is wrong: ");
    public static final ErrorCode SECURITY_DENY = new ErrorCode(Severity.INFO, Catetory.SECURITY, 5, "No permission: ");

    public static enum Severity {
        NONE,
        INFO,
        WARNING,
        ERROR,
        CRITICAL;
    }

/*    public static enum Catetory {
        NONE,
        COMMON,
        DATABASE,
        FILE,
        NETWORK,
        CACHE,
        MQ,
        SECURITY;
    }*/

    private int code;
    private int severity;
    private int category;
    private int sequence;
    private String reason;

    protected ErrorCode(Severity severity, Catetory category, int sequence, String reasonFormat) {
        this(severity.ordinal(), category.ordinal(), sequence, reasonFormat);
    }

    protected ErrorCode(int severity, int category, int sequence, String reason) {
        if (severity % 5 == 0 || category % 100 == 0 || sequence % 1000 == 0)
            throw new IllegalArgumentException(
                    String.format("wrong value range severity[%d] category[%d] sequence[%d]",
                            severity, category, sequence));

        this.severity = severity;
        this.category = category;
        this.sequence = sequence;
        this.reason = reason;

        this.code = (severity%5) * 100000 + (category%100) * 1000 + (sequence%1000);

        errorMap.put(getCode(), this);
    }

    public int getCode() {
        return code;
    }

    public int getSeverity() {
        return severity;
    }

    public int getCategory() {
        return category;
    }

    public int getSequence() {
        return sequence;
    }

    public String getReason() {
        return String.format("Server Error(%d, %d, %d): %s", severity, category, severity, reason);
    }
}
