package systems.emmelnet.coordsaver;

import net.md_5.bungee.chat.SelectorComponentSerializer;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import javax.sound.midi.MidiDevice;
import java.io.*;
import java.util.stream.Collectors;

public class CoordSaver extends JavaPlugin {

    File saveTo = null;
    String SAVE_FILE = "saved_positions";
    @Override
    public void onEnable()
    {
        File dataFolder = getDataFolder();
        if(!dataFolder.exists())
        {
            dataFolder.mkdir();
        }
        saveTo = new File(getDataFolder(), SAVE_FILE);
        if(!saveTo.exists())
        {
            try {
                saveTo.createNewFile();
                saveTo.setWritable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            saveTo.setWritable(true);
        }
        String[] saved = readFile(saveTo);
        getLogger().info("######## PLUGIN ########");
        getLogger().info("LOADING ALL SAVED POSITIONS...");
        for(int i = 0; i<saved.length; i++)
        {
            getLogger().info(i + ": " + saved[i]);
        }


    }

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        if (command.getName().equalsIgnoreCase("cs")) {
            if(args.length < 1)
                sender.sendMessage("Argument missing: /cs [save / load] name");
            else if (args.length < 2)
            {
                sender.sendMessage("Name missing: /cs [save / load] name");
            }
            else
            {
                if(args[0].equalsIgnoreCase("save") || args[0].equalsIgnoreCase("s"))
                {
                    if(args.length > 2)
                    {
                        if(args.length < 5) {
                            sender.sendMessage(ChatColor.RED + "Coordinates could not be saved because too few coordinates were specified.");
                            return true;
                        }

                        try
                        {
                            double x = Double.parseDouble(args[2]);
                            double y = Double.parseDouble(args[3]);
                            double z = Double.parseDouble(args[4]);
                        }
                        catch (NumberFormatException e)
                        {
                            sender.sendMessage(ChatColor.RED + "Coordinates could not be read.");
                            getLogger().info(e.getMessage());
                            return true;
                        }
                        sender.sendMessage(saveCommandCoords(args[1], args[2], args[3], args[4],  sender));


                    }
                    else
                        sender.sendMessage(saveCommand(args[1], sender));
                }
                else if(args[0].equalsIgnoreCase("load") || args[0].equalsIgnoreCase("l"))
                {
                    sender.sendMessage(loadCommand((args[1])));
                }
                else if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("r"))
                {
                    sender.sendMessage(removeCommand(args[1]));
                }
                else if(args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp"))
                {
                    sender.sendMessage(teleportCommand(args[1], sender));
                }
                else
                {
                    sender.sendMessage("Unknown argument: " + args[0]);
                }
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("sp")) {
            if(args.length < 1)
                sender.sendMessage("Argument missing: /sp name");
            else if(args.length > 1)
            {
                if(args.length < 4) {
                    sender.sendMessage(ChatColor.RED + "Coordinates could not be saved because too few coordinates were specified.");
                    return true;
                }

                try
                {
                    double x = Double.parseDouble(args[1]);
                    double y = Double.parseDouble(args[2]);
                    double z = Double.parseDouble(args[3]);
                }
                catch (NumberFormatException e)
                {
                    sender.sendMessage(ChatColor.RED + "Coordinates could not be read.");
                    getLogger().info(e.getMessage());
                    return true;
                }
                sender.sendMessage(saveCommandCoords(args[0], args[1], args[2], args[3],  sender));


            }
            else
            {
                sender.sendMessage(saveCommand(args[0], sender));
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("lp")) {
            if(args.length < 1)
                sender.sendMessage("Argument missing: /lp name");
            else
            {
                sender.sendMessage(loadCommand(args[0]));
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("rp")) {
            if(args.length < 1)
                sender.sendMessage("Argument missing: /rp name");
            else
            {
                sender.sendMessage(removeCommand(args[0]));
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("tsp")) {
            if(args.length < 1)
                sender.sendMessage("Argument missing: /tsp name");
            else
            {
                sender.sendMessage(teleportCommand(args[0], sender));
            }
            return true;
        }
        return false;
    }

    private String[] readFile(File file)
    {
        String[] stringArray = {};
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = bufferedReader.readLine();
            while(line != null) {
                stringArray = bufferedReader.lines().toArray(String[]::new);
                return stringArray;
            }
            return stringArray;
        } catch (FileNotFoundException e) {
            // TODO: handling
            return stringArray;
        } catch (IOException e) {
            // TODO: handling
            return stringArray;
        }
    }



    private boolean appendFile(File file, String appendContent)
    {
        String[] fileContent = readFile(file);
        String[] newFileContent = new String[fileContent.length + 1];
        int i;
        for(i = 0; i < fileContent.length; i++) {
            newFileContent[i] = fileContent[i];
        }
        newFileContent[i] = appendContent;
        String writeContent = String.join("\n", newFileContent);

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            PrintWriter pw = new PrintWriter(bufferedWriter);
            pw.println("");
            for(i = 0; i < newFileContent.length; i++) {
                pw.println(newFileContent[i]);
                getLogger().info("[AppendFile] wrote '" + newFileContent[i] + "' to '" + file.getName() + "'");
            }
            pw.close();
            return true;
        } catch (IOException e) {
            // Exception handling
            getLogger().info(e.getMessage());
            return false;
        }
    }


    private String saveCommand(String name, CommandSender sender)
    {
        Player executor = Bukkit.getPlayer(sender.getName());
        Location playerLocation = executor.getLocation();
        float x = playerLocation.getBlockX();
        float y = playerLocation.getBlockY();
        float z = playerLocation.getBlockZ();
        String txt =  x + " " + y + " " + z + " " + name + " " + sender.getName();
        if(exists(name))
            return ChatColor.RED + "'" + name + "' already exists";
        if(name.equalsIgnoreCase("all"))
            return ChatColor.RED + "'all' could not be used as name";
        if(appendFile(saveTo, txt))
        {
            return ChatColor.GREEN +"'" + name + "' was successfully saved";
        }
        else
        {
            return ChatColor.RED + "Saving '" + name + "' failed. Contact an administrator for more information.";
        }
    }

    private String saveCommandCoords(String name, String x, String y, String z, CommandSender sender)
    {
        Player executor = Bukkit.getPlayer(sender.getName());
        String txt =  x + " " + y + " " + z + " " + name + " " + sender.getName();
        if(exists(name))
            return ChatColor.RED + "'" + name + "' already exists";
        if(name.equalsIgnoreCase("all"))
            return ChatColor.RED + "'all' could not be used as name";
        if(appendFile(saveTo, txt))
        {
            return ChatColor.GREEN +"'" + name + "' wurde gespeichert";
        }
        else
        {
            return ChatColor.RED + "Saving '" + name + "' failed. Contact an administrator for more information.";
        }
    }

    private String loadCommand(String name)
    {
        String saved = loadByName(name);
        if(name.equalsIgnoreCase("all"))
        {
            String[] all = loadAll();
            String[] newArr = new String[all.length];
            for(int i = 0; i<all.length; i++)
            {
                String title = all[i].split(" ")[3];
                String author = all[i].split(" ")[4];
                newArr[i] = ChatColor.YELLOW + title + ": " + ChatColor.WHITE + all[i].split(" ")[0]  + ", " + all[i].split(" ")[1]  + ", " + all[i].split(" ")[2]  + ChatColor.GRAY+ " (added by " + author + ")";
            }
            return String.join("\n", newArr);
        }
        else if(saved != null)
        {
            String title = saved.split(" ")[3];
            String author = saved.split(" ")[4];
            return ChatColor.YELLOW + title + ": " + ChatColor.WHITE + saved.split(" ")[0]  + ", " + saved.split(" ")[1]  + ", " + saved.split(" ")[2]  + ChatColor.GRAY+ " (added by " + author + ")";
        }
        else
        {
            return ChatColor.RED + "'" + name + "' was not found.";
        }
    }

    private String removeCommand(String name)
    {
        String saved = loadByName(name);
        if(saved != null)
        {
            if(deleteByName(name))
                return "'" + name + "' was successfully deleted.";
            else
                return ChatColor.RED + "'" + name + "' could not be deleted.";
        }
        else
        {
            return ChatColor.RED + "'" + name + "' was not found.";
        }
    }

    private String teleportCommand(String name, CommandSender sender)
    {
        String saved = loadByName(name);
        if(name.equalsIgnoreCase("all"))
        {
            return ChatColor.RED + "Players can't be teleprted to all positions ;)";
        }
        else if(saved != null)
        {
            String title = saved.split(" ")[3];
            String author = saved.split(" ")[4];

            String x = saved.split(" ")[0];
            String y = saved.split(" ")[1];
            String z = saved.split(" ")[2];

            double xf = Double.parseDouble(x);
            double yf = Double.parseDouble(y);
            double zf = Double.parseDouble(z);

            Player executor = Bukkit.getPlayer(sender.getName());

            Location location = new Location(executor.getWorld(), xf, yf, zf);
            float oldX = location.getBlockX();
            float oldY = location.getBlockY();
            float oldZ = location.getBlockZ();
            executor.teleport(location);

            return ChatColor.YELLOW + "You have been teleported to: " + saved.split(" ")[0]  + ", " + saved.split(" ")[1]  + ", " + saved.split(" ")[2] + " ('" + name + "') \n" + ChatColor.GRAY + "Your previous position was: " + oldX + ", " + oldY + "," + oldZ;
        }
        else
        {
            return ChatColor.RED + "'" + name + "' was not found.";
        }
    }


    private String[] loadAll()
    {
        String[] all = readFile(saveTo);
        String[] allWithText = new String[all.length];
        for(int i = 0; i<all.length; i++){
            allWithText[i] = all[i];
        }
        return allWithText;
    }

    private String loadByName(String name)
    {
        String[] a = loadAll();
        String res = null;
        for(int i = 0; i<a.length; i++){
            String str = a[i].split(" ")[3];
            if(str.equalsIgnoreCase(name))
            {
                res = a[i];
            }
        }
        return res;
    }

    private boolean deleteByName(String name)
    {
        String[] allEntries = loadAll();
        String[] updatedEntries = new String[allEntries.length - 1];
        int b = 0;
        for(int i = 0; i<allEntries.length; i++){
            String str = allEntries[i].split(" ")[3];
            if(!str.equalsIgnoreCase(name))
            {
                updatedEntries[b] = allEntries[i];
                b += 1;
            }
        }
        try {
            saveTo.delete();
            saveTo.createNewFile();
            saveTo.setWritable(true);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        boolean success = true;
        for(int i = 0; i<updatedEntries.length; i++)
        {
            if(!appendFile(saveTo, updatedEntries[i]))
                success = false;
        }
        return success;
    }

    private boolean exists(String name)
    {
        String[] a = loadAll();
        boolean ret = false;
        for(int i = 0; i<a.length; i++){
            String str = a[i].split(" ")[3];
            if(str.equalsIgnoreCase(name))
            {
                ret = true;
            }
        }
        return ret;
    }


}
