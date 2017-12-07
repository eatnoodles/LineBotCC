package com.utils;

import java.util.List;

public class GaussUtil {
	
	public GaussUtil(int n) {
		this.n = n;
		this.a = new double[n][n + 1];
		this.b = new double[n + 1];
		this.x = new double[n];
	}

	private double x[];
	private double a[][];
	private double b[];
	private double m;
	private int n;

	/**
	 * 選主元
	 * 
	 * @param k
	 */
	public void selectAndChangeLine(int k) {
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
	 * 消元
	 * 
	 * @param k
	 */
	public void elimination(int k) {
		for (int i = k + 1; i < n; i++) {
			m = a[i][k] / a[k][k];
			a[i][k] = 0;
			for (int j = k + 1; j < n + 1; j++) {
				a[i][j] = a[i][j] - m * a[k][j];
			}
		}
	}

	/**
	 * 回代
	 * 
	 */
	public void backsSubstitution() {
		for (int i = n - 1; i >= 0; i--) {
			for (int j = n - 1; j > i; j--) {
				a[i][n] = a[i][n] - x[j] * a[i][j];
			}
			x[i] = a[i][n] / a[i][i];
		}
	}
	
	/**
	 * 
	 * @param list
	 */
	public void setA(List<Double> list) {
		int index = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n + 1; j++) {
				a[i][j] = list.get(index);
				index++;
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public double[] getResult() {
		for (int i = 0; i < n - 1; i++) {
			selectAndChangeLine(i);
			elimination(i);
		}
		backsSubstitution();
		return x;
	}
}
