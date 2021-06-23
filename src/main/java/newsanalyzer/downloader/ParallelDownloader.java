package newsanalyzer.downloader;

import newsapi.NewsApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelDownloader extends Downloader {

    @Override
    public int process(List<String> urls) throws Exception {
        int count = 0;
        int numWorkers = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = null;

        try {
            pool = Executors.newFixedThreadPool(numWorkers);

            List<Callable<String>> callables = new ArrayList<>();
            for (String url : urls) {
                Callable<String> task = () -> saveUrl2File(url);
                callables.add(task);
            }

            List<Future<String>> allFutures = pool.invokeAll(callables);
            for (Future<String> result : allFutures) {
                if(result.get() != null) count++;
            }
        } catch (InterruptedException e) {
            throw new NewsApiException("Interrupt exception occurred");
        } catch (ExecutionException e) {
            throw new NewsApiException("Execution exception occurred");
        } finally {
            if (pool != null) {
                pool.shutdown();
            }
        }

        return count;
    }
}
