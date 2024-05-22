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
                }
            }
        }
    }
}

