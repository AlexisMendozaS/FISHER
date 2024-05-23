import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class FisherDiscriminantApp {
    private JFrame frame;
    private JPanel panelTables;
    private int numGroups;
    private int[] objectsPerGroup;
    private int numNewObjects;
    private java.util.List<JTable> groupTables = new java.util.ArrayList<>();
    private JTable tableNewObjects; // Declaramos tableNewObjects como un campo de la clase
    private double[][][] scatterMatrices;
    private String[][] allObjects;
    private double[][] means;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                FisherDiscriminantApp window = new FisherDiscriminantApp();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public FisherDiscriminantApp() {
        initialize();
        collectGroupInfo();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        panelTables = new JPanel();
        panelTables.setLayout(new BoxLayout(panelTables, BoxLayout.Y_AXIS));
        frame.getContentPane().add(panelTables, BorderLayout.CENTER);

        JButton calculateButton = new JButton("Calcular Medias y Matrices de Dispersión");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateMeansAndScatterMatrices();
            }
        });
        frame.getContentPane().add(calculateButton, BorderLayout.SOUTH);
    }

    private void collectGroupInfo() {
        numGroups = Integer.parseInt(JOptionPane.showInputDialog("Número de grupos:"));
        objectsPerGroup = new int[numGroups];

        for (int i = 0; i < numGroups; i++) {
            objectsPerGroup[i] = Integer.parseInt(JOptionPane.showInputDialog("Número de objetos en el grupo " + (i + 1) + ":"));
        }

        numNewObjects = Integer.parseInt(JOptionPane.showInputDialog("Número de nuevos objetos:"));
        createTables();
    }

    private void createTables() {
        panelTables.removeAll();
        groupTables.clear();

        for (int i = 0; i < numGroups; i++) {
            JLabel titleLabel = new JLabel("Group " + (i + 1) + " Objects:", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JTable tableGroup = new JTable(objectsPerGroup[i], 3);
            tableGroup.getColumnModel().getColumn(0).setHeaderValue("Etiqueta");
            tableGroup.getColumnModel().getColumn(1).setHeaderValue("X");
            tableGroup.getColumnModel().getColumn(2).setHeaderValue("Y");

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            for (int j = 0; j < tableGroup.getColumnCount(); j++) {
                tableGroup.getColumnModel().getColumn(j).setCellRenderer(centerRenderer);
            }

            panelTables.add(titleLabel);
            panelTables.add(Box.createRigidArea(new Dimension(0, 5)));
            panelTables.add(new JScrollPane(tableGroup));
            panelTables.add(Box.createRigidArea(new Dimension(0, 10)));

            groupTables.add(tableGroup);
        }

        JLabel newObjectsLabel = new JLabel("New Objects:", SwingConstants.CENTER);
        newObjectsLabel.setFont(new Font("Serif", Font.BOLD, 18));
        newObjectsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        tableNewObjects = new JTable(numNewObjects, 3); // Inicializamos tableNewObjects aquí
        tableNewObjects.getColumnModel().getColumn(0).setHeaderValue("Etiqueta");
        tableNewObjects.getColumnModel().getColumn(1).setHeaderValue("X");
        tableNewObjects.getColumnModel().getColumn(2).setHeaderValue("Y");

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int j = 0; j < tableNewObjects.getColumnCount(); j++) {
            tableNewObjects.getColumnModel().getColumn(j).setCellRenderer(centerRenderer);
        }

        panelTables.add(newObjectsLabel);
        panelTables.add(Box.createRigidArea(new Dimension(0, 5)));
        panelTables.add(new JScrollPane(tableNewObjects));

        frame.revalidate();
        frame.repaint();
    }

    private void calculateMeansAndScatterMatrices() {
        means = new double[numGroups][2];
        scatterMatrices = new double[numGroups][2][2];

        // Gather all objects from tables
        int totalObjects = 0;
        for (int i = 0; i < numGroups; i++) {
            totalObjects += objectsPerGroup[i];
        }
        totalObjects += numNewObjects;

        allObjects = new String[totalObjects][3];
        int index = 0;
        for (int i = 0; i < numGroups; i++) {
            JTable tableGroup = groupTables.get(i);
            int rowCount = tableGroup.getRowCount();

            for (int row = 0; row < rowCount; row++) {
                allObjects[index][0] = (String) tableGroup.getValueAt(row, 0); // Label
                allObjects[index][1] = (String) tableGroup.getValueAt(row, 1); // X
                allObjects[index][2] = (String) tableGroup.getValueAt(row, 2); // Y
                index++;
            }
        }
        // Add new objects
        for (int row = 0; row < numNewObjects; row++) {
            allObjects[index][0] = (String) tableNewObjects.getValueAt(row, 0); // Label
            allObjects[index][1] = (String) tableNewObjects.getValueAt(row, 1); // X
            allObjects[index][2] = (String) tableNewObjects.getValueAt(row, 2); // Y
            index++;
        }

        // Print all objects
        System.out.println("Todos los objetos:");
        for (int i = 0; i < totalObjects; i++) {
            System.out.println((i + 1) + ": [" + allObjects[i][0] + ", " + allObjects[i][1] + ", " + allObjects[i][2] + "]");
        }
        System.out.println();

        // Calculate means
        for (int i = 0; i < numGroups; i++) {
            JTable tableGroup = groupTables.get(i);
            double sumX = 0;
            double sumY = 0;
            int rowCount = tableGroup.getRowCount();

            for (int row = 0; row < rowCount; row++) {
                String xValue = (String) tableGroup.getValueAt(row, 1);
                String yValue = (String) tableGroup.getValueAt(row, 2);

                if (xValue != null && yValue != null && !xValue.isEmpty() && !yValue.isEmpty()) {
                    sumX += Double.parseDouble(xValue);
                    sumY += Double.parseDouble(yValue);
                }
            }

            means[i][0] = sumX / rowCount;
            means[i][1] = sumY / rowCount;

            System.out.println("m" + (i + 1) + "= (" + means[i][0] + ", " + means[i][1] + ")");
        }

        // Calculate scatter matrices
        for (int i = 0; i < numGroups; i++) {
            JTable tableGroup = groupTables.get(i);
            double meanX = means[i][0];
            double meanY = means[i][1];
            int rowCount = tableGroup.getRowCount();

            System.out.println("Matriz de Dispersión del Grupo " + (i + 1) + ":");
            for (int row = 0; row < rowCount; row++) {
                String xValue = (String) tableGroup.getValueAt(row, 1);
                String yValue = (String) tableGroup.getValueAt(row, 2);

                if (xValue != null && yValue != null && !xValue.isEmpty() && !yValue.isEmpty()) {
                    double x = Double.parseDouble(xValue);
                    double y = Double.parseDouble(yValue);
                    double dx = x - meanX;
                    double dy = y - meanY;

                    System.out.println("(x" + (row + 1) + "-m" + (i + 1) + ")= (" + dx + ", " + dy + ")");

                    scatterMatrices[i][0][0] += dx * dx;
                    scatterMatrices[i][0][1] += dx * dy;
                    scatterMatrices[i][1][0] += dy * dx;
                    scatterMatrices[i][1][1] += dy * dy;
                }
            }

            System.out.println("S" + (i + 1) + "= [" + scatterMatrices[i][0][0] + " " + scatterMatrices[i][0][1] + "]");
            System.out.println("   [" + scatterMatrices[i][1][0] + " " + scatterMatrices[i][1][1] + "]");
        }

        // Calculate SW = S1 + S2
        double[][] sw = new double[2][2];
        for (int i = 0; i < numGroups; i++) {
            sw[0][0] += scatterMatrices[i][0][0];
            sw[0][1] += scatterMatrices[i][0][1];
            sw[1][0] += scatterMatrices[i][1][0];
            sw[1][1] += scatterMatrices[i][1][1];
        }

        System.out.println("SW = S1 + S2 =");
        System.out.println("[" + scatterMatrices[0][0][0] + " " + scatterMatrices[0][0][1] + "] + [" + scatterMatrices[1][0][0] + " " + scatterMatrices[1][0][1] + "]");
        System.out.println("[" + scatterMatrices[0][1][0] + " " + scatterMatrices[0][1][1] + "]   [" + scatterMatrices[1][1][0] + " " + scatterMatrices[1][1][1] + "]");

        System.out.println("SW =");
        System.out.println("[" + sw[0][0] + " " + sw[0][1] + "]");
        System.out.println("[" + sw[1][0] + " " + sw[1][1] + "]");

        // Calculate SW^-1
        double detSW = sw[0][0] * sw[1][1] - sw[0][1] * sw[1][0];
        System.out.println("Determinante de SW = " + detSW);

        if (detSW != 0) {
            double[][] swInverse = new double[2][2];
            swInverse[0][0] = sw[1][1] / detSW;
            swInverse[0][1] = -sw[0][1] / detSW;
            swInverse[1][0] = -sw[1][0] / detSW;
            swInverse[1][1] = sw[0][0] / detSW;

            System.out.println("SW^-1 = 1 / " + detSW + " *");
            System.out.println("[" + swInverse[0][0] * detSW + " " + swInverse[0][1] * detSW + "]");
            System.out.println("[" + swInverse[1][0] * detSW + " " + swInverse[1][1] * detSW + "]");

            System.out.println("SW^-1 =");
            System.out.println("[" + swInverse[0][0] + " " + swInverse[0][1] + "]");
            System.out.println("[" + swInverse[1][0] + " " + swInverse[1][1] + "]");

            // Verify SW * SW^-1
            double[][] identity = new double[2][2];
            identity[0][0] = sw[0][0] * swInverse[0][0] + sw[0][1] * swInverse[1][0];
            identity[0][1] = sw[0][0] * swInverse[0][1] + sw[0][1] * swInverse[1][1];
            identity[1][0] = sw[1][0] * swInverse[0][0] + sw[1][1] * swInverse[1][0];
            identity[1][1] = sw[1][0] * swInverse[0][1] + sw[1][1] * swInverse[1][1];

            System.out.println("SW * SW^-1 =");
            System.out.println("[" + sw[0][0] + " " + sw[0][1] + "] * [" + swInverse[0][0] + " " + swInverse[0][1] + "]");
            System.out.println("[" + sw[1][0] + " " + sw[1][1] + "]   [" + swInverse[1][0] + " " + swInverse[1][1] + "]");

            System.out.println("Resultado:");
            System.out.println("[" + identity[0][0] + " " + identity[0][1] + "]");
            System.out.println("[" + identity[1][0] + " " + identity[1][1] + "]");

            // Calculate w = SW^-1 * (m1 - m2)
            double[] differenceMeans = new double[2];
            differenceMeans[0] = means[0][0] - means[1][0];
            differenceMeans[1] = means[0][1] - means[1][1];

            System.out.println("m1 - m2 = (" + differenceMeans[0] + ", " + differenceMeans[1] + ")");

            double[] w = new double[2];
            w[0] = swInverse[0][0] * differenceMeans[0] + swInverse[0][1] * differenceMeans[1];
            w[1] = swInverse[1][0] * differenceMeans[0] + swInverse[1][1] * differenceMeans[1];

            System.out.println("w = SW^-1 * (m1 - m2) =");
            System.out.println("[" + w[0] + "]");
            System.out.println("[" + w[1] + "]");

            // Calculate Yi = wt * Xi
            System.out.println("Cálculo de Yi:");
            double[][] yiResults = new double[totalObjects][4];
            for (int i = 0; i < totalObjects; i++) {
                String label = allObjects[i][0];
                double x = Double.parseDouble(allObjects[i][1]);
                double y = Double.parseDouble(allObjects[i][2]);
                double yi = w[0] * x + w[1] * y;

                System.out.println("Y" + (i + 1) + " = (" + w[0] + " * " + x + ") + (" + w[1] + " * " + y + ") = " + yi);

                yiResults[i][0] = i + 1;
                yiResults[i][1] = x;
                yiResults[i][2] = y;
                yiResults[i][3] = yi;
            }

            showYiResults(yiResults);
        } else {
            System.out.println("La matriz SW no es invertible (determinante es 0). No se puede calcular la inversa y, por lo tanto, w.");
        }
    }

    // Método para graficar los puntos Yi en una recta
    private void plotYiGraph(List<Double> yiGroup1, List<Double> yiGroup2, List<Double> allYi, List<String> groupAssignments) {
        // Crear un nuevo JFrame para el gráfico
        JFrame graphFrame = new JFrame("Gráfico de Yi");
        graphFrame.setBounds(100, 100, 600, 400);
        graphFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Obtener el valor máximo y mínimo de Yi
        double minYi = Collections.min(allYi);
        double maxYi = Collections.max(allYi);

        // Ajustar los límites de la gráfica para asegurar que todos los puntos sean visibles
        double minYiAdjusted = minYi - 0.1 * (maxYi - minYi);
        double maxYiAdjusted = maxYi + 0.1 * (maxYi - minYi);

        // Crear un JPanel personalizado para dibujar el gráfico
        JPanel graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Obtener dimensiones del panel
                int width = getWidth();
                int height = getHeight();

                // Dibujar la línea base
                g.drawLine(50, height / 2, width - 50, height / 2);

                // Dibujar puntos Yi de todos los objetos
                for (int i = 0; i < allYi.size(); i++) {
                    Double yi = allYi.get(i);
                    String group = groupAssignments.get(i);
                    int x = (int) ((yi - minYiAdjusted) * (width - 100) / (maxYiAdjusted - minYiAdjusted) + 50);
                    int y = height / 2;
                    if (group.equals("Grupo 1")) {
                        g.setColor(Color.BLUE);
                    } else if (group.equals("Grupo 2")) {
                        g.setColor(Color.RED);
                    }
                    g.fillOval(x - 3, y - 3, 6, 6);
                }

                // Dibujar números debajo de la línea para indicar la escala
                g.setColor(Color.BLACK);
                for (int i = 0; i <= 10; i++) {
                    int x = 50 + (i * (width - 100) / 10);
                    double value = minYiAdjusted + i * (maxYiAdjusted - minYiAdjusted) / 10;
                    g.drawString(String.format("%.1f", value), x, height / 2 + 15);
                }
            }
        };

        graphFrame.getContentPane().add(graphPanel);
        graphFrame.setVisible(true);
    }



    private void showYiResults(double[][] yiResults) {
        JFrame resultsFrame = new JFrame("Resultados de Yi");
        resultsFrame.setBounds(100, 100, 700, 400);
        resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultsFrame.getContentPane().setLayout(new BorderLayout(0, 0));

        String[] columnNames = {"Etiqueta", "X", "Y", "Yi", "Grupo Asignado"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        List<Double> yiGroup1 = new ArrayList<>();
        List<Double> yiGroup2 = new ArrayList<>();

        for (double[] result : yiResults) {
            Object[] row = { "Objeto " + (int) result[0], result[1], result[2], result[3], "" };

            // Assign group based on source of object
            if ((int) result[0] <= groupTables.get(0).getRowCount()) {
                row[4] = "Grupo 1";
                yiGroup1.add(result[3]);
            } else if ((int) result[0] <= groupTables.get(0).getRowCount() + groupTables.get(1).getRowCount()) {
                row[4] = "Grupo 2";
                yiGroup2.add(result[3]);
            } else {
                // For new objects, determine group based on closest Yi
                double minDistanceGroup1 = Double.MAX_VALUE;
                double closestYiGroup1 = 0;
                for (double yi : yiGroup1) {
                    double distance = Math.abs(result[3] - yi);
                    if (distance < minDistanceGroup1) {
                        minDistanceGroup1 = distance;
                        closestYiGroup1 = yi;
                    }
                }

                double minDistanceGroup2 = Double.MAX_VALUE;
                double closestYiGroup2 = 0;
                for (double yi : yiGroup2) {
                    double distance = Math.abs(result[3] - yi);
                    if (distance < minDistanceGroup2) {
                        minDistanceGroup2 = distance;
                        closestYiGroup2 = yi;
                    }
                }

                double midpoint = (closestYiGroup1 + closestYiGroup2) / 2;

                if (result[3] <= midpoint) {
                    row[4] = "Grupo 1";
                } else {
                    row[4] = "Grupo 2";
                }
            }

            model.addRow(row);
        }

        JTable resultsTable = new JTable(model);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int j = 0; j < resultsTable.getColumnCount(); j++) {
            resultsTable.getColumnModel().getColumn(j).setCellRenderer(centerRenderer);
        }

        resultsFrame.getContentPane().add(new JScrollPane(resultsTable), BorderLayout.CENTER);
        resultsFrame.setVisible(true);
        // Agregar un botón para graficar los puntos Yi
        JButton graphButton = new JButton("Graficar");
        graphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Double> allYi = new ArrayList<>();
                List<String> groupAssignments = new ArrayList<>();

                for (double[] result : yiResults) {
                    allYi.add(result[3]);
                    if ((int) result[0] <= groupTables.get(0).getRowCount()) {
                        groupAssignments.add("Grupo 1");
                    } else {
                        groupAssignments.add("Grupo 2");
                    }
                }

                plotYiGraph(yiGroup1, yiGroup2, allYi, groupAssignments);
            }
        });
        resultsFrame.getContentPane().add(graphButton, BorderLayout.SOUTH);
    }

}
