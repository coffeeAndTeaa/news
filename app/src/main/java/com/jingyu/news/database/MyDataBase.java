package com.jingyu.news.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jingyu.news.model.Article;

@Database(entities = {Article.class}, version = 1, exportSchema = false)
public abstract class MyDataBase extends RoomDatabase {
    private static final String DBNAME = "myDatabase";

    private MyDataBase instance;

    public abstract ArticleDao articleDao();

    public MyDataBase getInstance(final Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(
                   context.getApplicationContext(),
                    MyDataBase.class,
                    DBNAME
            ).build();
        }
        return instance;
    }


}
