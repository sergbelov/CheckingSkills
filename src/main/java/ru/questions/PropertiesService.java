package ru.questions;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import static java.util.Map.Entry.comparingByKey;

/**
 * Created by Сергей on 19.05.2018.
 */
public class PropertiesService {
    private static final Logger LOG = LogManager.getLogger();

    private Map<String, String> propertyMap;

    public PropertiesService(Map<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }

    public void readProperties(String fileName) {
        StringBuilder report = new StringBuilder();
        report.append("Параметры из файла ").append(fileName).append(":");
        boolean fileExists = false;
        File file = new File(fileName);
        if (file.exists()) { // найден файл с параметрами
            try (InputStream is = new FileInputStream(file)) {
                Properties pr = new Properties();
                pr.load(is);
                for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
                    propertyMap.put(entry.getKey(), pr.getProperty(entry.getKey(), entry.getValue()));
                }
                fileExists = true;
            } catch (IOException e) {
                LOG.error(e);
            }
        } else {
            report.append("\r\n\tФайл не найден, параметры по умолчанию:");
        }

        // параметры со значениями
        propertyMap
                .entrySet()
                .stream()
//                .sorted(comparingByKey())
                .forEach(x -> {
                    report.append("\r\n\t")
                            .append(x.getKey())
                            .append(": ")
                            .append(x.getValue());
                });

        if (fileExists) {
            LOG.info(report);
        } else {
            LOG.warn(report);
        }
    }

    public String getString(String propertyName) {
        return propertyMap.get(propertyName);
    }

    public int getInt(String propertyName) {
        return Integer.parseInt(propertyMap.get(propertyName));
    }

    public Date getDate(String propertyName) {
        return getDate(propertyName, "dd/MM/yyyy");
    }

    public Date getDate(String propertyName, String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = simpleDateFormat.parse(propertyMap.get(propertyName));
        } catch (ParseException e) {
            LOG.error(e);
        }
        return date;
    }

    public boolean getBoolean(String propertyName) {
        return Boolean.parseBoolean(propertyMap.get(propertyName));
    }

    public Level getLevel(String propertyName) {
        return Level.getLevel(propertyMap.get(propertyName));
    }
}
