import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;

public class FormPendaftaranPasien extends JFrame {
    private JTextField namaField, tglLahirField, telpField;
    private JTextArea alamatArea;
    private JComboBox<String> genderBox;
    private JButton daftarButton, resetButton;

    private JTable table;
    private DefaultTableModel tableModel;

    private int selectedId = -1; // Digunakan untuk mode update

    private final String DB_URL = "jdbc:mysql://localhost:3306/tugas_akhir_oop";
    private final String DB_USER = "faizmysql";
    private final String DB_PASS = "030303";

    public FormPendaftaranPasien() {
        setTitle("Form Pendaftaran Pasien");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Nama:"));
        namaField = new JTextField();
        formPanel.add(namaField);

        formPanel.add(new JLabel("Jenis Kelamin:"));
        genderBox = new JComboBox<>(new String[] { "Laki-laki", "Perempuan" });
        formPanel.add(genderBox);

        formPanel.add(new JLabel("Tanggal Lahir (yyyy-mm-dd):"));
        tglLahirField = new JTextField();
        formPanel.add(tglLahirField);

        formPanel.add(new JLabel("Alamat:"));
        alamatArea = new JTextArea(3, 20);
        JScrollPane scrollPaneAlamat = new JScrollPane(alamatArea);
        formPanel.add(scrollPaneAlamat);

        formPanel.add(new JLabel("No. Telepon:"));
        telpField = new JTextField();
        formPanel.add(telpField);

        daftarButton = new JButton("Daftar");
        resetButton = new JButton("Reset");

        daftarButton.addActionListener(e -> {
            if (selectedId == -1) {
                simpanData(); // Tambah baru
            } else {
                updateData(); // Update data
            }
        });

        resetButton.addActionListener(e -> resetForm());

        formPanel.add(daftarButton);
        formPanel.add(resetButton);

        mainPanel.add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[] {
                "ID", "Nama", "Jenis Kelamin", "Tanggal Lahir", "Alamat", "Telepon"
        }, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPaneTable = new JScrollPane(table);
        mainPanel.add(scrollPaneTable, BorderLayout.CENTER);

        // Tambahkan listener untuk klik baris tabel
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedId = (int) tableModel.getValueAt(selectedRow, 0);
                    namaField.setText((String) tableModel.getValueAt(selectedRow, 1));
                    genderBox.setSelectedItem(tableModel.getValueAt(selectedRow, 2));
                    tglLahirField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    alamatArea.setText((String) tableModel.getValueAt(selectedRow, 4));
                    telpField.setText((String) tableModel.getValueAt(selectedRow, 5));
                    daftarButton.setText("Update"); // Ubah tombol
                }
            }
        });

        add(mainPanel);
        setVisible(true);
        loadData();
    }

    private void simpanData() {
        String nama = namaField.getText();
        String gender = (String) genderBox.getSelectedItem();
        String tglLahir = tglLahirField.getText();
        String alamat = alamatArea.getText();
        String telp = telpField.getText();

        String sql = "INSERT INTO patients (name, gender, date_of_birth, address, phone_number) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nama);
            stmt.setString(2, gender);
            stmt.setDate(3, Date.valueOf(tglLahir));
            stmt.setString(4, alamat);
            stmt.setString(5, telp);

            int inserted = stmt.executeUpdate();

            if (inserted > 0) {
                JOptionPane.showMessageDialog(this, "Data pasien berhasil disimpan!");
                loadData();
                resetForm();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + ex.getMessage());
        }
    }

    private void updateData() {
        if (selectedId == -1)
            return;

        String nama = namaField.getText();
        String gender = (String) genderBox.getSelectedItem();
        String tglLahir = tglLahirField.getText();
        String alamat = alamatArea.getText();
        String telp = telpField.getText();

        String sql = "UPDATE patients SET name=?, gender=?, date_of_birth=?, address=?, phone_number=? WHERE id=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nama);
            stmt.setString(2, gender);
            stmt.setDate(3, Date.valueOf(tglLahir));
            stmt.setString(4, alamat);
            stmt.setString(5, telp);
            stmt.setInt(6, selectedId);

            int updated = stmt.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!");
                loadData();
                resetForm();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal update data: " + ex.getMessage());
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM patients";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("gender"),
                        rs.getDate("date_of_birth"),
                        rs.getString("address"),
                        rs.getString("phone_number")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + ex.getMessage());
        }
    }

    private void resetForm() {
        namaField.setText("");
        tglLahirField.setText("");
        telpField.setText("");
        alamatArea.setText("");
        genderBox.setSelectedIndex(0);
        daftarButton.setText("Daftar");
        selectedId = -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FormPendaftaranPasien::new);
    }
}
