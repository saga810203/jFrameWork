package org.jfw.util;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public final class JpgUtil {
	/*
	 * 根据尺寸图片居中裁剪
	 */
	public static boolean cutCenterImage(InputStream src, OutputStream dest, int w, int h) throws IOException {
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("jpg");
		ImageReader reader = (ImageReader) iterator.next();
		ImageInputStream iis = ImageIO.createImageInputStream(src);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		int imageIndex = 0;
		Rectangle rect = new Rectangle((reader.getWidth(imageIndex) - w) / 2, (reader.getHeight(imageIndex) - h) / 2, w,
				h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		return ImageIO.write(bi, "jpg", dest);
	}

	/*
	 * 图片裁剪二分之一
	 */
	public static boolean cutHalfImage(InputStream src, OutputStream dest) throws IOException {
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("jpg");
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
		return ImageIO.write(bi, "jpg", dest);
	}
	/*
	 * 图片裁剪通用接口
	 */

	public static boolean cutImage(InputStream src, OutputStream dest, int x, int y, int w, int h) throws IOException {
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("jpg");
		ImageReader reader = iterator.next();
		ImageInputStream iis = ImageIO.createImageInputStream(src);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		Rectangle rect = new Rectangle(x, y, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		return ImageIO.write(bi, "jpg", dest);
	}

	public static boolean cutImageByPerCent(InputStream src, OutputStream dest, int top, int buttom, int left,
			int right) throws IOException {
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("jpg");
		ImageReader reader = iterator.next();
		ImageInputStream iis = ImageIO.createImageInputStream(src);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		int width = reader.getWidth(0) / 2;
		int height = reader.getHeight(0) / 2;
		int x = width * left / 100;
		int y = height * top / 100;
		int w = width * right / 100 - x;
		int h = height * buttom / 100 - y;
		Rectangle rect = new Rectangle(x, y, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		return ImageIO.write(bi, "jpg", dest);
	}

	/*
	 * 图片缩放
	 */
	public static boolean zoomImage(InputStream src, OutputStream dest, int w, int h) throws IOException {
		double wr = 0, hr = 0;
		BufferedImage bufImg = ImageIO.read(src);
		Image Itemp = bufImg.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		wr = w * 1.0 / bufImg.getWidth();
		hr = h * 1.0 / bufImg.getHeight();
		AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
		Itemp = ato.filter(bufImg, null);
		return ImageIO.write((BufferedImage) Itemp, "jpg", dest);
	}

	private JpgUtil() {
	}
}
