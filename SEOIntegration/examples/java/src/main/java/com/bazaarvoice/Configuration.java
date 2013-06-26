package com.bazaarvoice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    static Properties _properties;
    protected static final Log _log = LogFactory.getLog(Configuration.class);

    protected Configuration() {

    }

    static {
        Properties classProperties = new Properties();
        try {
            classProperties.load(BazaarvoiceDisplayHelper.class.getClassLoader().getResourceAsStream("bvconfig.properties"));
        } catch (IOException ex) {
            _log.error("Unable to find bvconfig.properties file in path.  Some required properties are not defined.");
            throw new RuntimeException(ex);
        }

        Properties clientProperties = new Properties();
        InputStream propertyStream;
        try {
            propertyStream = new FileInputStream("bvclient.properties");
        } catch (FileNotFoundException ex) {
            propertyStream = BazaarvoiceDisplayHelper.class.getClassLoader().getResourceAsStream("bvclient.properties");

            if (propertyStream == null) {
                _log.error("Unable to find bvclient.properties file in path.  Please make sure the file in your classpath or application root.  Some required properties are not defined.");
                throw new RuntimeException("Unable to find bvclient.properties file in path.  Please make sure the file in your classpath or application root.");
            }
        }

        try {
            clientProperties.load(propertyStream);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load bvclient.properties.  Some required properties are not defined.");
        }
        _properties = new Properties();
        _properties.putAll(classProperties);
        _properties.putAll(clientProperties);
    }

    static public String get(String key) {
        return _properties.getProperty(key);
    }

    static public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
