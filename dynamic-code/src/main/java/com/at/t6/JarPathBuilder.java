package com.at.t6;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.Optional;

/**
 * @create 2023-07-22
 */
public class JarPathBuilder {

    /**
     * 默认ext插件路径
     * 可以暴露出去，做到参数控制
     */
    private static final String DEFAULT_EXT_PLUGIN_PATH = "/ext-lib/";

    public static File getJatPath(final String path){

        if (StringUtils.isNotEmpty(path)){
            System.out.println("开始加载 【 " + path + " 】 路径下的 jar 包");
            return new File(path);
        }

        System.out.println("加载默认路径 【 " + DEFAULT_EXT_PLUGIN_PATH + " 】 路径下的 jar 包");

        return buildJarPath();


    }

    private static File buildJarPath(){
        URL url = JarPathBuilder.class.getResource(DEFAULT_EXT_PLUGIN_PATH);
        System.out.println("url = " + url.getPath());
        return Optional.ofNullable(url).map(u -> {
            final String file = u.getFile();
            System.out.println("file = " + file);
            return new File(file);
        }).orElseGet(() -> new File(DEFAULT_EXT_PLUGIN_PATH));
    }


}
