package info.futureme.abs.example.conf;

public enum JSAction{
    GET_PICTURE(0),
    OPEN_PICTURE(1),
    CHOOSE_FILE(2),
    SCAN_QRCODE(3),
    CHOOSE_TIME(4),
    RECORD_VOICE_START(5),
    CHOOSE_LIST(6),
    POST_DATA(7),
    EXECUTE_SQL(8),
    CALL(9),
    OPEN_VOICE(10),
    MORE_MENU(11),
    GOTO(12),
    AUTO_SIGN(13),
    TOAST(14),
    NAVIGATION(15),
    SEARCH(16),
    CONFIRM(17),
    REFRESHURL(18),
    SELECT_LOCATION(19),
    SEARCH_LOCATION(20),
    RETRIEVE_H5DATA_TO_PARENT(21),
    RECORD_VOICE_END(22),
    SEARCH_LIST(24),
    GRAB_TICKET(25),
    UNKNOWN(26);
    private final int value;
    JSAction(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
