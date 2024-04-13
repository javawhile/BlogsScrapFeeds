package com.blogs.scrap.service;

import com.blogs.scrap.model.Article;
import com.blogs.scrap.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import static com.blogs.scrap.utils.ObjectUtils.areNonNull;
import static com.blogs.scrap.utils.ObjectUtils.areValid;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public Article persist(final Article article) {
        if (areNonNull(article)) {
            Article existingArticle = findByUrls(article.getArticleListUrl(), article.getArticleUrl());
            if (areNonNull(existingArticle) &&
                    areValid(article.getArticleUrl(), article.getArticleListUrl(), article.getArticleTitle(), article.getArticleContent())
            ) {
                return existingArticle;
            }
            return articleRepository.save(article);
        }
        return article;
    }

    private Article findByUrls(final String articleListUrl, final String articleUrl) {
        Article article = new Article();
        article.setArticleUrl(articleUrl);
        return articleRepository.findAll(Example.of(article, ExampleMatcher.matchingAny()))
                .stream()
                .findFirst()
                .orElse(null);
    }

}
