import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class InterfazTabla extends JFrame {
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField codigoField; // Declare codigoField as an instance variable
    
    // Declare lblPhotoPreview as an instance variable
    private JLabel lblPhotoPreview;


    public InterfazTabla() {
        setTitle("Sistema de Gestión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Barra de navegación
        JPanel panelNavegacion = new JPanel();
        JLabel titulo = new JLabel("Sistema de Gestión");
        panelNavegacion.add(titulo);
        panelPrincipal.add(panelNavegacion, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"Código", "Nombre", "Apellidos", "Facultad", "Carrera", "Foto", "Editar", "Eliminar"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 5) {
                    return ImageIcon.class; // Usaremos ImageIcon para mostrar imágenes en esta columna.
                } else if (column == 6 || column == 7) {
                    return JButton.class;
                }
                return super.getColumnClass(column);
            }
        };
        tabla = new JTable(modelo);
        tabla.getColumnModel().getColumn(5).setCellRenderer(new ImageRenderer()); // Usamos ImageRenderer para mostrar las imágenes.
        tabla.getColumnModel().getColumn(5).setCellEditor(new ImageEditor()); // Usamos ImageEditor para editar las imágenes.
        tabla.getColumnModel().getColumn(6).setCellRenderer(new ButtonRendererEditar());
        tabla.getColumnModel().getColumn(6).setCellEditor(new ButtonEditorEditar());
        tabla.getColumnModel().getColumn(7).setCellRenderer(new ButtonRendererEliminar());
        tabla.getColumnModel().getColumn(7).setCellEditor(new ButtonEditorEliminar());
        JScrollPane scrollTabla = new JScrollPane(tabla);
        panelPrincipal.add(scrollTabla, BorderLayout.CENTER);

        // Botón Crear
        JButton btnCrear = new JButton("Crear");
        btnCrear.addActionListener(e -> mostrarDialogoNuevoEstudiante());
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnCrear);

        // Botón Salir
        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));
        panelBotones.add(btnSalir);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        getContentPane().add(panelPrincipal);

        // Cargar datos en la tabla
        cargarDatos();
        // Initialize lblPhotoPreview
        lblPhotoPreview = new JLabel();

    }
     private class ImageRenderer extends DefaultTableCellRenderer {
        public ImageRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Mostrar imagen si existe, o mostrar "No existe Foto" si no hay imagen.
            if (value instanceof ImageIcon) {
                setIcon((ImageIcon) value);
                setText("");
            } else {
                setIcon(null);
                setText("No existe Foto");
            }
            return this;
        }
    }

    private class ImageEditor extends DefaultCellEditor {
        private JButton button;
        private JFileChooser fileChooser;
        private File selectedFile;

        public ImageEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = tabla.getSelectedRow();
                    if (row != -1) {
                        fileChooser = new JFileChooser();
                        int returnValue = fileChooser.showOpenDialog(null);
                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                            selectedFile = fileChooser.getSelectedFile();
                            ImageIcon imageIcon = new ImageIcon(selectedFile.getAbsolutePath());
                            tabla.setValueAt(imageIcon, row, 5);
                        }
                    }
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }

        public Object getCellEditorValue() {
            return selectedFile != null ? new ImageIcon(selectedFile.getAbsolutePath()) : null;
        }
    }

    private void cargarDatos() {
        String url = "jdbc:mysql://localhost/card_bd1";
        String usuario = "root";
        String contrasena = ""; // Aquí debes agregar tu contraseña si la tienes configurada.

        try {
            Connection conexion = DriverManager.getConnection(url, usuario, contrasena);
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM estu_table1");

            while (rs.next()) {
                String codigo = rs.getString("codigo_estu");
                String nombre = rs.getString("nombre_estu");
                String apellidos = rs.getString("apellidos_estu");
                String facultad = rs.getString("facultad_estu");
                String carrera = rs.getString("carrera_estu");
                String fotoPath = rs.getString("foto_estu");

                ImageIcon fotoIcon;
                if (fotoPath != null) {
                    fotoIcon = new ImageIcon(fotoPath);
                } else {
                    fotoIcon = null;
                }

                // Agregar datos a la tabla
                modelo.addRow(new Object[]{codigo, nombre, apellidos, facultad, carrera, fotoIcon, "Editar", "Eliminar"});
            }

            rs.close();
            stmt.close();
            conexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class ButtonRendererEditar extends JButton implements TableCellRenderer {
        public ButtonRendererEditar() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText("Editar");
            return this;
        }
    }

    private class ButtonRendererEliminar extends JButton implements TableCellRenderer {
        public ButtonRendererEliminar() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText("Eliminar");
            return this;
        }
    }

    private class ButtonEditorEditar extends DefaultCellEditor {
        private JButton button;

        public ButtonEditorEditar() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = tabla.getSelectedRow();
                    if (row != -1) {
                        editarEstudiante(row);
                    }
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }

        public Object getCellEditorValue() {
            return "";
        }
    }

    private class ButtonEditorEliminar extends DefaultCellEditor {
        private JButton button;

        public ButtonEditorEliminar() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = tabla.getSelectedRow();
                    if (row != -1) {
                        eliminarEstudiante(row);
                    }
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }

        public Object getCellEditorValue() {
            return "";
        }
    }
    
    


    private void editarEstudiante(int rowIndex) {
        String codigo = (String) modelo.getValueAt(rowIndex, 0);
        String nombre = (String) modelo.getValueAt(rowIndex, 1);
        String apellidos = (String) modelo.getValueAt(rowIndex, 2);
        String facultad = (String) modelo.getValueAt(rowIndex, 3);
        String carrera = (String) modelo.getValueAt(rowIndex, 4);
    
        JTextField codigoField = new JTextField(10); // Initialize codigoField here
        codigoField.setText(codigo);
        JTextField nombreField = new JTextField(30);
        nombreField.setText(nombre);
        JTextField apellidosField = new JTextField(30);
        apellidosField.setText(apellidos);
        JTextField facultadField = new JTextField(20);
        facultadField.setText(facultad);
        JTextField carreraField = new JTextField(20);
        carreraField.setText(carrera);
        
        JButton btnUploadPhoto = new JButton("Subir Foto");
    
    btnUploadPhoto.addActionListener(e -> {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Leer la imagen seleccionada
                BufferedImage originalImage = ImageIO.read(selectedFile);
    
                // Redimensionar la imagen a 20x20 píxeles
                BufferedImage resizedImage = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = resizedImage.createGraphics();
                g.drawImage(originalImage, 0, 0, 20, 20, null);
                g.dispose();
    
                // Mostrar la imagen redimensionada en el JLabel
                lblPhotoPreview.setIcon(new ImageIcon(resizedImage));
    
                // Obtener la ruta de la imagen seleccionada    
                // Guardar la imagen en la base de datos y actualizar la tabla
                guardarImagenEnBD(codigoField.getText());

    
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    });
    
    
    

    JPanel panel = new JPanel(new GridLayout(7, 2));
    panel.add(new JLabel("Código:"));
    panel.add(codigoField);
    panel.add(new JLabel("Nombres:"));
    panel.add(nombreField);
    panel.add(new JLabel("Apellidos:"));
    panel.add(apellidosField);
    panel.add(new JLabel("Facultad:"));
    panel.add(facultadField);
    panel.add(new JLabel("Carrera:"));
    panel.add(carreraField);
    panel.add(btnUploadPhoto);
    panel.add(lblPhotoPreview);

    int result = JOptionPane.showConfirmDialog(null, panel, "Editar estudiante",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String nuevoCodigo = codigoField.getText();
                String nuevoNombre = nombreField.getText();
                String nuevoApellidos = apellidosField.getText();
                String nuevaFacultad = facultadField.getText();
                String nuevaCarrera = carreraField.getText();
            
                if (!nuevoCodigo.isEmpty() && !nuevoNombre.isEmpty() && !nuevoApellidos.isEmpty() && !nuevaFacultad.isEmpty()
                && !nuevaCarrera.isEmpty()) {
                // Guardar los datos editados en la base de datos y actualizar la tabla
                actualizarEstudiante(codigo, nuevoCodigo, nuevoNombre, nuevoApellidos, nuevaFacultad, nuevaCarrera, rowIndex);
        
                // Guardar la imagen en la base de datos y actualizar la tabla con la nueva imagen
                guardarImagenEnBD(codigoField.getText()); // Remove the argument here
        
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }            }

        
        }
    
        private void guardarImagenEnBD(String codigoEstudiante) {
            String url = "jdbc:mysql://localhost/card_bd1";
            String usuario = "root";
            String contrasena = ""; // Aquí debes agregar tu contraseña si la tienes configurada.
        
            try {
                Connection conexion = DriverManager.getConnection(url, usuario, contrasena);
                PreparedStatement stmt = conexion.prepareStatement(
                        "UPDATE estu_table1 SET foto_estu=? WHERE codigo_estu=?");
        
                // Obtener la imagen del JLabel lblPhotoPreview
                Icon icon = lblPhotoPreview.getIcon();
                if (icon instanceof ImageIcon) {
                    ImageIcon imageIcon = (ImageIcon) icon;
                    Image image = imageIcon.getImage();
                    BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = bufferedImage.createGraphics();
                    g2d.drawImage(image, 0, 0, null);
                    g2d.dispose();
        
                    // Guardar la imagen en la base de datos
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", baos);
                    byte[] imageBytes = baos.toByteArray();
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                    stmt.setBinaryStream(1, bais);
                } else {
                    // Si no hay imagen en lblPhotoPreview, almacenar un valor NULL en la base de datos
                    stmt.setNull(1, Types.BLOB);
                }
        
                stmt.setString(2, codigoEstudiante);
                stmt.executeUpdate();
                stmt.close();
                conexion.close();
        
                // Actualizar la fila en la tabla
                int rowIndex = tabla.getSelectedRow();
                modelo.setValueAt(lblPhotoPreview.getIcon(), rowIndex, 5);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al guardar la imagen en la base de datos.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        

    private void actualizarEstudiante(String codigo, String nuevoCodigo, String nuevoNombre, String nuevoApellidos,
                                  String nuevaFacultad, String nuevaCarrera, int rowIndex) {
        String url = "jdbc:mysql://localhost/card_bd1";
        String usuario = "root";
        String contrasena = ""; // Aquí debes agregar tu contraseña si la tienes configurada.

        try {
            Connection conexion = DriverManager.getConnection(url, usuario, contrasena);
            PreparedStatement stmt = conexion.prepareStatement(
                    "UPDATE estu_table1 SET codigo_estu=?, nombre_estu=?, apellidos_estu=?, facultad_estu=?, carrera_estu=? WHERE codigo_estu=?");
            stmt.setString(1, nuevoCodigo);
            stmt.setString(2, nuevoNombre);
            stmt.setString(3, nuevoApellidos);
            stmt.setString(4, nuevaFacultad);
            stmt.setString(5, nuevaCarrera);
            stmt.setString(6, codigo); // Use the 'codigo' parameter here
            stmt.executeUpdate();
            stmt.close();
            conexion.close();

            // Actualizar la fila en la tabla
            modelo.setValueAt(nuevoCodigo, rowIndex, 0);
            modelo.setValueAt(nuevoNombre, rowIndex, 1);
            modelo.setValueAt(nuevoApellidos, rowIndex, 2);
            modelo.setValueAt(nuevaFacultad, rowIndex, 3);
            modelo.setValueAt(nuevaCarrera, rowIndex, 4);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al actualizar el estudiante.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEstudiante(int rowIndex) {
        String codigo = (String) modelo.getValueAt(rowIndex, 0);
        String url = "jdbc:mysql://localhost/card_bd1";
        String usuario = "root";
        String contrasena = ""; // Aquí debes agregar tu contraseña si la tienes configurada.

        try {
            Connection conexion = DriverManager.getConnection(url, usuario, contrasena);
            PreparedStatement stmt = conexion.prepareStatement("DELETE FROM estu_table1 WHERE codigo_estu = ?");
            stmt.setString(1, codigo);
            stmt.executeUpdate();
            stmt.close();
            conexion.close();

            // Eliminar la fila de la tabla
            modelo.removeRow(rowIndex);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al eliminar el estudiante.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDialogoNuevoEstudiante() {
        codigoField = new JTextField(10); // Initialize codigoField here
        JTextField nombreField = new JTextField(30);
        JTextField apellidosField = new JTextField(30);
        JTextField facultadField = new JTextField(20);
        JTextField carreraField = new JTextField(20);
        
        
        JButton btnUploadPhoto = new JButton("Subir Foto");
        
        btnUploadPhoto.addActionListener(e -> {
    JFileChooser fileChooser = new JFileChooser();
    int returnValue = fileChooser.showOpenDialog(null);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        try {
            // Leer la imagen seleccionada
            BufferedImage originalImage = ImageIO.read(selectedFile);

            // Redimensionar la imagen a 5x5 píxeles
            BufferedImage resizedImage = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, 20, 20, null);
            g.dispose();

            // Mostrar la imagen redimensionada en el JLabel
            lblPhotoPreview.setIcon(new ImageIcon(resizedImage));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
});

    
        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Código:"));
        panel.add(codigoField);
        panel.add(new JLabel("Nombres:"));
        panel.add(nombreField);
        panel.add(new JLabel("Apellidos:"));
        panel.add(apellidosField);
        panel.add(new JLabel("Facultad:"));
        panel.add(facultadField);
        panel.add(new JLabel("Carrera:"));
        panel.add(carreraField);
        panel.add(btnUploadPhoto);
        panel.add(lblPhotoPreview);
    
        int result = JOptionPane.showConfirmDialog(null, panel, "Ingrese los datos del nuevo estudiante",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
        if (result == JOptionPane.OK_OPTION) {
            String codigo = codigoField.getText();
            String nombre = nombreField.getText();
            String apellidos = apellidosField.getText();
            String facultad = facultadField.getText();
            String carrera = carreraField.getText();

            if (!codigo.isEmpty() && !nombre.isEmpty() && !apellidos.isEmpty() && !facultad.isEmpty()
                    && !carrera.isEmpty()) {
                // Guardar los datos en la base de datos y actualizar la tabla
                guardarEstudiante(codigo, nombre, apellidos, facultad, carrera);
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarEstudiante(String codigo, String nombre, String apellidos, String facultad, String carrera) {
        String url = "jdbc:mysql://localhost/card_bd1";
        String usuario = "root";
        String contrasena = ""; // Aquí debes agregar tu contraseña si la tienes configurada.

        try {
            Connection conexion = DriverManager.getConnection(url, usuario, contrasena);
            PreparedStatement stmt = conexion.prepareStatement(
                    "INSERT INTO estu_table1 (codigo_estu, nombre_estu, apellidos_estu, facultad_estu, carrera_estu) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, codigo);
            stmt.setString(2, nombre);
            stmt.setString(3, apellidos);
            stmt.setString(4, facultad);
            stmt.setString(5, carrera);
            stmt.executeUpdate();
            stmt.close();
            conexion.close();

            // Agregar datos a la tabla
            modelo.addRow(new Object[]{codigo, nombre, apellidos, facultad, carrera, "Editar", "Eliminar"});
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar el estudiante.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

   public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfazTabla interfaz = new InterfazTabla();
            interfaz.setVisible(true);
        });
    }
}