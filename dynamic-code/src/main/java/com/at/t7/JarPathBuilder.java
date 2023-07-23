package com.at.t7;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.Objects;

/**
 * @create 2023-07-23
 */
public class JarPathBuilder {


    /**
     * 默认ext插件路径
     * 可以暴露出去，做到参数控制
     */
    private static final String DEFAULT_EXT_PLUGIN_PATH = "/ext-lib/";

    /**
     * 获取 jar 包的路径
     * @param path
     * @return
     */
    public static File getJarPath(String path){
        if (StringUtils.isNotEmpty(path)){
            System.out.println("加载 path = " + path + " 路径下的 jar 包");
            return new File(path);
        }

        System.out.println("加载默认路径 path = " + DEFAULT_EXT_PLUGIN_PATH +" 下的 jar 包");

        final URL url = JarPathBuilder.class.getResource(DEFAULT_EXT_PLUGIN_PATH);

        if (Objects.nonNull(url)){
            return new File(url.getFile());
        }

        return new File(DEFAULT_EXT_PLUGIN_PATH);

    }



}
