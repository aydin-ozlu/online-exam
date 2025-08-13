package com.aydin.exam.auth;

import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    private static final Map<String, String> users = new HashMap<>();

    static {
        users.put("user", "12345");
        users.put("admin", "adminpwd");
    }

    public static boolean isValidUser(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }
}
