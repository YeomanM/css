package com.demo;

import com.alibaba.fastjson.JSON;
import org.apache.fontbox.ttf.GlyphRenderer;
import org.junit.Before;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * @author 冯宇明
 * @version 1.0
 * @date 2020/1/6
 * @desc
 */
public class Test {

    private String xml = "F:\\soft\\java\\JetBrains\\project\\css\\temp.xml";
    private String woffXml = "F:\\soft\\java\\JetBrains\\project\\css\\woff.xml";

    @org.junit.Test
    public void test() throws IOException {
//        Map<String, List<GlyphRenderer.Point>> map = ConvertTTF2Points.getUniMap2Points("F:\\soft\\java\\JetBrains\\project\\css\\c2.ttf");
        Map<String, List<GlyphRenderer.Point>> map =
                ConvertTTF2Points.getUniMap2Points("F:\\soft\\java\\JetBrains\\project\\css\\src\\main\\resources\\woff\\ttf2.ttf");
        Map<String,String> result = ConvertTTF2Points.getUniMap2Value(xml, map);
        System.out.println(JSON.toJSONString(result));
    }

    @org.junit.Test
    public void test2() throws IOException, DataFormatException {
        String path = "F:\\soft\\java\\JetBrains\\project\\css\\src\\main\\resources\\woff\\test1.woff";
//        String path = "F:\\soft\\java\\JetBrains\\project\\css\\woff.woff";
//        String ttfPathName = "F:\\soft\\java\\JetBrains\\project\\css\\woff1.ttf";
        String ttfPathName = "F:\\soft\\java\\JetBrains\\project\\css\\src\\main\\resources\\woff\\ttf1.ttf";
        Convert2TTF.woffToTTF(path, ttfPathName);
        Map<String, List<GlyphRenderer.Point>> map = ConvertTTF2Points.getUniMap2Points(ttfPathName);
        Map<String,String> result = ConvertTTF2Points.getUniMap2Value(woffXml, map);
        System.out.println(JSON.toJSONString(result));
    }

    @org.junit.Test
    public void test3() throws IOException, DataFormatException {
//        String base64Str = "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";
//        String base64Str ="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";
        String base64Str = "d09GRgABAAAAAAjMAAsAAAAADKwAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAABHU1VCAAABCAAAADMAAABCsP6z7U9TLzIAAAE8AAAARAAAAFZW7lhhY21hcAAAAYAAAAC6AAACTDdbo7ZnbHlmAAACPAAABEAAAAUoyCBqn2hlYWQAAAZ8AAAALwAAADYYwJbSaGhlYQAABqwAAAAcAAAAJAeKAzlobXR4AAAGyAAAABIAAAAwGp4AAGxvY2EAAAbcAAAAGgAAABoHsgZCbWF4cAAABvgAAAAfAAAAIAEZAEVuYW1lAAAHGAAAAVcAAAKFkAhoC3Bvc3QAAAhwAAAAWwAAAI8GS527eJxjYGRgYOBikGPQYWB0cfMJYeBgYGGAAJAMY05meiJQDMoDyrGAaQ4gZoOIAgCKIwNPAHicY2BksmCcwMDKwMHUyXSGgYGhH0IzvmYwYuRgYGBiYGVmwAoC0lxTGBwYKn7cZ9b5r8MQw6zDcAUozAiSAwDrhgv0eJzFksENgzAMRX8KpS300BtIrFCxATOwDhP0xLEbsEFP3YYcYACUCHGjPzGXSnBtHb1ItiPb+g6AI4CA3EkIqDcUnL0YVT4eIPbxEA/6GW6MnFHrXJe66e1QjJVJbTuZuVsWvtjPbJlixa3jMgdOF7NXgBMiXJD4CaOdSj8w9b/W33b193P1ElKvcESdC9QPuhTcnnUjuH32VqC6GArB/YOxEqg4TCpQe9hW4BYwGcH1nTsB4Qeu1kO0AAB4nE1US2wbRRie2Y13vfbaibF3p37U7+za67Xjxz7seNfvxAlNmjRN7dRJ2rRJKGlICIJbVSJUChdUVeLCAW70UCRu7aESHCLE44JUCSFRod6AE3dOuOxurIid0/yjnf/7v8cACMCrv0ER+AAGgFpifGFfChifWf8No7EfQcmoh6HKKhLPJeKkqpBKqajIirUjEK+oxh6FoY8gWcQyPiIR5zlevT8+rzb6KUEPTmLuMSc2ve7IRMpCNo8PPNPVXjtSN35rJpOfHKzOzFCDx60OjvWP0ynHEk7iWr0Qi0SinYmNfDHMDLYX2IlCdu76x9O1vguYSF+9wCjsOUCAB8ArqTUDH7K6k4S1DAhqyYfYomrAM5ByJMFkAoH99cNyyBaicHdIkpZvz2vYIMvdFc67HXK6PHtegUt3ess9itrMLBaPKlU5tWycLQyeP61qevXJk5kS3eg8svr/YXDzC3CCcQBssk2GKo4YfhziHlyF3eEPcHWRemPNvkW9/Cv4zL0Gfx8fnsOu0M8gdML28DE9msFp8JsEikE3d0on6YZkDRp8jkYxuZQli2EWQWtGlVUVWeJ445SBX9LspJyJZZDL5cptSlcbdY9Qkub3Z7sXO+/1uzw9/MjhcwsJXhXSl3RNFDBKRhyayoT7cnFK3byizzUC0fbWya9bq4Wp3vC7rJgxGsGUvHThKkWpQm3khReYB/sGJAwvmKyOvECQvNdG2shT+hkTL2dq/ykX+HAgVqYj2bh3pQUP4PBlKjaT3Go3YWX5aLfWsF/7up6O734uil5fBMG38ZWx7xHCKa21t925TmniBjnq+6fB8wnwGyqrNkSM1CVQQjF9Z61Yyg5/difjcS7L0Ii335wgqYmozqqUOPwM2742UZsUKrVWtd7tH2Sy1dqdg/rUFH/mcXiM/WSpSHrMq08vrUMvfGcxLWQ1tlwLsl53GSZs2IUo8jNrwZI/Sfvhv5ERvjFDwxwAkyhm+BAbgVIkgx+CTJjh+L8viVOJY/AfehhyOkJpf4zX5CArLOrNi3CDqum3D+UmERJZb76QY/w0/Zod3tJ6nmwgnw8GYwUu46+8+fqCvlesOLTcQjUglVAsmo6jOKTPnWXDZWASQNlKbsnKgBERWWIk3vKUZSnVDC3JnokGc5A3Y2LpKz+g9UIuiEI+N0FCfyHcXb/bbO6s5DWNtGt6cb0vq4FdyDlXK9WcKKTfv/RFR/F6o1ElUA7itnEiEwzce+uo3cJ57uH9B49E0ZXI90kobq5jtvLU8lwuNVg0NcDP8CIwCWRDZwtc0bJTGI6eE5VTVAKx5igSZ746eBieJsEqEOjpYbNwMl0lbnVm7x/ttVoUNVdzIVEpJGOdvajoiOIJLiFEgwFG6H1w2Q7fTe/s8UpDa++n2ZvtreOGvtO98ZUSD4eSNzrRmD78NjUTcRIE5fKl7q1erv8HLXr1PXicY2BkYGAA4tlJ9Zfj+W2+MnCzMIDArQsrpyLo/zdYGJguA7kcDEwgUQBfdAyKAHicY2BkYGDW+a/DEMPCAAJAkpEBFfAAADNiAc14nGNhAIIUBgYmS+IwAEI2ArcAAAAAAAAADABaAJoAvAESAU4BfgGeAegCQAKUAAB4nGNgZGBg4GGwZGBmAAEmIOYCQgaG/2A+AwAPZAFfAHicZZG7bsJAFETHPPIAKUKJlCaKtE3SEMxDqVA6JCgjUdAbswYjv7RekEiXD8h35RPSpcsnpM9grhvHK++eOzN3fSUDuMY3HJyee74ndnDB6sQ1nONBuE79SbhBfhZuoo0X4TPqM+EWungVbuMGb7zBaVyyGuND2EEHn8I1XOFLuE79R7hB/hVu4tZpCp+h49wJt7BwusJtPDrvLaUmRntWr9TyoII0sT3fMybUhk7op8lRmuv1LvJMWZbnQps8TBM1dAelNNOJNuVt+X49sjZQgUljNaWroyhVmUm32rfuxtps3O8Hort+GnM8xTWBgYYHy33FeokD9wApEmo9+PQMV0jfSE9I9eiXqTm9NXaIimzVrdaL4qac+rFWGMLF4F9qxlRSJKuz5djzayOqlunjrIY9MWkqvZqTRGSFrPC2VHzqLjZFV8af3ecKKnm3mCH+A9idcsEAeJxtirsOgCAQBG99oYj/IoJKq8L9i42diZ9vPFq3mcxkqaA8Tf8zKFCiQo0GCi06aPQwGAiPuq8zjXYXzlP6yCFy7i6KO7sKrc/dsxf3m8u/cIgv7IheHU0XmwA=";
        Convert2TTF.base64ToTTF(base64Str, "F:\\soft\\java\\JetBrains\\project\\css\\src\\main\\resources\\woff\\ttf2.ttf");
    }

    @org.junit.Test
    public void test4() throws IOException, DataFormatException {
//        String base64Str = "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";
//        String base64Str = "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";
        String base64Str = "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";


        InputStream is = Convert2TTF.base64ToTTFStream(base64Str);
        Map<String,String> result = ConvertTTF2Points.getUniMap2Value(xml, ConvertTTF2Points.getUniMap2Points(is));
        System.out.println(JSON.toJSONString(result));
    }



}
