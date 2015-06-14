package be.everbuild.autosense.model.lightcontrol.light;

public class LightFactory {
    private int count = 0;

    public Light createLight(String id, String name) {
        count ++;
        if(id == null) {
            id = "" + count;
        }
        if(name == null) {
            name = "button " + id;
        }
        return new Light(id, name);
    }
}
