package xyz.rodeldev;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.rodeldev.commands.YouiCommand;
import xyz.rodeldev.session.SessionManager;
import xyz.rodeldev.templates.TemplateRegistry;

public class YouiPlugin extends JavaPlugin {

    private SessionManager sessionManager;
    public SessionManager getSessionManager() { return sessionManager; }

    private static YouiPlugin instance;
    public static YouiPlugin getInstance() { return instance; }

    private static FileSystem fileSystem;
    public FileSystem getFileSystem() { return fileSystem; }

    private static TemplateRegistry templateRegistry;
    public TemplateRegistry getTemplateRegistry() {return templateRegistry; }

    @Override
    public void onEnable() {
        instance = this;

        sessionManager = new SessionManager();
        fileSystem = new FileSystem(getDataFolder());
        templateRegistry = new TemplateRegistry();

        YouiCommand youiCommand = new YouiCommand();

        PluginCommand command = getCommand("youi");
        command.setExecutor(youiCommand);
        command.setTabCompleter(youiCommand);

        Bukkit.getPluginManager().registerEvents(new EventDispatcher(), this);

    }

    @Override
    public void onDisable() {

    }
}