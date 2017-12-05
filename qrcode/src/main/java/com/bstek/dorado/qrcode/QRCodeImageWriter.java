package com.bstek.dorado.qrcode;

import com.bstek.dorado.qrcode.QRCodeImage;
import com.bstek.dorado.qrcode.QRCodeRenderUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 *在dorado-qrcode的基础上修改了一个bug，新版本zxing的encode方法有所改动
 * @author &HY& 545546940@qq.com
 *
 */
public final class QRCodeImageWriter implements Writer {
	
	
	private  BufferedImage logoImage;
	private  QRCode qrcode;
	private  QRCodeImage qrcodeImage;
	private  int multiple = 1;
	private String logoSavePath;
	

	public QRCodeImageWriter() {
	}
	
	public String getLogoSavePath() {
		return logoSavePath;
	}

	public void setLogoSavePath(String logoSavePath) {
		this.logoSavePath = logoSavePath;
	}

	/**
	 * Renders a {@link BitMatrix} as an image, where "false" bits are rendered
	 * as QRCodeImage.backgroundColor, and "true" bits are rendered as  QRCodeImage.color.
	 * @throws IOException 
	 */
	public BufferedImage toBufferedImage(BitMatrix matrix) throws IOException {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		
		image = QRCodeRenderUtil.renderStyle(qrcodeImage.getRenderStyle(), image, matrix,
				Integer.parseInt( qrcodeImage.getBackgroundColor().indexOf("#")!=-1?qrcodeImage.getBackgroundColor().substring(1):qrcodeImage.getBackgroundColor(),16),
				Integer.parseInt( qrcodeImage.getColor().indexOf("#")!=-1?qrcodeImage.getColor().substring(1):qrcodeImage.getColor(),16), multiple);
		if(qrcodeImage.getLogo()!=null&&!"".equals(qrcodeImage.getLogo())){
			return embedLogoImage(image,qrcodeImage.getLogo());
		}else{
			return image;
		}
	}
	

	/**
	 * Embed logo to image锛�
	 * 
	 * @param inputImage
	 * @param logoPath
	 * @return
	 * @throws IOException 
	 */
	public BufferedImage embedLogoImage(BufferedImage inputImage,String logoPath) throws IOException{
		int width=inputImage.getWidth();
		int height=inputImage.getHeight();
		if(logoPath.equals("DEFAULT_LOGO")){
			logoImage=ImageIO.read(getClass().getResourceAsStream("/dorado/resources/qrcode-logo.gif"));
		}else if(logoPath.toLowerCase().startsWith("http://")){
			URL url = new URL(logoPath);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			logoImage = ImageIO.read(connection.getInputStream());
		}else {
			logoImage = ImageIO.read(new File(logoSavePath+"/"+logoPath));
		}
		
		double logoSideRatio = ((double)logoImage.getHeight()/(double)logoImage.getWidth());
		int logoArea = (int) ((double)inputImage.getHeight()*(double)inputImage.getWidth()*0.07);
		int lWidth = (int) Math.sqrt(logoArea/logoSideRatio)/2;
		int lHeight = (int)(lWidth * logoSideRatio);
		int lArc=(int) (Math.min(lWidth,lHeight) * 0.5);
		
		RoundRectangle2D rect=new RoundRectangle2D.Float( width / 2 - lWidth,height / 2 - lHeight,2 * lWidth,2 * lHeight,lArc,lArc);
		GeneralPath gp=new GeneralPath();
		inputImage.getGraphics().setColor(Color.WHITE);
		gp.append(rect,true);
		Graphics2D g2d = inputImage.createGraphics();
		g2d.setClip(gp);
		g2d.drawImage(logoImage, width / 2 - lWidth, height / 2 - lHeight, 2 * lWidth,
				2 * lHeight, null);
		
		Graphics2D g2d2 = inputImage.createGraphics();
		g2d2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		g2d2.setColor(Color.WHITE);
		g2d2.drawRoundRect( width / 2 - lWidth,height / 2 - lHeight,2 * lWidth,2 * lHeight,lArc,lArc);		
		
		
		for (int i = 1; i < 3; i++) {
			
			g2d2.setStroke(new BasicStroke(i+1, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));
			g2d2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f-0.25f*i));
			g2d2.drawRoundRect( width / 2 - lWidth-i,height / 2 - lHeight-i,2 * lWidth+2*i,2 * lHeight+2*i,lArc,lArc);
		}
		g2d2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		g2d2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
		g2d2.drawRoundRect( width / 2 - lWidth,height / 2 - lHeight,2 * lWidth-1,2 * lHeight-1,lArc,lArc);	
		return inputImage;
	}

	/**
	 * Writes a {@link BitMatrix} to a file.
	 * 
	 * @see #toBufferedImage(BitMatrix)
	 */
	public void writeToFile(BitMatrix matrix, String format, File file)
			throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, file)) {
			throw new IOException("Could not write an image of format "
					+ format + " to " + file);
		}
	}

	/**
	 * Writes a {@link BitMatrix} to a stream.
	 * 
	 * @see #toBufferedImage(BitMatrix)
	 */
	public void writeToStream(BitMatrix matrix, String format,
			OutputStream stream) throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, stream)) {
			throw new IOException("Could not write an image of format "
					+ format);
		}
	}

	@Override
	public BitMatrix encode(String contents, BarcodeFormat format, int width,
			int height) throws WriterException {
		return encode(contents, format, width, height, null);
	}
	@Override
	public BitMatrix encode(String contents, BarcodeFormat format, int width,
			int height, Map<EncodeHintType, ?> hints) throws WriterException {

		if (contents.length() == 0) {
			throw new IllegalArgumentException("Found empty contents");
		}

		if (format != BarcodeFormat.QR_CODE) {
			throw new IllegalArgumentException(
					"Can only encode QR_CODE, but got " + format);
		}

		if (width < 0 || height < 0) {
			throw new IllegalArgumentException(
					"Requested dimensions are too small: " + width + 'x'
							+ height);
		}

		ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
		if (hints != null) {
			ErrorCorrectionLevel requestedECLevel = (ErrorCorrectionLevel) hints
					.get(EncodeHintType.ERROR_CORRECTION);
			if (requestedECLevel != null) {
				errorCorrectionLevel = requestedECLevel;
			}
		}

		qrcode = Encoder.encode(contents, errorCorrectionLevel, hints);
		return renderResult(qrcode, width, height);
	}

	public BitMatrix encode(QRCodeImage image) throws WriterException, UnsupportedEncodingException {
		this.qrcodeImage = image;
		
		Hashtable<EncodeHintType, Object> hints= new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.valueOf(image.getErrorCorrectionLevel()));
        if(image.getCharacterSet()!=null){
        	hints.put(EncodeHintType.CHARACTER_SET,image.getCharacterSet());
        }
		return encode(image.getData(),  BarcodeFormat.QR_CODE, Integer.parseInt(image.getWidth()), Integer.parseInt(image.getHeight()), hints);
	}

	private BitMatrix renderResult(QRCode code, int width, int height) {
		ByteMatrix input = code.getMatrix();
		if (input == null) {
			throw new IllegalStateException();
		}
		int inputWidth = input.getWidth();
		int inputHeight = input.getHeight();
		int qrWidth = inputWidth + (qrcodeImage.getQuietZoneSize() << 1);
		int qrHeight = inputHeight + (qrcodeImage.getQuietZoneSize()<< 1);
		int outputWidth = Math.max(width, qrWidth);
		int outputHeight = Math.max(height, qrHeight);

		multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
		int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
		int topPadding = (outputHeight - (inputHeight * multiple)) / 2;

		BitMatrix output = new BitMatrix(outputWidth, outputHeight);
		for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += multiple) {
			for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
				if (input.get(inputX, inputY) == 1) {
					output.setRegion(outputX, outputY, multiple, multiple);
				}
			}
		}

		return output;
	}
}