package xyz.rodeldev;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import junit.framework.Assert;
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
    }

    @Test
    public void testOptions(){
        Assert.assertTrue("Number is not valid type", Option.validType(1));
        Assert.assertTrue("String is not valid type", Option.validType("test"));
        Assert.assertTrue("Enum is not valid type", Option.validType(InventoryType.CHEST));
        Assert.assertTrue("Boolean is not valid type", Option.validType(false));
        Assert.assertFalse("Array is not valid type", Option.validType(new int[0]));

        Option<InventoryType> invType = new Option<>("inventoryType", InventoryType.ANVIL);
        Assert.assertEquals(invType.asEnum("BEACON"), InventoryType.BEACON);
        Object a = InventoryType.ANVIL;
        System.out.println(a instanceof Number);
    }

    @Test
    public void str2num(){
        Assert.assertTrue("str2num", Helper.str2num("1", Integer.class) instanceof Integer);
        Assert.assertTrue("str2num", Helper.str2num("1", Long.class) instanceof Long);
        Assert.assertTrue("str2num", Helper.str2num("1", Float.class) instanceof Float);
        Assert.assertTrue("str2num", Helper.str2num("1", Double.class) instanceof Double);
    }
}
