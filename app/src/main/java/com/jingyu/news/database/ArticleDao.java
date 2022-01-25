package com.jingyu.news.database;

import static androidx.room.OnConflictStrategy.IGNORE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.jingyu.news.model.Article;

import java.util.List;

@Dao
public interface ArticleDao {

    @Insert(onConflict = IGNORE)
    void saveArticle(Article article);

    @Query("SELECT * from article")
    LiveData<List<Article>> getAllArticles();

    @Delete
    void deleteArticle(Article article);

}
