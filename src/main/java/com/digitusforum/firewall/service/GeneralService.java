package com.digitusforum.firewall.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public class GeneralService {
    public static final String MEDIA_BUCKET_NAME = "sales-plataform-media";
    private static AWSCredentials credentials = new BasicAWSCredentials(
            "AKIAWJKDMDKOMXTIBPHL",
            "jG9bduJlJsg4xQEiHdJCtKmd5vqM+xOwTktU5x2g"
    );
    public static AmazonS3 s3client = (AmazonS3Client) AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.SA_EAST_1)
            .build();

    public static boolean isProduction() {
        if (System.getenv("development") == null) {
            return true;
        } else {
            return false;
        }
    }

    public static String getFileExtension(MultipartFile multipartFile) {
        String extension = "";
        if (multipartFile == null)
            return "";
        String name = multipartFile.getOriginalFilename();
        return name.substring(name.length() - 5);
    }

    public static String generateMediaBucketName(String projectId, String ownerId) {
        return MEDIA_BUCKET_NAME + "/" + projectId + "/" + ownerId;
    }

    public static File convertMultiPartToFile(MultipartFile file) {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convFile;
    }

    public boolean pastOneMinute(ZonedDateTime createdIn) {
        return ZonedDateTime.now().isAfter(createdIn.plusMinutes(1));
    }


    public static String maxFormat(ZonedDateTime time) {
        if (time == null)
            return "";
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'-0000'"));
    }

    public static String dollarCurrency(BigDecimal number) {
        if (number == null)
            return "";
        return NumberFormat
                .getInstance(Locale.US)
                .format(number.setScale(2, BigDecimal.ROUND_HALF_EVEN));
    }

    public static String realCurrency(BigDecimal number) {
        if (number == null)
            return "";
        return "R$ " + NumberFormat
                .getInstance(new Locale("pt", "BR"))
                .format(number.setScale(2, BigDecimal.ROUND_HALF_EVEN));
    }

}
