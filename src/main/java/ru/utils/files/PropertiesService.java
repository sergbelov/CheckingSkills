package ru.utils.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Map.Entry.comparingByKey;

/**
 * Created by Сергей on 19.05.2018.
 */
public class PropertiesService {
    private static final Logger LOG = LogManager.getLogger();

    private boolean addKey;
    private Map<String, String> propertyMap;

    public PropertiesService() {
        addKey = true; // список параметров из файла
        propertyMap = new LinkedHashMap<String, String>();
    }

    public PropertiesService(Map<String, String> propertyMap) {
        addKey = false; // список параметров задан
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

                for (Map.Entry<Object, Object> entry : pr.entrySet()){
//                    System.out.println(entry.getKey());
                    if (addKey || propertyMap.get(entry.getKey()) != null) {
                        propertyMap.put(entry.getKey().toString(), entry.getValue().toString());
                    }
                }
/*
                for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
                    propertyMap.put(entry.getKey(), pr.getProperty(entry.getKey(), entry.getValue()));
                }
*/
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

    public <T> List<T> getJsonList(String propertyName, TypeToken typeToken) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(propertyMap.get(propertyName), typeToken.getType());
    }
/*
    public List<?> getJsonList(String propertyName) {
        Gson gson = new GsonBuilder().create();
        String jsonString = propertyMap.get(propertyName);
        return gson.fromJson(jsonString, new TypeToken<List<?>>(){}.getType());
    }
*/

}
