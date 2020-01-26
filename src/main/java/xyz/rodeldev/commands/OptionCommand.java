package xyz.rodeldev.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.rodeldev.Helper;
import xyz.rodeldev.XMaterial;
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
                Helper.sendMessage(sender, "%s (default: %s)", option.getName(), option.getDefaultValue().toString());
            }
        }else if(args.length==1){
            Optional<String> optionString = session.getYouiInventory().getOptionAsString(args[0]);
            if(optionString.isPresent()){
                Helper.sendMessage(sender, "&aValue of %s = %s", args[0], optionString.get());
            }else{
                Helper.sendMessage(sender, "&cCan't find value of %s", args[0]);
            }
        }else if(args.length>=2){
            String lastArgs[] = new String[args.length-1];
            System.arraycopy(args, 1, lastArgs, 0, lastArgs.length);

            Option<?> option = template.getOption(args[0]);
            if(option==null){
                Helper.sendMessage(sender, "&cCan't found option %s", args[0]);
                return true;
            }

            ValidationResult result;
            
            if(option.getDefaultValue() instanceof ItemStack){
                if(!args[1].equalsIgnoreCase("hand")){
                    Helper.sendMessage(sender, "Please use /youi option <option-name> hand");
                    return true;
                }

                ItemStack hand = player.getItemInHand();
                if(hand==null || hand.getType()==Material.AIR){
                    return true;
                }

                result = session.getYouiInventory().setOptionValue(args[0], hand);
            }else{
                result = session.getYouiInventory().setOptionValue(args[0], Arrays.asList(lastArgs).stream().collect(Collectors.joining(" ")));
            }
            if(result.getError().isPresent()){
                Helper.sendMessage(sender, result.getError().get());
            }else{
                Helper.sendMessage(sender, "Option updated!");
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
                    }else if(option.getDefaultValue() instanceof ItemStack){
                        result.add("hand");
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