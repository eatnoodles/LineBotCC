package com.utils;

import java.util.Scanner;

public class Gauss {


	/**
	 * 主元
	 * 
	 */
	static double x[];
	static double a[][];
	static double b[];
	static double m;
	static int n;

	/**
	 * 選主元
	 * 
	 * @param k
	 */
	public static void SelectAndChangeLine(int k) {
		int maxline = k;
		for (int i = k + 1; i < n; i++) {
			if (Math.abs(a[i][k]) > a[maxline][k]) {
				maxline = i;
			}
		}
		if (maxline != k) {
			for (int j = 0; j < n + 1; j++) {
				b[j] = a[k][j];
				a[k][j] = a[maxline][j];
				a[maxline][j] = b[j];
			}
		}
	}

	/**
	 * 消元計算
	 * 
	 * @param k
	 */
	public static void Elimination(int k) {
		for (int i = k + 1; i < n; i++) {
			m = a[i][k] / a[k][k];
			a[i][k] = 0;
			for (int j = k + 1; j < n + 1; j++) {
				a[i][j] = a[i][j] - m * a[k][j];
				// System.out.println("tt="+m*a[k][j]);
			}
		}
	}

	/**
	 * 回代計算
	 * 
	 */
	public static void BacksSubstitution() {
		for (int i = n - 1; i >= 0; i--) {
			for (int j = n - 1; j > i; j--) {
				a[i][n] = a[i][n] - x[j] * a[i][j];
			}
			System.out.println(a[i][n]);
			x[i] = a[i][n] / a[i][i];
		}
	}

	/**
	 * print line
	 * 
	 * @param args
	 */
	public static void PrintLine(double[] args) {
		for (int j = 0; j < args.length; j++) {
			System.out.print(args[j] + " ");
		}
	}

	/**
	 * print 矩陣
	 * 
	 * @param args
	 */
	public static void PrintMatrix(double[][] args) {
		for (int i = 0; i < args.length; i++) {
			for (int j = 0; j < args[i].length; j++) {
				System.out.print(args[i][j] + " ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		Scanner as = new Scanner(System.in);
		System.out.println("one：");
		n = as.nextInt();
		System.out.println("a：");
		a = new double[n][n + 1];
		b = new double[n + 1];
		x = new double[n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n + 1; j++) {
				a[i][j] = as.nextDouble();
			}
		}// [[2.0, 3.0, 20.0], [1.0, 1.0, 50.0]]
		as.close();
		for (int i = 0; i < n - 1; i++) {
			SelectAndChangeLine(i);
			System.out.println("第" + (i + 1) + "次换主元");
			PrintMatrix(a);
			Elimination(i);
			System.out.println("第" + (i + 1) + "次消元");
			PrintMatrix(a);
		}
		BacksSubstitution();
		PrintLine(x);
	}
}
