package com.blogs.scrap.feed.impl;

import com.blogs.scrap.feed.FeedJobService;
import com.blogs.scrap.fetcher.impl.blogsite1.ArticleListFetcher;
import com.blogs.scrap.model.Article;
import com.blogs.scrap.model.ArticleList;
import com.blogs.scrap.model.UrlHtml;
import com.blogs.scrap.service.ArticleService;
import com.blogs.scrap.service.UrlHtmlService;
import com.blogs.scrap.fetcher.impl.blogsite1.ArticleFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.blogs.scrap.utils.ObjectUtils.*;

@Service
public class BlogSite1 extends FeedJobService {

    @Autowired
    private ArticleListFetcher articleListFetcher;

    @Autowired
    private ArticleFetcher articleFetcher;

    @Autowired
    private UrlHtmlService urlHtmlService;

    @Autowired
    private ArticleService articleService;

    @Value("${blog.site1.base.url}")
    private String baseUrl;

    @Value("${blog.site1.blogs.ref}")
    private String blogsRefUrl;

    private int currentPage = 430;

    @Override
    public Function<String, String> urlDocumentListener() {
        return url -> {
            UrlHtml urlHtml = new UrlHtml();
            urlHtml.setUrl(url);
            UrlHtml persistedUrlHtml = urlHtmlService.persist(urlHtml);
            return areNonNull(persistedUrlHtml) ? persistedUrlHtml.getHtml() : null;
        };
    }

    @Override
    public BiConsumer<String, String> persistUrlDocumentListener() {
        return (url, html) -> {
            UrlHtml urlHtml = new UrlHtml();
            urlHtml.setUrl(url);
            urlHtml.setHtml(html);
            UrlHtml persistedUrlHtml = urlHtmlService.persist(urlHtml);
            if (areNonNull(persistedUrlHtml)) {
                log("urlHtml persist successfully, id={}, url={}", persistedUrlHtml.getId(), persistedUrlHtml.getUrl());
            }
        };
    }

    //373
    @Override
    public String getNextBaseUrl() {
        String nextBaseUrl = baseUrl + blogsRefUrl + currentPage;
        currentPage--;
        if (currentPage <= 0) {
            return null;
        }
        return nextBaseUrl;
    }

    @Override
    public Collection<ArticleList> getArticleList(String url) {
        Collection<ArticleList> articleLists = articleListFetcher.fetchForUrl(url, connectionConfig());
        for (ArticleList articleList : articleLists) {
            String articleHrefUrl = articleList.getArticleUrl();
            String articleFinalUrl = baseUrl + articleHrefUrl;
            articleList.setArticleUrl(articleFinalUrl);
        }
        return articleLists;
    }

    @Override
    public Article getArticle(String url) {
        return articleFetcher.fetchForUrl(url, connectionConfig());
    }

    @Override
    public void persistArticle(Article article, ArticleList articleList) {
        article.setArticleListUrl(articleList.getArticleListUrl());
        Article persisted = articleService.persist(article);
        if (areNonNull(persisted)) {
            log("persist successful. article: {}", article.getArticleUrl());
        } else {
            log("error while persisting article: {}", article.getArticleUrl());
        }
    }
}
