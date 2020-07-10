package com.dcall.core.configuration.utils;

import org.h2.tools.Server;
import org.h2.util.New;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;
import java.util.function.Predicate;
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

    public static String getName(final NetworkInterface iface) {
        return iface.getName();
    }

    public static String getName() {
        try {
            return getName(NetworkInterface.getNetworkInterfaces().nextElement());
        } catch (SocketException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    public static String getMacAddress(final NetworkInterface iface) {
        try {
            final byte[] mac = iface.getHardwareAddress();
            final StringBuilder sb = new StringBuilder();

            for (int i = 0; i < mac.length; i++)
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1 ? "-" : "")));

            return sb.toString();
        } catch (SocketException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    public static String getMacAddress(final InetAddress inetAddress) {
        try {
            final byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
            final StringBuilder sb = new StringBuilder();

            for (int i = 0; i < mac.length; i++)
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1 ? "-" : "")));

            return sb.toString();
        } catch (SocketException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    public static String getMacAddress() {
        try {
            return getMacAddress(NetworkInterface.getNetworkInterfaces().nextElement());
        } catch (SocketException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    public static String getAddress(final String addressType, final boolean ignoreVM) {
        String address = "";
        InetAddress lanIp = null;
        Predicate<byte[]> vmCond = ignoreVM ? b -> !isVMMac(b) : b -> true;
        try {
            final Enumeration<NetworkInterface> net = NetworkInterface.getNetworkInterfaces();
            String ipAddress;

            while (net.hasMoreElements()) {
                NetworkInterface element = net.nextElement();
                Enumeration<InetAddress> addresses = element.getInetAddresses();

                while (addresses.hasMoreElements() && element.getHardwareAddress() != null && vmCond.test(element.getHardwareAddress())) {
                    InetAddress ip = addresses.nextElement();
                    if (ip instanceof Inet4Address) {
                        if (ip.isSiteLocalAddress()) {
                            ipAddress = ip.getHostAddress();
                            lanIp = InetAddress.getByName(ipAddress);
                        }
                    }
                }
            }

            if (lanIp == null)
                return null;

            if (addressType.equals("ip")) {
                address = lanIp.toString().replaceAll("^/+", "");
            } else if (addressType.equals("mac")) {
                address = getMacAddress(lanIp);

            } else
                throw new Exception("Specify \"ip\" or \"mac\"");
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return address;
    }

    private static boolean isVMMac(byte[] mac) {
        if(null == mac) return false;
        byte invalidMacs[][] = {
                {0x00, 0x05, 0x69},             //VMWare
                {0x00, 0x1C, 0x14},             //VMWare
                {0x00, 0x0C, 0x29},             //VMWare
                {0x00, 0x50, 0x56},             //VMWare
                {0x08, 0x00, 0x27},             //Virtualbox
                {0x0A, 0x00, 0x27},             //Virtualbox
                {0x00, 0x03, (byte)0xFF},       //Virtual-PC
                {0x00, 0x15, 0x5D}              //Hyper-V
        };

        for (byte[] invalid: invalidMacs){
            if (invalid[0] == mac[0] && invalid[1] == mac[1] && invalid[2] == mac[2]) return true;
        }

        return false;
    }

}
