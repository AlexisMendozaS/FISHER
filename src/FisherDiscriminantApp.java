import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FisherDiscriminantApp {
    private JFrame frame;
    private JPanel panelTables;
    private int numGroups;
    private int[] objectsPerGroup;
    private int numNewObjects;
    private java.util.List<JTable> groupTables = new java.util.ArrayList<>();
    private double[][][] scatterMatrices;

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

        JTable tableNewObjects = new JTable(numNewObjects, 3);
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
        double[][] means = new double[numGroups][2];
        scatterMatrices = new double[numGroups][2][2];

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

            System.out.println("S" + (i + 1) + " =");
            System.out.println("[" + scatterMatrices[i][0][0] + " " + scatterMatrices[i][0][1] + "]");
            System.out.println("[" + scatterMatrices[i][1][0] + " " + scatterMatrices[i][1][1] + "]");
        }

        // Calculate SW = S1 + S2
        double[][] sw = new double[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                sw[i][j] = scatterMatrices[0][i][j] + scatterMatrices[1][i][j];
            }
        }

        // Print SW calculation
        System.out.println("SW = S1 + S2 =");
        System.out.println("[(" + scatterMatrices[0][0][0] + " + " + scatterMatrices[1][0][0] + ") (" + scatterMatrices[0][0][1] + " + " + scatterMatrices[1][0][1] + ")]");
        System.out.println("[(" + scatterMatrices[0][1][0] + " + " + scatterMatrices[1][1][0] + ") (" + scatterMatrices[0][1][1] + " + " + scatterMatrices[1][1][1] + ")]");

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
            System.out.println();
        } else {
            System.out.println("No se pudo calcular la diferencia de medias. Asegúrate de que los valores estén presentes y sean numéricos.");
        }
    }
}
