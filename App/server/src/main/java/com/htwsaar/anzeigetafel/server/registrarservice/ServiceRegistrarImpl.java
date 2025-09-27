package com.htwsaar.anzeigetafel.server.registrarservice;

import java.rmi.RemoteException;
import java.util.Map;

public class ServiceRegistrarImpl implements IServiceRegistrar {

    private ServiceRegistrar serviceRegistrar;

    public ServiceRegistrarImpl(ServiceRegistrar serviceRegistrar){
        this.serviceRegistrar = serviceRegistrar;
    }

    @Override
    public void registerService(ServiceRegistrar.ServiceInfo hostOrIp, String serviceName) throws RemoteException {
        serviceRegistrar.addNewService(hostOrIp, serviceName);
    }

    @Override
    public ServiceRegistrar.ServiceInfo searchService(String serviceName) throws RemoteException {
        return serviceRegistrar.getHostName(serviceName);
    }

    @Override
    public Map<String, ServiceRegistrar.ServiceInfo> getAllServices() throws RemoteException {
        return serviceRegistrar.getAllServices();
    }
}
