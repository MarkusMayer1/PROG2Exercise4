package newsanalyzer.downloader;

import newsapi.NewsApiException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public abstract class Downloader {

    public static final String HTML_EXTENTION = ".html";
    public static final String DIRECTORY_DOWNLOAD = "./download/";

    public abstract int process(List<String> urls) throws Exception;

    public String saveUrl2File(String urlString) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        String fileName = "";
        try {
            URL url4download = new URL(urlString);
            is = url4download.openStream();

            fileName = urlString.substring(urlString.lastIndexOf('/') + 1);
            if (fileName.isEmpty()) {
                fileName = url4download.getHost() + HTML_EXTENTION;
            }
            fileName = fileName.replace("\\", "")
                                .replace("/", "")
                                .replace(":", "")
                                .replace("*", "")
                                .replace("?", "")
                                .replace("\"", "")
                                .replace("<", "")
                                .replace(">", "")
                                .replace("|", "");
            os = new FileOutputStream(DIRECTORY_DOWNLOAD + fileName);

            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            throw new NewsApiException("Input output exception occurred");
        } finally {
            try {
                Objects.requireNonNull(is).close();
                Objects.requireNonNull(os).close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return fileName;
    }
}
