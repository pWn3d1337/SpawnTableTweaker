package spawntabletweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * The craft tweaker methods that are called from zen scripts
 */

@ZenClass("mods.spawntabletweaker")
public class SpawnTableTweakerCT {

	@ZenMethod
	public static void addSpawn(String registryName, int weight, int min, int max, String creatureType, String[] names) {
		CraftTweakerAPI.apply(new addSpawnAction(registryName, weight, min, max, creatureType, names, MODE.NAMES));
	}
	
	@ZenMethod
	public static void addSpawnTagsWhitelist(String registryName, int weight, int min, int max, String creatureType, String[] names) {
		CraftTweakerAPI.apply(new addSpawnAction(registryName, weight, min, max, creatureType, names, MODE.TAG_WHITELIST));
	}
	
	@ZenMethod
	public static void addSpawnTagsBlacklist(String registryName, int weight, int min, int max, String creatureType, String[] names) {
		CraftTweakerAPI.apply(new addSpawnAction(registryName, weight, min, max, creatureType, names, MODE.TAG_BLACKLIST));
	}
	
	@ZenMethod
	public static void removeSpawn(String registryName, String creatureType, String[] names) {
		CraftTweakerAPI.apply(new removeSpawnAction(registryName, creatureType, names));
	}
	
	@ZenMethod
	public static void removeSpawn(String registryName, String creatureType) {
		CraftTweakerAPI.apply(new removeSpawnAction(registryName, creatureType));
	}
	
	private static enum MODE {
		NAMES("biome names"),
		TAG_WHITELIST("biome tag whitelist"),
		TAG_BLACKLIST("biome tag blacklist");
		
		private String name;
		private MODE(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}
	
	private static class addSpawnAction implements IAction {
		private String registryName;
		private int weight;
		private int min;
		private int max;
		private String creaturetype;
		private String[] names;
		private MODE mode;
		
		public addSpawnAction(String registryName, int weight, int min, int max, String creatureType, String[] names,
				MODE mode) {
			super();
			this.registryName = registryName;
			this.weight = weight;
			this.min = min;
			this.max = max;
			this.creaturetype = creatureType;
			this.names = names;
			this.mode = mode;
		}

		@Override
		public void apply() {
			switch(mode) {
			case NAMES:
				SpawnTableTweakerMC.addSpawn(registryName, weight, min, max, creaturetype, names);
				break;
			case TAG_WHITELIST:
				SpawnTableTweakerMC.addSpawnToTags(registryName, weight, min, max, creaturetype, names);
				break;
			case TAG_BLACKLIST:
				SpawnTableTweakerMC.addSpawnToTagsBlacklist(registryName, weight, min, max, creaturetype, names);
				break;
			}
		}

		@Override
		public String describe() {
			return "Adding Spawns for "+registryName+" with: "+mode;
		}
		
	}
	
	private static class removeSpawnAction implements IAction {
		private String registryName;
		private String creaturetype;
		private String[] names;
		boolean all=false;
		
		public removeSpawnAction(String registryName, String creatureType, String[] names) {
			super();
			this.registryName = registryName;
			this.creaturetype = creatureType;
			this.names = names;
		}
		
		public removeSpawnAction(String registryName, String creatureType) {
			super();
			this.registryName = registryName;
			this.creaturetype = creatureType;
			this.names = null;
			all=true;
		}

		@Override
		public void apply() {
			if(!all) {
				SpawnTableTweakerMC.removeSpawn(registryName, creaturetype, names);
			} else {
				SpawnTableTweakerMC.removeSpawnAllBiomes(registryName, creaturetype);
			}
		}

		@Override
		public String describe() {
			return "Removing Spawns for "+registryName+" from "+(all?"all":"passed")+" biomes";
		}
		
	}
}
