import java.sql.*;
import java.sql.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class FormReservasi extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<String> cbPasien;
    private JComboBox<String> cbDokter;
    private Map<String, Integer> pasienIdMap = new HashMap<>();
    private Map<String, Integer> dokterIdMap = new HashMap<>();
    private Map<Integer, String> dokterRuangMap = new HashMap<>();

    private JTextField txtTanggal, txtRuang;

    public FormReservasi() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setTitle("Form Reservasi");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(750, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("FORM RESERVASI");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 20, 0);
        formPanel.add(lblTitle, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Pasien
        JLabel lblPasien = new JLabel("Nama Pasien:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblPasien, gbc);

        cbPasien = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(cbPasien, gbc);
        loadPasien();

        // Dokter
        JLabel lblDokter = new JLabel("Nama Dokter:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblDokter, gbc);

        cbDokter = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(cbDokter, gbc);
        loadDokter();

        cbDokter.addActionListener(e -> {
            String selected = (String) cbDokter.getSelectedItem();
            if (selected != null) {
                int id = dokterIdMap.get(selected);
                String ruang = dokterRuangMap.get(id);
                txtRuang.setText(ruang);
            }
        });

        // Tanggal
        JLabel lblTanggal = new JLabel("Tgl. Kunjungan (yyyy-mm-dd):");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(lblTanggal, gbc);

        txtTanggal = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtTanggal, gbc);

        // Jam
        JLabel lblJam = new JLabel("Jam Kunjungan:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(lblJam, gbc);

        JComboBox<String> cbJam = new JComboBox<>(new String[] { "08:00:00", "09:00:00", "10:00:00", "11:00:00" });
        gbc.gridx = 1;
        formPanel.add(cbJam, gbc);

        // Ruang
        JLabel lblRuang = new JLabel("Ruang:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(lblRuang, gbc);

        txtRuang = new JTextField(20);
        txtRuang.setEditable(false);
        gbc.gridx = 1;
        formPanel.add(txtRuang, gbc);

        // Catatan
        JLabel lblCatatan = new JLabel("Catatan:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(lblCatatan, gbc);

        JTextArea txtCatatan = new JTextArea(3, 20);
        JScrollPane scroll = new JScrollPane(txtCatatan);
        gbc.gridx = 1;
        formPanel.add(scroll, gbc);

        // Tombol
        JButton btnKirim = new JButton("Kirim");
        JButton btnTutup = new JButton("Tutup");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnKirim);
        buttonPanel.add(btnTutup);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Tabel
        tableModel = new DefaultTableModel(new String[] {
                "ID", "Pasien", "Dokter", "Tanggal", "Jam", "Ruang", "Status", "Catatan"
        }, 0);
        table = new JTable(tableModel);
        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Daftar Reservasi"));
        add(scrollTable, BorderLayout.CENTER);

        // Event Kirim
        btnKirim.addActionListener(e -> {
            String pasien = (String) cbPasien.getSelectedItem();
            String dokter = (String) cbDokter.getSelectedItem();
            String tanggal = txtTanggal.getText();
            String jam = cbJam.getSelectedItem().toString();
            String ruang = txtRuang.getText();
            String catatan = txtCatatan.getText();

            if (pasien == null || dokter == null || tanggal.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lengkapi semua data!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int pasienId = pasienIdMap.get(pasien);
            int dokterId = dokterIdMap.get(dokter);

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tugas_akhir_oop",
                    "faizmysql", "030303");
                    PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO reservations (patient_id, doctor_id, visit_date, visit_time, room, status, remarks) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

                stmt.setInt(1, pasienId);
                stmt.setInt(2, dokterId);
                stmt.setDate(3, Date.valueOf(tanggal));
                stmt.setString(4, jam);
                stmt.setString(5, ruang);
                stmt.setString(6, "Terkirim");
                stmt.setString(7, catatan);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Reservasi berhasil dikirim!");

                txtTanggal.setText("");
                txtCatatan.setText("");
                cbJam.setSelectedIndex(0);
                cbDokter.setSelectedIndex(0);
                cbPasien.setSelectedIndex(0);
                txtRuang.setText("");

                loadData();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage());
            }
        });

        btnTutup.addActionListener(e -> dispose());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tugas_akhir_oop",
                "faizmysql", "030303");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT r.id, p.name AS pasien, d.doctor_name AS dokter, r.visit_date, r.visit_time, r.room, r.status, r.remarks "
                                +
                                "FROM reservations r " +
                                "JOIN patients p ON r.patient_id = p.id " +
                                "JOIN doctor_schedules d ON r.doctor_id = d.id ORDER BY id ASC")) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("pasien"),
                        rs.getString("dokter"),
                        rs.getDate("visit_date"),
                        rs.getTime("visit_time"),
                        rs.getString("room"),
                        rs.getString("status"),
                        rs.getString("remarks")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + ex.getMessage());
        }
    }

    private void loadPasien() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tugas_akhir_oop",
                "faizmysql", "030303");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, name FROM patients")) {

            cbPasien.removeAllItems();
            pasienIdMap.clear();

            cbPasien.addItem(""); // Tambahkan item kosong dulu

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                cbPasien.addItem(name);
                pasienIdMap.put(name, id);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat pasien: " + ex.getMessage());
        }
    }

    private void loadDokter() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tugas_akhir_oop",
                "faizmysql", "030303");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, doctor_name, specialist, room FROM doctor_schedules")) {

            cbDokter.removeAllItems();
            dokterIdMap.clear();
            dokterRuangMap.clear();

            cbDokter.addItem(""); // Tambahkan item kosong dulu

            while (rs.next()) {
                int id = rs.getInt("id");
                String nama = rs.getString("doctor_name");
                String spesialis = rs.getString("specialist");
                String ruang = rs.getString("room");

                String display = nama + " (" + spesialis + ")";
                cbDokter.addItem(display);
                dokterIdMap.put(display, id);
                dokterRuangMap.put(id, ruang);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat dokter: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormReservasi().setVisible(true));
    }
}
