package com.sjl.gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Autowired
    private OSSClient ossClient;

    @Test
    void upload() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream("F:\\Desktop\\直角引号.png");
        ossClient.putObject("gulimall-upload", "bbb.png", inputStream);
        ossClient.shutdown();
    }

    @Test
    public void test() {
        LocalDate localDate = LocalDate.now();
        System.out.println("时间：" + localDate.toString());
    }

}
