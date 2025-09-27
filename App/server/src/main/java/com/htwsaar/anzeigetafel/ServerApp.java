package com.htwsaar.anzeigetafel;

import com.htwsaar.anzeigetafel.server.DbService;
import com.htwsaar.anzeigetafel.server.Server;
import com.htwsaar.anzeigetafel.server.ServerRMI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ServerApp {

    @Autowired
    private DbService dbService;

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ServerApp.class, args);

        DbService dbService = applicationContext.getBean(DbService.class);
        ServerRMI serverRMI = applicationContext.getBean(ServerRMI.class);

        Server server = new Server(dbService, serverRMI);
        server.run(args);
        server.shutdown();

        SpringApplication.exit(applicationContext, () -> 0);
    }
}
