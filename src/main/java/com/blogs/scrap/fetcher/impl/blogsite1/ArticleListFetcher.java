package com.blogs.scrap.fetcher.impl.blogsite1;

import com.blogs.scrap.fetcher.BaseFetcher;
import com.blogs.scrap.fetcher.impl.DocumentFetcher;
import com.blogs.scrap.model.ConnectionConfig;
import com.blogs.scrap.model.ArticleList;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class ArticleListFetcher extends BaseFetcher<Collection<ArticleList>> {

    @Autowired
    private DocumentFetcher documentFetcher;

    @Override
    public Collection<ArticleList> fetchForUrl(String url, ConnectionConfig connectionConfig) {
        Set<ArticleList> articleListSet = new HashSet<>();
        Document document = documentFetcher.fetchForUrl(url, connectionConfig);
        if (document == null) {
            return Collections.emptySet();
        }
        for (Element element : document.getElementsByClass("article__link eqheight")) {
            String articleUrl = element.attributes().get("href");
            String articleTitle = element.data();
            articleListSet.add(new ArticleList(url, articleTitle, articleUrl));
        }
        return articleListSet;
    }
}
