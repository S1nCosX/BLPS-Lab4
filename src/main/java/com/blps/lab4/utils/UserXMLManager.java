package com.blps.lab4.utils;

import com.blps.lab4.model.user.User;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserXMLManager {
    private final String filePath = "users.xml";
    private final XmlMapper xmlMapper = new XmlMapper();

    public void saveUser(User user) {
        List<User> users = readUsers();
        users.add(user);
        try {
            File xmlFile = new File(filePath);
            if (!xmlFile.exists()) xmlFile.createNewFile();
            xmlMapper.writeValue(xmlFile, users);
        } catch (IOException e) {
            e.printStackTrace(); // Обработка ошибок
        }
    }

    public List<User> readUsers() {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return xmlMapper.readValue(file, xmlMapper.getTypeFactory().constructCollectionType(List.class, User.class));
            }
            else
                throw new FileNotFoundException();
        } catch (Exception e) {
            e.printStackTrace(); // Обработка ошибок
        }
        return new ArrayList<>();
    }
}