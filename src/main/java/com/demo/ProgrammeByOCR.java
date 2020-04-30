package com.demo;

import com.demo.utils.CommandUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ProgrammeByOCR {

    public static void main(String[] args) throws Exception {
        System.setProperty("webdriver.gecko.driver","C:\\Program Files (x86)\\Mozilla Firefox\\geckodriver.exe");
        System.setProperty("webdriver.firefox.bin", "C:/Program Files (x86)/Mozilla Firefox/firefox.exe");
        WebDriver driver = new FirefoxDriver();
        File parent = new File("temp");
        if (!parent.exists()) {
            parent.mkdirs();
        }
        driver.get("http://127.0.0.1:8848/test/list.html");

        int x = 1200 - 252, y = 240, high = 60, count = 0;
        for (int i = 0; i < 10; i++) {
            File verifyCodeFile = new File(parent.getAbsolutePath() + "/name.png");

            OutputStream ops = new FileOutputStream(verifyCodeFile);

            //获取验证码图片
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File fullImageFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            BufferedImage image = ImageIO.read(fullImageFile).getSubimage(x, y + i * high, 100, 40);
            ImageIO.write(image, "png", ops);
            ops.flush();
            ops.close();
            CommandUtil.run("tesseract " + verifyCodeFile.getAbsolutePath() + " " + verifyCodeFile.getParent() + "/out --psm 7");
            File out = new File(verifyCodeFile.getParent() + "/out.txt");
            FileInputStream in = new FileInputStream(out);
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            String str = new String(bytes);
            System.out.println(i + " : " + str.split("\n")[0]);
        }

        driver.close();
        parent.deleteOnExit();

    }
}
