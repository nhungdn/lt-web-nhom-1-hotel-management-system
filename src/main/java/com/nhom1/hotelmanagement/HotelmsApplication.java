package com.nhom1.hotelmanagement;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
public class HotelmsApplication implements ApplicationListener<ApplicationReadyEvent>{

    public static void main(String[] args) {
            SpringApplication.run(HotelmsApplication.class, args);
    }
        
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String port = event.getApplicationContext().getEnvironment().getProperty("server.port");
        String url = "http://localhost:" + (port != null ? port : "8080") + "/login";

        System.out.println("App ready! Open browser...");

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
                Desktop.getDesktop().browse(new URI(url));
            else {
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win"))
                    new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
                
            }

            // 3. High-visibility Warning Message (English)
            System.out.println("\n" + "=".repeat(70));
            System.err.println("  CRITICAL WARNING: DO NOT RE-RUN WHILE APP IS ACTIVE!");
            System.out.println("  Reason: Port " + port + " will be blocked, causing a crash.");
            System.out.println("  If you get a 'Port in use' error:");
            System.out.println("  1. Open CMD (Admin)");
            System.out.println("  2. Run: netstat -ano | findstr :" + port);
            System.out.println("  3. Run: taskkill /F /PID <Found_PID>");
            System.out.println("  1b. OR press the red square button at previous Run() tab");
            System.out.println("  YOU HAVE BEEN WARNED.");
            System.out.println("=".repeat(70) + "\n");

        } catch (IOException | URISyntaxException e) {
            System.err.println("Failed to auto-open browser. Please click: " + url);
        }
    }

}
