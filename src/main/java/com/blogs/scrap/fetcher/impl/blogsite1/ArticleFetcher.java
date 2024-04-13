package com.blogs.scrap.fetcher.impl.blogsite1;

import com.blogs.scrap.fetcher.BaseFetcher;
import com.blogs.scrap.fetcher.impl.DocumentFetcher;
import com.blogs.scrap.model.Article;
import com.blogs.scrap.model.ConnectionConfig;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArticleFetcher extends BaseFetcher<Article> {

    @Autowired
    private DocumentFetcher documentFetcher;

    @Override
    public Article fetchForUrl(String url, ConnectionConfig connectionConfig) {
        Article article = new Article();
        Document document = documentFetcher.fetchForUrl(url, connectionConfig);
        if(document == null) {
            return null;
        }
        for (Element element : document.getElementsByClass("video-container")) {
            String articleTitle = document.title();
            String articleContent = element.html();
            article.setArticleUrl(url);
            article.setArticleTitle(articleTitle);
            article.setArticleContent(articleContent);
            break;
        }
        return article;
    }
}
