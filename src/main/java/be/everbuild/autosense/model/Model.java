package be.everbuild.autosense.model;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private List<Module> modules = new ArrayList<>();

    public List<Module> getModules() {
        return modules; // TODO dont expose mutable list
    }
}
