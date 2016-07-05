package org.jfw.apt.demo.web.pojo;

import java.math.BigInteger;
import java.util.List;

public class TestPojo extends BaseTestPojo {
	private String[] names;
	private int[] ages;
	
	private float hight;
	
	
	private double weight;
	
	private BigInteger bi;
	
	private List<String> ss;

	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	public int[] getAges() {
		return ages;
	}

	public void setAges(int[] ages) {
		this.ages = ages;
	}

	public float getHight() {
		return hight;
	}

	public void setHight(float hight) {
		this.hight = hight;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public BigInteger getBi() {
		return bi;
	}

	public void setBi(BigInteger bi) {
		this.bi = bi;
	}

	public List<String> getSs() {
		return ss;
	}

	public void setSs(List<String> ss) {
		this.ss = ss;
	}

}
