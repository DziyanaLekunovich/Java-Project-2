package Projekt2;

import java.time.LocalDate;

public class Mecz {
	Druzyna d1;
	Druzyna d2;
	LocalDate data;
	int d1w;
	int d2w;
	int numer;
	String faza;
	String d1s;
	String d2s;
	Druzyna zwyciezca;
	
	public Mecz(LocalDate d, int n, String f) {
		this.data = d;
		d1 = null;
		d2 = null;
		d1w = 0;
		d2w = 0;
		numer = n;
		faza = f;
		d1s = "";
		d2s = "";
		zwyciezca = (Druzyna) null;
	}
	
	public void setD1S(String d) {
		d1s = d;
	}
	
	public void setD2S(String d) {
		d2s =d;
	}
	
	public void setD1(Druzyna d) {
		d1 = d;
	}
	
	public void setD2(Druzyna d) {
		d2 = d;
	}
	
	public void setD1W(int w) {
		d1w = w;
	}
	
	public void setD2W(int w) {
		d2w = w;
	}
	
	public void setZwyciezca(Druzyna d) {
		this.zwyciezca = d;
	}
}
