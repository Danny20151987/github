package com.constellation.cancer.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/20 5:35 PM
 */
public class IOUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);

    public IOUtils() {
    }

    public static String stream2string(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            for(boolean firstLine = true; (line = reader.readLine()) != null; sb.append(line)) {
                if (!firstLine) {
                    sb.append(System.getProperty("line.separator"));
                } else {
                    firstLine = false;
                }
            }

            return sb.toString();
        } catch (IOException ex) {
            throw ex;
        } finally {
            try {
                is.close();
            } catch (Exception ex) {
                LOGGER.error("Resource close exception", ex);
            }

        }
    }

    public static Properties stream2props(InputStream is) throws IOException {
        Properties props = new Properties();

        Properties properties;
        try {
            props.load(is);
            properties = props;
        } catch (IOException ex) {
            throw ex;
        } finally {
            try {
                is.close();
            } catch (Exception ex) {
                LOGGER.error("Resource close exception", ex);
            }

        }

        return properties;
    }
}
