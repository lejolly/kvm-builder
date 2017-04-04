package utils;

import java.util.regex.Pattern;

public class IPUtils {

    private static final Pattern IPV4_PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean isValidIP(String ip) {
        return IPV4_PATTERN.matcher(ip).matches();
    }

    public static String getPhysicalIP(int nodeNumber) {
        return "172.30.1." + (nodeNumber + 2);
    }

    public static String getVMIP(int nodeNumber, int vmNumber) {
        return "192.168." + (nodeNumber + 101) + "." + (vmNumber + 2);
    }

    public static String getVMSubnet(int nodeNumber) {
        return "192.168." + (nodeNumber + 101) + ".0/24";
    }

    public static String getVMGateway(int nodeNumber) {
        return "192.168." + (nodeNumber + 101) + ".1";
    }

    public static String getVMStartIP(int nodeNumber) {
        return "192.168." + (nodeNumber + 101) + ".2";
    }

    public static String getVMEndIP(int nodeNumber, int vmsPerNode) {
        return "192.168." + (nodeNumber + 101) + "." + (vmsPerNode + 1);
    }

    public static String getVMSubnetMask() {
        return "255.255.255.0";
    }

}
