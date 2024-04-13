package com.blogs.scrap.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleList {
    private String articleListUrl;
    private String articleTitle;
    private String articleUrl;
}
