package newsanalyzer.ui;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import newsanalyzer.ctrl.Controller;
import newsapi.NewsApiException;
import newsapi.enums.*;

public class UserInterface 
{

	private Controller ctrl = new Controller();

	public void getDataFromCtrl1(){
		try {
			ctrl.process("corona", Endpoint.TOP_HEADLINES, Language.de, Country.at, Category.health, SortBy.RELEVANCY);
		} catch (NewsApiException e) {
			System.out.println("NewsApiException: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Error: General exception");
		}
	}

	public void getDataFromCtrl2(){
		try {
			ctrl.process("", Endpoint.TOP_HEADLINES, Language.de, Country.at, Category.technology, SortBy.RELEVANCY);
		} catch (NewsApiException e) {
			System.out.println("NewsApiException: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Error: General exception");
		}
		// TODO implement me
	}

	public void getDataFromCtrl3(){
		try {
			ctrl.process("", Endpoint.TOP_HEADLINES, Language.de, Country.at, Category.science, SortBy.RELEVANCY);
		} catch (NewsApiException e) {
			System.out.println("NewsApiException: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Error: General exception");
		}
		// TODO implement me
	}
	
	public void getDataForCustomInput() {
		System.out.print("Geben sie einen Suchbegriff ein: ");
		String query = readLine();

		System.out.println("Kategorien: " + java.util.Arrays.asList(Category.values()));
		String categoryString;
		do {
			System.out.print("Geben sie eine Kategorie ein: ");
			categoryString = readLine();
			categoryString = categoryString.toLowerCase();
		} while (!checkCategory(categoryString));
		Category category = Category.valueOf(categoryString);

		try {
			ctrl.process(query, Endpoint.TOP_HEADLINES, Language.de, Country.at, category, SortBy.RELEVANCY);
		} catch (NewsApiException e) {
			System.out.println("NewsApiException: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Error: General exception");
		}
		// TODO implement me
	}


	public void start() {
		Menu<Runnable> menu = new Menu<>("User Interface");
		menu.setTitel("Wählen Sie aus:");
		menu.insert("a", "Aktuelle Nachrichten zu COVID-19 aus Österreich", this::getDataFromCtrl1);
		menu.insert("b", "Aktuelle Nachrichten zum Thema Technologie aus Österreich", this::getDataFromCtrl2);
		menu.insert("c", "Aktuelle Nachrichten zum Thema Wissenschaft aus Österreich", this::getDataFromCtrl3);
		menu.insert("d", "Benutzereingabe:",this::getDataForCustomInput);
		menu.insert("q", "Quit", null);
		Runnable choice;
		while ((choice = menu.exec()) != null) {
			 choice.run();
		}
		System.out.println("Program finished");
	}


    protected String readLine() {
		String value = "\0";
		BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			value = inReader.readLine();
        } catch (IOException ignored) {
		}
		return value.trim();
	}

	protected Double readDouble(int lowerlimit, int upperlimit) 	{
		Double number = null;
        while (number == null) {
			String str = this.readLine();
			try {
				number = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                number = null;
				System.out.println("Please enter a valid number:");
				continue;
			}
            if (number < lowerlimit) {
				System.out.println("Please enter a higher number:");
                number = null;
            } else if (number > upperlimit) {
				System.out.println("Please enter a lower number:");
                number = null;
			}
		}
		return number;
	}

	private boolean checkCategory(String categoryString) {
		for (Category c : Category.values()) {
			if (c.name().equals(categoryString)) return true;
		}
		return false;
	}
}
