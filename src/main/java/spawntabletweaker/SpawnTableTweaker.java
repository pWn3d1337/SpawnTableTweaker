package spawntabletweaker;

import org.apache.logging.log4j.Logger;

import crafttweaker.CraftTweakerAPI;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = SpawnTableTweaker.MODID, name = SpawnTableTweaker.NAME, version = SpawnTableTweaker.VERSION, acceptedMinecraftVersions=SpawnTableTweaker.MCVERSION, dependencies=SpawnTableTweaker.DEPENDENCIES, updateJSON=SpawnTableTweaker.UPDATEURL)
public class SpawnTableTweaker {

    public static final String MODID = "spawntabletweaker";
    public static final String NAME = "SpawnTableTweaker";
    public static final String MCVERSION = "1.12.2";
    public static final String VERSION = "1.0";
    public static final String FORGE_BUILD = "14.23.5.2768";
    public static final String DEPENDENCIES = "required:forge@["+FORGE_BUILD+",);required-after:crafttweaker";
    public static final String UPDATEURL = "https://raw.githubusercontent.com/pWn3d1337/SpawnTableTweaker/master/update.json";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //logger = event.getModLog();
        SpawnTableTweakerMC.preInit();
        CraftTweakerAPI.registerClass(SpawnTableTweakerCT.class);
    }

}
