package client.json.configs.hostconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortBinding {
    private Map<String, List<HostPort>> portMap = new HashMap<>();

    public void setPortMap(String exposedPort, List<HostPort> hostPorts) {
        portMap.put(exposedPort, hostPorts);
    }

    public Map<String, List<HostPort>> getPortMap() {
        return portMap;
    }

    public static class HostPort {
        @JsonProperty("HostIp")
        private String hostIp;
        @JsonProperty("HostPort")
        private String hostPort;

        public HostPort(String hostIp, String hostPort) {
            this.hostIp = hostIp;
            this.hostPort = hostPort;
        }

        public String getHostIp() {
            return hostIp;
        }

        public String getHostPort() {
            return hostPort;
        }
    }
}
