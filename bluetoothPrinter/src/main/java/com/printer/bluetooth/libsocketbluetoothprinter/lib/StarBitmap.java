package com.printer.bluetooth.libsocketbluetoothprinter.lib;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;

public class StarBitmap {

	public static final int RASTERCOMMANDHEADER = 9;
	int[] pixels;
	int height;
	int width;
	boolean dithering;
	byte[] imageData;

	public StarBitmap(Bitmap picture, boolean supportDithering, int maxWidth) {
		try {
			if (picture.getWidth() > maxWidth) {
				ScallImage(picture, maxWidth);
			} else {
				height = picture.getHeight();
				width = picture.getWidth();
				pixels = new int[height * width];

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						pixels[PixelIndex(x, y)] = picture.getPixel(x, y);
					}
				}
				// picture.getPixels(pixels, 0, width, 0, 0, width, height);
			}

			dithering = supportDithering;
			imageData = null;
		} catch (OutOfMemoryError|NullPointerException e) {
			throw e;
		}

	}

	private int pixelBrightness(int red, int green, int blue) {
		int level = (red + green + blue) / 3;
		return level;
	}

	private int PixelIndex(int x, int y) {
		return (y * width) + x;
	}

	public void ScallImage(Bitmap picture, int newWidth) {
		int w1 = picture.getWidth();
		int h1 = picture.getHeight();
		int newHeight = newWidth * h1;
		newHeight = newHeight / w1;
		Bitmap bm = Bitmap.createScaledBitmap(picture, newWidth, newHeight, false);
		height = bm.getHeight();
		width = bm.getWidth();
		pixels = new int[height * width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixels[PixelIndex(x, y)] = bm.getPixel(x, y);
			}
		}
	}

	private int GetGreyLevel(int pixel, float intensity) {
		/*
		 * if(Color.alpha(pixel) == 0) { return 255; }
		 */

		float red = Color.red(pixel);
		float green = Color.green(pixel);
		float blue = Color.blue(pixel);
		float parcial = red + green + blue;
		parcial = (float) (parcial / 3.0);
		int gray = (int) (parcial * intensity);
		if (gray > 255) {
			gray = 255;
		}
		return gray;
	}

	private void ConvertToMonochromeSteinbertDithering(float intensity) {
		int[][] levelmap = new int[width][height];
		for (int y = 0; y < height; y++) {
			if ((y & 1) == 0) {
				for (int x = 0; x < width; x++) {
					int pixel = pixels[PixelIndex(x, y)];
					levelmap[x][y] += 255 - GetGreyLevel(pixel, intensity);
					if (levelmap[x][y] >= 255) {
						levelmap[x][y] -= 255;
						pixels[PixelIndex(x, y)] = Color.BLACK;
					} else {
						pixels[PixelIndex(x, y)] = Color.WHITE;
					}

					int sixteenthOfQuantError = levelmap[x][y] / 16;

					if (x < width - 1)
						levelmap[x + 1][y] += sixteenthOfQuantError * 7;

					if (y < height - 1) {
						levelmap[x][y + 1] += sixteenthOfQuantError * 5;

						if (x > 0)
							levelmap[x - 1][y + 1] += sixteenthOfQuantError * 3;
						if (x < width - 1)
							levelmap[x + 1][y + 1] += sixteenthOfQuantError;
					}
				}
			} else {
				for (int x = width - 1; x >= 0; x--) {
					int pixel = pixels[PixelIndex(x, y)];

					levelmap[x][y] += 255 - GetGreyLevel(pixel, intensity);

					if (levelmap[x][y] >= 255) {
						levelmap[x][y] -= 255;
						pixels[PixelIndex(x, y)] = Color.BLACK;
					} else {
						pixels[PixelIndex(x, y)] = Color.WHITE;
					}

					int sixteenthOfQuantError = levelmap[x][y] / 16;

					if (x > 0)
						levelmap[x - 1][y] += sixteenthOfQuantError * 7;

					if (y < height - 1) {
						levelmap[x][y + 1] += sixteenthOfQuantError * 5;

						if (x < width - 1)
							levelmap[x + 1][y + 1] += sixteenthOfQuantError * 3;

						if (x > 0)
							levelmap[x - 1][y + 1] += sixteenthOfQuantError;
					}
				}
			}
		}
	}

	public byte[] getImageRasterDataForPrinting_Standard(boolean compressionEnable) {
		if (imageData != null) {
			return imageData;
		}

		// Converts the image to a Monochrome image using a Steinbert Dithering algorithm. This call can be removed but it that will also remove any dithering.
		if (dithering == true) {
			ConvertToMonochromeSteinbertDithering((float) 1.5);
		}

		int mWidth = width / 8;
		if ((width % 8) != 0) {
			mWidth++;
		}

		ArrayList<byte[]> list = new ArrayList<byte[]>();
		int dataLength = 0;
		byte[] constructedBytes = new byte[3 + mWidth];

		constructedBytes[0] = 'b';

		int blank = 0;

		for (int y = 0; y < height; y++) {
			int pos = 0;

			for (int x = 0; x < mWidth; x++) {
				byte constructedByte = 0x00;

				for (int j = 0; j < 8; j++) {
					int pixel;
					constructedByte = (byte) (constructedByte << 1);

					if (pos < width) {
						pixel = pixels[PixelIndex(pos, y)];

						if (pixelBrightness(Color.red(pixel), Color.green(pixel), Color.blue(pixel)) < 127) {
							constructedByte |= 0x01;
						}
					}

					pos++;
				}

				constructedBytes[3 + x] = constructedByte;
			}

			int work = mWidth;

			if (compressionEnable) {
				while (work != 0) {
					work--;

					if (constructedBytes[3 + work] != 0x00) {
						work++;
						break;
					}
				}
			}

			if (work != 0) {
				while (blank >= 1000) {
					list.add(new byte[]{0x1b, '*', 'r', 'Y', '1', '0', '0', '0', 0x00});
					dataLength += 9;
					blank -= 1000;
				}

				if (blank != 0) {
					list.add(new byte[]{ 0x1b, '*', 'r', 'Y', (byte)('0' + blank / 100), (byte)('0' + (blank % 100) / 10), (byte)('0' + blank % 10), 0x00 });
					dataLength += 8;
				}

				blank = 0;

				constructedBytes[1] = (byte) (work % 256);
				constructedBytes[2] = (byte) (work / 256);

				list.add(constructedBytes.clone());
				dataLength += 3 + mWidth;
			} else {
				blank++;
			}
		}

		while (blank >= 1000) {
			list.add(new byte[]{0x1b, '*', 'r', 'Y', '1', '0', '0', '0', 0x00});
			dataLength += 9;
			blank -= 1000;
		}

		if (blank != 0) {
			list.add(new byte[]{ 0x1b, '*', 'r', 'Y', (byte)('0' + blank / 100), (byte)('0' + (blank % 100) / 10), (byte)('0' + blank % 10), 0x00 });
			dataLength += 8;
		}

		int distPosition = 0;
		imageData = new byte[dataLength];
		for (int i = 0; i < list.size(); i++) {
			System.arraycopy(list.get(i), 0, imageData, distPosition, list.get(i).length);
			distPosition += list.get(i).length;
		}

		return imageData;
	}

	public byte[] getImageRasterDataForPrinting_graphic(boolean compressionEnable) {
		if (imageData != null) {
			return imageData;
		}

		// Converts the image to a Monochrome image using a Steinbert Dithering algorithm. This call can be removed but it that will also remove any dithering.
		if (dithering == true) {
			ConvertToMonochromeSteinbertDithering((float) 1.5);
		}

		int mWidth = width / 8;
		if ((width % 8) != 0) {
			mWidth++;
		}

		ArrayList<byte[]> list = new ArrayList<byte[]>();
		int dataLength = 0;
		byte[] constructedBytes = new byte[RASTERCOMMANDHEADER + mWidth];

		constructedBytes[0] = 0x1b;
		constructedBytes[1] = 0x1d;
		constructedBytes[2] = 'S';
		constructedBytes[3] = 0x01;
		constructedBytes[4] = 0x00;	// xL
		constructedBytes[5] = 0x00;	// xH
		constructedBytes[6] = 0x01;	// yL
		constructedBytes[7] = 0x00;	// yH
		constructedBytes[8] = 0x00;

		int blank = 0;

		for (int y = 0; y < height; y++) {
			int pos = 0;

			for (int x = 0; x < mWidth; x++) {
				byte constructedByte = 0x00;

				for (int j = 0; j < 8; j++) {
					int pixel;
					constructedByte = (byte) (constructedByte << 1);

					if (pos < width) {
						pixel = pixels[PixelIndex(pos, y)];

						if (pixelBrightness(Color.red(pixel), Color.green(pixel), Color.blue(pixel)) < 127) {
							constructedByte |= 0x01;
						}
					}

					pos++;
				}

				constructedBytes[RASTERCOMMANDHEADER + x] = constructedByte;
			}

			int work = mWidth;

			if (compressionEnable) {
				while (work != 0) {
					work--;

					if (constructedBytes[RASTERCOMMANDHEADER + work] != 0x00) {
						work++;
						break;
					}
				}
			}

			if (work != 0) {
				while (blank >= 255) {
					list.add(new byte[]{0x1b, 'I', (byte)0xff});
					dataLength += 3;
					blank -= 255;
				}

				if (blank != 0) {
					list.add(new byte[]{ 0x1b, 'I', (byte)blank});
					dataLength += 3;
				}

				blank = 0;

				constructedBytes[4] = (byte) (mWidth % 256);
				constructedBytes[5] = (byte) (mWidth / 256);

				list.add(constructedBytes.clone());
				dataLength += RASTERCOMMANDHEADER + mWidth;
			} else {
				blank++;
			}
		}

		while (blank >= 255) {
			list.add(new byte[]{0x1b, 'I', (byte)0xff});
			dataLength += 3;
			blank -= 255;
		}

		if (blank != 0) {
			list.add(new byte[]{ 0x1b, 'I', (byte)blank});
			dataLength += 3;
		}

		int distPosition = 0;
		imageData = new byte[dataLength];
		for (int i = 0; i < list.size(); i++) {
			System.arraycopy(list.get(i), 0, imageData, distPosition, list.get(i).length);
			distPosition += list.get(i).length;
		}

		return imageData;
	}

	public byte[] getImageESCPOSRasterDataForPrinting() {
		if (imageData != null) {
			return imageData;
		}

		// Converts the image to a Monochrome image using a Steinbert Dithering algorithm. This call can be removed but it that will also remove any dithering.
		if (dithering == true) {
			ConvertToMonochromeSteinbertDithering((float) 1.5);
		}

		int mWidth = width / 8;
		if ((width % 8) != 0) {
			mWidth++;
		}

		ArrayList<Byte> data = new ArrayList<Byte>();

		// The real algorithm for converting an image to escpos data is below
		int commandSize = mWidth * height;

		byte p1 = (byte) ((commandSize - ((commandSize / 65536) * 65536) - ((commandSize / 16777216) * 16777216)) % 256);
		byte p2 = (byte) ((commandSize - ((commandSize / 65536) * 65536) - ((commandSize / 16777216) * 16777216)) / 256);
		byte p3 = (byte) ((commandSize - ((commandSize / 16777216) * 16777216)) / 65536);
		byte p4 = (byte) (commandSize / 16777216);
		byte m = 48;
		byte fn = 112;
		byte a = 48;
		byte bx = 1;
		byte by = 1;
		byte c = 49;
		byte xL = (byte) (width % 256);
		byte xH = (byte) (width / 256);
		byte yL = (byte) (height % 256);
		byte yH = (byte) (height / 256);

		byte[] rasterCommand = new byte[] { 0x1d, 0x38, 0x4c, p1, p2, p3, p4, m, fn, a, bx, by, c, xL, xH, yL, yH };

		for (int count = 0; count < rasterCommand.length; count++) {
			data.add(rasterCommand[count]);
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < mWidth; x++) {
				byte constructedByte = 0;

				for (int j = 0; j < 8; j++) {
					constructedByte = (byte) (constructedByte << 1);

					int pixel;
					int widthPixel = (x * 8) + j;
					if (widthPixel < width) {
						pixel = pixels[PixelIndex(widthPixel, y)];
					} else {
						pixel = Color.WHITE;
					}

					if (pixelBrightness(Color.red(pixel), Color.green(pixel), Color.blue(pixel)) < 127) {
						constructedByte = (byte) (constructedByte | 1);
					}
				}
				data.add(constructedByte);
			}
		}

		imageData = new byte[data.size()];
		for (int count = 0; count < data.size(); count++) {
			imageData[count] = data.get(count);
		}

		return imageData;
	}


	public byte[] getImageImpactPrinterForPrinting() {
		if (imageData != null) {
			return imageData;
		}

		if (dithering == true) {
			ConvertToMonochromeSteinbertDithering((float) 1.5);
		}

		int mHeight = height / 8;
		if ((height % 8) != 0) {
			mHeight++;
		}

		ArrayList<Byte> data = new ArrayList<Byte>();
		int heightLocation = 0;
		int bitLocation = 0;
		byte nextByte = 0;

		int cwidth = width;
		if (cwidth > 199) {
			cwidth = 199;
		}

		byte[] cancelColor = new byte[] { 0x1b, 0x1e, 'C', 48 };
		for (int count = 0; count < cancelColor.length; count++) {
			data.add(cancelColor[count]);
		}

		for (int x = 0; x < mHeight; x++) {
			byte[] imageCommand = new byte[] { 0x1b, 'K', (byte) cwidth, 0 };
			for (int count = 0; count < imageCommand.length; count++) {
				data.add(imageCommand[count]);
			}

			for (int w = 0; w < cwidth; w++) {
				for (int j = 0; j < 8; j++) {
					int pixel;
					if (j + (heightLocation * 8) < height) {
						pixel = pixels[PixelIndex(w, j + (heightLocation * 8))];
					} else {
						pixel = Color.WHITE;
					}
					if (pixelBrightness(Color.red(pixel), Color.green(pixel), Color.blue(pixel)) < 127) {
						nextByte = (byte) (nextByte | (1 << (7 - bitLocation)));
					}
					bitLocation++;
					if (bitLocation == 8) {
						bitLocation = 0;
						data.add(nextByte);
						nextByte = 0;
					}

				}
			}
			heightLocation++;
			byte[] lineFeed = new byte[] { 0x1b, 0x49, 0x10 };
			for (int count = 0; count < lineFeed.length; count++) {
				data.add(lineFeed[count]);
			}
		}

		imageData = new byte[data.size()];
		for (int count = 0; count < imageData.length; count++) {
			imageData[count] = data.get(count);
		}

		return imageData;
	}
}
