package xyz.rodeldev;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import org.junit.Assert;

import xyz.rodeldev.inventory.PlaceholderInstance;
import xyz.rodeldev.templates.Option;
import xyz.rodeldev.templates.Placeholder;
import xyz.rodeldev.templates.Template;

@RunWith(PowerMockRunner.class)
public class YouiTest {

    public Plugin testPlugin;
    public Plugin testPluginWSpaces;

    @Before
    public void setup(){
        testPlugin = mock(Plugin.class);
        when(testPlugin.getName()).thenReturn("MyPlugin");
        testPluginWSpaces = mock(Plugin.class);
        when(testPluginWSpaces.getName()).thenReturn("My Plugin");
    }

    @Test
    public void testTemplate(){
        Template template = new Template(testPlugin, "template1");
        Assert.assertEquals("myplugin:template1", template.getFullName());
    }

    @Test
    public void testTemplateWSpaces(){
        Template template = new Template(testPluginWSpaces, "template1");
        Assert.assertEquals("my_plugin:template1", template.getFullName());
    }

    @Test
    public void testPlaceholders(){
        Template template = new Template(testPlugin, "template1");
        template.registerPlaceholder(new Placeholder("placeholderTest"));
        template.registerPlaceholder(new Placeholder("brackets{}"));
        template.registerPlaceholder(new Placeholder("brackets[]"));
        Assert.assertNotNull(template.getPlaceholder("placeholderTest"));
        Assert.assertNull(template.getPlaceholder("brackets{}"));
    }

    @Test
    public void testOptions(){
        Assert.assertTrue("Number is valid type", Option.validType(1));
        Assert.assertTrue("String is valid type", Option.validType("test"));
        Assert.assertTrue("Enum is valid type", Option.validType(InventoryType.CHEST));
        Assert.assertTrue("Boolean is valid type", Option.validType(false));
        Assert.assertTrue("ItemStack is valid type", Option.validType(new ItemStack(Material.ANVIL)));
        Assert.assertFalse("Array is not valid type", Option.validType(new int[0]));

        Option<InventoryType> invType = new Option<>("inventoryType", InventoryType.ANVIL);
        Assert.assertEquals(invType.asEnum("BEACON"), InventoryType.BEACON);
    }

    @Test
    public void str2num(){
        Assert.assertTrue("int str2num", Helper.str2num("1", Integer.class) instanceof Integer);
        Assert.assertTrue("long str2num", Helper.str2num("1", Long.class) instanceof Long);
        Assert.assertTrue("float str2num", Helper.str2num("1", Float.class) instanceof Float);
        Assert.assertTrue("double str2num", Helper.str2num("1", Double.class) instanceof Double);
        Assert.assertNull("null str2num", Helper.str2num("asd", Double.class));
    }

    @Test
    public void placeholderData(){
        Assert.assertEquals(PlaceholderInstance.fromPlain("placeholder{\"test\":1}").getPlaceholderName(), "placeholder");
        Assert.assertEquals(PlaceholderInstance.fromPlain("placeholder{\"test\":1}").getPlaceholderData().get().getAsJsonObject().get("test").getAsInt(), 1);
        Assert.assertEquals(PlaceholderInstance.fromPlain(PlaceholderInstance.fromPlain("placeholder{\"test\":1}").toPlain()).toPlain(), "placeholder{\"test\":1}");
    }
}