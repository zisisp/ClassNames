package com.example.demo;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.DeflaterInputStream;
import java.util.zip.InflaterInputStream;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.util.DigestUtils;

@Log
@SpringBootApplication
public class DemoApplication {

    @Value("${basePath}")
    private String basePath;

    @Value("${contains}")
    private String contains;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


    @Component
    public class PostC {

        @PostConstruct
        public void run() throws IOException {
            log.info("START");
            List<Path> el = getPaths(Paths.get(basePath));

            el.forEach(this::convertFile);
            log.info("FINISHED");
//            readSaveFile();


        }

        private void readSaveFile() {
            Path path = Paths.get(
                "c:\\Users\\zisis\\AppData\\Local\\StoneShard\\characters_v1\\character_2\\exitsave_1\\data.sav");

            FileInputStream fis2 = null;
            try {
//                fis2 = new FileInputStream("c:\\Users\\zisis\\AppData\\Local\\StoneShard\\characters_v1\\character_2\\save_1\\data.sav");
//                InflaterInputStream iis = new InflaterInputStream(fis2);
//                FileOutputStream fos1 = new FileOutputStream("c:\\Users\\zisis\\AppData\\Local\\StoneShard\\characters_v1\\character_2\\save_1\\data.json");
//                int oneByte;
//                while ((oneByte = iis.read()) != -1) {
//                    fos1.write(oneByte);
//                }
//                fos1.close();
//                iis.close();

                fis2 = new FileInputStream("c:\\Users\\zisis\\AppData\\Local\\StoneShard\\characters_v1\\character_2\\save_1\\data.json");
                StringBuffer sb=new StringBuffer();

                String json = new String(fis2.readAllBytes(),StandardCharsets.UTF_8);
                String md5=CalcMd5(json, "character_2", "save_1");
                json=json+md5;
                InputStream jsonIS=new ByteArrayInputStream(json.getBytes());
                DeflaterInputStream dis = new DeflaterInputStream(jsonIS);
                FileOutputStream fos2 = new FileOutputStream("c:\\Users\\zisis\\AppData\\Local\\StoneShard\\newSave.sav.json");
                int twoByte;
                while ((twoByte = dis.read()) != -1) {
                    fos2.write(twoByte);
                }
                fis2.close();
                jsonIS.close();
                fos2.close();
                dis.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        private String CalcMd5(String json, String character, String save)
        {
            //see scr_slotSaveDataMapSave in decompiled game code:
            //salt: "stOne!characters_v1!" + character_N + "!" + save_folder + "!shArd"
            var salt = "stOne!characters_v1!" + character +
                "!" + save + "!shArd";
            //var salt = "stOne!characters_v1!shArd";
            var md5Input = (json + salt).getBytes(StandardCharsets.UTF_8);

            return DigestUtils.md5DigestAsHex(md5Input);
        }

        private List<Path> getPaths(Path path) throws IOException {
            List<Path> el;
            if (contains == null || contains.length() == 0) {
                el = Files.find(path, 1, (xPath, a) ->
                                xPath.getFileName().toString().endsWith(".txt"))
                        .toList();
            } else {
                el = Files.find(path, 1, (xPath, a) ->
                                xPath.getFileName().toString().contains(contains) &&
                                        xPath.getFileName().toString().endsWith(".txt"))
                        .toList();
            }
            return el;
        }

        private void convertFile(Path x) {
            log.info("======Starting to convert filename: " + x + " =======");
            try {
                List<String> strings = Files.readAllLines(x);
                int counter = 1;
                File toWrite = new File(x.getFileName() + ".srt");
                StringBuilder stringToWrite = new StringBuilder();
                if (strings.isEmpty()) {
                    log.info("========= No files found =========");
                    return;
                }
                for (String line : strings) {
                    //ignore
                    if (line.matches("[0-9].*")) {
                        line = convertLine(line);
                        line = counter + "\n" + line;
                        counter++;
                    }
                    stringToWrite.append(line).append("\n");
                }
                Files.writeString(toWrite.toPath(), stringToWrite.toString(), Charset.defaultCharset());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private String convertLine(String line) {
            //FROM:	00:00:18:01 - 00:00:19:16
            //TO:  	00:00:18,010 --> 00:00:19,160
            String regex = "([0-9]{2}:[0-9]{2}:[0-9]{2}):([0-9]{2}) - ([0-9]{2}:[0-9]{2}:[0-9]{2}):([0-9]{2})";

            return Pattern.compile(regex).matcher(line).replaceAll("$1,$20 --> $3,$40");
        }
    }
}