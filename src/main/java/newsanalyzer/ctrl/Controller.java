package newsanalyzer.ctrl;

import newsanalyzer.downloader.Downloader;
import newsapi.NewsApi;
import newsapi.NewsApiBuilder;
import newsapi.NewsApiException;
import newsapi.beans.Article;
import newsapi.beans.NewsReponse;
import newsapi.enums.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {

	public static final String APIKEY = "962e4c2a95984cbd9de1734c95868998";

	private List<String> URLList;

	public void process(String query, Endpoint endpoint, Language language, Country country, Category category,
						SortBy sortBy, int pageSize) throws Exception {
		System.out.println("Start process");

		NewsApi newApi = new NewsApiBuilder()
				.setApiKey(APIKEY)
				.setQ(query)
				.setEndPoint(endpoint)
				.setLanguage(language)
				.setSourceCountry(country)
				.setSourceCategory(category)
				.setSortBy(sortBy)
				.setPageSize(Integer.toString(pageSize))
				.createNewsApi();

		NewsReponse newsReponse = (NewsReponse) getData(newApi);

		if (newsReponse != null) {
			if (newsReponse.getTotalResults() == 0) throw new NewsApiException("No news found");

			List<Article> articles = newsReponse.getArticles();
			articles.stream().forEach(article -> System.out.println(article.toString()));

			try {
				System.out.println(System.lineSeparator() + "Analyse der Ergebnisse:");
				System.out.println("Anzahl der Artikel: " + getArticleCount(articles));
				System.out.println("Meisten Artikel von: " + getMostFrequentProvider(articles));
				System.out.println("Autor mit dem kürzesten Namen: " + getAuthorWithShortestName(articles));
				System.out.println("Sortiert: ");
				List<Article> sortedByTitle = sortByTitleLength(articles);
				sortedByTitle.stream().forEach(e -> System.out.println(e.getTitle()));
			} catch (Exception e) {
				throw new NewsApiException("Statistic error");
			}

			URLList = getURLList(articles);

//			System.out.println(System.lineSeparator() + "Do you want to download the articles?");
//			System.out.println("[Y]es or [N]o");
//			Scanner scanner = new Scanner(System.in);
//			String choice = scanner.nextLine();
//
//			if (choice.toLowerCase(Locale.ROOT).equals("y")) download(newApi, articles);
		} else {
			throw new NewsApiException("News response is null");
		}

		//TODO implement Error handling

		//TODO load the news based on the parameters

		//TODO implement methods for analysis

		System.out.println("End process");
	}

	public void download(Downloader downloader) throws Exception {
		if (URLList == null) throw new NewsApiException("Keine Artikel zum herunterladen gefunden");
		int count = downloader.process(URLList);
		System.out.print(count + " Artikel heruntergeladen");
	}

	private List<String> getURLList(List<Article> articles) {
		return articles
				.stream()
				.map(article -> article.getUrl())
				.collect(Collectors.toList());
	}

	private long getArticleCount(List<Article> articles) {
		return articles.stream().count();
	}

	private String getMostFrequentProvider(List<Article> articles) throws NullPointerException, NoSuchElementException {
		return articles.stream()
				.collect(Collectors.groupingBy(article -> article.getSource().getName(), Collectors.counting()))
				.entrySet()
				.stream()
				.max(Map.Entry.comparingByValue())
				.orElseThrow()
				.getKey();
	}

	private String getAuthorWithShortestName(List<Article> articles) throws NullPointerException {
		Optional<Article> shortestName = articles.stream()
				.filter(article -> article.getAuthor() != null)
				.min(Comparator.comparingInt(article -> article.getAuthor().length()));
		return shortestName.isPresent() ? shortestName.get().getAuthor() : "not found";
	}

	private List<Article> sortByTitleLength(List<Article> articles) throws NullPointerException {
		Comparator<Article> compByTitleLength = Comparator.comparingInt(article -> article.getTitle().length());
		return articles.stream()
				.sorted(compByTitleLength.reversed()
						.thenComparing(Article::getTitle))
				.collect(Collectors.toList());
	}

	public void downloadArticles(NewsApi newsApi, List<Article> articles) throws NewsApiException {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
		int i = 0;
		for (Article a : articles) {
			LocalDateTime now = LocalDateTime.now();
			newsApi.downloadArticleAsHTML(a.getUrl(), "downloads\\" + dateTimeFormatter.format(now) + "_html" + i);
			i++;
		}
	}

	public Object getData(NewsApi newsApi) throws NewsApiException {
		return newsApi.getNews();
	}
}
