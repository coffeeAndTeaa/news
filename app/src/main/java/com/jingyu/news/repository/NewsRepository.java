package com.jingyu.news.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jingyu.news.database.MyDataBase;
import com.jingyu.news.model.Article;
import com.jingyu.news.model.NewsResponse;
import com.jingyu.news.network.NewsApi;
import com.jingyu.news.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {
    private final NewsApi newsApi;
    private final MyDataBase dataBase;

    public NewsRepository(Context context) {
        this.newsApi = RetrofitClient.newInstance().create(NewsApi.class);
        this.dataBase = MyDataBase.getInstance(context);
    }

    public LiveData<NewsResponse> getTopHeadlines(String country) {
        MutableLiveData<NewsResponse> topHeadlinesLiveData = new MutableLiveData<>();
        newsApi.getTopHeadlines(country)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        if (response.isSuccessful()) {
                            topHeadlinesLiveData.setValue(response.body());
                        } else {
                            topHeadlinesLiveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        topHeadlinesLiveData.setValue(null);
                    }
                });
        return topHeadlinesLiveData;
    }

    public LiveData<NewsResponse> searchNews(String query) {
        MutableLiveData<NewsResponse> everyThingLiveData = new MutableLiveData<>();
        newsApi.getEverything(query, 40)
                .enqueue(
                        new Callback<NewsResponse>() {
                            @Override
                            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                                if (response.isSuccessful()) {
                                    everyThingLiveData.setValue(response.body());
                                } else {
                                    everyThingLiveData.setValue(null);
                                }
                            }

                            @Override
                            public void onFailure(Call<NewsResponse> call, Throwable t) {
                                everyThingLiveData.setValue(null);
                            }
                        });
        return everyThingLiveData;
    }

    // all the async task that will be executed
    private static class FavoriteAsyncTask extends AsyncTask<Article, Void, Boolean> {

        private final MyDataBase db;
        private final MutableLiveData<Boolean> liveData;

        public FavoriteAsyncTask(MyDataBase db, MutableLiveData<Boolean> liveData) {
            this.db = db;
            this.liveData = liveData;
        }

        @Override
        protected Boolean doInBackground(Article... articles) {
            Article article = articles[0];
            try {
                db.articleDao().saveArticle(article);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            liveData.setValue(aBoolean);
        }
    }

    public LiveData<Boolean> favoriteArticle(Article article) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        new FavoriteAsyncTask(dataBase, resultLiveData).execute(article);
        return resultLiveData;
    }

    public LiveData<List<Article>> getAllSavedArticles() {
        return dataBase.articleDao().getAllArticles();
    }

    public void deleteSavedArticle(Article article) {
        AsyncTask.execute(() -> dataBase.articleDao().deleteArticle(article));
    }


}
