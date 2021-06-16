package newsapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import newsapi.beans.NewsReponse;
import newsapi.enums.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NewsApi {

    public static final String DELIMITER = "&";

    public static final String NEWS_API_URL = "http://newsapi.org/v2/%s?q=%s&apiKey=%s";

    private Endpoint endpoint;
    private String q;
    private String qInTitle;
    private Country sourceCountry;
    private Category sourceCategory;
    private String domains;
    private String excludeDomains;
    private String from;
    private String to;
    private Language language;
    private SortBy sortBy;
    private String pageSize;
    private String page;
    private String apiKey;

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public String getQ() {
        return q;
    }

    public String getqInTitle() {
        return qInTitle;
    }

    public Country getSourceCountry() {
        return sourceCountry;
    }

    public Category getSourceCategory() {
        return sourceCategory;
    }

    public String getDomains() {
        return domains;
    }

    public String getExcludeDomains() {
        return excludeDomains;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Language getLanguage() {
        return language;
    }

    public SortBy getSortBy() {
        return sortBy;
    }

    public String getPageSize() {
        return pageSize;
    }

    public String getPage() {
        return page;
    }

    public String getApiKey() {
        return apiKey;
    }

    public NewsApi(String q, String qInTitle, Country sourceCountry, Category sourceCategory, String domains, String excludeDomains, String from, String to, Language language, SortBy sortBy, String pageSize, String page, String apiKey, Endpoint endpoint) {
        this.q = q;
        this.qInTitle = qInTitle;
        this.sourceCountry = sourceCountry;
        this.sourceCategory = sourceCategory;
        this.domains = domains;
        this.excludeDomains = excludeDomains;
        this.from = from;
        this.to = to;
        this.language = language;
        this.sortBy = sortBy;
        this.pageSize = pageSize;
        this.page = page;
        this.apiKey = apiKey;
        this.endpoint = endpoint;
    }

    protected String requestData() throws NewsApiException {
        String url = buildURL();
        System.out.println("URL: "+url);
        URL obj = null;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            throw new NewsApiException("URL not correct");
            // TODO improve ErrorHandling
            // e.printStackTrace();
        }
        HttpURLConnection con;
        StringBuilder response = new StringBuilder();
        try {
            con = (HttpURLConnection) obj.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            throw new NewsApiException("Input output exception occurred");
            // TODO improve ErrorHandling
            // System.out.println("Error "+e.getMessage());
        }
        return response.toString();
    }

    protected String buildURL() throws NewsApiException {
        // TODO ErrorHandling

        if (getEndpoint().getValue() == null || getQ() == null || getApiKey() == null) {
            throw new NewsApiException("Mandatory URL parameter was null");
        }

        String urlbase = String.format(NEWS_API_URL,getEndpoint().getValue(),getQ(),getApiKey());
        StringBuilder sb = new StringBuilder(urlbase);

        if(getFrom() != null){
            sb.append(DELIMITER).append("from=").append(getFrom());
        }
        if(getTo() != null){
            sb.append(DELIMITER).append("to=").append(getTo());
        }
        if(getPage() != null){
            sb.append(DELIMITER).append("page=").append(getPage());
        }
        if(getPageSize() != null){
            sb.append(DELIMITER).append("pageSize=").append(getPageSize());
        }
        if(getLanguage() != null){
            sb.append(DELIMITER).append("language=").append(getLanguage());
        }
        if(getSourceCountry() != null){
            sb.append(DELIMITER).append("country=").append(getSourceCountry());
        }
        if(getSourceCategory() != null){
            sb.append(DELIMITER).append("category=").append(getSourceCategory());
        }
        if(getDomains() != null){
            sb.append(DELIMITER).append("domains=").append(getDomains());
        }
        if(getExcludeDomains() != null){
            sb.append(DELIMITER).append("excludeDomains=").append(getExcludeDomains());
        }
        if(getqInTitle() != null){
            sb.append(DELIMITER).append("qInTitle=").append(getqInTitle());
        }
        if(getSortBy() != null){
            sb.append(DELIMITER).append("sortBy=").append(getSortBy());
        }
        return sb.toString();
    }

    public NewsReponse getNews() throws NewsApiException {
        NewsReponse newsReponse = null;
        String jsonResponse = requestData();
        if(jsonResponse != null && !jsonResponse.isEmpty()){

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                newsReponse = objectMapper.readValue(jsonResponse, NewsReponse.class);
                if(!"ok".equals(newsReponse.getStatus())){
                    System.out.println("Error: "+newsReponse.getStatus());
                }
            } catch (JsonProcessingException e) {
                throw new NewsApiException("Json response could not be processed");
                // System.out.println("Error: "+e.getMessage());
            }
        }
        //TODO improve Errorhandling
        return newsReponse;
    }

    public void downloadArticleAsHTML(String stringUrl, String filename) throws NewsApiException {
        try {
            URL url = new URL(stringUrl);
            BufferedReader readr = new BufferedReader(new InputStreamReader(url.openStream()));
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename + ".html"));

            String line;
            while ((line = readr.readLine()) != null) {
                writer.write(line);
            }

            readr.close();
            writer.close();
        } catch (MalformedURLException e) {
            throw new NewsApiException("URL not correct");
        } catch (IOException e) {
            throw new NewsApiException("Input output exception occurred");
        }
    }
}

