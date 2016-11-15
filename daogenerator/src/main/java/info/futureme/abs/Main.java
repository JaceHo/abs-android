package info.futureme.abs;

import java.util.HashMap;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * This file and it's dependencies are not included in the main project, it's only
 * used for code generation, when schema changes, <b>you must change DB_VERSION
 * and then re-run Main.java's main method for code generation</b>!!!!!!!!!!!!!!!
 * @author Jeffrey
 * @version 1.0
 * @updated 17-一月-2016 11:26:13
 */

public class Main {
    /**
     * You must change DB_VERSION and then re-run Main.java's main method after scheme definition
     * change!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     */
    public static final int DB_VERSION = 42;
    static HashMap<String, Entity> map = new HashMap<String, Entity>();

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(DB_VERSION, "info.futureme.abs.example.entity.g");
        addEntity(schema);
        // createEntity(schema);
        new DaoGenerator().generateAll(schema, "./app/src/main/java/");
    }

    static Entity addEntity(String name, Schema schema) {
        map.put(name, schema.addEntity(name));
        return map.get(name);
    }

    static void addEntity(Schema schema) {
        /*
        {“type”:num,”title”:””, “desc”:””, “content”:””}
         */
        Entity notification = addEntity("Notification", schema);
        notification.setTableName("notification");
        notification.addLongProperty("id").primaryKey().autoincrement().index();
        notification.addStringProperty("title");
        notification.addIntProperty("type");
        notification.addDateProperty("timestamp");
        notification.addStringProperty("desc");
        notification.addStringProperty("content");
        notification.addStringProperty("img");
        notification.addStringProperty("url");
        notification.addStringProperty("data");
        notification.addStringProperty("ticketid");
        notification.addDateProperty("date");
        notification.addStringProperty("account");
        notification.addBooleanProperty("readed");

        Entity attachment = addEntity("Attachment", schema);
        attachment.setTableName("attachment");
        attachment.addLongProperty("id").primaryKey().autoincrement().index();
        attachment.addStringProperty("type");
        attachment.addStringProperty("ticketid");
        attachment.addIntProperty("status");
        attachment.addStringProperty("clientName");
        attachment.addStringProperty("path");
        attachment.addStringProperty("lazyId");
        attachment.addIntProperty("failedTime");
        attachment.addStringProperty("account");
        attachment.addLongProperty("time");

        Entity project = addEntity("Project", schema);
        project.setTableName("project");
        project.addLongProperty("id").primaryKey().autoincrement().index();
        project.addIntProperty("projectid");
        project.addStringProperty("name");
        project.addIntProperty("status");
        project.addStringProperty("description");
        project.addStringProperty("itsmcode");

        Entity site = addEntity("Client", schema);
        site.setTableName("client");
        site.addLongProperty("_id").primaryKey().autoincrement().index();
        site.addIntProperty("clientid");
        site.addStringProperty("name");
        site.addStringProperty("address");
        site.addDoubleProperty("longitude");
        site.addDoubleProperty("latitude");
        site.addStringProperty("others");

        Entity station = addEntity("Station", schema);
        station.setTableName("station");
        station.addLongProperty("id").primaryKey().autoincrement().index();
        station.addIntProperty("level");
        station.addStringProperty("stationCode");
        station.addStringProperty("stationPrefix");

        Entity addressDetail = addEntity("UploadLocation", schema);
        addressDetail.setTableName("locationdetail");
        addressDetail.addLongProperty("id").primaryKey().autoincrement().index();
        addressDetail.addStringProperty("location");
        addressDetail.addDoubleProperty("latitude");
        addressDetail.addDoubleProperty("longtitude");

        Entity sysDict = addEntity("SysDict",schema);
        sysDict.setTableName("sysdict");
        sysDict.addLongProperty("id").primaryKey().autoincrement().index();
        sysDict.addIntProperty("dictId");
        sysDict.addIntProperty("dictType");
        sysDict.addStringProperty("dictCode");
        sysDict.addStringProperty("dictName");
        sysDict.addIntProperty("isDelete");
        sysDict.addIntProperty("rank");

        Entity requestCache = addEntity("FileCache", schema);
        requestCache.setTableName("filecache");
        requestCache.addLongProperty("id").primaryKey().autoincrement().index();
        requestCache.addStringProperty("url");
        requestCache.addStringProperty("path");
        requestCache.addStringProperty("source_type");
        requestCache.addStringProperty("content_type");
        requestCache.addLongProperty("timestamp");

        Entity syncCode = addEntity("SyncCode", schema);
        syncCode.setTableName("synccode");
        syncCode.addLongProperty("id").primaryKey().autoincrement().index();
        syncCode.addLongProperty("code");
        syncCode.addStringProperty("type");
    }
}
