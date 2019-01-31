package de.MrBaumeister98.GunGame.ArenaWorldRestore.Util;
//CODE BY: Arceus02
import java.io.File;

public class FileUtil {
	
	public static void delete(File file) {
		if(file.isDirectory()) {
			for(File subfile : file.listFiles()) {
				delete(subfile);
			}
		} else {
			file.delete();
		}
	}
	
}
