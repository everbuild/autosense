package be.everbuild.autosense;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created by Evert on 25/07/15.
 */
public interface Shared {
    File HOME_FILE = new File(System.getProperty("user.home"), ".autosense");
    String HOME_PATH = HOME_FILE.getPath();
    Charset UTF8 = Charset.forName("UTF-8");
    Charset CHARSET = UTF8;
}
