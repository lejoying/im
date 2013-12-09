package com.lejoying.utils;

import java.util.ArrayList;
import java.util.List;

public class RSA {
	// One file that integrated with BigInt.js and Barrett.js and RSA.js

	// BigInt, a suite of routines for performing multiple-precision arithmetic
	// in
	// JavaScript.
	//
	// Copyright 1998-2005 David Shapiro.
	//
	// You may use, re-use, abuse,
	// copy, and modify this code to your liking, but please keep this header.
	// Thanks!
	//
	// Dave Shapiro
	// dave@ohdave.com

	// IMPORTANT THING: Be sure to set maxDigits according to your precision
	// needs. Use the setMaxDigits() function to do this. See comments below.
	//
	// Tweaked by Ian Bunning
	// Alterations:
	// Fix bug in function biFromHex(s) to allow
	// parsing of strings of length != 0 (mod 4)

	// Changes made by Dave Shapiro as of 12/30/2004:
	//
	// The BigInt() constructor doesn't take a string anymore. If you want to
	// create a BigInt from a string, use biFromDecimal() for base-10
	// representations, biFromHex() for base-16 representations, or
	// biFromString() for base-2-to-36 representations.
	//
	// biFromArray() has been removed. Use biCopy() instead, passing a BigInt
	// instead of an array.
	//
	// The BigInt() constructor now only constructs a zeroed-out array.
	// Alternatively, if you pass <true>, it won't construct any array. See the
	// biCopy() method for an example of this.
	//
	// Be sure to set maxDigits depending on your precision needs. The default
	// zeroed-out array ZERO_ARRAY is constructed inside the setMaxDigits()
	// function. So use this function to set the variable. DON'T JUST SET THE
	// VALUE. USE THE FUNCTION.
	//
	// ZERO_ARRAY exists to hopefully speed up construction of BigInts(). By
	// precalculating the zero array, we can just use slice(0) to make copies of
	// it. Presumably this calls faster native code, as opposed to setting the
	// elements one at a time. I have not done any timing tests to verify this
	// claim.

	// Max number = 10^16 - 2 = 9999999999999998;
	// 2^53 = 9007199254740992;

	long biRadixBase = 2;
	long biRadixBits = 16;
	long bitsPerDigit = biRadixBits;
	long biRadix = 1 << 16; // = 2^16 = 65536
	long biHalfRadix = biRadix >>> 1;
	long biRadixSquared = biRadix * biRadix;
	long maxDigitVal = biRadix - 1;
	long maxInteger = Long.valueOf("9999999999999998");

	// maxDigits:
	// Change this to accommodate your largest number size. Use setMaxDigits()
	// to change it!
	//
	// In general, if you're working with numbers of size N bits, you'll need
	// 2*N
	// bits of storage. Each digit holds 16 bits. So, a 1024-bit key will need
	//
	// 1024 * 2 / 16 = 128 digits of storage.
	//

	int maxDigits;
	List<Long> ZERO_ARRAY;
	BigInt bigZero, bigOne;

	public RSA() {
		setMaxDigits(38);
	}

	public void setMaxDigits(int value) {
		maxDigits = value;
		ZERO_ARRAY = new ArrayList<Long>();
		for (int iza = 0; iza < maxDigits; iza++)
			ZERO_ARRAY.add(iza, 0l);
		bigZero = new BigInt();
		bigOne = new BigInt();
		bigOne.digits.set(0, 1l);
		lr10 = biFromNumber(Long.valueOf("1000000000000000"));
	}

	// The maximum number of digits in base 10 you can convert to an
	// integer without JavaScript throwing up on you.
	int dpl10 = 15;
	// lr10 = 10 ^ dpl10
	BigInt lr10;

	class BigInt {
		public boolean isNeg = false;
		public List<Long> digits;

		public BigInt() {
			digits = new ArrayList<Long>();
			digits.addAll(ZERO_ARRAY);
		}

		public BigInt(boolean flag) {
			this();
			if (flag == true) {
				digits = null;
			}
		}

		@Override
		public String toString() {
			return "BigInt [isNeg=" + isNeg + ", digits=" + digits + "]";
		}

	}

	public BigInt biFromDecimal(String s) {
		boolean isNeg = s.charAt(0) == '-';
		int i = isNeg ? 1 : 0;
		BigInt result;
		// Skip leading zeros.
		while (i < s.length() && s.charAt(i) == '0')
			++i;
		if (i == s.length()) {
			result = new BigInt();
		} else {
			int digitCount = s.length() - i;
			int fgl = digitCount % dpl10;
			if (fgl == 0)
				fgl = dpl10;
			result = biFromNumber(Long.valueOf((s.substring(i, fgl))));
			i += fgl;
			while (i < s.length()) {
				result = biAdd(biMultiply(result, lr10),
						biFromNumber(Long.valueOf((s.substring(i, dpl10)))));
				i += dpl10;
			}
			result.isNeg = isNeg;
		}
		return result;
	}

	public BigInt biCopy(BigInt bi) {
		BigInt result = new BigInt(true);
		result.digits = new ArrayList<Long>();
		result.digits.addAll(bi.digits);
		result.isNeg = bi.isNeg;
		return result;
	}

	public BigInt biFromNumber(long i) {
		BigInt result = new BigInt();
		result.isNeg = i < 0;
		i = Math.abs(i);
		int j = 0;
		while (i > 0) {
			result.digits.set(j++, i & maxDigitVal);
			i >>= biRadixBits;
		}
		return result;
	}

	public String reverseStr(String s) {
		String result = "";
		for (int i = s.length() - 1; i > -1; --i) {
			result += s.charAt(i);
		}
		return result;
	}

	char[] hexatrigesimalToChar = new char[] { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z' };

	public String biToString(BigInt x, long radix)
	// 2 <= radix <= 36
	{
		BigInt b = new BigInt();
		b.digits.set(0, radix);
		BigInt[] qr = biDivideModulo(x, b);
		String result = String.valueOf(hexatrigesimalToChar[(int) qr[1].digits
				.get(0).longValue()]);
		while (biCompare(qr[0], bigZero) == 1) {
			qr = biDivideModulo(qr[0], b);
			long digit = qr[1].digits.get(0);
			result += hexatrigesimalToChar[(int) digit];
		}
		return (x.isNeg ? "-" : "") + reverseStr(result);
	}

	public String biToDecimal(BigInt x) {
		BigInt b = new BigInt();
		b.digits.set(0, 10l);
		BigInt[] qr = biDivideModulo(x, b);
		String result = String.valueOf(qr[1].digits.get(0));
		while (biCompare(qr[0], bigZero) == 1) {
			qr = biDivideModulo(qr[0], b);
			result += String.valueOf(qr[1].digits.get(0));
		}
		return (x.isNeg ? "-" : "") + reverseStr(result);
	}

	char[] hexToChar = new char[] { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public String digitToHex(long digits) {
		int mask = 0xf;
		String result = "";
		for (int i = 0; i < 4; ++i) {
			result += hexToChar[(int) (digits & mask)];
			digits >>>= 4;
		}
		return reverseStr(result);
	}

	public String biToHex(BigInt x) {
		String result = "";
		int n = biHighIndex(x);
		for (int i = n; i > -1; --i) {
			result += digitToHex(x.digits.get(i));
		}
		return result;
	}

	public int charToHex(int c) {
		int ZERO = 48;
		int NINE = ZERO + 9;
		int littleA = 97;
		int littleZ = littleA + 25;
		int bigA = 65;
		int bigZ = 65 + 25;
		int result;

		if (c >= ZERO && c <= NINE) {
			result = c - ZERO;
		} else if (c >= bigA && c <= bigZ) {
			result = 10 + c - bigA;
		} else if (c >= littleA && c <= littleZ) {
			result = 10 + c - littleA;
		} else {
			result = 0;
		}
		return result;
	}

	public int hexToDigit(String s) {
		int result = 0;
		int sl = Math.min(s.length(), 4);
		for (int i = 0; i < sl; ++i) {
			result <<= 4;
			result |= charToHex((int) s.charAt(i));
		}
		return result;
	}

	public BigInt biFromHex(String s) {
		BigInt result = new BigInt();
		int sl = s.length();
		for (int i = sl, j = 0; i > 0; i -= 4, ++j) {
			result.digits.set(
					j,
					(long) hexToDigit(s.substring(Math.max(i - 4, 0),
							Math.max(i - 4, 0) + Math.min(i, 4))));
		}
		return result;
	}

	public BigInt biFromString(String s, int radix) {
		boolean isNeg = s.charAt(0) == '-';
		int istop = isNeg ? 1 : 0;
		BigInt result = new BigInt();
		BigInt place = new BigInt();
		place.digits.set(0, 1l); // radix^0
		for (int i = s.length() - 1; i >= istop; i--) {
			int c = (int) s.charAt(i);
			int digit = charToHex(c);
			BigInt biDigit = biMultiplyDigit(place, digit);
			result = biAdd(result, biDigit);
			place = biMultiplyDigit(place, radix);
		}
		result.isNeg = isNeg;
		return result;
	}

	public String biDump(BigInt b) {
		String str = "";
		for (long l : b.digits) {
			str += l + " ";
		}
		str.substring(0, str.length() - 1);
		return (b.isNeg ? "-" : "") + str;
	}

	public BigInt biAdd(BigInt x, BigInt y) {
		BigInt result;

		if (x.isNeg != y.isNeg) {
			y.isNeg = !y.isNeg;
			result = biSubtract(x, y);
			y.isNeg = !y.isNeg;
		} else {
			result = new BigInt();
			int c = 0;
			long n;
			for (int i = 0; i < x.digits.size(); ++i) {
				n = x.digits.get(i) + y.digits.get(i) + c;
				result.digits.set(i, n & 0xffff);
				c = Number(n >= biRadix);
			}
			result.isNeg = x.isNeg;
		}
		return result;
	}

	public BigInt biSubtract(BigInt x, BigInt y) {
		BigInt result;
		if (x.isNeg != y.isNeg) {
			y.isNeg = !y.isNeg;
			result = biAdd(x, y);
			y.isNeg = !y.isNeg;
		} else {
			result = new BigInt();
			long n, c;
			c = 0;
			for (int i = 0; i < x.digits.size(); ++i) {
				n = x.digits.get(i) - y.digits.get(i) + c;
				result.digits.set(i, n & 0xffff);
				// Stupid non-conforming modulus operation.
				if (result.digits.get(i) < 0) {
					long temp = result.digits.get(i);
					result.digits.set(i, temp += biRadix);
				}
				c = 0 - Number(n < 0);
			}
			// Fix up the negative sign, if any.
			if (c == -1) {
				c = 0;
				for (int i = 0; i < x.digits.size(); ++i) {
					n = 0 - result.digits.get(i) + c;
					result.digits.set(i, n & 0xffff);
					// Stupid non-conforming modulus operation.
					if (result.digits.get(i) < 0) {
						long temp = result.digits.get(i);
						result.digits.set(i, temp += biRadix);
					}
					c = 0 - Number(n < 0);
				}
				// Result is opposite sign of arguments.
				result.isNeg = !x.isNeg;
			} else {
				// Result is same sign.
				result.isNeg = x.isNeg;
			}
		}
		return result;
	}

	public int biHighIndex(BigInt x) {
		int result = x.digits.size() - 1;
		while (result > 0 && x.digits.get(result) == 0)
			--result;
		return result;
	}

	public long biNumBits(BigInt x) {
		int n = biHighIndex(x);
		long d = x.digits.get(n);
		long m = (n + 1) * bitsPerDigit;
		long result;
		for (result = m; result > m - bitsPerDigit; --result) {
			if ((d & 0x8000) != 0)
				break;
			d <<= 1;
		}
		return result;
	}

	public BigInt biMultiply(BigInt x, BigInt y) {
		BigInt result = new BigInt();
		long c;
		int n = biHighIndex(x);
		int t = biHighIndex(y);
		long uv;
		int k;

		for (int i = 0; i <= t; ++i) {
			c = 0;
			k = i;
			for (int j = 0; j <= n; ++j, ++k) {
				uv = result.digits.get(k) + x.digits.get(j) * y.digits.get(i)
						+ c;
				result.digits.set(k, uv & maxDigitVal);
				c = uv >>> biRadixBits;
			}
			result.digits.set(i + n + 1, c);
		}
		// Someone give me a logical xor, please.
		result.isNeg = x.isNeg != y.isNeg;
		return result;
	}

	public BigInt biMultiplyDigit(BigInt x, int y) {
		int n;
		long c, uv;

		BigInt result = new BigInt();
		n = biHighIndex(x);
		c = 0;
		for (int j = 0; j <= n; ++j) {
			uv = result.digits.get(j) + x.digits.get(j) * y + c;
			result.digits.set(j, uv & maxDigitVal);
			c = uv >>> biRadixBits;
		}
		result.digits.set(1 + n, c);
		return result;
	}

	public void arrayCopy(List<Long> src, double srcStart, List<Long> dest,
			double destStart, double n) {
		double m = Math.min(srcStart + n, src.size());
		for (int i = (int) srcStart, j = (int) destStart; i < m; ++i, ++j) {
			dest.set(j, src.get(i));
		}
	}

	long[] highBitMasks = new long[] { 0x0000, 0x8000, 0xC000, 0xE000, 0xF000,
			0xF800, 0xFC00, 0xFE00, 0xFF00, 0xFF80, 0xFFC0, 0xFFE0, 0xFFF0,
			0xFFF8, 0xFFFC, 0xFFFE, 0xFFFF };

	public BigInt biShiftLeft(BigInt x, long n) {
		double digitCount = Math.floor(n / bitsPerDigit);
		BigInt result = new BigInt();
		arrayCopy(x.digits, 0, result.digits, digitCount, result.digits.size()
				- digitCount);
		long bits = n % bitsPerDigit;
		long rightBits = bitsPerDigit - bits;
		int i;
		int i1;
		for (i = result.digits.size() - 1, i1 = i - 1; i > 0; --i, --i1) {
			result.digits
					.set(i,
							((result.digits.get(i) << bits) & maxDigitVal)
									| ((result.digits.get(i1) & highBitMasks[(int) bits]) >>> (rightBits)));
		}
		result.digits.set(0, ((result.digits.get(i) << bits) & maxDigitVal));
		result.isNeg = x.isNeg;
		return result;
	}

	long[] lowBitMasks = new long[] { 0x0000, 0x0001, 0x0003, 0x0007, 0x000F,
			0x001F, 0x003F, 0x007F, 0x00FF, 0x01FF, 0x03FF, 0x07FF, 0x0FFF,
			0x1FFF, 0x3FFF, 0x7FFF, 0xFFFF };

	public BigInt biShiftRight(BigInt x, long n) {
		double digitCount = Math.floor(n / bitsPerDigit);
		BigInt result = new BigInt();
		arrayCopy(x.digits, digitCount, result.digits, 0, x.digits.size()
				- digitCount);
		long bits = n % bitsPerDigit;
		long leftBits = bitsPerDigit - bits;
		for (int i = 0, i1 = i + 1; i < result.digits.size() - 1; ++i, ++i1) {
			result.digits
					.set(i,
							(result.digits.get(i) >>> bits)
									| ((result.digits.get(i1) & lowBitMasks[(int) bits]) << leftBits));
		}
		long temp = result.digits.get(result.digits.size() - 1);
		result.digits.set(result.digits.size() - 1, temp >>>= bits);
		result.isNeg = x.isNeg;
		return result;
	}

	public BigInt biMultiplyByRadixPower(BigInt x, long n) {
		BigInt result = new BigInt();
		arrayCopy(x.digits, 0, result.digits, n, result.digits.size() - n);
		return result;
	}

	public BigInt biDivideByRadixPower(BigInt x, long n) {
		BigInt result = new BigInt();
		arrayCopy(x.digits, n, result.digits, 0, result.digits.size() - n);
		return result;
	}

	public BigInt biModuloByRadixPower(BigInt x, double n) {
		BigInt result = new BigInt();
		arrayCopy(x.digits, 0d, result.digits, 0d, n);
		return result;
	}

	public long biCompare(BigInt x, BigInt y) {
		if (x.isNeg != y.isNeg) {
			return 1 - 2 * Number(x.isNeg);
		}
		for (int i = x.digits.size() - 1; i >= 0; --i) {
			if (x.digits.get(i) != y.digits.get(i)) {
				if (x.isNeg) {
					return 1 - 2 * Number(x.digits.get(i) > y.digits.get(i));
				} else {
					return 1 - 2 * Number(x.digits.get(i) < y.digits.get(i));
				}
			}
		}
		return 0;
	}

	public BigInt[] biDivideModulo(BigInt x, BigInt y) {
		long nb = biNumBits(x);
		long tb = biNumBits(y);
		boolean origYIsNeg = y.isNeg;
		BigInt q, r;
		if (nb < tb) {
			// |x| < |y|
			if (x.isNeg) {
				q = biCopy(bigOne);
				q.isNeg = !y.isNeg;
				x.isNeg = false;
				y.isNeg = false;
				r = biSubtract(y, x);
				// Restore signs, 'cause they're references.
				x.isNeg = true;
				y.isNeg = origYIsNeg;
			} else {
				q = new BigInt();
				r = biCopy(x);
			}
			return new BigInt[] { q, r };
		}

		q = new BigInt();
		r = x;

		// Normalize Y.
		double t = Math.ceil(tb / bitsPerDigit) - 1;
		int lambda = 0;
		while (y.digits.get((int) t) < biHalfRadix) {
			y = biShiftLeft(y, 1);
			++lambda;
			++tb;
			t = Math.ceil(tb / bitsPerDigit) - 1;
		}
		// Shift r over to keep the quotient constant. We'll shift the
		// remainder back at the end.
		r = biShiftLeft(r, lambda);
		nb += lambda; // Update the bit count for x.
		double n = Math.ceil(nb / bitsPerDigit) - 1;

		BigInt b = biMultiplyByRadixPower(y, (long) (n - t));
		while (biCompare(r, b) != -1) {
			long temp = q.digits.get((int) (n - t));
			q.digits.set((int) (n - t), ++temp);
			r = biSubtract(r, b);
		}
		for (int i = (int) n; i > t; --i) {
			long ri = (i >= r.digits.size()) ? 0 : r.digits.get(i);
			long ri1 = (i - 1 >= r.digits.size()) ? 0 : r.digits.get(i - 1);
			long ri2 = (i - 2 >= r.digits.size()) ? 0 : r.digits.get(i - 2);
			long yt = (t >= y.digits.size()) ? 0 : y.digits.get((int) t);
			long yt1 = (t - 1 >= y.digits.size()) ? 0 : y.digits
					.get((int) (t - 1));
			if (ri == yt) {
				q.digits.set((int) (i - t - 1), maxDigitVal);
			} else {
				q.digits.set((int) (i - t - 1),
						(long) Math.floor((ri * biRadix + ri1) / yt));
			}

			long c1 = q.digits.get((int) (i - t - 1)) * ((yt * biRadix) + yt1);
			long c2 = (ri * biRadixSquared) + ((ri1 * biRadix) + ri2);
			while (c1 > c2) {
				long temp = q.digits.get((int) (i - t - 1));
				q.digits.set((int) (i - t - 1), --temp);
				c1 = q.digits.get((int) (i - t - 1)) * ((yt * biRadix) | yt1);
				c2 = (ri * biRadix * biRadix) + ((ri1 * biRadix) + ri2);
			}

			b = biMultiplyByRadixPower(y, (long) (i - t - 1));
			r = biSubtract(
					r,
					biMultiplyDigit(b, q.digits.get((int) (i - t - 1))
							.intValue()));
			if (r.isNeg) {
				r = biAdd(r, b);
				long temp = q.digits.get((int) (i - t - 1));
				q.digits.set((int) (i - t - 1), --temp);
			}
		}
		r = biShiftRight(r, lambda);
		// Fiddle with the signs and stuff to make sure that 0 <= r < y.
		q.isNeg = x.isNeg != origYIsNeg;
		if (x.isNeg) {
			if (origYIsNeg) {
				q = biAdd(q, bigOne);
			} else {
				q = biSubtract(q, bigOne);
			}
			y = biShiftRight(y, lambda);
			r = biSubtract(y, r);
		}
		// Check for the unbelievably stupid degenerate case of r == -0.
		if (r.digits.get(0) == 0 && biHighIndex(r) == 0)
			r.isNeg = false;

		return new BigInt[] { q, r };
	}

	public BigInt biDivide(BigInt x, BigInt y) {
		return biDivideModulo(x, y)[0];
	}

	public BigInt biModulo(BigInt x, BigInt y) {
		return biDivideModulo(x, y)[1];
	}

	public BigInt biMultiplyMod(BigInt x, BigInt y, BigInt m) {
		return biModulo(biMultiply(x, y), m);
	}

	public BigInt biPow(BigInt x, int y) {
		BigInt result = bigOne;
		BigInt a = x;
		while (true) {
			if ((y & 1) != 0)
				result = biMultiply(result, a);
			y >>= 1;
			if (y == 0)
				break;
			a = biMultiply(a, a);
		}
		return result;
	}

	public BigInt biPowMod(BigInt x, BigInt y, BigInt m) {
		BigInt result = bigOne;
		BigInt a = x;
		BigInt k = y;
		while (true) {
			if ((k.digits.get(0) & 1) != 0)
				result = biMultiplyMod(result, a, m);
			k = biShiftRight(k, 1);
			if (k.digits.get(0) == 0 && biHighIndex(k) == 0)
				break;
			a = biMultiplyMod(a, a, m);
		}
		return result;
	}

	// BarrettMu, a class for performing Barrett modular reduction computations
	// in
	// JavaScript.
	//
	// Requires BigInt.js.
	//
	// Copyright 2004-2005 David Shapiro.
	//
	// You may use, re-use, abuse, copy, and modify this code to your liking,
	// but
	// please keep this header.
	//
	// Thanks!
	//
	// Dave Shapiro
	// dave@ohdave.com

	class BarrettMu {
		public BigInt modulus;
		public int k;
		public BigInt b2k;
		public BigInt bkplus1;
		// this.modulo = BarrettMu_modulo;
		//
		public BigInt mu;

		//
		// this.multiplyMod = BarrettMu_multiplyMod;
		// this.powMod = BarrettMu_powMod;
		public BarrettMu(BigInt m) {
			modulus = biCopy(m);
			k = biHighIndex(this.modulus) + 1;
			b2k = new BigInt();
			b2k.digits.set(2 * k, 1l); // b2k = b^(2k)
			bkplus1 = new BigInt();
			bkplus1.digits.set(k + 1, 1l); // bkplus1 = b^(k+1)
			mu = biDivide(b2k, this.modulus);
		}

		public BigInt BarrettMu_modulo(BigInt x) {
			BigInt q1 = biDivideByRadixPower(x, this.k - 1);
			BigInt q2 = biMultiply(q1, this.mu);
			BigInt q3 = biDivideByRadixPower(q2, this.k + 1);
			BigInt r1 = biModuloByRadixPower(x, this.k + 1);
			BigInt r2term = biMultiply(q3, this.modulus);
			BigInt r2 = biModuloByRadixPower(r2term, this.k + 1);
			BigInt r = biSubtract(r1, r2);
			if (r.isNeg) {
				r = biAdd(r, this.bkplus1);
			}
			boolean rgtem = biCompare(r, this.modulus) >= 0;
			while (rgtem) {
				r = biSubtract(r, this.modulus);
				rgtem = biCompare(r, this.modulus) >= 0;
			}
			return r;
		}

		public BigInt BarrettMu_multiplyMod(BigInt x, BigInt y) {
			/*
			 * x = this.modulo(x); y = this.modulo(y);
			 */
			BigInt xy = biMultiply(x, y);
			return this.BarrettMu_modulo(xy);
		}

		public BigInt BarrettMu_powMod(BigInt x, BigInt y) {
			BigInt result = new BigInt();
			result.digits.set(0, 1l);
			BigInt a = x;
			BigInt k = y;
			int i = 0;
			while (true) {
				if ((k.digits.get(0) & 1) != 0)
					result = BarrettMu_multiplyMod(result, a);
				k = biShiftRight(k, 1);
				if (k.digits.get(0) == 0 && biHighIndex(k) == 0)
					break;
				a = BarrettMu_multiplyMod(a, a);
				i++;
				System.out.println(i);
			}
			return result;
		}
	}

	// RSA, a suite of routines for performing RSA public-key computations in
	// JavaScript.
	//
	// Requires BigInt.js and Barrett.js.
	//
	// Copyright 1998-2005 David Shapiro.
	//
	// You may use, re-use, abuse, copy, and modify this code to your liking,
	// but
	// please keep this header.
	//
	// Thanks!
	//
	// Dave Shapiro
	// dave@ohdave.com

	public String RSAKeyStr(String encryptionExponent,
			String decryptionExponent, String modulus) {
		String str = encryptionExponent + "#" + decryptionExponent + "#"
				+ modulus;
		return str;
	}

	public RSAKeyPair RSAKey(String str) {
		String[] keyStr = str.split("#");
		RSAKeyPair key = new RSAKeyPair(keyStr[0], keyStr[1], keyStr[2]);
		return key;
	}

	public class RSAKeyPair {
		public BigInt e;
		public BigInt d;
		public BigInt m;
		// We can do two bytes per digit, so
		// chunkSize = 2 * (number of digits in modulus - 1).
		// Since biHighIndex returns the high index, not the number of digits, 1
		// has
		// already been subtracted.
		public int chunkSize;
		public int radix;
		public BarrettMu barrett;

		public RSAKeyPair(String encryptionExponent, String decryptionExponent,
				String modulus) {
			radix = 16;
			e = biFromHex(encryptionExponent);
			d = biFromHex(decryptionExponent);
			m = biFromHex(modulus);
			chunkSize = 2 * biHighIndex(this.m);
			barrett = new BarrettMu(this.m);
		}
	}

	public String twoDigit(int n) {
		return (n < 10 ? "0" : "") + String.valueOf(n);
	}

	public String encryptedString(RSAKeyPair key, String s)
	// Altered by Rob Saunders (rob@robsaunders.net). New routine pads the
	// string after it has been converted to an array. This fixes an
	// incompatibility with Flash MX's ActionScript.
	{
		List<Integer> a = new ArrayList<Integer>();
		int sl = s.length();
		int i = 0;
		while (i < sl) {
			a.add((int) s.charAt(i));
			i++;
		}
		while (a.size() % key.chunkSize != 0) {
			a.add(i++, 0);
		}
		int al = a.size();
		String result = "";
		int j, k;
		BigInt block;
		for (i = 0; i < al; i += key.chunkSize) {
			block = new BigInt();
			j = 0;
			for (k = i; k < i + key.chunkSize; ++j) {
				block.digits.set(j, (long) a.get(k++).intValue());
				long temp = block.digits.get(j);
				block.digits.set(j, temp += a.get(k++) << 8);
			}
			BigInt crypt = key.barrett.BarrettMu_powMod(block, key.e);
			System.out.println(i+":::::");
			String text = key.radix == 16 ? biToHex(crypt) : biToString(crypt,
					key.radix);
			result += text + " ";
			System.out.println(i);
		}
		return result.substring(0, result.length() - 1); // Remove last space.
	}

	public String decryptedString(RSAKeyPair key, String s) {
		String[] blocks = s.split(" ");
		String result = "";
		BigInt block;
		int i, j;
		for (i = 0; i < blocks.length; ++i) {
			BigInt bi;
			if (key.radix == 16) {
				bi = biFromHex(blocks[i]);
			} else {
				bi = biFromString(blocks[i], key.radix);
			}
			block = key.barrett.BarrettMu_powMod(bi, key.d);
			for (j = 0; j <= biHighIndex(block); ++j) {
				result += ((char) (block.digits.get(j) & 255))
						+ ((char) (block.digits.get(j) >> 8));
			}
		}
		// Remove trailing null, if any.
		if ((int) result.charAt(result.length() - 1) == 0) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	public int Number(boolean flag) {
		if (flag) {
			return 1;
		} else {
			return 0;
		}
	}

}
