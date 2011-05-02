package edu.j4f.aprog.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyManager {

	private static final Logger logger = LoggerFactory.getLogger(PropertyManager.class);
	private static PropertyManager instance = null;
	private Properties properties = null;
	
	private PropertyManager() {
		logger.debug("Create PropertyManager");
		properties = new Properties();
		try {
			ClassLoader loader = this.getClass().getClassLoader();
			properties.load(loader.getResourceAsStream("config.properties"));
		} catch (FileNotFoundException e) {
			logger.error("File with configuration properties was not found", e);
		} catch (IOException e) {
			logger.error("Failed to load properties", e);
		}
	}
	
	public static PropertyManager getInstance() {
		if (instance == null) {
			instance = new PropertyManager();
		}
		return instance;
	}

	public String getProperty(String key) {
		logger.debug("Get property with key: " + key);
		return properties.getProperty(key, null);
	}
	
	public int getIntegerProperty(String key) {
		logger.debug("Get integer property with key: " + key);
		int property = -1;
		try {
			property = Integer.parseInt(properties.getProperty(key));
		} catch (NumberFormatException e) {
			logger.error("Fail to get integer property with key: " + key, e);
		}
		return property;
	}

}
