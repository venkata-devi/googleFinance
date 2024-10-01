package com.googleFinance.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private Properties properties;
    private static ConfigLoader instance;

    private ConfigLoader() {
        properties = new Properties();
        try {
			FileInputStream fs = new FileInputStream(System.getProperty("user.dir")+"/src/main/java/com/googleFinance"+"/config/config.properties");
			properties.load(fs);
		}catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}