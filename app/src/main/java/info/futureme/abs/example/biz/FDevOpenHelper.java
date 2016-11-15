package info.futureme.abs.example.biz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import info.futureme.abs.util.DLog;
import info.futureme.abs.example.entity.g.DaoMaster;

public class FDevOpenHelper extends DaoMaster.DevOpenHelper {
    public FDevOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DLog.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
        if(newVersion > oldVersion) {
            DaoMaster.dropAllTables(db, true);
            onCreate(db);
        }
    }
}
