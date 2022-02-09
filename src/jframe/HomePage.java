/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jframe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.table.TableModel;
/**
 *
 * @author PV
 */
public class HomePage extends javax.swing.JFrame {

    /**
     * Creates new form HomePage
     */
    
    DefaultTableModel model; // 
    JPanel current; // Panel hiện tại
    BorrowBook borrowBookPanel = new BorrowBook(); 
    Student studentPanel = new Student();
    ReturnBooks returnBookPanel = new ReturnBooks();
    
    // Constructor
    public HomePage() {
        initComponents();
        this.showPieChart(); 
        this.setGoodBookDetailsToTable(); 
        setBookDetailToTable();
        setDataToCards();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.helloName.setText("Xin chào, " + LoginPage.user_name);
        
        // Set up GUI for borrow book panel
        borrowBookPanel.setSize(1314, 811);
        borrowBook.add(borrowBookPanel);
        borrowBook.setVisible(false);
        
        // Set up GUI for student panel        
        studentPanel.setSize(1314, 811);
        student.add(studentPanel);
        student.setVisible(false);
        
        // Set up GUI for return book panel
        returnBookPanel.setSize(1314, 811);
        returnBook.add(returnBookPanel);
        returnBook.setVisible(false);
        
        current = this.home;
        current.setVisible(true);
        //home.setVisible(true);
        
    }
    
    /*
    * Clear bảng thông tin sách để hiển thị lại
    */
    public void clearTableBookDetails(){
        DefaultTableModel model = (DefaultTableModel) this.tbl_BookDetails.getModel();
        model.setRowCount(0); 
    }
    
    
    // Add danh sách các sách hay được mượn vào bảng
    // Các sách được sắp xếp theo thứ tự giảm dần về số lần mượn
    public void setGoodBookDetailsToTable(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms", "root", "");
            java.sql.Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select book_details.book_name, book_details.author, book_details.category, COUNT(*) as numberOfBorrows\n" +
                                "    from borrow_book_detail, book_details\n" +
                                "    where borrow_book_detail.book_id = book_details.book_id\n" +
                                "    group by book_details.book_name, book_details.author, book_details.category\n" +
                                "    order by numberOfBorrows desc");
            while(rs.next()){
                String bookName = rs.getString("book_name");
                String author = rs.getString("author");
                String category = rs.getString("category");
                String numberOfBorrows = rs.getString("numberOfBorrows");
                Object obj[] = {bookName, author, category, numberOfBorrows};
                model = (DefaultTableModel) tbl_GoodBook.getModel();
                model.addRow(obj); 
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    // Thêm thông tin các sách hiện có vào bảng
    // Các sách được sắp xếp theo id tăng dần
    public void setBookDetailToTable( ){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms", "root", "");
            java.sql.Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from book_details where available = 'yes'");
            while(rs.next()){
                String bookId = rs.getString("book_id");
                String bookName = rs.getString("book_name");
                String author = rs.getString("author");
                int quantity = rs.getInt("quantity");
                String category = rs.getString("category");
                Object obj[] = {bookId, bookName, author, quantity, category};
                model = (DefaultTableModel) tbl_BookDetails.getModel();
                model.addRow(obj);
                
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    // Hiển thị thông tin số sách, số bạn đọc, số sách đang mượn, số bạn đọc đang mượn lên homepage
    public void setDataToCards(){
        Statement st = null;
        ResultSet rs = null;
        
        long l =System.currentTimeMillis();
        Date todaysDate = new Date(l);
        
        try {
            Connection con = DBConnection.getConnection();
            st = con.createStatement();
             rs = st.executeQuery("select * from book_details");
              rs.last();
              lbl_nofOfBooks.setText(Integer.toString(rs.getRow()));
              
              rs = st.executeQuery("select * from student_detail");
              rs.last();
              lbl_noOfStudent.setText(Integer.toString(rs.getRow()));
              
              rs = st.executeQuery("select * from borrow_book_detail where status = 'pending'");
              rs.last();
              lbl_issueBooks.setText(Integer.toString(rs.getRow()));
              
              String sql = "select student_id from borrow_book_detail where status = 'pending' group by student_id";
              PreparedStatement pst = con.prepareStatement(sql);
              rs = pst.executeQuery();
              rs.last();
              lbl_defaulterList.setText(Integer.toString(rs.getRow()));
              
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    //Thống kê các sách hay được mượn theo thể loại
    public void showPieChart() {

        //create dataset
        DefaultPieDataset barDataset = new DefaultPieDataset();
        
        try {
            Connection con = DBConnection.getConnection();
            String sql = "select category, count(*) numberOfBorrows\n" +
                            "from borrow_book_detail, book_details\n" +
                            "where borrow_book_detail.book_id = book_details.book_id\n" +
                            "group by category\n" +
                            "order by numberOfBorrows desc";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
              barDataset.setValue(rs.getString("category"), new Double(rs.getDouble("numberOfBorrows")));  
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //create chart
        JFreeChart piechart = ChartFactory.createPieChart("Thể loại hay được mượn", barDataset, true, true, false);//explain
        PiePlot piePlot = (PiePlot) piechart.getPlot();
        piePlot.setBackgroundPaint(Color.white);

        //create chartPanel to display chart(graph)
        ChartPanel barChartPanel = new ChartPanel(piechart);
        categoryStatistics.removeAll();
        categoryStatistics.add(barChartPanel, BorderLayout.CENTER);
        categoryStatistics.validate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMain = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        helloName = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        home = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        lbl_nofOfBooks = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        lbl_noOfStudent = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        lbl_issueBooks = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        lbl_defaulterList = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        categoryStatistics = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbl_GoodBook = new rojeru_san.complementos.RSTableMetro();
        book = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_BookDetails = new rojeru_san.complementos.RSTableMetro();
        txt_bookName = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        borrowBook = new javax.swing.JPanel();
        student = new javax.swing.JPanel();
        returnBook = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        panelMain.setPreferredSize(new java.awt.Dimension(1548, 871));
        panelMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(34, 36, 36));
        jPanel2.setForeground(new java.awt.Color(102, 102, 102));
        jPanel2.setPreferredSize(new java.awt.Dimension(1308, 60));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 35)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("X");
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1260, 0, -1, -1));

        helloName.setBackground(new java.awt.Color(255, 255, 255));
        helloName.setFont(new java.awt.Font("Segoe UI Semibold", 0, 25)); // NOI18N
        helloName.setForeground(new java.awt.Color(255, 255, 255));
        helloName.setText("Xin chao");
        jPanel2.add(helloName, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 10, 180, 40));

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Segoe UI Semibold", 0, 25)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Hệ thống quản lý thư viện");
        jPanel2.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        panelMain.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 0, 1314, -1));

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));
        jPanel3.setPreferredSize(new java.awt.Dimension(240, 811));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));
        jPanel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel4MouseExited(evt);
            }
        });
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(153, 153, 153));
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/home_24px.png"))); // NOI18N
        jLabel5.setText("  Trang Chủ");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 240, 50));

        jPanel5.setBackground(new java.awt.Color(0, 0, 0));
        jPanel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel5MouseClicked(evt);
            }
        });
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Chức năng");
        jPanel5.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, -1, -1));

        jPanel3.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 170, 240, 50));

        jPanel6.setBackground(new java.awt.Color(0, 0, 0));
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel6MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel6MouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel6MouseReleased(evt);
            }
        });
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(153, 153, 153));
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        jLabel7.setText(" Đăng xuất");
        jPanel6.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jPanel3.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 560, 240, 50));

        jPanel7.setBackground(new java.awt.Color(0, 0, 0));
        jPanel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel7MouseClicked(evt);
            }
        });
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(153, 153, 153));
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_Library_26px_1.png"))); // NOI18N
        jLabel8.setText("  Thư Viện");
        jPanel7.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jPanel3.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 240, 50));

        jPanel8.setBackground(new java.awt.Color(0, 0, 0));
        jPanel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel8MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel8MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel8MouseExited(evt);
            }
        });
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(153, 153, 153));
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_Books_26px.png"))); // NOI18N
        jLabel9.setText("  Sách");
        jPanel8.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jPanel3.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, 240, 50));

        jPanel9.setBackground(new java.awt.Color(0, 0, 0));
        jPanel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel9MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel9MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel9MouseExited(evt);
            }
        });
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(153, 153, 153));
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_Read_Online_26px.png"))); // NOI18N
        jLabel10.setText("  Bạn đọc");
        jPanel9.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jPanel3.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 260, 240, 50));

        jPanel10.setBackground(new java.awt.Color(0, 0, 0));
        jPanel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel10MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel10MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel10MouseExited(evt);
            }
        });
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(153, 153, 153));
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_Sell_26px.png"))); // NOI18N
        jLabel11.setText("  Cho mượn sách");
        jPanel10.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jPanel3.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 310, 240, 50));

        jPanel11.setBackground(new java.awt.Color(0, 0, 0));
        jPanel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel11MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel11MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel11MouseExited(evt);
            }
        });
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(153, 153, 153));
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_Return_Purchase_26px.png"))); // NOI18N
        jLabel12.setText("  Nhận trả sách");
        jPanel11.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jPanel3.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 360, 240, 50));

        jPanel12.setBackground(new java.awt.Color(0, 0, 0));
        jPanel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel12MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel12MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel12MouseExited(evt);
            }
        });
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(153, 153, 153));
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_View_Details_26px.png"))); // NOI18N
        jLabel13.setText("  Tra cứu lịch sử");
        jPanel12.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jPanel3.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 240, 50));

        jPanel13.setBackground(new java.awt.Color(0, 0, 0));
        jPanel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel13MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel13MouseExited(evt);
            }
        });
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(153, 153, 153));
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_Book_26px.png"))); // NOI18N
        jLabel14.setText("  Sách đã mượn");
        jPanel13.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jPanel3.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 460, 240, 50));

        jPanel14.setBackground(new java.awt.Color(51, 51, 51));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_Library_26px_1.png"))); // NOI18N
        jLabel15.setText("  View Issued Books");
        jPanel14.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jPanel3.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 420, 240, 50));

        jPanel15.setBackground(new java.awt.Color(0, 0, 0));
        jPanel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel15MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel15MouseExited(evt);
            }
        });
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(153, 153, 153));
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_Conference_26px.png"))); // NOI18N
        jLabel16.setText("  Danh sách đen");
        jPanel15.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        jPanel3.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 510, 240, 50));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_menu_48px_1.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 40, 30));

        panelMain.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 871));

        home.setBackground(new java.awt.Color(51, 51, 51));
        home.setPreferredSize(new java.awt.Dimension(1308, 811));
        home.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homeMouseClicked(evt);
            }
        });
        home.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel17.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 51, 51)));

        lbl_nofOfBooks.setFont(new java.awt.Font("Segoe UI Semibold", 0, 50)); // NOI18N
        lbl_nofOfBooks.setForeground(new java.awt.Color(102, 102, 102));
        lbl_nofOfBooks.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_Book_Shelf_50px.png"))); // NOI18N
        lbl_nofOfBooks.setText("10");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lbl_nofOfBooks, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lbl_nofOfBooks, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        home.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 50, 170, -1));

        jLabel19.setFont(new java.awt.Font("Segoe UI Semibold", 1, 20)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(102, 102, 102));
        jLabel19.setText("Số bạn đọc");
        home.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 20, -1, -1));

        jPanel18.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(102, 102, 255)));

        lbl_noOfStudent.setFont(new java.awt.Font("Segoe UI Semibold", 0, 50)); // NOI18N
        lbl_noOfStudent.setForeground(new java.awt.Color(102, 102, 102));
        lbl_noOfStudent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_People_50px.png"))); // NOI18N
        lbl_noOfStudent.setText("10");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lbl_noOfStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lbl_noOfStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        home.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 50, 170, -1));

        jLabel21.setFont(new java.awt.Font("Segoe UI Semibold", 1, 20)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(102, 102, 102));
        jLabel21.setText("Số sách đang mượn");
        home.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 20, -1, -1));

        jPanel19.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 51, 51)));

        lbl_issueBooks.setFont(new java.awt.Font("Segoe UI Semibold", 0, 50)); // NOI18N
        lbl_issueBooks.setForeground(new java.awt.Color(102, 102, 102));
        lbl_issueBooks.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_Sell_50px.png"))); // NOI18N
        lbl_issueBooks.setText("10");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lbl_issueBooks, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lbl_issueBooks, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        home.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 50, 170, -1));

        jLabel27.setFont(new java.awt.Font("Segoe UI Semibold", 1, 20)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(102, 102, 102));
        jLabel27.setText("Số người đang mượn");
        home.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 20, -1, -1));

        jPanel22.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(102, 102, 255)));

        lbl_defaulterList.setFont(new java.awt.Font("Segoe UI Semibold", 0, 50)); // NOI18N
        lbl_defaulterList.setForeground(new java.awt.Color(102, 102, 102));
        lbl_defaulterList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_List_of_Thumbnails_50px.png"))); // NOI18N
        lbl_defaulterList.setText("10");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lbl_defaulterList, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lbl_defaulterList, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        home.add(jPanel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 50, 170, -1));

        jLabel23.setFont(new java.awt.Font("Segoe UI Semibold", 1, 20)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(102, 102, 102));
        jLabel23.setText("Số sách");
        home.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, -1, -1));

        jLabel24.setFont(new java.awt.Font("Segoe UI Semibold", 1, 20)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(102, 102, 102));
        jLabel24.setText("Các sách hay được mượn");
        jLabel24.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel24MouseClicked(evt);
            }
        });
        home.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 200, -1, -1));

        categoryStatistics.setBackground(new java.awt.Color(102, 102, 102));
        categoryStatistics.setForeground(new java.awt.Color(102, 102, 102));
        categoryStatistics.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        categoryStatistics.setLayout(new java.awt.BorderLayout());
        home.add(categoryStatistics, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 240, 360, 340));

        tbl_GoodBook.setBackground(new java.awt.Color(204, 204, 204));
        tbl_GoodBook.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tên", "Tác giả", "Thể loại", "Số lần"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_GoodBook.setColorBackgoundHead(new java.awt.Color(153, 153, 153));
        tbl_GoodBook.setColorBordeFilas(new java.awt.Color(102, 102, 255));
        tbl_GoodBook.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        tbl_GoodBook.setColorFilasForeground1(new java.awt.Color(0, 0, 0));
        tbl_GoodBook.setColorFilasForeground2(new java.awt.Color(0, 0, 0));
        tbl_GoodBook.setColorSelBackgound(new java.awt.Color(153, 153, 153));
        tbl_GoodBook.setColorSelForeground(new java.awt.Color(0, 0, 0));
        tbl_GoodBook.setFont(new java.awt.Font("Dialog", 0, 25)); // NOI18N
        tbl_GoodBook.setFuenteFilas(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tbl_GoodBook.setFuenteFilasSelect(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tbl_GoodBook.setFuenteHead(new java.awt.Font("Dialog", 1, 20)); // NOI18N
        tbl_GoodBook.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tbl_GoodBook.setRowHeight(40);
        tbl_GoodBook.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_GoodBookMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tbl_GoodBook);
        if (tbl_GoodBook.getColumnModel().getColumnCount() > 0) {
            tbl_GoodBook.getColumnModel().getColumn(2).setMinWidth(150);
            tbl_GoodBook.getColumnModel().getColumn(2).setMaxWidth(150);
        }

        home.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 240, 770, 400));

        panelMain.add(home, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 60, 1314, -1));

        book.setBackground(new java.awt.Color(51, 51, 51));
        book.setPreferredSize(new java.awt.Dimension(1308, 811));
        book.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel16.setBackground(new java.awt.Color(59, 60, 63));
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Dialog", 1, 20)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Sách được ưa thích");
        jPanel16.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 200, -1));

        book.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 100, 230, 120));

        jPanel21.setBackground(new java.awt.Color(59, 60, 63));
        jPanel21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel21MouseClicked(evt);
            }
        });
        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel22.setFont(new java.awt.Font("Dialog", 1, 20)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Đóng góp sách");
        jPanel21.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, 150, -1));

        book.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 100, 230, 120));

        tbl_BookDetails.setBackground(new java.awt.Color(204, 204, 204));
        tbl_BookDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Tên sách", "Tác giả", "Số lượng", "Thể loại"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_BookDetails.setToolTipText("");
        tbl_BookDetails.setColorBackgoundHead(new java.awt.Color(153, 153, 153));
        tbl_BookDetails.setColorBordeFilas(new java.awt.Color(102, 102, 255));
        tbl_BookDetails.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        tbl_BookDetails.setColorFilasForeground1(new java.awt.Color(0, 0, 0));
        tbl_BookDetails.setColorFilasForeground2(new java.awt.Color(0, 0, 0));
        tbl_BookDetails.setColorSelBackgound(new java.awt.Color(153, 153, 153));
        tbl_BookDetails.setColorSelForeground(new java.awt.Color(0, 0, 0));
        tbl_BookDetails.setFont(new java.awt.Font("Dialog", 0, 25)); // NOI18N
        tbl_BookDetails.setFuenteFilas(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tbl_BookDetails.setFuenteFilasSelect(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tbl_BookDetails.setFuenteHead(new java.awt.Font("Dialog", 1, 20)); // NOI18N
        tbl_BookDetails.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tbl_BookDetails.setRowHeight(40);
        tbl_BookDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_BookDetailsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_BookDetails);
        if (tbl_BookDetails.getColumnModel().getColumnCount() > 0) {
            tbl_BookDetails.getColumnModel().getColumn(0).setMinWidth(65);
            tbl_BookDetails.getColumnModel().getColumn(0).setMaxWidth(65);
            tbl_BookDetails.getColumnModel().getColumn(3).setMinWidth(100);
            tbl_BookDetails.getColumnModel().getColumn(3).setMaxWidth(100);
            tbl_BookDetails.getColumnModel().getColumn(4).setMinWidth(200);
            tbl_BookDetails.getColumnModel().getColumn(4).setMaxWidth(200);
        }

        book.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 460, 1050, 310));

        txt_bookName.setBackground(new java.awt.Color(51, 51, 51));
        txt_bookName.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txt_bookName.setForeground(new java.awt.Color(153, 153, 153));
        txt_bookName.setText("Nhập tên sách, tác giả hoặc thể loại ...");
        txt_bookName.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txt_bookName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_bookNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_bookNameFocusLost(evt);
            }
        });
        txt_bookName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_bookNameActionPerformed(evt);
            }
        });
        txt_bookName.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txt_bookNamePropertyChange(evt);
            }
        });
        txt_bookName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_bookNameKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bookNameKeyTyped(evt);
            }
        });
        txt_bookName.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                txt_bookNameVetoableChange(evt);
            }
        });
        book.add(txt_bookName, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 370, 310, 40));

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("Tìm kiếm sách");
        book.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 320, 180, 40));

        jLabel29.setFont(new java.awt.Font("Serif", 0, 17)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddNewBookIcons/icons8_Moleskine_26px.png"))); // NOI18N
        book.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 370, 60, 40));

        jPanel23.setBackground(new java.awt.Color(59, 60, 63));
        jPanel23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel23MouseClicked(evt);
            }
        });
        jPanel23.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel25.setFont(new java.awt.Font("Dialog", 1, 20)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Thêm sách");
        jPanel23.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, 110, -1));

        book.add(jPanel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 100, 230, 120));

        panelMain.add(book, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 60, 1314, -1));

        javax.swing.GroupLayout borrowBookLayout = new javax.swing.GroupLayout(borrowBook);
        borrowBook.setLayout(borrowBookLayout);
        borrowBookLayout.setHorizontalGroup(
            borrowBookLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1310, Short.MAX_VALUE)
        );
        borrowBookLayout.setVerticalGroup(
            borrowBookLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 810, Short.MAX_VALUE)
        );

        panelMain.add(borrowBook, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 60, 1310, 810));

        javax.swing.GroupLayout studentLayout = new javax.swing.GroupLayout(student);
        student.setLayout(studentLayout);
        studentLayout.setHorizontalGroup(
            studentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1310, Short.MAX_VALUE)
        );
        studentLayout.setVerticalGroup(
            studentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 810, Short.MAX_VALUE)
        );

        panelMain.add(student, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 60, 1310, 810));

        javax.swing.GroupLayout returnBookLayout = new javax.swing.GroupLayout(returnBook);
        returnBook.setLayout(returnBookLayout);
        returnBookLayout.setHorizontalGroup(
            returnBookLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1310, Short.MAX_VALUE)
        );
        returnBookLayout.setVerticalGroup(
            returnBookLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 810, Short.MAX_VALUE)
        );

        panelMain.add(returnBook, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 60, 1310, 810));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setSize(new java.awt.Dimension(1548, 871));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txt_bookNameVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_txt_bookNameVetoableChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_bookNameVetoableChange

    private void txt_bookNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bookNameKeyTyped

    }//GEN-LAST:event_txt_bookNameKeyTyped

    private void txt_bookNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bookNameKeyReleased
        try{
            this.clearTableBookDetails();
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms", "root", "");
            java.sql.Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from book_details where available = 'yes' and (book_name like N'%" +
                this.txt_bookName.getText() + "%' or author like N'%" +
                this.txt_bookName.getText() + "%' or category like N'%" +
                this.txt_bookName.getText() + "%')");
            while(rs.next()){
                String bookId = rs.getString("book_id");
                String bookName = rs.getString("book_name");
                String author = rs.getString("author");
                int quantity = rs.getInt("quantity");
                String category = rs.getString("category");
                Object obj[] = {bookId, bookName, author, quantity, category};
                model = (DefaultTableModel) tbl_BookDetails.getModel();
                model.addRow(obj);

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_txt_bookNameKeyReleased

    private void txt_bookNamePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txt_bookNamePropertyChange
        //        this.txt_bookId.setText(this.txt_bookName.getText());
    }//GEN-LAST:event_txt_bookNamePropertyChange

    private void txt_bookNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_bookNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_bookNameActionPerformed

    private void txt_bookNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_bookNameFocusLost
        if(txt_bookName.getText().equals("")){
            txt_bookName.setText("Nhập tên sách, tác giả hoặc thể loại ...");
            txt_bookName.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_txt_bookNameFocusLost

    private void txt_bookNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_bookNameFocusGained
        if(txt_bookName.getText().equals("Nhập tên sách, tác giả hoặc thể loại ...")){
            txt_bookName.setText("");
            txt_bookName.setForeground(new Color(255,255,255));
        }
    }//GEN-LAST:event_txt_bookNameFocusGained

    private void tbl_BookDetailsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_BookDetailsMouseClicked
        int rowNumber = this.tbl_BookDetails.getSelectedRow();
        TableModel model = this.tbl_BookDetails.getModel();

        if (evt.getClickCount() == 2 && !evt.isConsumed()) {
            evt.consume();

            int id = Integer.parseInt(model.getValueAt(rowNumber, 0).toString());
            String name = model.getValueAt(rowNumber, 1).toString();
            String author = model.getValueAt(rowNumber, 2).toString();
            int quantity = Integer.parseInt(model.getValueAt(rowNumber, 3).toString());
            String category = model.getValueAt(rowNumber, 4).toString();

            //            System.out.print(id + name + author + quantity + category);
            ManageBooks manageBook = new ManageBooks(id, name, author, quantity, category, this);
            manageBook.setVisible(true);
       
            this.clearTableBookDetails();
            this.setBookDetailToTable();
        }

    }//GEN-LAST:event_tbl_BookDetailsMouseClicked

    private void homeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeMouseClicked
        
    }//GEN-LAST:event_homeMouseClicked

    private void tbl_GoodBookMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_GoodBookMouseClicked

    }//GEN-LAST:event_tbl_GoodBookMouseClicked

    private void jLabel24MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel24MouseClicked

    }//GEN-LAST:event_jLabel24MouseClicked

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked

    }//GEN-LAST:event_jLabel1MouseClicked

    private void jPanel15MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel15MouseExited
        this.jPanel15.setBackground(new Color(0,0,0));
    }//GEN-LAST:event_jPanel15MouseExited

    private void jPanel15MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel15MouseEntered
        this.jPanel15.setBackground(new Color(51, 51, 51));
    }//GEN-LAST:event_jPanel15MouseEntered

    private void jPanel13MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel13MouseExited

        this.jPanel13.setBackground(new Color(0,0,0));
    }//GEN-LAST:event_jPanel13MouseExited

    private void jPanel13MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel13MouseEntered
        this.jPanel13.setBackground(new Color(51, 51, 51));

    }//GEN-LAST:event_jPanel13MouseEntered

    private void jPanel12MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel12MouseExited

        this.jPanel12.setBackground(new Color(0,0,0));
    }//GEN-LAST:event_jPanel12MouseExited

    private void jPanel12MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel12MouseEntered
        this.jPanel12.setBackground(new Color(51, 51, 51));

    }//GEN-LAST:event_jPanel12MouseEntered

    private void jPanel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel12MouseClicked
        ViewAllRecord allRecord = new ViewAllRecord();
        allRecord.setVisible(true);
        dispose();
    }//GEN-LAST:event_jPanel12MouseClicked

    private void jPanel11MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel11MouseExited

        this.jPanel11.setBackground(new Color(0,0,0));
    }//GEN-LAST:event_jPanel11MouseExited

    private void jPanel11MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel11MouseEntered
        this.jPanel11.setBackground(new Color(51, 51, 51));

    }//GEN-LAST:event_jPanel11MouseEntered

    private void jPanel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel11MouseClicked
        current.setVisible(false);
        current = returnBook;
        current.setVisible(true);
    }//GEN-LAST:event_jPanel11MouseClicked

    private void jPanel10MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel10MouseExited

        this.jPanel10.setBackground(new Color(0,0,0));
    }//GEN-LAST:event_jPanel10MouseExited

    private void jPanel10MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel10MouseEntered
        this.jPanel10.setBackground(new Color(51, 51, 51));

    }//GEN-LAST:event_jPanel10MouseEntered

    private void jPanel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel10MouseClicked
        current.setVisible(false);
        current = borrowBook;
       // bo.setVisible(true);
        current.setVisible(true);
    }//GEN-LAST:event_jPanel10MouseClicked

    private void jPanel9MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel9MouseExited

        this.jPanel9.setBackground(new Color(0,0,0));
    }//GEN-LAST:event_jPanel9MouseExited

    private void jPanel9MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel9MouseEntered

        this.jPanel9.setBackground(new Color(51, 51, 51));
    }//GEN-LAST:event_jPanel9MouseEntered

    private void jPanel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel9MouseClicked
        current.setVisible(false);
        current = student;
       // bo.setVisible(true);
        current.setVisible(true);
    }//GEN-LAST:event_jPanel9MouseClicked

    private void jPanel8MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel8MouseExited
        jPanel8.setBackground(new Color(0,0,0));
    }//GEN-LAST:event_jPanel8MouseExited

    private void jPanel8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel8MouseEntered
        jPanel8.setBackground(new Color(51, 51, 51));
    }//GEN-LAST:event_jPanel8MouseEntered

    private void jPanel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel8MouseClicked

        current.setVisible(false);
        current = book;
        current.setVisible(true);

    }//GEN-LAST:event_jPanel8MouseClicked

    private void jPanel6MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseReleased

    }//GEN-LAST:event_jPanel6MouseReleased

    private void jPanel6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseExited
        jPanel6.setBackground(new Color(0,0,0));
    }//GEN-LAST:event_jPanel6MouseExited

    private void jPanel6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseEntered
        jPanel6.setBackground(new Color(51, 51, 51));
    }//GEN-LAST:event_jPanel6MouseEntered
    
    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
        LoginPage page = new LoginPage();
        page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jPanel6MouseClicked

    private void jPanel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel5MouseClicked

    private void jPanel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseClicked
        current.setVisible(false);
        current = home;
        current.setVisible(true);
    }//GEN-LAST:event_jPanel4MouseClicked

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel2MouseClicked

    private void jPanel21MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel21MouseClicked
        ContributeBook contribute = new ContributeBook();
        contribute.setVisible(true);
        
    }//GEN-LAST:event_jPanel21MouseClicked

    private void jPanel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel7MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel7MouseClicked

    private void jPanel4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseEntered
        jPanel4.setBackground(new Color(51, 51, 51));
    }//GEN-LAST:event_jPanel4MouseEntered

    private void jPanel4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseExited
        jPanel4.setBackground(new Color(0,0,0));
    }//GEN-LAST:event_jPanel4MouseExited

    private void jPanel23MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel23MouseClicked
        AddBook add = new AddBook();
        add.setVisible(true);
    }//GEN-LAST:event_jPanel23MouseClicked
    int xx = 0;    int x = 240;    int xxx = 0;
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomePage().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel book;
    private javax.swing.JPanel borrowBook;
    private javax.swing.JPanel categoryStatistics;
    private javax.swing.JLabel helloName;
    private javax.swing.JPanel home;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lbl_defaulterList;
    private javax.swing.JLabel lbl_issueBooks;
    private javax.swing.JLabel lbl_noOfStudent;
    private javax.swing.JLabel lbl_nofOfBooks;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel returnBook;
    private javax.swing.JPanel student;
    private rojeru_san.complementos.RSTableMetro tbl_BookDetails;
    private rojeru_san.complementos.RSTableMetro tbl_GoodBook;
    private javax.swing.JTextField txt_bookName;
    // End of variables declaration//GEN-END:variables
}
