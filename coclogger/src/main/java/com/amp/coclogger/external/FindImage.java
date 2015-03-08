package com.amp.coclogger.external;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBufferByte;

public class FindImage {

	public static MatrixPosition matrixMatch(int[][] big, int[][] small) {
		int firstElem = small[0][0];

		for (int i = 0; i < big.length - small.length + 1; i++) {
			// Scan column by column
			__columnscan: for (int j = 0; j < big[0].length - small[0].length; j++) {
				if (big[i][j] != firstElem)
					continue __columnscan; // No first match
				// There is a match for the first element in small
				// Check if all the elements in small matches those in big
				for (int ii = 0; ii < small.length; ii++)
					for (int jj = 0; jj < small[0].length; jj++)
						// If there is at least one difference, there is no
						// match
						if (big[i + ii][j + jj] != small[ii][jj])
							continue __columnscan;
				// If arrived here, then the small matches a region of big
				MatrixPosition result = new MatrixPosition();
				result.line = i;
				result.column = j;
				System.out.println("Matching at line=" + result.line
						+ ", column=" + result.column);
				return result;
			}
		}
		return null;
	}

	public static class MatrixPosition {
		public int line = -1;
		public int column = -1;
	}

	public static BufferedImage convertBufferedImageType(BufferedImage bi, int newType) {
		BufferedImage newBi = new BufferedImage(bi.getWidth(), bi.getHeight(),
				newType);
		ColorConvertOp convertOp = new ColorConvertOp(null);
		convertOp.filter(bi, newBi);
		return newBi;
	}

	public static int[][] convertToIntArray(BufferedImage image) {
		
		final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		final boolean hasAlphaChannel = image.getAlphaRaster() != null;

		int[][] result = new int[height][width];
		if (hasAlphaChannel) {
			final int pixelLength = 4;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
				argb += ((int) pixels[pixel + 1] & 0xff); // blue
				argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
				argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
				result[row][col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		} else {
			final int pixelLength = 3;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb += -16777216; // 255 alpha
				argb += ((int) pixels[pixel] & 0xff); // blue
				argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
				argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
				result[row][col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		}

		return result;
	}
}
