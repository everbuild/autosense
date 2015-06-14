package be.everbuild.autosense.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Configuration {

    private List<Button> buttons;
    private List<Light> lights;
    private List<Module> modules;
    private Long maxPressDuration;
    private String host;
    private Integer port;

    public List<Button> getButtons() {
        return buttons;
    }

    public List<Light> getLights() {
        return lights;
    }

    public List<Module> getModules() {
        return modules;
    }

    public Long getMaxPressDuration() {
        return maxPressDuration;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public static class Button {
        private String id;
        private String name;
        private String onPress;
        private String onRelease;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getOnPress() {
            return onPress;
        }

        public String getOnRelease() {
            return onRelease;
        }
    }

    public static class Light {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public static class Module {
        private String id;
        private Integer busNumber;
        private String address;
        private Map<Integer, String> buttons = Collections.emptyMap();
        private Map<Integer, String> lights = Collections.emptyMap();

        public String getId() {
            return id;
        }

        public Integer getBusNumber() {
            return busNumber;
        }

        public String getAddress() {
            return address;
        }

        public Map<Integer, String> getButtons() {
            return buttons;
        }

        public Map<Integer, String> getLights() {
            return lights;
        }
    }
}
