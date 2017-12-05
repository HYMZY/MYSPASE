package com.bstek.dorado.qrcode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.bstek.dorado.core.Configure;
import com.bstek.dorado.qrcode.QRCodeImage;
import com.bstek.dorado.qrcode.QRCodeImageWriter;
import com.bstek.dorado.web.resolver.AbstractResolver;
import com.google.zxing.common.BitMatrix;

/**
 * 鐢熸垚QRCode
 * 
 * @author Gavin Zhang (mailto:gavin.zhang@bstek.com)
 * @since 2012-08-01
 */
public class QRCodeResolver extends AbstractResolver{
	
	public QRCodeResolver(){
		ImageIO.setUseCache(false);
	}
	
	
	protected ModelAndView doHandleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        
        String data = request.getParameter("dt");
        String outputformat = request.getParameter("opf");
        String characterSet = request.getParameter("cs");
        String errorCorrectionLevel = request.getParameter("ecl");
        String sideLength = request.getParameter("sl");
        String backgroundColor = request.getParameter("bgclr");
        String color = request.getParameter("clr");
        String quietZoneSize = request.getParameter("qzs");
        String logo = request.getParameter("lg");
        String renderStyle = request.getParameter("rs"); 
        QRCodeImage qrcodeImage=new QRCodeImage();
        
        qrcodeImage.setData(UriUtils.decode(data, "utf8"));
        
        if(sideLength!=null&&!"".equals(sideLength)){
        	qrcodeImage.setHeight(sideLength);
        	qrcodeImage.setWidth(sideLength);
        }else{
        	qrcodeImage.setHeight("400");
        	qrcodeImage.setWidth("400");
        }
        
        if(quietZoneSize!=null&&!"".equals(quietZoneSize)){
        	qrcodeImage.setQuietZoneSize(Integer.parseInt(quietZoneSize));
        }
       
        if(renderStyle!=null&&!"".equals(renderStyle)){
        	qrcodeImage.setRenderStyle(Integer.parseInt(renderStyle));
        }
        
        if(outputformat!=null&&!"".equals(outputformat)){
        	qrcodeImage.setOutputFomart(outputformat);
        }
        
        if(backgroundColor!=null&&!"".equals(backgroundColor)){
        	qrcodeImage.setBackgroundColor(backgroundColor);
        }
        
        if(color!=null&&!"".equals(color)){
        	qrcodeImage.setColor(color);
        }
        
        if(logo!=null&&!"".equals(logo)){
        	qrcodeImage.setLogo(logo);
        }
        
        if(characterSet!=null&&!"".equals(characterSet)){
        	qrcodeImage.setCharacterSet(characterSet);
        }
        
        if(errorCorrectionLevel!=null&&!"".equals(errorCorrectionLevel)){
        	qrcodeImage.setErrorCorrectionLevel(qrcodeImage.getLogo()!=null?"H":errorCorrectionLevel);
        }
        
        QRCodeImageWriter imageWriter=new QRCodeImageWriter();
        imageWriter.setLogoSavePath(parsePath(Configure.getString("qrcodeImange.fileUploadPath"),request.getSession().getServletContext()));
        javax.servlet.ServletOutputStream out = response.getOutputStream();
        try{
        	BitMatrix bitMatrix = imageWriter.encode(qrcodeImage);   
			imageWriter.writeToStream(bitMatrix, qrcodeImage.getOutputFomart(), out);
		}catch(Exception e){
			e.printStackTrace();
			BufferedImage buffImage = ImageIO.read(getClass().getResourceAsStream("/dorado/resources/qrcode-err.gif"));
			ImageIO.write(buffImage, outputformat, out);
		}
		return null;
	}
	
	/**
	 * 娣诲姞configure.properties涓鍙栧浘鐗囪矾寰勭殑閰嶇疆qrcodeImange.fileUploadPath
	 * 鍐呯疆鏇挎崲璺緞鍙傛暟
	 * \\webapp.home\\ 涓� webroot璺緞
	 * \\webapp.tmpdir\\ 涓� 搴旂敤鏈嶅姟鐨勪复鏃惰矾寰�
	 * 濡傛棤浠ヤ笂涓ょ鐩存帴鍐欑洰褰曡涓虹郴缁熻矾寰�
	 * 濡傛灉configure.properties涓棤瀹氫箟qrcodeImange.fileUploadPath 灏嗛粯璁や负/webapp.home/
	 * 
	 * @param uploadDir
	 * @param servletContext
	 * @return
	 */
	private String parsePath(String uploadDir,ServletContext servletContext ) {
		if(StringUtils.isEmpty(uploadDir)){
			uploadDir="/webapp.home/";
		}
	    Pattern pattern = Pattern.compile("^([/|\\\\](.*)[/|\\\\])");;
	    Matcher matcher = pattern.matcher(uploadDir);
	    if (matcher.find()) {
	      String var = matcher.group(2);
	      StringBuffer dirBuf = new StringBuffer();
	      if (var.equals("webapp.home")) {
	    	  matcher.appendReplacement(dirBuf, servletContext.getRealPath("/").replaceAll("\\\\", "/"));
	    	  dirBuf.append("/");
	      } else if (var.equals("webapp.tmpdir")) {
	    	  matcher.appendReplacement(dirBuf, WebUtils.getTempDir(servletContext).getAbsolutePath().replaceAll("\\\\", "/"));
	    	  dirBuf.append("/");
	      } else {
	    	  matcher.appendReplacement(dirBuf, matcher.group(0).replaceAll("\\\\", "/"));
	      }
	      matcher.appendTail(dirBuf);
	      return dirBuf.toString();
	    }
	    return uploadDir;
	  }
	
	public static void main(String[] args) {
		try {
			QRCodeImage qi=new QRCodeImage();
			qi.setCharacterSet("utf8");
			qi.setOutputFomart("png");
			qi.setRenderStyle(3);
			qi.setHeight("70");
			qi.setWidth("70");
			qi.setBackgroundColor("#F0F0F0");
			qi.setColor("#F2860D");
			qi.setErrorCorrectionLevel("H");
			qi.setQuietZoneSize(4);
			qi.setLogo("DEFAULT_LOGO");
//			qrcodeImage1.setLogo("http://bsdn.org/content/10254/images/logo.png");
			
			qi.setData("Dorado 7 Addon QRCode");
	        
	        QRCodeImageWriter qrcodeImageWriter = new QRCodeImageWriter();
			BitMatrix bitMatrix = qrcodeImageWriter.encode(qi);
			qrcodeImageWriter.writeToFile(bitMatrix, qi.getOutputFomart(), new File("D://1.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
