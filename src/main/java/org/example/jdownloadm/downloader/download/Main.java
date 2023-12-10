package org.example.jdownloadm.downloader.download;


import java.io.IOException;
import java.net.URISyntaxException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException {
//      new Download("https://ash-speed.hetzner.com/1GB.bin", "src/main/resources/parts").run();
      new Download("https://download.microsoft.com/download/8/1/d/81d1f546-f951-45c5-964d-56bdbd758ba4/w2k3sp2_3959_usa_x64fre_spcd.iso", "src/main/resources/parts").run();
//      new Download("https://get.videolan.org/vlc/3.0.20/win32/vlc-3.0.20-win32.exe", "src/main/resources/parts").run();
    }


}