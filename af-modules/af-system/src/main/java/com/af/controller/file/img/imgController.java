package com.af.controller.file.img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.af.common.util.RedisUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.date.TimeInterval;
//import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Positions;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.filters.ScaleFilter;
import ws.schild.jave.filters.helpers.ForceOriginalAspectRatio;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.info.VideoSize;

/**
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/08/18 23:26:21 
 * 
 */
@Slf4j
@Api(tags = "图片相关")
@RequestMapping("img")
@RestController
public class imgController {

	@Autowired
	private RedisUtil redisUtil;
	
    /**
     * 截取视频中某一帧作为图片
     * @throws IOException 
     */
    @ApiOperation("图片或视频截图")
    @GetMapping("/processImage")
    public  boolean getVideoProcessImage( ) throws IOException {
       
    	//源地址
//    	File videoSource = new File("E:\\视频媒体资源\\Camera Roll\\图片\\14.jpeg");
    	File videoSource = new File("E:\\视频媒体资源\\DSC02468.JPG");
//    	File videoSource = new File("E:\\视频媒体资源\\DSC02376.JPG");
    	
    	//输出地址
    	File imageTarget = new File("E:\\视频媒体资源\\12345");
    	
    	log.info("媒体截图开始 videoSource:{} , imageTarget:{}", videoSource.getAbsolutePath(), imageTarget.getAbsolutePath());
    	
    	
    	File fromPic  = videoSource;
//    	//压缩至指定图片尺寸，保持图片不变形，多余部分裁剪掉
//    	//压缩至指定图片尺寸（例如：横400高300），保持图片不变形，多余部分裁剪掉
    	BufferedImage image = ImageIO.read(fromPic);
//    	
//    	Builder<BufferedImage> builder = null; 
    	int imageWidth = image.getWidth(); 
    	int imageHeitht = image.getHeight(); 
//    	int newWidth = 896;
//    	int newHeitht = 504;
//    	if ((float)newHeitht / newWidth != (float)imageWidth / imageHeitht) { 
//    		if (imageWidth > imageHeitht) { 
//    			image = Thumbnails.of(fromPic).height(newHeitht).asBufferedImage(); 
//    		} else { 
//    			image = Thumbnails.of(fromPic).width(newWidth).asBufferedImage(); 
//    		} 
//    		builder = Thumbnails.of(image).scale(1f).sourceRegion(Positions.CENTER, newWidth, newHeitht).size(newWidth, newHeitht); 
//    	} else { 
//    		builder = Thumbnails.of(image).size(newWidth, newHeitht); 
//    	} 
//    	builder.outputFormat("png").toFile(imageTarget); 
    	//scale 参数是浮点数,大于1表示放大,小于1表示缩小
    	//outputQuality 参数是浮点数,质量压缩,0-1之间 
    	//keepAspectRatio 在调整尺寸时保持比例,默认为true,如果要剪裁到特定的比例,设置为false即可 

    	
        try {
			Thumbnails.of(videoSource)
			.scale(0.12f)
//			.keepAspectRatio(false)
			.outputQuality(0.4f)
//			.size(896, 504)
			.outputFormat("png")
			.toFile(imageTarget);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        MultimediaObject object = new MultimediaObject(videoSource);
        try {
            MultimediaInfo multimediaInfo = object.getInfo();

            if (multimediaInfo.getVideo() == null) {
                log.info("该媒体暂无视频信息..获取截图失败");
                return false;
            }

            VideoAttributes video = new VideoAttributes();
            video.setCodec("png");
            
            Integer height = multimediaInfo.getVideo().getSize().getHeight();
            Integer width = multimediaInfo.getVideo().getSize().getWidth();
            
            
            if(height > width) {
            	video.addFilter(new ScaleFilter(new VideoSize(504, 896), ForceOriginalAspectRatio.DECREASE));
            	video.setBitRate(1000 * 590);
            }else {
            	video.addFilter(new ScaleFilter(new VideoSize(896, 504), ForceOriginalAspectRatio.DECREASE));
            	video.setBitRate(590 * 1000);
            }
            System.out.println(height+"*"+width);
            
            double he = height;
            double wi = width;
            
//            double hw = (double)Math.round((wi/he)*100)/100;
//            
//            int newHeight = 896;
//            int newWidth = (int) (896*hw);
            
//            width/height
            //video.setSize(new VideoSize(896, 504));
            //等比例大小缩放
//            video.addFilter(new ScaleFilter(new VideoSize(newWidth,newHeight ), ForceOriginalAspectRatio.DECREASE));

            //video.setSize(multimediaInfo.getVideo().getSize());

            video.setQuality(2);

            EncodingAttributes attrs = new EncodingAttributes();
            //VideoAttributes attrs = ecodeAttrs.getVideoAttributes().get();
            attrs.setOutputFormat("image2");
            attrs.setDuration(0.01f);//设置转码持续时间（1秒）


//            if (multimediaInfo.getDuration() > 1000) {
//                //取前面 1/3中的 随机帧
//                float offset = RandomUtil.randomInt(0, (int) (multimediaInfo.getDuration() / 1000));
//                log.info("截图偏移秒数:{}", offset);
//                attrs.setOffset(offset);
//            }

//            attrs.setVideoAttributes(video);
//
//            Encoder encoder = new Encoder();
//            encoder.encode(object, imageTarget, attrs);
            return true;
        } catch (Exception e) {
            log.error("获取视频截图异常:msg:{} ,videoSource :{}", e.getMessage(), videoSource.getAbsolutePath(), e);
        } finally {
//            log.info("截图 总耗时:{} 毫秒,videoSource:{} ,imageTarget:{}", timer.intervalMs(), videoSource.getAbsolutePath(), imageTarget.getAbsolutePath());
        }
        return false;
    }
    
    /**
     * 测试OOM内存溢出
     * @return
     * @throws IOException 
     * @throws FileNotFoundException 
     */
	@GetMapping("test008")
	public void test08() throws FileNotFoundException, IOException {
		
		//支持的格式
		String[] arr = ImageIO.getReaderFormatNames();
		for (int x = 0; x < arr.length; x++) {
			
			System.out.println(arr[x]);
		}
        // 文件对象
        File file = new File("E:\\视频媒体资源\\场景3.ai");
//        File file = new File("E:\\视频媒体资源\\Camera Roll\\图片\\8.png");
        
//        String fileStr = "E:\\视频媒体资源\\场景3.ai";
        String fileStr = "E:\\视频媒体资源\\Camera Roll\\图片\\8.png";
        FileInputStream file1 = new FileInputStream(fileStr);
        byte[] bytes = new byte[64];
        file1.read(bytes, 0, bytes.length);
//        int width = ((int) bytes[27] & 0xff) << 8 | ((int) bytes[26] & 0xff);
//        int height = ((int) bytes[29] & 0xff) << 8 | ((int) bytes[28] & 0xff);
        
        int width = ((int) bytes[36] & 0xff) << 8 | ((int) bytes[35] & 0xff);
        int height = ((int) bytes[38] & 0xff) << 8 | ((int) bytes[37] & 0xff);
        System.out.println("width:"+width);
        System.out.println("height:"+height);
        
        // 文件大小；其中file.length()获取的是字节，除以1024可以得到以kb为单位的文件大小
//        long size = file.length() / 1024;
        
//        FileInputStream fileInputStream = new FileInputStream(file);
//        // 图片对象
//        BufferedImage bufferedImage = ImageIO.read(fileInputStream);
//        // 宽度
//        int width1 = bufferedImage.getWidth();
//        // 高度
//        int height1 = bufferedImage.getHeight();
//        // 打印信息
//        System.out.printf("图片大小：%skb；图片宽度：%s像素；图片高度：%s像素", size, width1, height1);
	}
	
	
    /**
     * 测试OOM内存溢出
     * @return
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws ImageReadException 
     */
	@GetMapping("test009")
	public void test09() throws FileNotFoundException, IOException, ImageReadException {
		
		String[] arr = ImageIO.getReaderFormatNames();
		for (int x = 0; x < arr.length; x++) {
//			System.out.println(arr[x]);
		}
        // 文件对象
        File file = new File("E:\\视频媒体资源\\场景3.ai");
//        File file = new File("E:\\视频媒体资源\\Camera Roll\\图片\\8.png");
        
        String fileStr = "E:\\视频媒体资源\\场景3.ai";
//        String fileStr = "E:\\视频媒体资源\\Camera Roll\\图片\\8.png";
        byte[] bytesArray = new byte[(int) file.length()]; 
        
        FileInputStream fis = new FileInputStream(file);
        fis.read(bytesArray); //read file into bytes[]
        fis.close();
        ImageMetadata metadata = Imaging.getMetadata(bytesArray);
        
        System.out.println(metadata);
        
        //图像转换
        Thumbnails.of(fileStr)
        .scale(1f)
        .outputFormat("jpg")
        .toFile("E:\\视频媒体资源\\test009");
	}
	
    /**
     * 截取视频中某一帧作为图片
     */
    @ApiOperation("图片或视频截图")
    @GetMapping("/set")
    public  boolean set( ) {
    	
    	redisUtil.set("set:123", "456");
    	redisUtil.set("set:1234", "456");
    	redisUtil.set("set:1235", "456");
    	
		return true;
    }
    
    /**
     * 截取视频中某一帧作为图片
     */
    @ApiOperation("图片或视频截图")
    @GetMapping("/del")
    public  boolean del( ) {
    	
    	redisUtil.delByprex("set:");
    	
		return true;
    }
}
