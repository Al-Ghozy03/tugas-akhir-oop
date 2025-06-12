import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;

public class FormPendaftaranPasien extends JFrame {
    private JTextField namaField, tglLahirField, telpField;
    private JTextArea alamatArea;
    private JComboBox<String> genderBox;
    private JButton daftarButton, resetButton;

    private final String DB_URL = "jdbc:mysql://localhost:3306/tugas_akhir_oop";
    private final String DB_USER = "faizmysql";
    private final String DB_PASS = "030303";

    public FormPendaftaranPasien() {
        setTitle("Form Pendaftaran Pasien");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Nama:"));
        namaField = new JTextField();
        panel.add(namaField);

        panel.add(new JLabel("Jenis Kelamin:"));
        genderBox = new JComboBox<>(new String[] { "Laki-laki", "Perempuan" });
        panel.add(genderBox);

        panel.add(new JLabel("Tanggal Lahir (yyyy-mm-dd):"));
        tglLahirField = new JTextField();
        panel.add(tglLahirField);

        panel.add(new JLabel("Alamat:"));
        alamatArea = new JTextArea(3, 20);
        JScrollPane scrollPane = new JScrollPane(alamatArea);
        panel.add(scrollPane);

        panel.add(new JLabel("No. Telepon:"));
        telpField = new JTextField();
        panel.add(telpField);

        daftarButton = new JButton("Daftar");
        resetButton = new JButton("Reset");

        daftarButton.addActionListener(e -> simpanData());

        resetButton.addActionListener(e -> {
            namaField.setText("");
            tglLahirField.setText("");
            telpField.setText("");
            alamatArea.setText("");
            genderBox.setSelectedIndex(0);
        });

        panel.add(daftarButton);
        panel.add(resetButton);

        add(panel);
        setVisible(true);
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
                JOptionPane.showMessageDialog(this, "Data pasien berhasil disimpan!", "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Kesalahan koneksi/database: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FormPendaftaranPasien::new);
    }
}
