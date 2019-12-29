package xyz.rodeldev.commands;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.rodeldev.Helper;
import xyz.rodeldev.YouiPlugin;
import xyz.rodeldev.session.Session;
import xyz.rodeldev.templates.Option;
import xyz.rodeldev.templates.Template;
import xyz.rodeldev.templates.ValidationResult;

public class OptionCommand extends ISubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Session session = YouiPlugin.getInstance().getSessionManager().getSession(player.getUniqueId());
        if(session==null){
            Helper.sendMessage(sender, "&cYou are not editing any menu, please use /youi resume <menu> to enter the session");
            return true;
        }

        Template template = session.getTemplate();

        if(args.length==0){
            Helper.sendMessage(sender, "&cPlease specify an option, available:");
            for(Option<?> option : template.getOptions()){
                Helper.sendMessage(sender, option.getName()+" (default: "+option.getDefaultValue().toString()+")");
            }
        }else if(args.length==1){
            Optional<String> optionString = session.getYouiInventory().getOptionAsString(args[0]);
            if(optionString.isPresent()){
                Helper.sendMessage(sender, "&aValue of "+args[0]+" "+optionString.get());
            }else{
                Helper.sendMessage(sender, "&cCan't find value of "+args[0]);
            }
        }else if(args.length==2){
            ValidationResult result = 
            session.getYouiInventory().setOptionValue(args[0], args[1]);
            if(result.getError().isPresent()){
                Helper.sendMessage(sender, result.getError().get());
            }else{
                Helper.sendMessage(sender, "Option changed!");
            }
            session.save();
        }

        return true;
    }

    @Override
    public void tabComplete(CommandSender sender, String[] args, List<String> result) {
        Player player = (Player) sender;
        Session session = YouiPlugin.getInstance().getSessionManager().getSession(player.getUniqueId());
        if(session!=null){
            Template template = session.getTemplate();
            if(args.length==1){
                result.addAll(template.getOptions().stream().map(Option::getName).collect(Collectors.toList()));
            }

            if(args.length==2){
                Option<?> option = template.getOption(args[0]);
                if(option!=null){
                    if(option.getDefaultValue().getClass().isEnum()){
                        for(Object enumConstant : option.getDefaultValue().getClass().getEnumConstants()){
                            result.add(((Enum<?>)enumConstant).name());
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

    @Override
    public String getHelp() {
        return super.getHelp()+" <option> [new-value] &7(Get or set an option on the current editing menu)";
    }

    @Override
    public String getName() {
        return "option";
    }
}