

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class format {

    private static final String WEBSITE_URL = "https://row.gymshark.com/collections/all-products"; // Replace with the actual URL
    private static final String CSV_FILE_PATH = "products.csv";

    public static void main(String[] args) {
        List<Product> products = new ArrayList<>();

        try {
            // Connect to the website and parse the HTML
            Document doc = Jsoup.connect(WEBSITE_URL).get();

            // Select product elements (adjust selectors based on the target website's HTML structure)
            Elements productElements = doc.select(".product-item"); // Example: div with class "product-item"

            for (Element productElement : productElements) {
                // Extract product name
                String name = productElement.select(".product-name").text(); // Example: h2 with class "product-name"

                // Extract product price (handling potential currency symbols)
                String priceText = productElement.select(".product-price").text().replaceAll("[^\\d.]", ""); // Example: span with class "product-price"
                double price = 0.0;
                try {
                    price = Double.parseDouble(priceText);
                } catch (NumberFormatException e) {
                    System.err.println("Could not parse price for product: " + name);
                }

                // Extract product rating
                String ratingText = productElement.select(".product-rating").text(); // Example: span with class "product-rating"
                double rating = 0.0;
                try {
                    rating = Double.parseDouble(ratingText);
                } catch (NumberFormatException e) {
                    System.err.println("Could not parse rating for product: " + name);
                }

                products.add(new Product(name, price, rating));
            }

            // Write data to CSV file
            writeProductsToCsv(products, CSV_FILE_PATH);
            System.out.println("Product data successfully scraped and saved to " + CSV_FILE_PATH);

        } catch (IOException e) {
            System.err.println("Error during web scraping or file writing: " + e.getMessage());
        }
    }

    private static void writeProductsToCsv(List<Product> products, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.append("Name,Price,Rating\n");

            // Write product data
            for (Product product : products) {
                writer.append(String.format("%s,%.2f,%.1f\n", product.getName(), product.getPrice(), product.getRating()));
            }
        }
    }

    // Product class to hold extracted data
    static class Product {
        private String name;
        private double price;
        private double rating;

        public Product(String name, double price, double rating) {
            this.name = name;
            this.price = price;
            this.rating = rating;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public double getRating() {
            return rating;
        }
    }
}