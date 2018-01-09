package com.hg6kwan.sdk.inner.utils.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.hg6kwan.sdk.inner.platform.ControlUI;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Roman on 2017/5/25.
 */

public class NoticeDBDao {

    private static final String DB_NAME = "notice.db";//数据库名称
    private static final String TABLE_NAME = "notice_info";//数据表名称
    private static final int DB_VERSION = 1;//数据库版本

    //表的字段名
    private static String KEY_ID = "id";
    private static String KEY_MESSAGE_ID = "messageID";
    private static String KEY_CONTENT = "content";
    private static String KEY_ISNEEDSHOWN = "isNeedShown";

    private SQLiteDatabase mDatabase;
    private Context mContext;
    private TreeDBOpenHelper mDbOpenHelper;//数据库打开帮助类

    private static NoticeDBDao mNoticeDBDao;

    private NoticeDBDao(Context context) {
        mContext = context;
    }

    public static NoticeDBDao getInstance(Context context) {
        if (mNoticeDBDao == null) {
            mNoticeDBDao = new NoticeDBDao(context);
        }
        return mNoticeDBDao;
    }

    //打开数据库
    public void openDataBase() {
        mDbOpenHelper = new TreeDBOpenHelper(mContext, DB_NAME, null, DB_VERSION);
        try {
            mDatabase = mDbOpenHelper.getWritableDatabase();//获取可写数据库
        } catch (SQLException e) {
            mDatabase = mDbOpenHelper.getReadableDatabase();//获取只读数据库
        }
    }

    //关闭数据库
    public void closeDataBase() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    //插入一条数据
    public long insertData(NoticeDomain notice) {
        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE_ID, notice.getMessageID());
        values.put(KEY_CONTENT, notice.getContent());
        values.put(KEY_ISNEEDSHOWN, notice.isNeedShown());
        return mDatabase.insert(TABLE_NAME, null, values);
    }

    //删除一条数据
    public long deleteData(long id) {
        int delete = mDatabase.delete(TABLE_NAME, KEY_ID + "=" + id, null);
        return mDatabase.delete(TABLE_NAME, KEY_ID + "=" + id, null);
    }

    //根据MessageID删除一条数据
    public long deleteData(String messageID) {
        return mDatabase.delete(TABLE_NAME, KEY_MESSAGE_ID + "=" + messageID, null);
    }

    //删除所有数据
    public long deleteAllData() {
        return mDatabase.delete(TABLE_NAME, null, null);
    }

    //更新一条数据
    public long updateData(long id, String isNeedShown) {
        ContentValues values = new ContentValues();
        values.put(KEY_ISNEEDSHOWN, isNeedShown);
        return mDatabase.update(TABLE_NAME, values, KEY_ID + "=" + "?", new String[]{String
                .valueOf(id)});
    }

    //查询一条数据
    public List<NoticeDomain> queryData(long id) {
        try {
            Cursor results = mDatabase.query(TABLE_NAME, new String[]{KEY_ID, KEY_MESSAGE_ID,
                            KEY_CONTENT,
                            KEY_ISNEEDSHOWN},
                    KEY_ID + "=" + id, null, null, null, null);
            return convertToNotice(results);
        } catch (Exception e) {
            return null;
        }
    }

    //查询所有数据
    public List<NoticeDomain> queryDataList() {
        try {
            Cursor results = mDatabase.query(TABLE_NAME, new String[]{KEY_ID, KEY_MESSAGE_ID,
                            KEY_CONTENT,
                            KEY_ISNEEDSHOWN},
                    null, null, null, null, null);
            if (results == null) {
                return null;
            } else {
                return convertToNotice(results);
            }
        } catch (Exception e) {
            return null;
        }


    }

    //根据MessageID查询一条数据
    public List<NoticeDomain> queryData(String messageID) {
        try {
            Cursor results = mDatabase.query(TABLE_NAME, new String[]{KEY_ID, KEY_MESSAGE_ID,
                            KEY_CONTENT,
                            KEY_ISNEEDSHOWN},
                    KEY_MESSAGE_ID + "=" + messageID, null, null, null, null);
            return convertToNotice(results);
        } catch (Exception e) {
            return null;
        }
    }

    private List<NoticeDomain> convertToNotice(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        List<NoticeDomain> mNoticeList = new ArrayList<>();
        for (int i = 0; i < resultCounts; i++) {
            NoticeDomain notice = new NoticeDomain();
            notice.setId(cursor.getInt(0));
            notice.setMessageID(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_ID)));
            notice.setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
            notice.setNeedShown(cursor.getString(cursor.getColumnIndex(KEY_ISNEEDSHOWN)));
            mNoticeList.add(notice);
            cursor.moveToNext();
        }
        return mNoticeList;
    }

    public void isOpenNoticeDialog() {
        //获取数据库连接对象
        NoticeDBDao instance = NoticeDBDao.getInstance(mContext);
        instance.openDataBase();
        //查询数据库里面是否有数据
        if (instance.queryDataList() != null) {
            //从数据库获取数据
            List<NoticeDomain> domainList = instance.queryDataList();
            NoticeDomain noticeDomain = domainList.get(0);
            String noticeContent = noticeDomain.getContent();
            String isNeedShown = noticeDomain.isNeedShown();
            //公告内容不为空,且isNeedShown的值为"1"的时候才显示,isNeedShown由公告栏里面的不再显示此消息控制
            if (!TextUtils.isEmpty(noticeContent) && "1".equals(isNeedShown)) {
                instance.closeDataBase();
                //弹出公告信息窗口
                ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.Notice);
            }
        }
    }

    /**
     * 数据表打开帮助类
     */
    private static class TreeDBOpenHelper extends SQLiteOpenHelper {

        public TreeDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory
                factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            final String sqlStr = "create table if not exists " + TABLE_NAME + " (" + KEY_ID + " " +
                    "integer primary key autoincrement, " + KEY_MESSAGE_ID + " text not null, " +
                    KEY_CONTENT + " text not null, " + KEY_ISNEEDSHOWN + " text not null);";
            db.execSQL(sqlStr);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            final String sqlStr = "DROP TABLE IF EXISTS " + TABLE_NAME;
            db.execSQL(sqlStr);
            onCreate(db);
        }
    }

}
