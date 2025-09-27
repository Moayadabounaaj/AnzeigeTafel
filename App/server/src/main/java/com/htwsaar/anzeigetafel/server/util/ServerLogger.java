package com.htwsaar.anzeigetafel.server.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerLogger {

    private static final String LOG_FILE_PATH = "server.log";
    private static final boolean LOG_TO_FILE = false;

    public enum LogLevel {
        Info, Warn, Error, Critical
    }

    private static void logInternal(LogLevel level, String msg) {
        if (LOG_TO_FILE)
        {
            logToFile(level.toString(),msg);
        }else {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            System.out.println("[" + formattedDateTime + "] " + "Level: " + level + ", Message: " + msg + "\n");
        }
    }

    private static void logToFile(String level, String msg) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            File logFile = new File(LOG_FILE_PATH);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            writer.write("[" + formattedDateTime + "] " + "Level: " + level + ", Message: " + msg + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //@TODO: fix methods name later

    public static void LogInfo(String msg) {
        logInternal(LogLevel.Info, msg);
    }

    public static void LogWarn(String msg) {
        logInternal(LogLevel.Warn, msg);
    }

    public static void LogError(String msg) {
        logInternal(LogLevel.Error, msg);
    }

    public static void LogCritical(String msg) {
        logInternal(LogLevel.Critical, msg);
    }
}
