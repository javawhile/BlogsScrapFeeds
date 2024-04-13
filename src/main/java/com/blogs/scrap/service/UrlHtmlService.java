package com.blogs.scrap.service;

import com.blogs.scrap.model.UrlHtml;
import com.blogs.scrap.repository.UrlHtmlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import static com.blogs.scrap.utils.ObjectUtils.*;

@Service
public class UrlHtmlService {

    @Autowired
    private UrlHtmlRepository urlHtmlRepository;

    public UrlHtml persist(final UrlHtml urlHtml) {
        if (areNonNull(urlHtml) && areValid(urlHtml.getHtml())) {
            String url = urlHtml.getUrl().trim();
            UrlHtml existingHtml = findByUrl(url);
            if (areNonNull(existingHtml) && areValid(existingHtml.getUrl(), existingHtml.getHtml())) {
                return existingHtml;
            }
            return urlHtmlRepository.save(urlHtml);
        }
        return urlHtml;
    }

    private UrlHtml findByUrl(final String url) {
        UrlHtml exampleUrlHtml = new UrlHtml();
        exampleUrlHtml.setHtml("");
        exampleUrlHtml.setUrl(url);
        return urlHtmlRepository.findAll(Example.of(exampleUrlHtml, ExampleMatcher.matchingAny()))
                .stream()
                .findFirst()
                .orElse(null);
    }
}
