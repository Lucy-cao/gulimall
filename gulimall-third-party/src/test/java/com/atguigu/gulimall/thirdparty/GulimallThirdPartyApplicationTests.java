package com.atguigu.gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.InputStream;

@SpringBootTest
class GulimallThirdPartyApplicationTests {
    @Autowired(required = false)
    private OSSClient ossClient;

    @Test
    void testUploadOss() {
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "lucy-typora";
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String objectName = "img/笔记本选择参考5.jpg";

        try {
            //上传文件流
            InputStream inputStream = new FileInputStream("/Users/caolu/Desktop/笔记本选择参考.jpg");
            ossClient.putObject(bucketName, objectName, inputStream);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (Exception e) {
            System.out.println("Error Message:" + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    @Test
    void contextLoads() {
    }

}
