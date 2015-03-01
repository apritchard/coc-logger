package com.amp.coclogger.external;


public class FindImage {


	public static MatrixPosition MatrixMatch(int[][] big, int[][] small) {
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
				// System.out.println("Matching at line=" + result.line +
				// ", column=" + result.column);
				return result;
			}
		}
		return null;
	}

	static class MatrixPosition {
		int line = -1;
		int column = -1;
	}
}
// @author MichelD
// @author http://www.liveperson.com/micheld/

