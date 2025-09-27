package com.htwsaar.anzeigetafel.server.registrarservice;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IServiceRegistrar extends Remote {

    void registerService(ServiceRegistrar.ServiceInfo hostOrIp, String serviceName) throws RemoteException;


    /**
     * @param serviceName name of display-board
     * @return return ip or hostname
     */
    ServiceRegistrar.ServiceInfo searchService(String serviceName) throws RemoteException;

    Map<String, ServiceRegistrar.ServiceInfo> getAllServices() throws RemoteException;
}
