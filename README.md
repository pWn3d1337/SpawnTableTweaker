# SpawnTableTweaker
Simple Minecraft spawn table addon with crafttweaker interface

The goal of this mod is to allow calling `EnityRegistry.addSpawn(entClass, weight, min, max, type, biomes);` directly from crafttweaker.
Also removing spawns from vanilla spawn table is supported.

The reason I made this mod is to be able to add Techguns npc as regular spawns so they are compatible with other spawn manipulating mods, but the problem is generic enough to be released as standalone mod and can be used with all vanilla or modded npcs.

**Important note**: adding an npc to the spawn table still uses the behaviors of the npcs. If an npc has special constraints it might not work.

**The only requirements for this mod are crafttweaker and forge**.

5 methods are callable from crafttweaker scripts:
which are defined in this code: https://github.com/pWn3d1337/SpawnTableTweaker/blob/master/src/main/java/spawntabletweaker/SpawnTableTweakerCT.java#L16-L39

The Wiki (https://github.com/pWn3d1337/SpawnTableTweaker/wiki) shows examples for these methods.
