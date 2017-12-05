package com.bstek.dorado.qrcode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import com.google.zxing.common.BitMatrix;

public class QRCodeRenderUtil {
	public static final int DEFAULT = 0 ;
	public static final int DOTS = 1 ;
	public static final int SIEVE = 2 ;
	public static final int ROUNDNESS = 3 ;
	
	public static BufferedImage renderStyle(int renderingStyle,BufferedImage image,BitMatrix matrix,int bgColor,int color,int multiple){
		BufferedImage outputImage=null;
		switch(renderingStyle){
		case  QRCodeRenderUtil.DOTS:
			renderDotsStyle(image, matrix,bgColor, color, multiple);
			outputImage=blurImage(image);
			break;
		case  QRCodeRenderUtil.SIEVE:
			renderSieveStyle(image, matrix,bgColor, color, multiple);
			outputImage=blurImage(image);
			break;
		case  QRCodeRenderUtil.ROUNDNESS:
			renderRoundnessStyle(image, matrix,bgColor, color, multiple);
			outputImage=blurImage(image);
			break;
		default:
			renderDefaultStyle(image, matrix,bgColor, color);
			outputImage =image;
		}
		
		return outputImage;
	}
	
	private static void renderDefaultStyle(BufferedImage inputImage,BitMatrix matrix,int bgColor,int color){
		for (int x = 0; x < inputImage.getWidth(); x++) {
			for (int y = 0; y < inputImage.getHeight(); y++) {
				inputImage.setRGB(x, y, matrix.get(x, y) ? color : bgColor);
			}
		}
	}
	
	private static void renderDotsStyle(BufferedImage image,BitMatrix matrix,int bgColor,int color,int multiple){
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.setColor(new Color(color));
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				if (matrix.get(x, y)) {
					g.fillOval(x, y, multiple,multiple);
					y = y + multiple-1;
				}
			}
			x = x + multiple-1;
		}
	}
	
	private static void renderSieveStyle(BufferedImage image,BitMatrix matrix,int bgColor,int color,int multiple){
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.setColor(new Color(color));
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				if (matrix.get(x, y)) {
					g.fillRect(x, y, multiple-1,multiple-1);
					y = y + multiple-1;
				}
			}
			x = x + multiple-1;
		}
	}
	
	private static void renderRoundnessStyle(BufferedImage image,BitMatrix matrix,int bgColor,int color,int multiple){
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		g.setColor(new Color(bgColor));
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.setColor(new Color(color));
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				if (matrix.get(x, y)&&matrix.get(x + multiple-1, y + multiple-1)&&matrix.get(x, y + multiple-1)&&matrix.get(x + multiple-1, y)) {
					g.fillOval(x, y, multiple,multiple);
				}
			}
		}
	}
	
	private static  BufferedImage blurImage(BufferedImage inputImage) {
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		float[] data = { 0.0625f, 0.125f, 0.0625f, 0.125f, 0.025f, 0.125f,
				0.0625f, 0.125f, 0.0625f };
		Kernel kernel = new Kernel(3, 3, data);
		ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
		BufferedImage outputImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB_PRE);
		cop.filter(inputImage, outputImage);
		return outputImage;
	}
}
