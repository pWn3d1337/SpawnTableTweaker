package spawntabletweaker;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.Level;

import crafttweaker.CraftTweakerAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.scoreboard.IScoreCriteria.EnumRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRiver;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * The minecraft related code to add spawns
 */
public class SpawnTableTweakerMC {
	private static Map<Class<? extends Entity>, EntityEntry> entityMap;
	private static final ResourceLocation ENTITY_CLASS_TO_ENTRY = new ResourceLocation("forge:entity_class_to_entry");
	
	private static Field BiomeTags = null;
	
	public static void preInit() {
		try {
			BiomeTags = BiomeDictionary.Type.class.getDeclaredField("byName");
			BiomeTags.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		entityMap = GameRegistry.findRegistry(EntityEntry.class).getSlaveMap(ENTITY_CLASS_TO_ENTRY, Map.class);	
	}
	
	public static void addSpawnVanillaOverworldNoOcean(String registryName, int weight, int min, int max, String creaturetype) {
		addSpawn(registryName, weight, min, max, creaturetype, getVanillaOverWorldNoOceanBiomes());
	}
	
	public static void removeSpawn(String registryName, String creaturetype, String... names) {
		Biome[] biomes = getBiomeList(names);
		if(biomes!=null) {
			removeSpawn(registryName, creaturetype, biomes);
		}
	}
	
	public static void removeSpawn(String registryName, String creaturetype, Biome[] biomes) {
		
		Class<? extends EntityLiving> entClass =null;
		
		for (EntityEntry e: entityMap.values()) {
			
			if(registryName.equals(e.getRegistryName().toString())){
				if (EntityLiving.class.isAssignableFrom(e.getEntityClass())){
					entClass = (Class<? extends EntityLiving>) e.getEntityClass();
				}
				break;
			}
		};
		if(entClass!=null) {
			
			
			EnumCreatureType type = getEnumCreatureType(creaturetype);
			
			if(type!=null) {
				
				if(biomes!=null) {
					EntityRegistry.removeSpawn(entClass, type, biomes);
					log(Level.INFO, "Removing SpawnEntries for: "+entClass.getCanonicalName());
				}
			} else {
				log(Level.ERROR, "Invalid Enum Constant: "+creaturetype);
			}
			
		} else {
			log(Level.ERROR, "Invalid Entity Registration: "+registryName);
		}
	}
	
	public static void removeSpawnAllBiomes(String registryName, String creaturetype) {
		removeSpawn(registryName, creaturetype, getAllBiomes());
	}
	
	public static void addSpawn(String registryName, int weight, int min, int max, String creaturetype, String... names) {
		Biome[] biomes = getBiomeList(names);
		if(biomes!=null) {
			addSpawn(registryName, weight, min, max, creaturetype, biomes);
		}
	}
	
	public static void addSpawnToTags(String registryName, int weight, int min, int max, String creaturetype, String... tags) {
		BiomeDictionary.Type[] t = getTags(tags);
		if(t!=null) {
			Biome[] biomes = getBiomesWithTags(t);
			if(biomes!=null) {
				addSpawn(registryName, weight, min, max, creaturetype, biomes);
			}
		}
	}
	
	public static void addSpawnToTagsBlacklist(String registryName, int weight, int min, int max, String creaturetype, String... tags) {
		BiomeDictionary.Type[] t = getTags(tags);
		if(t!=null) {
			Biome[] biomes = getBiomesWithoutTags(t);
			if(biomes!=null) {
				addSpawn(registryName, weight, min, max, creaturetype, biomes);
			}
		}
	}

	public static void addSpawn(String registryName, int weight, int min, int max, String creaturetype, Biome[] biomes) {
		Class<? extends EntityLiving> entClass =null;
		
		for (EntityEntry e: entityMap.values()) {
			
			//System.out.println("E:"+e.getRegistryName());
			if(registryName.equals(e.getRegistryName().toString())){
				if (EntityLiving.class.isAssignableFrom(e.getEntityClass())){
					entClass = (Class<? extends EntityLiving>) e.getEntityClass();
				}
				break;
			}
		};
		if(entClass!=null) {
			
			
			EnumCreatureType type = getEnumCreatureType(creaturetype);
			
			if(type!=null) {
				
				if(biomes!=null) {
					EntityRegistry.addSpawn(entClass, weight, min, max, type, biomes);
					log(Level.INFO, "Successfully added SpawnEntry for: "+entClass.getCanonicalName());
				}
			} else {
				log(Level.ERROR, "Invalid Enum Constant: "+creaturetype);
			}
			
		} else {
			log(Level.ERROR, "Invalid Entity Registration: "+registryName);
		}
		
	}

	private static EnumCreatureType getEnumCreatureType(String type) {
		try {
			EnumCreatureType e = EnumCreatureType.valueOf(type);
			return e;
		} catch (IllegalArgumentException ex) {
			return null;
		} catch (NullPointerException ex) {
			return null;
		}
	}
	
	private static Biome[] getBiomeList(String... names) {
		Biome[] biomes = new Biome[names.length];
		
		for(int i=0;i<biomes.length;i++) {
			biomes[i]=Biome.REGISTRY.getObject(new ResourceLocation(names[i]));
			if(biomes[i]==null) {
				log(Level.ERROR, "Invalid Biome Registration: "+names[i]);
				return null;
			}
			
		}
		
		return biomes;
	}
	
	private static Biome[] getAllBiomes() {
		Set<ResourceLocation> keys = Biome.REGISTRY.getKeys();
		
		Set<Entry<ResourceLocation, Biome>> entries = ForgeRegistries.BIOMES.getEntries(); 
		Biome[] biomes = new Biome[entries.size()];
		int i=0;
		for(Entry<ResourceLocation, Biome> e: entries) {
			biomes[i++]=e.getValue();
		}
		return biomes;
	}
	
	public static BiomeDictionary.Type[] getTags(String... names){
		BiomeDictionary.Type[] types = new BiomeDictionary.Type[names.length];
		
		 Map<String, Type> byName = null;
		try {
			byName = (Map<String, Type>) BiomeTags.get(BiomeDictionary.Type.class);
			
			for(int i=0;i<names.length;i++) {
				types[i]=byName.get(names[i]);
				if(types[i]==null) {
					log(Level.ERROR, "Invalid BiomeDictionary Tag: "+names[i]);
					return null;
				}
			}
			
			return types;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Biome[] getBiomesWithTags(BiomeDictionary.Type[] tags) {
		
		LinkedList<Biome> biomes = new LinkedList<>();
		ForgeRegistries.BIOMES.forEach(b -> {
			if(hasAnyOfTags(b, tags)) {
				biomes.add(b);
			}
		});
		return biomes.toArray(new Biome[biomes.size()]);
	}
	
	public static Biome[] getBiomesWithoutTags(BiomeDictionary.Type[] tags) {
		
		LinkedList<Biome> biomes = new LinkedList<>();
		ForgeRegistries.BIOMES.forEach(b -> {
			if(!hasAnyOfTags(b, tags)) {
				biomes.add(b);
			}
		});
		return biomes.toArray(new Biome[biomes.size()]);
	}
	
	protected static boolean hasAnyOfTags(Biome biome, BiomeDictionary.Type[] tags) {
		
		for(BiomeDictionary.Type tag : tags) {
			if(BiomeDictionary.hasType(biome, tag)) {
				return true;
			}
		}
		return false;
	}
	
	private static Biome[] getVanillaOverWorldNoOceanBiomes() {
		Biome[] biomes = {
				//Biomes.OCEAN,
				//Biomes.DEFAULT,
				Biomes.PLAINS,
				Biomes.DESERT,
				Biomes.EXTREME_HILLS,
				Biomes.FOREST,
				Biomes.TAIGA,
				Biomes.SWAMPLAND,
				Biomes.RIVER,
				//Biomes.HELL,
				Biomes.SKY,
				//Biomes.FROZEN_OCEAN,
				Biomes.FROZEN_RIVER,
				Biomes.ICE_PLAINS,
				Biomes.ICE_MOUNTAINS,
				Biomes.MUSHROOM_ISLAND,
				Biomes.MUSHROOM_ISLAND_SHORE,
				Biomes.BEACH,
				Biomes.DESERT_HILLS,
				Biomes.FOREST_HILLS,
				Biomes.TAIGA_HILLS,
				Biomes.EXTREME_HILLS_EDGE,
				Biomes.JUNGLE,
				Biomes.JUNGLE_HILLS,
				Biomes.JUNGLE_EDGE,
				//Biomes.DEEP_OCEAN,
				Biomes.STONE_BEACH,
				Biomes.COLD_BEACH,
				Biomes.BIRCH_FOREST,
				Biomes.BIRCH_FOREST_HILLS,
				Biomes.ROOFED_FOREST,
				Biomes.COLD_TAIGA,
				Biomes.COLD_TAIGA_HILLS,
				Biomes.REDWOOD_TAIGA,
				Biomes.REDWOOD_TAIGA_HILLS,
				Biomes.EXTREME_HILLS_WITH_TREES,
				Biomes.SAVANNA,
				Biomes.SAVANNA_PLATEAU,
				Biomes.MESA,
				Biomes.MESA_ROCK,
				Biomes.MESA_CLEAR_ROCK,
				//Biomes.VOID,
				Biomes.MUTATED_PLAINS,
				Biomes.MUTATED_DESERT,
				Biomes.MUTATED_EXTREME_HILLS,
				Biomes.MUTATED_FOREST,
				Biomes.MUTATED_TAIGA,
				Biomes.MUTATED_SWAMPLAND,
				Biomes.MUTATED_ICE_FLATS,
				Biomes.MUTATED_JUNGLE,
				Biomes.MUTATED_JUNGLE_EDGE,
				Biomes.MUTATED_BIRCH_FOREST,
				Biomes.MUTATED_BIRCH_FOREST_HILLS,
				Biomes.MUTATED_ROOFED_FOREST,
				Biomes.MUTATED_TAIGA_COLD,
				Biomes.MUTATED_REDWOOD_TAIGA,
				Biomes.MUTATED_REDWOOD_TAIGA_HILLS,
				Biomes.MUTATED_EXTREME_HILLS_WITH_TREES,
				Biomes.MUTATED_SAVANNA,
				Biomes.MUTATED_SAVANNA_ROCK,
				Biomes.MUTATED_MESA,
				Biomes.MUTATED_MESA_ROCK,
				Biomes.MUTATED_MESA_CLEAR_ROCK
		};
		return biomes;
	}
	
	protected static void log(Level l, String message) {
		if(l == Level.ERROR) {
		CraftTweakerAPI.logError(message);
		} else if(l == Level.WARN) {
			CraftTweakerAPI.logWarning(message);
		} else if(l == Level.INFO) {
			CraftTweakerAPI.logInfo(message);
		} else {
			CraftTweakerAPI.logDefault(message);
		}
	}
}
