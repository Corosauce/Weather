package weather;

import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SoundLoader {
	@ForgeSubscribe
    public void onSound(SoundLoadEvent event) {
		
		registerSound(event.manager, "waterfall.ogg", "/resources/sound/waterfall.ogg");
		
		
    }
    
    private void registerSound(SoundManager manager, String name, String path) {
        try {
            URL filePath = SoundLoader.class.getResource(path);
            if (filePath != null) {
                manager.soundPoolSounds.addSound(name, filePath);
            } else {
                throw new FileNotFoundException();
            }
        } catch (Exception ex) {
            System.out.println(String.format("Warning: unable to load sound file %s", path));
        }
    }
    
    private void registerStreaming(SoundManager manager, String name, String path) {
        try {
            URL filePath = SoundLoader.class.getResource(path);
            if (filePath != null) {
                manager.soundPoolStreaming.addSound(name, filePath);
            } else {
                throw new FileNotFoundException();
            }
        } catch (Exception ex) {
            System.out.println(String.format("Warning: unable to load sound file %s"));
        }
    }

}
