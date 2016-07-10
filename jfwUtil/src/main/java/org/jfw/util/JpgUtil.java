package org.jfw.util;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public final class JpgUtil {
	public static final String JPG = "jpg";

	
	public static byte[] read(byte[] src) throws IOException{
		 BufferedImage im = ImageIO.read(new ByteArrayInputStream(src));
		 ByteArrayOutputStream os = new ByteArrayOutputStream();
		 ImageIO.write(im, JPG, os);
		 return os.toByteArray();
	}

	/*
	 * 根据尺寸图片居中裁剪
	 */
	public static boolean cutCenterImage(InputStream src, OutputStream dest, int w, int h)
			throws IOException {
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(JPG);
		ImageReader reader = (ImageReader) iterator.next();
		ImageInputStream iis = ImageIO.createImageInputStream(src);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		int imageIndex = 0;
		Rectangle rect = new Rectangle((reader.getWidth(imageIndex) - w) / 2, (reader.getHeight(imageIndex) - h) / 2, w,
				h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		return ImageIO.write(bi, JPG, dest);
	}

	/*
	 * 图片裁剪二分之一
	 */
	public static boolean cutHalfImage(InputStream src, OutputStream dest) throws IOException {
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(JPG);
		ImageReader reader = iterator.next();
		ImageInputStream iis = ImageIO.createImageInputStream(src);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		int imageIndex = 0;
		int width = reader.getWidth(imageIndex) / 2;
		int height = reader.getHeight(imageIndex) / 2;
		Rectangle rect = new Rectangle(width / 2, height / 2, width, height);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		return ImageIO.write(bi, JPG, dest);
	}
	/*
	 * 图片裁剪通用接口
	 */

	public static boolean cutImage(InputStream src, OutputStream dest, int x, int y, int w, int h)
			throws IOException {
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("JPG");
		ImageReader reader = iterator.next();
		ImageInputStream iis = ImageIO.createImageInputStream(src);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		Rectangle rect = new Rectangle(x, y, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		return ImageIO.write(bi, JPG, dest);
	}

	public static boolean cutByPerCent(InputStream src, OutputStream dest, int px, int py, int pw,
			int ph) throws IOException {
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(JPG);
		ImageReader reader = iterator.next();
		ImageInputStream iis = ImageIO.createImageInputStream(src);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		int width = reader.getWidth(0) ;
		int height = reader.getHeight(0);
		int x = width * px / 100;
		int y = height * py / 100;
		int w = width * pw / 100;
		int h = height * ph / 100;
		Rectangle rect = new Rectangle(x, y, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		return ImageIO.write(bi, JPG, dest);
	}

	/*
	 * 图片缩放
	 */
	public static boolean zoom(InputStream src, OutputStream dest, int w, int h) throws IOException {
		double wr = 0, hr = 0;
		BufferedImage bufImg = ImageIO.read(src);
		Image Itemp = bufImg.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		wr = w * 1.0 / bufImg.getWidth();
		hr = h * 1.0 / bufImg.getHeight();
		AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
		Itemp = ato.filter(bufImg, null);
		return ImageIO.write((BufferedImage) Itemp, JPG, dest);
	}

	private JpgUtil() {
	}
}
