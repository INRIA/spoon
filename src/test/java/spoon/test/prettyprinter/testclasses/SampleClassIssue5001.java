package spoon.test.prettyprinter.testclasses;

public class SampleClassIssue5001 {
    String binaryIpStart = "start";
    String binaryIpEnd = "end";
    String sql = "Select distinct t.NETWORK_IP, t.NETWORK_IP1, t.NETWORK_IP2, t.NETWORK_IP3, t.NETWORK_IP4 " +
            "from (SELECT DISTINCT t1.ipv4digit1 || '.' || t1.ipv4digit2 || '.' || t1.ipv4digit3 " +
            " || '.0' network_ip, " +
            " TO_NUMBER (t1.ipv4digit1) network_ip1, " +
            " TO_NUMBER (t1.ipv4digit2) network_ip2, " +
            " TO_NUMBER (t1.ipv4digit3) network_ip3, " +
            " TO_NUMBER ('0') network_ip4, t1.t2_team_id, " +
            " t1.system_owner_id, t1.system_owner_team_id " +
            " FROM ip_info t1 " +
            " where t1.binary_ip >= '" + binaryIpStart + "' " +
            " and t1.binary_ip <= '" + binaryIpEnd + "' " +
            " ORDER BY network_ip1, network_ip2, network_ip3  " +
            " ) t order by t.NETWORK_IP1,t.NETWORK_IP2,t.NETWORK_IP3,t.NETWORK_IP4 ";
}