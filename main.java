import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Programa principal del proyecto Generacion y Clasificacion de Datos.
 *
 * Lee los archivos generados por GenerateInfoFiles y crea:
 *
 * 1. Reporte de vendedores.
 * 2. Reporte de productos.
 *
 * @author Karen Dayana Morales
 * @version 1.0
 */
public class main {

    private static final String DATA_FOLDER = "data";
    private static final String REPORT_FOLDER = "reports";

    /**
     * Metodo principal.
     *
     * @param args argumentos de ejecucion, no utilizados
     */
    public static void main(String[] args) {

        try {

            Map<Long, Seller> sellers = readSellers();
            Map<Integer, Product> products = readProducts();

            processSalesFiles(sellers, products);

            createSellerReport(sellers);
            createProductReport(products);

            System.out.println("==============================================");
            System.out.println("REPORTES GENERADOS CORRECTAMENTE");
            System.out.println("==============================================");

        } catch (Exception exception) {

            System.out.println("==============================================");
            System.out.println("ERROR AL GENERAR LOS REPORTES");
            System.out.println("Detalle: " + exception.getMessage());
            System.out.println("==============================================");
        }
    }

    /**
     * Lee la informacion de los vendedores.
     *
     * @return mapa de vendedores
     * @throws IOException si ocurre un error de lectura
     */
    private static Map<Long, Seller> readSellers()
            throws IOException {

        Map<Long, Seller> sellers = new HashMap<>();

        File file = new File(
                DATA_FOLDER
                + File.separator
                + "salesmen.txt"
        );

        if (!file.exists()) {
            throw new IOException(
                    "No existe el archivo salesmen.txt");
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";");

                if (parts.length != 4) {
                    throw new IOException(
                            "Formato incorrecto en salesmen.txt: "
                            + line);
                }

                long document =
                        Long.parseLong(parts[1]);

                String name = parts[2];
                String lastName = parts[3];

                Seller seller =
                        new Seller(
                                document,
                                name,
                                lastName
                        );

                sellers.put(document, seller);
            }
        }

        return sellers;
    }

    /**
     * Lee la informacion de los productos.
     *
     * @return mapa de productos
     * @throws IOException si ocurre un error de lectura
     */
    private static Map<Integer, Product> readProducts()
            throws IOException {

        Map<Integer, Product> products = new HashMap<>();

        File file = new File(
                DATA_FOLDER
                + File.separator
                + "products.txt"
        );

        if (!file.exists()) {
            throw new IOException(
                    "No existe el archivo products.txt");
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";");

                if (parts.length != 3) {
                    throw new IOException(
                            "Formato incorrecto en products.txt: "
                            + line);
                }

                int id =
                        Integer.parseInt(parts[0]);

                String name = parts[1];

                double price =
                        Double.parseDouble(parts[2]);

                if (price < 0) {
                    throw new IOException(
                            "Precio negativo para producto "
                            + id);
                }

                products.put(
                        id,
                        new Product(
                                id,
                                name,
                                price
                        )
                );
            }
        }

        return products;
    }

    /**
     * Procesa todos los archivos de ventas.
     *
     * @param sellers vendedores disponibles
     * @param products productos disponibles
     * @throws IOException si existe informacion incorrecta
     */
    private static void processSalesFiles(
            Map<Long, Seller> sellers,
            Map<Integer, Product> products)
            throws IOException {

        File folder = new File(DATA_FOLDER);

        File[] files = folder.listFiles();

        if (files == null) {
            throw new IOException(
                    "No fue posible leer la carpeta data.");
        }

        for (File file : files) {

            String fileName = file.getName();

            if (!file.isFile()
                    || !fileName.startsWith("sales_")
                    || !fileName.endsWith(".txt")) {
                continue;
            }

            processSingleSalesFile(
                    file,
                    sellers,
                    products
            );
        }
    }

    /**
     * Procesa el archivo de ventas de un vendedor.
     *
     * @param file archivo de ventas
     * @param sellers vendedores
     * @param products productos
     * @throws IOException si el formato es incorrecto
     */
    private static void processSingleSalesFile(
            File file,
            Map<Long, Seller> sellers,
            Map<Integer, Product> products)
            throws IOException {

        try (BufferedReader reader =
                new BufferedReader(new FileReader(file))) {

            String firstLine = reader.readLine();

            if (firstLine == null) {
                throw new IOException(
                        "Archivo vacio: "
                        + file.getName());
            }

            String[] sellerData =
                    firstLine.split(";");

            if (sellerData.length != 2) {
                throw new IOException(
                        "Primera linea incorrecta en "
                        + file.getName());
            }

            long sellerId =
                    Long.parseLong(sellerData[1]);

            Seller seller = sellers.get(sellerId);

            if (seller == null) {
                throw new IOException(
                        "El vendedor "
                        + sellerId
                        + " no existe en salesmen.txt");
            }

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";");

                if (parts.length < 2) {
                    throw new IOException(
                            "Formato incorrecto en "
                            + file.getName()
                            + ": "
                            + line);
                }

                int productId =
                        Integer.parseInt(parts[0]);

                int quantity =
                        Integer.parseInt(parts[1]);

                if (quantity < 0) {
                    throw new IOException(
                            "Cantidad negativa en "
                            + file.getName());
                }

                Product product =
                        products.get(productId);

                if (product == null) {
                    throw new IOException(
                            "El producto "
                            + productId
                            + " no existe.");
                }

                seller.addRevenue(
                        product.getPrice()
                        * quantity
                );

                product.addQuantity(quantity);
            }
        }
    }

    /**
     * Crea el reporte de vendedores.
     *
     * @param sellers vendedores procesados
     * @throws IOException si ocurre un error de escritura
     */
    private static void createSellerReport(
            Map<Long, Seller> sellers)
            throws IOException {

        createReportFolder();

        List<Seller> sellerList =
                new ArrayList<>(sellers.values());

        sellerList.sort(
                Comparator.comparingDouble(
                        Seller::getRevenue
                ).reversed()
        );

        File file = new File(
                REPORT_FOLDER
                + File.separator
                + "reporte_vendedores.csv"
        );

        try (BufferedWriter writer =
                new BufferedWriter(new FileWriter(file))) {

            for (Seller seller : sellerList) {

                writer.write(
                        seller.getFullName()
                        + ";"
                        + String.format(
                                "%.2f",
                                seller.getRevenue()
                        )
                );

                writer.newLine();
            }
        }
    }

    /**
     * Crea el reporte de productos.
     *
     * @param products productos procesados
     * @throws IOException si ocurre un error de escritura
     */
    private static void createProductReport(
            Map<Integer, Product> products)
            throws IOException {

        createReportFolder();

        List<Product> productList =
                new ArrayList<>(products.values());

        productList.sort(
                Comparator.comparingInt(
                        Product::getQuantitySold
                ).reversed()
        );

        File file = new File(
                REPORT_FOLDER
                + File.separator
                + "reporte_productos.csv"
        );

        try (BufferedWriter writer =
                new BufferedWriter(new FileWriter(file))) {

            for (Product product : productList) {

                writer.write(
                        product.getName()
                        + ";"
                        + String.format(
                                "%.2f",
                                product.getPrice()
                        )
                );

                writer.newLine();
            }
        }
    }

    /**
     * Crea la carpeta de reportes.
     *
     * @throws IOException si no puede crearse
     */
    private static void createReportFolder()
            throws IOException {

        File folder = new File(REPORT_FOLDER);

        if (!folder.exists()
                && !folder.mkdirs()) {

            throw new IOException(
                    "No fue posible crear la carpeta reports.");
        }
    }

    /**
     * Representa un vendedor.
     */
    private static class Seller {

        private final long document;
        private final String name;
        private final String lastName;
        private double revenue;

        Seller(
                long document,
                String name,
                String lastName) {

            this.document = document;
            this.name = name;
            this.lastName = lastName;
            this.revenue = 0.0;
        }

        void addRevenue(double value) {
            revenue += value;
        }

        double getRevenue() {
            return revenue;
        }

        String getFullName() {
            return name + " " + lastName;
        }
    }

    /**
     * Representa un producto.
     */
    private static class Product {

        private final int id;
        private final String name;
        private final double price;
        private int quantitySold;

        Product(
                int id,
                String name,
                double price) {

            this.id = id;
            this.name = name;
            this.price = price;
            this.quantitySold = 0;
        }

        void addQuantity(int quantity) {
            quantitySold += quantity;
        }

        String getName() {
            return name;
        }

        double getPrice() {
            return price;
        }

        int getQuantitySold() {
            return quantitySold;
        }
    }
}
