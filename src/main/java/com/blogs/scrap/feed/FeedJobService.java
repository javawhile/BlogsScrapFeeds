package com.blogs.scrap.feed;

import com.blogs.scrap.model.ConnectionConfig;
import com.blogs.scrap.model.Article;
import com.blogs.scrap.model.ArticleList;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.blogs.scrap.utils.ObjectUtils.*;

@Getter
@Setter
public abstract class FeedJobService {

    private Integer iterationsPerBlock = 50;
    private Long sleepTimePerBlock = 10000L;

    private final Logger logger = LoggerFactory.getLogger(FeedJobService.class);

    public abstract Function<String, String> urlDocumentListener();

    public abstract BiConsumer<String, String> persistUrlDocumentListener();

    public abstract String getNextBaseUrl();

    public abstract Collection<ArticleList> getArticleList(String url);

    public abstract Article getArticle(String url);

    public abstract void persistArticle(Article article, ArticleList articleList);

    protected void log(String message, Object... arguments) {
        logger.info(message, arguments);
    }

    public void startFeedJob() {
        Runnable jobRunnable = this::startFeedJobBackground;
        String jobThreadName = String.format("feedJobThread_%s", System.currentTimeMillis());
        Thread thread = new Thread(jobRunnable, jobThreadName);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setDaemon(true);
        thread.start();
        log("feedJob: threadName: {}, threadId: {}, threadPriority: {}", thread.getName(), thread.getId(), thread.getPriority());
    }

    private void startFeedJobBackground() {
        log("feedJob: started");
        while (true) {
            String nextBaseUrl = getNextBaseUrl();
            if (!isValid(nextBaseUrl)) {
                break;
            }
            process(nextBaseUrl);
        }
        log("feedJob: ended");
    }

    private void process(String articleListPageUrl) {
        if (iterationsPerBlock < 0) {
            log("feedJob: (iterationsPerBlock < 0)");
            return;
        }

        if (sleepTimePerBlock < 0) {
            log("feedJob: (sleepTimePerBlock < 0)");
            return;
        }

        int iterations = iterationsPerBlock;
        Collection<ArticleList> articleLists = getArticleList(articleListPageUrl);

        if (articleLists == null || articleLists.isEmpty()) {
            log("feedJob: articleLists cannot be null or empty");
            return;
        }

        for (ArticleList articleList : articleLists) {
            log("feedJob: articleList={} started", articleList.getArticleTitle());
            String articleUrl = articleList.getArticleUrl();
            if (!areValid(articleUrl)) {
                log("feedJob: articleUrl is null or empty");
                continue;
            }

            log("feedJob: articleUrl={} fetch started", articleUrl);
            Article article = getArticle(articleUrl);

            if (!areNonNull(article)) {
                log("feedJob: articleUrl={} fetched is null", articleUrl);
                continue;
            }

            persistArticle(article, articleList);
            log("feedJob: articleUrl={} persist called", articleUrl);

            iterations--;
            if (iterations <= 0) {
                log("feedJob: iterations={} exhausted", iterations);
                iterations = iterationsPerBlock;
                log("feedJob: entering sleep mode for {} ms", sleepTimePerBlock);
                sleepThread(sleepTimePerBlock);
                log("feedJob: sleep mode exit");
            }
        }
    }

    private void sleepThread(final long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public ConnectionConfig connectionConfig() {
        ConnectionConfig connectionConfig = new ConnectionConfig();
        connectionConfig.setUrlDocumentListener(urlDocumentListener());
        connectionConfig.setPersistUrlDocumentListener(persistUrlDocumentListener());
        return connectionConfig;
    }
}
