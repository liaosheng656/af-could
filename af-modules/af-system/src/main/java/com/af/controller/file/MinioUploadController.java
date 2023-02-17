package com.af.controller.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.annotation.Resource;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.GetPresignedObjectUrlArgs.Builder;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import com.af.common.vo.Result;

/**
 * 上传
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/04/01 16:57:06 
 *
 */
@Api(tags = "上传相关")
@RequestMapping("upload")
@RestController
public class MinioUploadController {

    @Resource
    private MinioClient minioClient;

    private String bucketName = "test";
    
    /**
     * 普通文件上传
     * @param file
     * @return
     */
    @ApiOperation("minio普通文件上传")
    @PostMapping("/minioCommonUpload")
    public Result<Object> minioCommonUpload(MultipartFile file){

        try {
        	BucketExistsArgs existsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
        	
            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists(existsArgs);
            if(isExist) {
            	
              //bucket早已存在
              System.out.println("Bucket already exists.");
            } else {
            	MakeBucketArgs bucketArgs = MakeBucketArgs.builder().bucket(bucketName).build();
              //不存在则创建
              minioClient.makeBucket(bucketArgs);
            }
            
            String contentType = file.getContentType();
            
            //流的格式
            contentType = "application/octet-stream";
            
            
            //文件上传到abc123文件夹下
            PutObjectArgs objectArgs1 = PutObjectArgs.builder()
            										.object("abc123/"+file.getOriginalFilename())
            										.bucket(bucketName)
            										.contentType(contentType)
            										.stream(file.getInputStream(),file.getSize(),-1)
            										.build();
            
            minioClient.putObject(objectArgs1);
//            Result.builder().
            return Result.ok();
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("普通文件上传失败！");
        }
    }
    
    /**
     * 获取对应文件的下载路径
     * @param fileName 文件名称
     * @return
     */
    @ApiOperation("minio获取下载链接")
    @GetMapping("/downloadUrl")
    public Result<String> downloadUrl(String fileName) {
    	
        GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(fileName)
                //默认是7天有效
                 .expiry(15*24*60*60)
                .build();
        String url = null;
		try {
			url = minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return Result.ok(url);
    }
    
    /**
     * 下载文件
     * @param fileName
     * @return
     */
    @ApiImplicitParam(name="fileName",value="文件名称",required=true)
    @ApiOperation("minio获取下载文件")
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam(name="fileName", defaultValue = "0", required = true) String fileName) {
    	
        ResponseEntity<byte[]> responseEntity = null;
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
        	in = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
            out = new ByteArrayOutputStream();
            IOUtils.copy(in, out);
            //封装返回值
            byte[] bytes = out.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            try {
                headers.add("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            headers.setContentLength(bytes.length);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setAccessControlExposeHeaders(Arrays.asList("*"));
            responseEntity = new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseEntity;
    }
    
    /**
     * 获取minio上传路径
     * @param fileName
     * @return
     */
	@ApiImplicitParam(name="fileName",value="文件名称",required=true)
    @ApiOperation("获取minio上传路径")
    @GetMapping("/uploadUrl")
    public String uploadUrl(@RequestParam(name="fileName", defaultValue = "0", required = true) String fileName) {
		
    	GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
    		.bucket("test2")
    		.method(Method.PUT)
    		.object(fileName).build();
    	String presignedObjectUrl = null;
    	try {
			presignedObjectUrl = minioClient.getPresignedObjectUrl(args);
			
		} catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
				| InvalidResponseException | NoSuchAlgorithmException | XmlParserException | ServerException
				| IOException e) {
			e.printStackTrace();
		}
    	return presignedObjectUrl;

    }
}
