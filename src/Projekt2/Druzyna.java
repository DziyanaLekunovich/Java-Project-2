package Projekt2;

import java.util.Random;

public class Druzyna implements Comparable<Druzyna>{
	String kraj;
	String grupa;
	Integer punkty;
	Integer bilans;
	Integer goli;
	int wygrane;
	int remisy;
	int porazki;
	
	
	public Druzyna(String k, String g) {
		this.grupa = g;
		this.kraj = k;
		this.punkty = 0;
		this.bilans = 0;
		this.goli = 0;
		this.wygrane = 0;
		this.remisy = 0;
		this.porazki = 0;
	}

	public String getKraj() {
		return kraj;
	}

	public void setKraj(String kraj) {
		this.kraj = kraj;
	}

	public String getGrupa() {
		return grupa;
	}

	public void setGrupa(String grupa) {
		this.grupa = grupa;
	}
	
	public int compareTo(Druzyna d) {
		if (!this.grupa.equals(d.grupa))
			return this.grupa.compareTo(d.grupa);
		else {
			if (this.punkty != d.punkty)
				return d.punkty.compareTo(this.punkty);
			else {
				if (this.bilans != d.bilans)
					return d.bilans.compareTo(this.bilans);
				else {
					if (this.goli != d.goli)
						return d.goli.compareTo(this.goli);
					else {
						Random generator = new Random();
						if (generator.nextInt(2) == 1)
							return this.kraj.compareTo(d.kraj);
						else
							return d.kraj.compareTo(this.kraj);
					}
				}
			}
		}
		
	}
}
