package newsapi;

import newsapi.beans.Article;
import newsapi.beans.NewsReponse;
import newsapi.enums.Category;
import newsapi.enums.Country;
import newsapi.enums.Endpoint;

import java.util.List;

public class NewsAPIExample {

    public static final String APIKEY = "962e4c2a95984cbd9de1734c95868998";

    public static void main(String[] args){

        NewsApi newsApi = new NewsApiBuilder()
                .setApiKey(APIKEY)
                .setQ("corona")
                .setEndPoint(Endpoint.TOP_HEADLINES)
                .setSourceCountry(Country.at)
                .setSourceCategory(Category.health)
                .createNewsApi();

        NewsReponse newsResponse = null;
        try {
            newsResponse = newsApi.getNews();
        } catch (NewsApiException e) {
            System.out.println(e.getMessage());
        }
        if(newsResponse != null){
                List<Article> articles = newsResponse.getArticles();
                articles.stream().forEach(article -> System.out.println(article.toString()));
            }

        newsApi = new NewsApiBuilder()
                .setApiKey(APIKEY)
                .setQ("corona")
                .setEndPoint(Endpoint.EVERYTHING)
                .setFrom("2020-03-20")
                .setExcludeDomains("Lifehacker.com")
                .createNewsApi();

        try {
            newsResponse = newsApi.getNews();
        } catch (NewsApiException e) {
            System.out.println(e.getMessage());
        }

        if(newsResponse != null){
            List<Article> articles = newsResponse.getArticles();
            articles.stream().forEach(article -> System.out.println(article.toString()));
        }

    }
}
