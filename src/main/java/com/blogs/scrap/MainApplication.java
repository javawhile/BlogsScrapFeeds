package com.blogs.scrap;

import com.blogs.scrap.feed.impl.BlogSite1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

@SpringBootApplication
@PropertySource("classpath:/prod/application-prod.properties")
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Autowired
    private BlogSite1 blogSite1;

    @PostConstruct
    public void startFeedJobs() {
        blogSite1.startFeedJob();
    }

}
