package com.blogs.scrap.fetcher;

import com.blogs.scrap.model.ConnectionConfig;

public abstract class BaseFetcher<T> {
    public abstract T fetchForUrl(final String url, final ConnectionConfig connectionConfig);
}
