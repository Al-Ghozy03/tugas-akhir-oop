import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FormJadwalDokter extends JFrame {
    private JTextField namaDokterField, spesialisField, tanggalField, jamMulaiField, jamSelesaiField, ruanganField,
            keteranganField;
    private JButton simpanButton, resetButton, batalEditButton;

    private JTable table;
    private DefaultTableModel tableModel;

    private final String DB_URL = "jdbc:mysql://localhost:3306/tugas_akhir_oop";
    private final String DB_USER = "faizmysql";
    private final String DB_PASS = "030303";

    private Integer editId = null; // Untuk menyimpan ID saat mode edit

    public FormJadwalDokter() {
        setTitle("Form Jadwal Dokter");
        setSize(750, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelUtama = new JPanel(new BorderLayout());

        // Panel input
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Nama Dokter:"));
        namaDokterField = new JTextField();
        formPanel.add(namaDokterField);

        formPanel.add(new JLabel("Spesialis:"));
        spesialisField = new JTextField();
        formPanel.add(spesialisField);

        formPanel.add(new JLabel("Tanggal Praktik (yyyy-mm-dd):"));
        tanggalField = new JTextField();
        formPanel.add(tanggalField);

        formPanel.add(new JLabel("Jam Mulai (HH:mm:ss):"));
        jamMulaiField = new JTextField();
        formPanel.add(jamMulaiField);

        formPanel.add(new JLabel("Jam Selesai (HH:mm:ss):"));
        jamSelesaiField = new JTextField();
        formPanel.add(jamSelesaiField);

        formPanel.add(new JLabel("Ruangan:"));
        ruanganField = new JTextField();
        formPanel.add(ruanganField);

        formPanel.add(new JLabel("Keterangan:"));
        keteranganField = new JTextField();
        formPanel.add(keteranganField);

        simpanButton = new JButton("Simpan");
        resetButton = new JButton("Reset");
        batalEditButton = new JButton("Batal Edit");
        batalEditButton.setVisible(false); // hanya muncul saat mode edit

        formPanel.add(simpanButton);
        formPanel.add(resetButton);
        formPanel.add(new JLabel()); // Kosong
        formPanel.add(batalEditButton);

        panelUtama.add(formPanel, BorderLayout.NORTH);

        // Tabel
        tableModel = new DefaultTableModel(new String[] {
                "ID", "Nama Dokter", "Spesialis", "Tanggal", "Jam Mulai", "Jam Selesai", "Ruangan", "Keterangan"
        }, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panelUtama.add(scrollPane, BorderLayout.CENTER);

        add(panelUtama);
        setVisible(true);

        // Events
        simpanButton.addActionListener(e -> {
            if (editId == null) {
                simpanJadwal();
            } else {
                updateJadwal();
            }
        });

        resetButton.addActionListener(e -> resetForm());
        batalEditButton.addActionListener(e -> {
            resetForm();
            simpanButton.setText("Simpan");
            batalEditButton.setVisible(false);
            editId = null;
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                editId = (Integer) tableModel.getValueAt(row, 0);
                namaDokterField.setText((String) tableModel.getValueAt(row, 1));
                spesialisField.setText((String) tableModel.getValueAt(row, 2));
                tanggalField.setText(tableModel.getValueAt(row, 3).toString());
                jamMulaiField.setText(tableModel.getValueAt(row, 4).toString());
                jamSelesaiField.setText(tableModel.getValueAt(row, 5).toString());
                ruanganField.setText((String) tableModel.getValueAt(row, 6));
                keteranganField.setText((String) tableModel.getValueAt(row, 7));

                simpanButton.setText("Update");
                batalEditButton.setVisible(true);
            }
        });

        loadData();
    }

    private void simpanJadwal() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO doctor_schedules (doctor_name, specialist, practice_date, start_time, end_time, room, remarks) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            stmt.setString(1, namaDokterField.getText());
            stmt.setString(2, spesialisField.getText());
            stmt.setDate(3, Date.valueOf(tanggalField.getText()));
            stmt.setTime(4, Time.valueOf(jamMulaiField.getText()));
            stmt.setTime(5, Time.valueOf(jamSelesaiField.getText()));
            stmt.setString(6, ruanganField.getText());
            stmt.setString(7, keteranganField.getText());

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Jadwal berhasil disimpan!");
            resetForm();
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Kesalahan: " + ex.getMessage());
        }
    }

    private void updateJadwal() {
        if (editId == null)
            return;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE doctor_schedules SET doctor_name=?, specialist=?, practice_date=?, start_time=?, end_time=?, room=?, remarks=? WHERE id=?")) {

            stmt.setString(1, namaDokterField.getText());
            stmt.setString(2, spesialisField.getText());
            stmt.setDate(3, Date.valueOf(tanggalField.getText()));
            stmt.setTime(4, Time.valueOf(jamMulaiField.getText()));
            stmt.setTime(5, Time.valueOf(jamSelesaiField.getText()));
            stmt.setString(6, ruanganField.getText());
            stmt.setString(7, keteranganField.getText());
            stmt.setInt(8, editId);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Jadwal berhasil diperbarui!");
            resetForm();
            loadData();

            editId = null;
            simpanButton.setText("Simpan");
            batalEditButton.setVisible(false);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Kesalahan: " + ex.getMessage());
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM doctor_schedules";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("doctor_name"),
                        rs.getString("specialist"),
                        rs.getDate("practice_date"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"),
                        rs.getString("room"),
                        rs.getString("remarks")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + ex.getMessage());
        }
    }

    private void resetForm() {
        namaDokterField.setText("");
        spesialisField.setText("");
        tanggalField.setText("");
        jamMulaiField.setText("");
        jamSelesaiField.setText("");
        ruanganField.setText("");
        keteranganField.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FormJadwalDokter::new);
    }
}
