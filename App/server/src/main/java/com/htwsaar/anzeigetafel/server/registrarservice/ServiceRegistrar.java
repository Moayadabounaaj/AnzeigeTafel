package com.htwsaar.anzeigetafel.server.registrarservice;

import com.htwsaar.anzeigetafel.server.util.ServerLogger;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/*
    This class could be in a separate module, but it is a very simple load balancer.
    Its job is just to maintain all server IPs and their hosted objects names (display boards).

    This should be run first before any server startup.
    The IP address of this Registrar Service will be used to connect to the servers. Additionally,
    the client will connect to this service initially to obtain the IP address of the desired server
    that hosts the desired display board.

    The steps for running:
    1. Start the Registrar Service.

    2. When starting the server, provide the IP address of the Registrar Service,
        which will contain all servers and their services.

    3. When starting the client, provide the IP address of the Registrar Service and then the name of the display board.
        The Registrar Service will then "connect" the client directly to the server that hosts the "display board" service.
 */
public class ServiceRegistrar {
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "RegisterService";


    private Registry registry = null;

    // map service name (key) to hosts/ip (value)
    private Map<String, ServiceInfo> services;


    public static class ServiceInfo implements Serializable {
        public String hostIP;
        public Integer port;
    }

    public ServiceRegistrar() {
        services = new HashMap<>();
    }

    public void startServer() {
        try {
            registry = LocateRegistry.createRegistry(PORT);
            InetAddress localHost = InetAddress.getLocalHost();

            ServerLogger.LogInfo("Service started, ip: " + localHost.getHostAddress() + ", on port: " + PORT);

            ServiceRegistrarImpl obj = new ServiceRegistrarImpl(this);
            IServiceRegistrar stub = (IServiceRegistrar) UnicastRemoteObject.exportObject(obj, PORT);
            registry.rebind(SERVICE_NAME, stub);
        } catch (RemoteException e) {
            ServerLogger.LogError(e.getMessage());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public void addNewService(ServiceInfo info, String serviceName) {
        if (services.containsKey(serviceName)) {
            ServerLogger.LogError(serviceName + " exists before");
            return;
        }

        services.put(serviceName, info);
    }

    public ServiceInfo getHostName(String serviceName) {
        if (!services.containsKey(serviceName)) {
            return null;
        }

        return services.get(serviceName); // return ip address of server that host the service
    }

    public Map<String, ServiceInfo> getAllServices(){
        return this.services;
    }

    public static void main(String[] args) {
        ServiceRegistrar serviceRegistrar = new ServiceRegistrar();
        serviceRegistrar.startServer();

        int input = 0;
        Scanner scanner = new Scanner(System.in);
        while (input != -1) {
            System.out.println("-1 to shutdown");
            input = scanner.nextInt();
        }

    }

}
