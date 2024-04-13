package com.blogs.scrap.repository;

import com.blogs.scrap.model.UrlHtml;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlHtmlRepository extends JpaRepository<UrlHtml, Integer> {
}
