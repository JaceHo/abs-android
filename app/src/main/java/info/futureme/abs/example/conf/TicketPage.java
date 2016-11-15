package info.futureme.abs.example.conf;

/**
页面名称，可选值
        unknown -未知页面
 activedetail - 活动订单详情
 closeddetail - 历史订单详情
        imspection - 巡检单详情
        attachment - 附件信息
        parts - 备件信息
        partslist - 备件列表
        breakoff - 快速关单操作页面
        reject - 退单操作页面
        solution - 解决方案操作页面
        upload - 上传附件操作页面
        reappoint - 改约操作页面
        reconfirm - 再约操作页面
        deviceinput - 备件录入页面

 * Created by Jeffrey on 2016/5/6.
 */
public enum TicketPage {
    UNKNOWN("unknown"),
    ACTIVE_DETAIL("activedetail"),
    CLOSE_DETAIL("closeddetail"),
    INSPECTION("inspection"),
    ATTACHMENT("attachment "),
    PARTS("parts "),
    PARTS_LIST("partslist"),
    BREAK_OFF("breakoff"),
    REJECT("reject"),
    SOLUTION("solution"),
    UPLOAD("upload"),
    REAPPOINT("reappint"),
    RECONFIRM("reconfirm"),
    DEVICE_INPUT("device_input");
    private final String value;
    TicketPage(String value) {
        this.value = value;
    }

    public final String getValue() {
        return value;
    }
}
