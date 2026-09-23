import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Genera los archivos de texto que sirven como entrada para el proyecto
 * de Generacion y Clasificacion de Datos.
 *
 * <p>Esta clase corresponde a la primera entrega del proyecto. Su objetivo
 * es crear informacion de prueba para vendedores, productos y ventas.</p>
 *
 * @author Karen Dayana Morales
 * @version 1.0
 */
public class GenerateInfoFiles {

    private static final String DATA_FOLDER = "data";
    private static final Random RANDOM = new Random();

    private static final String[] NAMES = {
        "Karen", "Laura", "Sofia", "Daniela", "Camila",
        "Valentina", "Andres", "Juan", "Carlos", "Miguel",
        "David", "Sebastian", "Nicolas", "Mateo", "Juliana"
    };

    private static final String[] LAST_NAMES = {
        "Morales", "Rodriguez", "Gomez", "Martinez", "Garcia",
        "Lopez", "Hernandez", "Torres", "Ramirez", "Castro",
        "Vargas", "Rojas", "Perez", "Sanchez", "Diaz"
    };

    private static final String[] PRODUCT_NAMES = {
        "Computador", "Teclado", "Mouse", "Monitor", "Impresora",
        "Memoria USB", "Disco Duro", "Audifonos", "Webcam", "Router",
        "Tablet", "Celular", "Cable HDMI", "Parlantes", "Microfono"
    };

    /**
     * Metodo principal. Genera los archivos de prueba sin solicitar
     * informacion al usuario.
     *
     * @param args argumentos de ejecucion, no utilizados
     */
    public static void main(String[] args) {
        try {
            createDataFolder();

            int productsCount = 15;
            int salesmanCount = 5;
            int salesPerSalesman = 8;

            createProductsFile(productsCount);
            createSalesManInfoFile(salesmanCount);

            createSalesFiles(salesmanCount, salesPerSalesman);

            System.out.println("==============================================");
            System.out.println("GENERACION DE ARCHIVOS FINALIZADA CON EXITO");
            System.out.println("Los archivos fueron creados en la carpeta: " + DATA_FOLDER);
            System.out.println("==============================================");
        } catch (IOException exception) {
            System.out.println("ERROR: No fue posible generar los archivos.");
            System.out.println("Detalle: " + exception.getMessage());
        }
    }

    /**
     * Crea la carpeta donde se almacenan los archivos generados.
     *
     * @throws IOException si no es posible crear la carpeta.
     */
    private static void createDataFolder() throws IOException {
        File folder = new File(DATA_FOLDER);

        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("No fue posible crear la carpeta " + DATA_FOLDER);
        }
    }

    /**
     * Crea un archivo de ventas para un vendedor.
     *
     * Formato:
     * IDProducto;CantidadProductoVendido;
     *
     * @param randomSalesCount cantidad de ventas a generar
     * @param name nombre del vendedor, utilizado para construir el nombre
     *             del archivo
     * @param id identificacion del vendedor
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createSalesMenFile(int randomSalesCount,
                                           String name,
                                           long id) throws IOException {

        String safeName = name.replaceAll("[^a-zA-Z0-9_-]", "_");
        File file = new File(DATA_FOLDER + File.separator
                + "sales_" + id + "_" + safeName + ".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < randomSalesCount; i++) {
                int productId = 1 + RANDOM.nextInt(15);
                int quantity = 1 + RANDOM.nextInt(10);

                writer.write(productId + ";" + quantity + ";");
                writer.newLine();
            }
        }
    }

    /**
     * Crea el archivo con la informacion de los productos disponibles.
     *
     * Formato:
     * IDProducto;NombreProducto;PrecioPorUnidadProducto
     *
     * @param productsCount cantidad de productos a generar
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createProductsFile(int productsCount) throws IOException {
        if (productsCount <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de productos debe ser mayor que cero.");
        }

        File file = new File(DATA_FOLDER + File.separator + "products.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 1; i <= productsCount; i++) {
                String productName = PRODUCT_NAMES[(i - 1) % PRODUCT_NAMES.length];
                int price = 20000 + RANDOM.nextInt(481) * 1000;

                writer.write(i + ";" + productName + ";" + price);
                writer.newLine();
            }
        }
    }

    /**
     * Crea el archivo con la informacion de los vendedores.
     *
     * Formato:
     * TipoDocumento;NumeroDocumento;Nombres;Apellidos
     *
     * @param salesmanCount cantidad de vendedores a generar
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        if (salesmanCount <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de vendedores debe ser mayor que cero.");
        }

        File file = new File(DATA_FOLDER + File.separator + "salesmen.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 1; i <= salesmanCount; i++) {
                String name = NAMES[(i - 1) % NAMES.length];
                String lastName = LAST_NAMES[(i - 2 + LAST_NAMES.length)
                        % LAST_NAMES.length];
                long document = 1000000000L + RANDOM.nextInt(900000000);

                writer.write("CC;" + document + ";" + name + ";" + lastName);
                writer.newLine();
            }
        }
    }

    /**
     * Genera un archivo de ventas por cada vendedor.
     *
     * @param salesmanCount cantidad de vendedores
     * @param salesPerSalesman cantidad de registros de venta por vendedor
     * @throws IOException si ocurre un error al escribir los archivos
     */
    private static void createSalesFiles(int salesmanCount,
                                         int salesPerSalesman) throws IOException {
        for (int i = 1; i <= salesmanCount; i++) {
            String name = NAMES[(i - 1) % NAMES.length];
            long document = 1000000000L + i;
            createSalesMenFile(salesPerSalesman, name, document);
        }
    }
}
