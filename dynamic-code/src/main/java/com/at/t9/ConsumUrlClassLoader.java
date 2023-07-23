package com.at.t9;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @create 2023-07-23
 */
public class ConsumUrlClassLoader extends URLClassLoader {

    public ConsumUrlClassLoader(URL url){
        super(new URL[]{url},null);
    }

    public ConsumUrlClassLoader(URL[] urls){
        super(urls,null);
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
    }
}
