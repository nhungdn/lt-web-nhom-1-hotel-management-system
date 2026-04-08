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
        System.out.println("\n" + "=".repeat(70));
        System.err.println("  ENDTASK COMMAND IF PORT IS BLOCKED:  ");
        System.out.println("  netstat -ano | findstr :8080  ");
        System.out.println("  taskkill /F /PID <Found_PID>  ");
        System.out.println("  APP START AT: http://localhost:8080  ");
        System.out.println("=".repeat(70) + "\n");
    }
        
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        boolean isRestart = System.getProperty("spring.devtools.restart.enabled") != null;
        if (!isRestart) {
            String port = event.getApplicationContext().getEnvironment().getProperty("server.port");
            String url = "http://localhost:" + (port != null ? port : "8080");

            System.out.println("Open browser...");

            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(url));
                } else {
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("win")) {
                        new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
                    }
                }

            } catch (IOException | URISyntaxException e) {
                System.err.println("Failed to auto-open browser. Please click: \n" + url);
            }
        }
    }

}
