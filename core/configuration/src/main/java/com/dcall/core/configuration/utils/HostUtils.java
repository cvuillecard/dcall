package com.dcall.core.configuration.utils;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.stream.IntStream;

public final class HostUtils {
    private static final Logger LOG = LoggerFactory.getLogger(HostUtils.class);
    private static final String localhost = "127.0.0.1";
    private static final String myIpURL = "https://checkip.amazonaws.com";

    public static String getPublicIp() {
        try {
            return new BufferedReader(new InputStreamReader(new URL(myIpURL).openStream())).readLine();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return localhost;
    }

    public static String getLocalHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOG.error(e.getMessage());
        }
        return localhost;
    }

    /**
     * Try to find an available port in the range of ports given as parameters.
     *
     * Note : If bad arguments given,the default ephemeral ports range for dynamic or private ports
     * is used to find one as suggested by the Internet Assigned Numbers Authority (IANA).
     *
     * @param firstPort
     * @param lastPort
     * @return port <OR> -1 if no port is assignable for use
     */
    public static int getAvailablePort(Integer firstPort, Integer lastPort) {
        if (firstPort == null || firstPort <= 0)
            firstPort = 49152;
        if (lastPort == null || lastPort <= 0)
            lastPort = 65535;

        for (int port = firstPort; port <= lastPort; port++) {
            try {
                ServerSocket sock = new ServerSocket(port);
                sock.close();
                return port;
            } catch (IOException ex) {
                LOG.info("port " + port + " is not available. Trying next port..");
            }
        }
        return -1;
    }
}
