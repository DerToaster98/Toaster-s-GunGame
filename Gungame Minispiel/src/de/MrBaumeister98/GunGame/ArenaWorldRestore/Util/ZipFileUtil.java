package de.MrBaumeister98.GunGame.ArenaWorldRestore.Util;
//CODE BY: Arceus02
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public final class ZipFileUtil {
	public static void zipDirectory(File dir, File zipFile) throws IOException {
		FileOutputStream fout = new FileOutputStream(zipFile);
		ZipOutputStream zout = new ZipOutputStream(fout);
		zipSubDirectory("", dir, zout);
		zout.close();
	}

	private static void zipSubDirectory(String basePath, File dir, ZipOutputStream zout) throws IOException {
		byte[] buffer = new byte[4096];
		File[] files = dir.listFiles();
		/*ArrayList<File> files = new ArrayList<File>();
		File dataFolder = new File(dir.getAbsolutePath() + "/data");
		File advancementFolder = new File(dir.getAbsolutePath() + "/advancements");
		File regionsFolder = new File(dir.getAbsolutePath() + "/region");
		for(File f : dataFolder.listFiles()) {
			files.add(f);
		}
		for(File f : advancementFolder.listFiles()) {
			files.add(f);
		}
		for(File f : regionsFolder.listFiles()) {
			files.add(f);
		}*/
		for (File file : files) {
			if (file.isDirectory()) {
				String path = basePath + file.getName() + "/";
				zout.putNextEntry(new ZipEntry(path));
				zipSubDirectory(path, file, zout);
				zout.closeEntry();
			} else if(!(file.getName().equalsIgnoreCase("uid.dat") || file.getName().equalsIgnoreCase("level.dat") || file.getName().equalsIgnoreCase("session.lock"))){
				FileInputStream fin = new FileInputStream(file);
				zout.putNextEntry(new ZipEntry(basePath + file.getName()));
				int length;
				while ((length = fin.read(buffer)) > 0) {
					zout.write(buffer, 0, length);
				}
				zout.closeEntry();
				fin.close();
			}
		}
	}

	public static void unzipFileIntoDirectory(File file, File jiniHomeParentDir) throws ZipException, IOException {
		@SuppressWarnings("resource")
		ZipFile zipFile = new ZipFile(file);
		Enumeration<?> files = zipFile.entries();
		File f = null;
		FileOutputStream fos = null;

		while (files.hasMoreElements()) {
			try {
				ZipEntry entry = (ZipEntry) files.nextElement();
				InputStream eis = zipFile.getInputStream(entry);
				byte[] buffer = new byte[1024];
				int bytesRead = 0;

				f = new File(jiniHomeParentDir.getAbsolutePath() + File.separator + entry.getName());

				if (entry.isDirectory()) {
					f.mkdirs();
					continue;
				} else {
					f.getParentFile().mkdirs();
					f.createNewFile();
				}

				fos = new FileOutputStream(f);

				while ((bytesRead = eis.read(buffer)) != -1) {
					fos.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}
	}
}
