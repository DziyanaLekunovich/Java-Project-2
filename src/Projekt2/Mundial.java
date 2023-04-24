package Projekt2;

import java.util.ArrayList;
import java.util.List;

public class Mundial {
	private static final Mundial INSTANCE = new Mundial();
	private static List<Druzyna> druzyny;
	private static List<Mecz> mecze;
	
	private Mundial() {
		druzyny = new ArrayList<>();
		mecze = new ArrayList<>();
	}

	public static List<Druzyna> getDruzyny() {
		return druzyny;
	}

	public static void setDruzyny(List<Druzyna> druzyny) {
		Mundial.druzyny = druzyny;
	}

	public static List<Mecz> getMecze() {
		return mecze;
	}

	public static void setMecze(List<Mecz> mecze) {
		Mundial.mecze = mecze;
	}

	public static Mundial getInstance() {
		return INSTANCE;
	}
	
}
