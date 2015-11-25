package be.everbuild.autosense.user;

import be.everbuild.autosense.Shared;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Evert on 25/07/15.
 */
public class UserStore {
    private static final Logger log = LoggerFactory.getLogger(UserStore.class);
    private static final File USERS_FILE = new File(Shared.HOME_FILE, "users");
    public static final char SEPARATOR = ':';

    private final Map<String, SimpleUser> users = new HashMap<>();

    public UserStore() {
        load();
    }

    public List<SimpleUser> getAllUsers() {
        return ImmutableList.copyOf(users.values());
    }

    public SimpleUser getUser(String name) {
        return users.get(name);
    }

    public void saveUser(SimpleUser user) {
        users.put(user.getName(), user);
        save();
    }

    public void deleteUser(String name) {
        users.remove(name);
        save();
    }

    private void load() {
        try {
            if (USERS_FILE.exists()) {
                List<String> lines = FileUtils.readLines(USERS_FILE, Shared.CHARSET);
                users.clear();
                lines.stream()
                        .map(line -> StringUtils.split(line, SEPARATOR))
                        .filter(parts -> parts.length >= 3)
                        .map(parts -> new SimpleUser(parts[0], new HashedPass(parts[1]), Role.valueOf(parts[2])))
                        .forEach(user -> {
                            users.put(user.getName(), user);
                        });
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void save() {
        List<Object> lines = users.values().stream()
                .map(user -> String.format("%s:%s:%s", user.getName(), user.getPassword(), user.getRole().name()))
                .collect(Collectors.toList());
        try {
            FileUtils.writeLines(USERS_FILE, lines);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
