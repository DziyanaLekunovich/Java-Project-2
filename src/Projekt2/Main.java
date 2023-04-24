package Projekt2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
	public static void main(String[] args) throws InterruptedException, IOException {
		List<Druzyna> d = new ArrayList<>();
		List<Mecz> m = new ArrayList<>();
		
		Mundial mundial = Mundial.getInstance();
		mundial.setDruzyny(d);
		mundial.setMecze(m);
		
		try(BufferedReader reader = Files.newBufferedReader(Paths.get("dane.txt"))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				if(line.startsWith("17"))
					break;
				String[] fields = line.split(";");
				Druzyna d1 = new Druzyna(fields[2], fields[5]);
				Druzyna d2 = new Druzyna(fields[3], fields[5]);
				mundial.getDruzyny().add(d1);
				mundial.getDruzyny().add(d2);
			}
		}
		catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
		
		Charset charset = Charset.forName("UTF-8");
		try(BufferedReader reader = Files.newBufferedReader(Paths.get("dane.txt"), charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] fields = line.split(";");
				
				int n = Integer.parseInt(fields[0]);
				
				Mecz m1 = new Mecz(LocalDate.parse(fields[4]), n, fields[1]);
				for(Druzyna d1: mundial.getDruzyny()) {
					if (d1.getKraj().equals(fields[2]))
						m1.setD1(d1);
					else
						m1.setD1S(fields[2]);
				}
				for(Druzyna d2: mundial.getDruzyny()) {
					if (d2.getKraj().equals(fields[3]))
						m1.setD2(d2); 
					else
						m1.setD2S(fields[3]);
				}
				mundial.getMecze().add(m1);
			}
		}
		catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
		catch (NumberFormatException e) {
			System.err.format("NumberFormatException: %s%n", e);
		}
		
		
		rozegracGrupowa1(mundial.getMecze());
		rozegracGrupowa2(mundial.getMecze());
		rozegracGrupowa3(mundial.getMecze());
		
		Collections.sort(mundial.getDruzyny());
		
		for(int i = 0; i < 32; i++) {
			System.out.println(mundial.getDruzyny().get(i).kraj + " " + mundial.getDruzyny().get(i).grupa + " " + mundial.getDruzyny().get(i).punkty + " " + mundial.getDruzyny().get(i).bilans + " " + mundial.getDruzyny().get(i).goli);
		}
		
		List<String> l = new ArrayList<>();
		
		druzyny8finalu(mundial.getMecze(), mundial.getDruzyny(), l);
		rozegrac8Finalu(mundial.getMecze());
		druzyny4finalu(mundial.getMecze(), mundial.getDruzyny());
		rozegrac4Finalu(mundial.getMecze());
		druzynyPulFinalu(mundial.getMecze(), mundial.getDruzyny());
		rozegracPolFinalu(mundial.getMecze());
		druzynyFinalu(mundial.getMecze(), mundial.getDruzyny());
		rozegracFinalu(mundial.getMecze());
		druzynyMeczaO3Miejsce(mundial.getMecze(), mundial.getDruzyny());
		rozegracMeczO3Miejsce(mundial.getMecze());
		
		raport(mundial.getMecze());
		//wypisz(mundial.getDruzyny().get(1), mundial.getMecze());
		
		mundial.getMecze()
			.stream()
			.filter(mecz -> (mecz.d1w == mecz.d2w))
			.filter(mecz -> mecz.faza.equals("grupowa 1") || mecz.faza.equals("grupowa 2") || mecz.faza.equals("grupowa 3"))
			.forEach(mecz -> System.out.println(mecz.d1.kraj + " - " + mecz.d2.kraj));
		
		mundial.getDruzyny()
			.stream()
			.filter(dr -> dr.kraj.equals("Polska"))
			.forEach(dr -> wypisz(dr, mundial.getMecze()));
		
			
	}
	
	public static void wypisz(Druzyna d, List<Mecz> m) {
		System.out.println("Nazwa: " + d.kraj);
		for(;;) {
			if (m.get(63).zwyciezca == null) {
				if (m.get(63).d1w > m.get(63).d2w) {
					if (m.get(63).d1.equals(d)) {
						System.out.println("Miejsce: 1");
						break;
					}
					else if (m.get(63).d2.equals(d)){
						System.out.println("Miejsce: 2");
						break;
					}
				}
				else {
					if (m.get(63).d1.equals(d)) {
						System.out.println("Miejsce: 2");
						break;
					}
					else if (m.get(63).d2.equals(d)){
						System.out.println("Miejsce: 1");
						break;
					}
				}
			}
			else {
				if (m.get(63).zwyciezca.equals(d)) {
					System.out.println("Miejsce: 1");
					break;
				}
			}
			
			
			if (m.get(62).zwyciezca == null) {
				if (m.get(62).d1w > m.get(62).d2w) {
					if (m.get(62).d1.equals(d)) {
						System.out.println("Miejsce: 3");
						break;
					}
				}
				else {
					if (m.get(62).d2.equals(d)){
						System.out.println("Miejsce: 3");
						break;
					}
				}
			}
			else {
				if (m.get(62).zwyciezca.equals(d)) {
					System.out.println("Miejsce: 3");
					break;
				}
			}
			break;
		}
		System.out.println("Liczba goli strzelonych: " + d.goli);
		System.out.println("Liczba goli straconych: " + (d.goli - d.bilans));
		System.out.println("Liczba gier wygranych: " + d.wygrane);
		System.out.println("Liczba remisow: " + d.remisy);
		System.out.println("Liczba porazek: " + d.porazki);
	}
	
	public static void raport(List<Mecz> m) throws IOException {
		Path p1 = Paths.get("raport.txt");
		Files.createFile(p1);
		try (BufferedWriter writer = Files.newBufferedWriter(p1, StandardOpenOption.APPEND)){
			writer.write("--------------\n");
			writer.write("Faza grupowa\n");
			writer.write("--------------\n");
			for(int i = 0; i < 48; i++) {
				writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + ":" + m.get(i).d2w + "\n");
			}
			writer.write("--------------\n");
			writer.write("1/8 finalu\n");
			writer.write("--------------\n");
			for(int i = 48; i < 56; i++) {
				if (m.get(i).d1w != m.get(i).d2w)
					writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + ":" + m.get(i).d2w + "\n");
				else {
					if (m.get(i).zwyciezca.equals(m.get(i).d1))
						writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + "(W):" + m.get(i).d2w + "\n");
					else
						writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + ":" + m.get(i).d2w + "(W)\n");
				}
			}
			writer.write("--------------\n");
			writer.write("1/4 finalu\n");
			writer.write("--------------\n");
			for(int i = 56; i < 60; i++) {
				if (m.get(i).d1w != m.get(i).d2w)
					writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + ":" + m.get(i).d2w + "\n");
				else {
					if (m.get(i).zwyciezca.equals(m.get(i).d1))
						writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + "(W):" + m.get(i).d2w + "\n");
					else
						writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + ":" + m.get(i).d2w + "(W)\n");
				}
			}
			writer.write("--------------\n");
			writer.write("Polfinal\n");
			writer.write("--------------\n");
			for(int i = 60; i < 62; i++) {
				if (m.get(i).d1w != m.get(i).d2w)
					writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + ":" + m.get(i).d2w + "\n");
				else {
					if (m.get(i).zwyciezca.equals(m.get(i).d1))
						writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + "(W):" + m.get(i).d2w + "\n");
					else
						writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + ":" + m.get(i).d2w + "(W)\n");
				}
			}
			writer.write("--------------\n");
			writer.write("Mecz o 3 miejsce\n");
			writer.write("--------------\n");
			for(int i = 62; i < 63; i++) {
				if (m.get(i).d1w != m.get(i).d2w)
					writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + ":" + m.get(i).d2w + "\n");
				else {
					if (m.get(i).zwyciezca.equals(m.get(i).d1))
						writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + "(W):" + m.get(i).d2w + "\n");
					else
						writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + ":" + m.get(i).d2w + "(W)\n");
				}
			}
			writer.write("--------------\n");
			writer.write("Final\n");
			writer.write("--------------\n");
			for(int i = 63; i < 64; i++) {
				if (m.get(i).d1w != m.get(i).d2w)
					writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + ":" + m.get(i).d2w + "\n");
				else {
					if (m.get(i).zwyciezca.equals(m.get(i).d1))
						writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + "(W):" + m.get(i).d2w + "\n");
					else
						writer.write(m.get(i).d1.kraj + " - " + m.get(i).d2.kraj + " " + m.get(i).d1w + ":" + m.get(i).d2w + "(W)\n");
				}
			}
			writer.write("--------------\n");
			writer.write("MISTRZ SWIATA: ");
			if (m.get(63).d1w != m.get(63).d2w)
				if (m.get(63).d1w > m.get(63).d2w)
					writer.write(m.get(63).d1.kraj + "\n");
				else
					writer.write(m.get(63).d2.kraj + "\n");
			else {
				if (m.get(63).zwyciezca.equals(m.get(63).d1))
					writer.write(m.get(63).d1.kraj + "\n");
				else
					writer.write(m.get(63).d2.kraj + "\n");
			}
		}
		catch (IOException x) {
			System.err.format("IOException : %s%n", x);
		}
	}
	
	public static void rozegrac(Mecz m) {
		Random generator = new Random();
		m.setD1W(generator.nextInt(4));
		m.setD2W(generator.nextInt(4));
		if(m.faza != "grupowa 1" && m.faza != "grupowa 2" && m.faza != "grupowa 3" && m.d1w == m.d2w) {
			if(generator.nextInt(2) == 1)
				m.setZwyciezca(m.d1);
			else
				m.setZwyciezca(m.d2);
		}
		
		m.d1.goli += m.d1w;
		m.d2.goli += m.d2w;
		
		m.d1.bilans += m.d1w;
		m.d1.bilans -= m.d2w;
		m.d2.bilans += m.d2w;
		m.d2.bilans -= m.d1w;
		
		if(m.d1w > m.d2w) {
			m.d1.punkty += 3;
			m.d1.wygrane += 1;
			m.d2.porazki += 1;
		}
		else if(m.d1w == m.d2w) {
			m.d2.punkty += 1;
			m.d1.punkty += 1;
			m.d1.remisy += 1;
			m.d2.remisy += 1;
		}
		else {
			m.d2.punkty += 3;
			m.d2.wygrane += 1;
			m.d1.porazki += 1;
		}
	}
	
	public static void rozegracGrupowa1(List<Mecz> m) throws InterruptedException{
		ExecutorService exe = Executors.newFixedThreadPool(16);
		
		List<Callable<Object>> list = new ArrayList<>();
		
		for (int i = 0; i < 16; i++) {
			final Integer j = i;
			Runnable run = new Runnable() {
				public void run() {
					rozegrac(m.get(j));	
				}
			};
			list.add(Executors.callable(run));
		}
		
		exe.invokeAll(list);
			
	}
	
	public static void rozegracGrupowa2(List<Mecz> m) throws InterruptedException{
		ExecutorService exe = Executors.newFixedThreadPool(16);
		
		List<Callable<Object>> list = new ArrayList<>();
		
		for (int i = 16; i < 32; i++) {
			final Integer j = i;
			Runnable run = new Runnable() {
				public void run() {
					rozegrac(m.get(j));	
				}
			};
			list.add(Executors.callable(run));
		}
		exe.invokeAll(list);
			
	}
	
	public static void rozegracGrupowa3(List<Mecz> m) throws InterruptedException{
		ExecutorService exe = Executors.newFixedThreadPool(16);
		
		List<Callable<Object>> list = new ArrayList<>();
		
		for (int i = 32; i < 48; i++) {
			final Integer j = i;
			Runnable run = new Runnable() {
				public void run() {
					rozegrac(m.get(j));	
				}
			};
			list.add(Executors.callable(run));
		}
		exe.invokeAll(list);
	}
	
	public static void rozegrac8Finalu(List<Mecz> m) throws InterruptedException{
		ExecutorService exe = Executors.newFixedThreadPool(8);
		
		List<Callable<Object>> list = new ArrayList<>();
		
		for (int i = 48; i < 56; i++) {
			final Integer j = i;
			Runnable run = new Runnable() {
				public void run() {
					rozegrac(m.get(j));	
				}
			};
			list.add(Executors.callable(run));
		}
		
		exe.invokeAll(list);
			
	}
	
	public static void rozegrac4Finalu(List<Mecz> m) throws InterruptedException{
		ExecutorService exe = Executors.newFixedThreadPool(4);
		
		List<Callable<Object>> list = new ArrayList<>();
		
		for (int i = 56; i < 60; i++) {
			final Integer j = i;
			Runnable run = new Runnable() {
				public void run() {
					rozegrac(m.get(j));	
				}
			};
			list.add(Executors.callable(run));
		}
		
		exe.invokeAll(list);
			
	}
	
	public static void rozegracPolFinalu(List<Mecz> m) throws InterruptedException{
		ExecutorService exe = Executors.newFixedThreadPool(2);
		
		List<Callable<Object>> list = new ArrayList<>();
		
		for (int i = 60; i < 62; i++) {
			final Integer j = i;
			Runnable run = new Runnable() {
				public void run() {
					rozegrac(m.get(j));	
				}
			};
			list.add(Executors.callable(run));
		}
		
		exe.invokeAll(list);
			
	}
	
	public static void rozegracFinalu(List<Mecz> m) throws InterruptedException{
		ExecutorService exe = Executors.newFixedThreadPool(1);
		
		List<Callable<Object>> list = new ArrayList<>();
		
		for (int i = 63; i < 64; i++) {
			final Integer j = i;
			Runnable run = new Runnable() {
				public void run() {
					rozegrac(m.get(j));	
				}
			};
			list.add(Executors.callable(run));
		}
		
		exe.invokeAll(list);
			
	}
	
	public static void rozegracMeczO3Miejsce(List<Mecz> m) throws InterruptedException{
		ExecutorService exe = Executors.newFixedThreadPool(1);
		
		List<Callable<Object>> list = new ArrayList<>();
		
		for (int i = 62; i < 63; i++) {
			final Integer j = i;
			Runnable run = new Runnable() {
				public void run() {
					rozegrac(m.get(j));	
				}
			};
			list.add(Executors.callable(run));
		}
		
		exe.invokeAll(list);
			
	}
	
	public static void druzyny8finalu(List<Mecz> m, List<Druzyna> d, List<String> kraje) {
		for (int i = 48; i < 56; i++ ) {
			String druzyna1 = m.get(i).d1s;
			
			StringBuilder str = new StringBuilder(druzyna1);
			str.deleteCharAt(1);
			String grupa = str.toString();
			for(Druzyna dr : d) {
				if (dr.grupa.equals(grupa)) {
					String kraj = dr.kraj;
					kraje.add(kraj);
					m.get(i).setD1(dr);
					break;
				}
			}
		}
		
		for (int i = 48; i < 56; i++ ) {
			String druzyna2 = m.get(i).d2s;
			
			StringBuilder str = new StringBuilder(druzyna2);
			str.deleteCharAt(1);
			String grupa = str.toString();
			for(Druzyna dr : d) {
				if (dr.grupa.equals(grupa) && (!kraje.contains(dr.kraj))) {
					m.get(i).setD2(dr);
					break;
				}
			}
		}
	}
	
	public static void druzyny4finalu(List<Mecz> m, List<Druzyna> d) {
		for (int j = 56; j < 60; j++) {
		
			String str = m.get(j).d1s;
			int i = Integer.parseInt(str);
			
			
					if(m.get(i - 1).d1w > m.get(i - 1).d2w) {
						m.get(j).setD1(m.get(i - 1).d1);
					}
					else if (m.get(i - 1).d2w > m.get(i - 1).d1w)
						m.get(j).setD1(m.get(i - 1).d2);
					else
						m.get(j).setD1(m.get(i - 1).zwyciezca);
					
				
			
		}
		
		for (int j = 56; j < 60; j++) {
		
			String str = m.get(j).d2s;
			int i = Integer.parseInt(str);
			
					if(m.get(i - 1).d1w > m.get(i - 1).d2w) {
						m.get(j).setD2(m.get(i - 1).d1);
					}
					else if (m.get(i - 1).d2w > m.get(i - 1).d1w)
						m.get(j).setD2(m.get(i - 1).d2);
					else
						m.get(j).setD2(m.get(i - 1).zwyciezca);
					
				
			
		}
	}
	
	public static void druzynyPulFinalu(List<Mecz> m, List<Druzyna> d) {
		for (int j = 60; j < 62; j++) {
			
			String str = m.get(j).d1s;
			int i = Integer.parseInt(str);
			
			
					if(m.get(i - 1).d1w > m.get(i - 1).d2w) {
						m.get(j).setD1(m.get(i - 1).d1);
					}
					else if (m.get(i - 1).d2w > m.get(i - 1).d1w)
						m.get(j).setD1(m.get(i - 1).d2);
					else
						m.get(j).setD1(m.get(i - 1).zwyciezca);
					
				
			
		}
		
		for (int j = 60; j < 62; j++) {
		
			String str = m.get(j).d2s;
			int i = Integer.parseInt(str);
			
					if(m.get(i - 1).d1w > m.get(i - 1).d2w) {
						m.get(j).setD2(m.get(i - 1).d1);
					}
					else if (m.get(i - 1).d2w > m.get(i - 1).d1w)
						m.get(j).setD2(m.get(i - 1).d2);
					else
						m.get(j).setD2(m.get(i - 1).zwyciezca);
					
				
			
		}
	}
	
	public static void druzynyFinalu(List<Mecz> m, List<Druzyna> d) {
		for (int j = 63; j < 64; j++) {
			
			String str = m.get(j).d1s;
			int i = Integer.parseInt(str);
			
			
					if(m.get(i - 1).d1w > m.get(i - 1).d2w) {
						m.get(j).setD1(m.get(i - 1).d1);
					}
					else if (m.get(i - 1).d2w > m.get(i - 1).d1w)
						m.get(j).setD1(m.get(i - 1).d2);
					else
						m.get(j).setD1(m.get(i - 1).zwyciezca);
					
				
			
		}
		
		for (int j = 63; j < 64; j++) {
		
			String str = m.get(j).d2s;
			int i = Integer.parseInt(str);
			
					if(m.get(i - 1).d1w > m.get(i - 1).d2w) {
						m.get(j).setD2(m.get(i - 1).d1);
					}
					else if (m.get(i - 1).d2w > m.get(i - 1).d1w)
						m.get(j).setD2(m.get(i - 1).d2);
					else
						m.get(j).setD2(m.get(i - 1).zwyciezca);
					
				
			
		}
	}
	
	public static void druzynyMeczaO3Miejsce(List<Mecz> m, List<Druzyna> d) {
		for (int j = 62; j < 63; j++) {
			
			StringBuilder str = new StringBuilder(m.get(j).d1s);
			str.deleteCharAt(0);
			String s = str.toString();
			
			int i = Integer.parseInt(s);
			
			
					if(m.get(i - 1).d1w > m.get(i - 1).d2w) {
						m.get(j).setD1(m.get(i - 1).d2);
					}
					else if (m.get(i - 1).d2w > m.get(i - 1).d1w)
						m.get(j).setD1(m.get(i - 1).d1);
					else {
						if (m.get(i - 1).zwyciezca.equals(m.get(i - 1).d1))
							m.get(j).setD1(m.get(i - 1).d2);
						else
							m.get(j).setD1(m.get(i - 1).d1);
					}
					
		}
		
		for (int j = 62; j < 63; j++) {
		
			StringBuilder str = new StringBuilder(m.get(j).d2s);
			str.deleteCharAt(0);
			String s = str.toString();
			
			int i = Integer.parseInt(s);
			
					if(m.get(i - 1).d1w > m.get(i - 1).d2w) {
						m.get(j).setD2(m.get(i - 1).d2);
					}
					else if (m.get(i - 1).d2w > m.get(i - 1).d1w)
						m.get(j).setD2(m.get(i - 1).d1);
					else {
						if (m.get(i - 1).zwyciezca.equals(m.get(i - 1).d1))
							m.get(j).setD2(m.get(i - 1).d2);
						else
							m.get(j).setD2(m.get(i - 1).d1);
					}
					
				
			
		}
	}
}
