package be.everbuild.autosense.config;

import be.everbuild.autosense.Shared;
import be.everbuild.autosense.context.AutomationContext;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by Evert on 21/07/15.
 */
public class JavaScriptConfigurator {
    private static final Logger log = LoggerFactory.getLogger(JavaScriptConfigurator.class);

    private final AutomationContext context;
    private final ScriptEngineManager scriptEngineManager;
    private final String configFilePath;

    public JavaScriptConfigurator(AutomationContext context) {
        this(context, System.getProperty("config"));
    }

    public JavaScriptConfigurator(AutomationContext context, String configFilePath) {
        this.context = context;
        this.configFilePath = configFilePath != null ? configFilePath : getDefaultConfigPath();
        scriptEngineManager = new ScriptEngineManager();
        scriptEngineManager.put("context", context);
        apply();
    }

    private String getDefaultConfigPath() {
        return (new File(Shared.HOME_FILE, "config.js")).getPath();
    }

    private void apply() {
        NashornScriptEngine engine = getScriptEngine();
        try {
            evalFromClasspath(engine, "/lib.js");
            evalFromFile(engine, configFilePath);
        } catch (ScriptException e1) {
            log.error("Config error: {}", e1.getMessage());
        } catch (FileNotFoundException e2) {
            log.error("Config file not found: {}", e2.getMessage());
        }
    }

    private NashornScriptEngine getScriptEngine() {
        return (NashornScriptEngine) scriptEngineManager.getEngineByName("nashorn");
    }

    private static void evalFromClasspath(ScriptEngine engine, String path) throws ScriptException {
        try(InputStream stream = JavaScriptConfigurator.class.getResourceAsStream(path)) {
            eval(engine, stream, path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void evalFromFile(ScriptEngine engine, String path) throws FileNotFoundException, ScriptException {
        try(InputStream stream = new FileInputStream(path)) {
            eval(engine, stream, path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void eval(ScriptEngine engine, InputStream stream, String filename) throws ScriptException {
        engine.put(ScriptEngine.FILENAME, filename);
        engine.eval(new InputStreamReader(stream, Shared.CHARSET));
    }
}
