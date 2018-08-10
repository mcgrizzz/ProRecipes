package mc.mcgrizzz.prorecipes.NBTChecker;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.lang.reflect.Constructor;

public enum MinecraftVersion {

	v1_13_R1("v_1_13_R1",NBTChecker_v1_13_R1.class),
	
	v1_12_R1("v_1_12_R2", NBTChecker_v1_12_R1.class),
	
	v1_11_R1("v_1_11_R1", NBTChecker_v1_11_R1.class),
	
	v1_10_R1("v_1_10_R1", NBTChecker_v1_10_R1.class),
	
	v1_9_R2("v_1_9_R2", NBTChecker_v1_9_R2.class),
	v1_9_R1("v_1_9_R1", NBTChecker_v1_9_R1.class),
	
	v1_8_R3("v_1_8_R3",  NBTChecker_v1_8_R3.class),
	v1_8_R2("v_1_8_R2",  NBTChecker_v1_8_R2.class),
	v1_8_R1("v_1_8_R1", NBTChecker_v1_8_R1.class),
	
	v1_7_R4("v_1_7_R4",  NBTChecker_v1_7_R4.class),
	
	NoVersion("NOPE",  NoVersion.class);
	
	
	String id; 
	Class<? extends NBTChecker> checker;
	NBTChecker cCheck = null;
	
	MinecraftVersion(String id, Class<? extends NBTChecker> checker){
		this.id = id;
		this.checker = checker;
	}
	
	public NBTChecker getChecker(){
		if(cCheck == null){
			try{
				Constructor<?> ctor = checker.getConstructor();
				Object object = ctor.newInstance();
				cCheck = (NBTChecker)object;
				return cCheck;
			}catch(Exception e){
				e.printStackTrace();
				return new NoVersion();
			}
		}else{
			return cCheck;
		}
	}
	
	public static MinecraftVersion fromId(String s){
		for(MinecraftVersion m : values()){
			if(m.name().equalsIgnoreCase(s)){
				return m;
			}
		}
		return NoVersion;
	}

	public static boolean up13(){
		return Integer.valueOf(Bukkit.getVersion().split("\\.")[1].replace(")",""))>12;
	}

	public static Material getMaterial(String v12,String v13){
		return Material.valueOf(up13()?v13:v12);
	}

}
