/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jframe;

import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import swing.DataSearch;
import swing.EventClick;
import swing.PanelSearch;

/**
 *
 * @author PV
 */
public class ContributeBook extends javax.swing.JFrame {

    /**
     * Creates new form ManageBooks
     */
    
    // Information book to contribute
    String bookName, author, category;
    int bookId, quantity;
    DefaultTableModel model;
    HomePage home;
    
    // param to search student
    private JPopupMenu menuStudent;
    private PanelSearch searchStudent;
    ArrayList<String> listStudent = new ArrayList<String>();
    ArrayList<String> dataStudentStory = new ArrayList<String>();
    
    public ContributeBook() {
        initComponents();
        setupStudentSearch();
    }
    
    /*
    * To setup GUI, menu popup to student search
    */
    public void setupStudentSearch() {
        addDataStudentStory();
        addListStudent();
        menuStudent = new JPopupMenu();
        searchStudent = new PanelSearch();
        menuStudent.setBorder(BorderFactory.createLineBorder(new Color(164, 164, 164)));
        menuStudent.add(searchStudent);
        menuStudent.setFocusable(false);
        searchStudent.addEventClick(new EventClick() {
            @Override
            public void itemClick(DataSearch data) {
                menuStudent.setVisible(false);
                txtStudentSearch.setText(data.getText());
                addStudentStory(data.getText());
                dataStudentStory.clear();
                addDataStudentStory();
                System.out.println("Click Item : " + data.getText());
            }

            @Override
            public void itemRemove(Component com, DataSearch data) {
                searchStudent.remove(com);
                removeStudentHistory(data.getText());
                menuStudent.setPopupSize(menuStudent.getWidth(), (searchStudent.getItemSize() * 35) + 2);
                if (searchStudent.getItemSize() == 0) {
                    menuStudent.setVisible(false);
                }
                System.out.println("Remove Student Item : " + data.getText());
            }
        });
    }
    
    // Constructor book information to contribute
    public ContributeBook(int id, String name, String author, int quantity, String category, HomePage home) {
        initComponents();
        bookId = id;
        this.txt_authorName.setText(author);
        this.txt_bookName.setText(name);
        this.txt_quantity.setText(String.valueOf(quantity));
        this.txt_category.setText((category));
        this.home = home;
    }

    // Add book to library
    public boolean addBook() {
        boolean isAdded = false;
        
        bookName = txt_bookName.getText();
        author = txt_authorName.getText();
        quantity = Integer.parseInt(txt_quantity.getText());
        category = txt_category.getText();
        try {
            Connection con = DBConnection.getConnection();
            String sql = "insert into book_details (book_name, author, quantity, category, available) values"
                    + "(?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, bookName);
            pst.setString(2, author);
            pst.setInt(3, quantity);
            pst.setString(4, category);
            pst.setString(5, "yes");
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0) {
                isAdded = true;
            }
            else {
                isAdded = false;
            } 
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return isAdded;

    }
    
    // Update status to good when student contribute book to library
    public void updateStatus(){
        String[] nameid = txtStudentSearch.getText().split("-");
//        String studentName = nameid[0].trim();
        int idStudent = Integer.parseInt(nameid[1].trim());
        try {
            Connection con = DBConnection.getConnection();
            String sql = "update student_detail set status = ? where student_id = ?";
            
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, "GOOD");
            pst.setInt(2, idStudent);
            pst.execute();
            }    
        catch(Exception e) {
            e.printStackTrace();
        }
    }
        
    
    
    //**************************************************************************************************************
    //// Method for search Student
    
    /**
    * Lưu lại thông tin bạn đọc đã tìm kiếm vào search history để gợi ý các lần tìm kiếm tiếp theo
    * Chỉ lưu lại 6 lần tìm kiếm gần nhất
    * Tham số là tên bạn đọc đã click
    */
    public void addStudentStory(String student_name) {
        try{
            Connection con = DBConnection.getConnection();
            String sql = "delete from student_data_story where student_name = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, student_name);
            pst.execute();
            
            sql = "insert into student_data_story (student_name) values(?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, student_name);
            pst.execute();
            
            sql = "DELETE FROM student_data_story where student_name = (SELECT student_name from student_data_story limit 1) \n" +
"	and 7 = (select count(id) FROM student_data_story)";

            pst = con.prepareStatement(sql);
            pst.execute();
            
            
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
    * Xóa bạn đọc khỏi search history
    * Tham số là tên bạn đọc được click
    */
    public void removeStudentStory(String student_name) {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms", "root", "");
            String sql = "delete from student_data_story where student_name = ? ";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, student_name);
            int rowCount = pst.executeUpdate();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
    * Thêm các bạn đọc tìm kiếm gần đây vào các mục gợi ý tìm kiếm
    */
    public void addDataStudentStory() {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms", "root", "");
            java.sql.Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select student_name from student_data_story");
            while(rs.next()){
                String studentName = rs.getString("student_name");
                dataStudentStory.add(studentName);
                
            }
        }catch(Exception e){
         
        }
    }
    
    /**
    * danh bạn đọc tất cả các bạn đọc vào listbook
    * Các bạn đọc được sắp theo thứ tự alphabet
    */
    public void addListStudent() {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms", "root", "");
            java.sql.Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select student_name, student_id from student_detail where available = 'yes'");
            while(rs.next()){
                String studentName = rs.getString("student_name");
                int studentId = rs.getInt("student_id");
                
                listStudent.add(studentName + " - " + studentId);
                
            }
            Collections.sort(listStudent);
   
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
    * Trả về các bạn đọc được gợi ý theo type release của người nhập
    * Số lượng tối đa bạn đọc gợi ý là 7
    */  
    private List<DataSearch> searchStudent(String search) {
        int limitData = 7;
        List<DataSearch> list = new ArrayList<>();
        
        
        for (String d : listStudent) {
            if (d.toLowerCase().contains(search)) {
                boolean story = isStudentStory(d);
                if (story) {
                    list.add(0, new DataSearch(d, story));
                    //  add or insert to first record
                }
                if (list.size() == limitData) {
                    return list;
                }
            }
        }
        for (String d : listStudent) {
            if (d.toLowerCase().contains(search)) {
                boolean story = isStudentStory(d);
                if (!story) {
                    list.add(new DataSearch(d, story));
                    //  add or insert to first record
                }
                if (list.size() == limitData) {
                    return list;
                }
            }
        }
        return list;
    }

    /**
    * Clear danh bạn đọc các bạn đọc gợi ý
    */
    private void removeStudentHistory(String text) {
        try{
            for (int i = 0; i < dataStudentStory.size(); i++) {
                String d = dataStudentStory.get(i);
                if (d.toLowerCase().equals(text.toLowerCase())) {
    //                dataStory[i] = "";
                    removeStudentStory(dataStudentStory.get(i));
                    dataStudentStory.clear();
                    addDataStudentStory();
                    break;
                }
            }   
        } catch (Exception e) {
            
        }
        
    }
    
    /**
    * Kiểm tra bạn đọc có ở trong lịch sử tìm kiếm không
    */
    private boolean isStudentStory(String text) {
        for (String d : dataStudentStory) {
            if (d.toLowerCase().equals(text.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txt_bookName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txt_authorName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txt_quantity = new javax.swing.JTextField();
        rSMaterialButtonCircle4 = new rojerusan.RSMaterialButtonCircle();
        jLabel8 = new javax.swing.JLabel();
        txt_category = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        notification = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtStudentSearch = new swing.MyTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 102, 153));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 102, 153));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel5.setFont(new java.awt.Font("Serif", 0, 17)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddNewBookIcons/icons8_Moleskine_26px.png"))); // NOI18N
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 240, 60, 40));

        jLabel10.setFont(new java.awt.Font("Dialog", 0, 17)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Tên sách");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 200, 180, 40));

        txt_bookName.setBackground(new java.awt.Color(0, 102, 153));
        txt_bookName.setFont(new java.awt.Font("Dialog", 0, 17)); // NOI18N
        txt_bookName.setForeground(new java.awt.Color(153, 153, 153));
        txt_bookName.setText("Nhập tên sách ...");
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
        jPanel1.add(txt_bookName, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 240, 310, 40));

        jLabel6.setFont(new java.awt.Font("Serif", 0, 17)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddNewBookIcons/icons8_Collaborator_Male_26px.png"))); // NOI18N
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 340, 60, 40));

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 17)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Tên tác giả");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 300, 180, 40));

        txt_authorName.setBackground(new java.awt.Color(0, 102, 153));
        txt_authorName.setFont(new java.awt.Font("Dialog", 0, 17)); // NOI18N
        txt_authorName.setForeground(new java.awt.Color(153, 153, 153));
        txt_authorName.setText("Nhập tên tác giả ...");
        txt_authorName.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txt_authorName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_authorNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_authorNameFocusLost(evt);
            }
        });
        txt_authorName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_authorNameActionPerformed(evt);
            }
        });
        jPanel1.add(txt_authorName, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 340, 310, 40));

        jLabel7.setFont(new java.awt.Font("Serif", 0, 17)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddNewBookIcons/icons8_Unit_26px.png"))); // NOI18N
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 440, 60, 40));

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 17)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Số lượng");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 400, 180, 40));

        txt_quantity.setBackground(new java.awt.Color(0, 102, 153));
        txt_quantity.setFont(new java.awt.Font("Dialog", 0, 17)); // NOI18N
        txt_quantity.setForeground(new java.awt.Color(153, 153, 153));
        txt_quantity.setText("Số lượng ...");
        txt_quantity.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txt_quantity.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_quantityFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_quantityFocusLost(evt);
            }
        });
        txt_quantity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_quantityActionPerformed(evt);
            }
        });
        jPanel1.add(txt_quantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 440, 310, 40));

        rSMaterialButtonCircle4.setBackground(new java.awt.Color(102, 102, 102));
        rSMaterialButtonCircle4.setText("Đóng góp sách");
        rSMaterialButtonCircle4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rSMaterialButtonCircle4MouseClicked(evt);
            }
        });
        rSMaterialButtonCircle4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonCircle4ActionPerformed(evt);
            }
        });
        jPanel1.add(rSMaterialButtonCircle4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 670, 380, 60));

        jLabel8.setFont(new java.awt.Font("Serif", 0, 17)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddNewBookIcons/icons8_Unit_26px.png"))); // NOI18N
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 550, 60, 40));

        txt_category.setBackground(new java.awt.Color(0, 102, 153));
        txt_category.setFont(new java.awt.Font("Dialog", 0, 17)); // NOI18N
        txt_category.setForeground(new java.awt.Color(153, 153, 153));
        txt_category.setText("Thể loại ...");
        txt_category.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txt_category.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_categoryFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_categoryFocusLost(evt);
            }
        });
        txt_category.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_categoryActionPerformed(evt);
            }
        });
        jPanel1.add(txt_category, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 550, 310, 40));

        jLabel13.setFont(new java.awt.Font("Serif", 0, 17)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Thể loại");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 510, 180, 40));

        notification.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        notification.setForeground(new java.awt.Color(0, 102, 153));
        notification.setText("Cập nhật thành công");
        jPanel1.add(notification, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 610, 230, 36));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("X");
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 0, 30, 40));

        jLabel14.setFont(new java.awt.Font("Dialog", 0, 17)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Tên bạn đọc");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 110, 180, 40));

        jLabel9.setFont(new java.awt.Font("Serif", 0, 17)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddNewBookIcons/icons8_Collaborator_Male_26px.png"))); // NOI18N
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 150, 60, 40));

        txtStudentSearch.setBackground(new java.awt.Color(0, 102, 153));
        txtStudentSearch.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtStudentSearch.setForeground(new java.awt.Color(153, 153, 153));
        txtStudentSearch.setText("Nhập tên bạn đọc ...");
        txtStudentSearch.setFont(new java.awt.Font("Dialog", 0, 17)); // NOI18N
        txtStudentSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStudentSearchFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtStudentSearchFocusLost(evt);
            }
        });
        txtStudentSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtStudentSearchMouseClicked(evt);
            }
        });
        txtStudentSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStudentSearchActionPerformed(evt);
            }
        });
        txtStudentSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtStudentSearchKeyReleased(evt);
            }
        });
        jPanel1.add(txtStudentSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 150, 310, 40));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 490, 830));

        setSize(new java.awt.Dimension(489, 736));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txt_bookNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_bookNameFocusGained
        if(txt_bookName.getText().equals("Nhập tên sách ...")){
            txt_bookName.setText("");
            txt_bookName.setForeground(new Color(255,255,255));
        }
    }//GEN-LAST:event_txt_bookNameFocusGained

    private void txt_bookNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_bookNameFocusLost
        if(txt_bookName.getText().equals("")){
            txt_bookName.setText("Nhập tên sách ...");
            txt_bookName.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_txt_bookNameFocusLost

    private void txt_bookNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_bookNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_bookNameActionPerformed

    private void txt_authorNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_authorNameFocusGained
        if(txt_authorName.getText().equals("Nhập tên tác giả ...")){
            txt_authorName.setText("");
            txt_authorName.setForeground(new Color(255,255,255));
        }
    }//GEN-LAST:event_txt_authorNameFocusGained

    private void txt_authorNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_authorNameFocusLost
        if(txt_authorName.getText().equals("")){
            txt_authorName.setText("Nhập tên tác giả ...");
            txt_authorName.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_txt_authorNameFocusLost

    private void txt_authorNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_authorNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_authorNameActionPerformed

    private void txt_quantityFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_quantityFocusGained
        if(txt_quantity.getText().equals("Số lượng ...")){
            txt_quantity.setText("");
            txt_quantity.setForeground(new Color(255,255,255));
        }
    }//GEN-LAST:event_txt_quantityFocusGained

    private void txt_quantityFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_quantityFocusLost
        if(txt_quantity.getText().equals("")){
            txt_quantity.setText("Số lượng ...");
            txt_quantity.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_txt_quantityFocusLost

    private void txt_quantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_quantityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_quantityActionPerformed

    private void rSMaterialButtonCircle4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonCircle4ActionPerformed
        if(this.addBook()== true){
            this.updateStatus();
            notification.setText("Cập nhật thành công");
            this.notification.setForeground(Color.yellow);
        }
        else {
            notification.setText("Cập nhật thất bại");
            this.notification.setForeground(Color.yellow);
        }
        
    }//GEN-LAST:event_rSMaterialButtonCircle4ActionPerformed

    private void txt_bookNamePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txt_bookNamePropertyChange
//        this.txt_bookId.setText(this.txt_bookName.getText());
    }//GEN-LAST:event_txt_bookNamePropertyChange

    private void txt_bookNameVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_txt_bookNameVetoableChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_bookNameVetoableChange

    private void txt_bookNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bookNameKeyReleased
        
    }//GEN-LAST:event_txt_bookNameKeyReleased

    private void txt_bookNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bookNameKeyTyped
        
    }//GEN-LAST:event_txt_bookNameKeyTyped

    private void txt_categoryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_categoryFocusGained
        if(txt_category.getText().equals("Thể loại ...")){
            txt_category.setText("");
            txt_category.setForeground(new Color(255,255,255));
        }
    }//GEN-LAST:event_txt_categoryFocusGained

    private void txt_categoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_categoryFocusLost
        if(txt_category.getText().equals("")){
            txt_category.setText("Thể loại ...");
            txt_category.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_txt_categoryFocusLost

    private void txt_categoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_categoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_categoryActionPerformed

    private void rSMaterialButtonCircle4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rSMaterialButtonCircle4MouseClicked
        
    }//GEN-LAST:event_rSMaterialButtonCircle4MouseClicked

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked

        this.dispose();
    }//GEN-LAST:event_jLabel2MouseClicked

    private void txtStudentSearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStudentSearchFocusGained
        if(txtStudentSearch.getText().equals("Nhập tên bạn đọc ...")){
            txtStudentSearch.setText("");
            txtStudentSearch.setForeground(new Color(255,255,255));
        }
    }//GEN-LAST:event_txtStudentSearchFocusGained

    private void txtStudentSearchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStudentSearchFocusLost
        if (txtStudentSearch.getText().equals("Nhập tên bạn đọc ...")) {

        }
        else if(!txtStudentSearch.getText().equals("")){
           // this.getStudentDetails();
        }
        else if(txtStudentSearch.getText().equals("")){
            txtStudentSearch.setText("Nhập tên bạn đọc ...");
            txtStudentSearch.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_txtStudentSearchFocusLost

    private void txtStudentSearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtStudentSearchMouseClicked
        if (searchStudent.getItemSize() > 0) {
            menuStudent.show(txtStudentSearch, 0, txtStudentSearch.getHeight());
        }
    }//GEN-LAST:event_txtStudentSearchMouseClicked

    private void txtStudentSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStudentSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStudentSearchActionPerformed

    private void txtStudentSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtStudentSearchKeyReleased
        String text = txtStudentSearch.getText().trim().toLowerCase();
        searchStudent.setData(searchStudent(text));
        if (searchStudent.getItemSize() > 0) {
            //  * 2 top and bot border
            menuStudent.show(txtStudentSearch, 0, txtStudentSearch.getHeight());
            menuStudent.setPopupSize(menuStudent.getWidth(), (searchStudent.getItemSize() * 35) + 2);
        } else {
            menuStudent.setVisible(false);
        }
    }//GEN-LAST:event_txtStudentSearchKeyReleased

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
            java.util.logging.Logger.getLogger(ContributeBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ContributeBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ContributeBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ContributeBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ContributeBook().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel notification;
    private rojerusan.RSMaterialButtonCircle rSMaterialButtonCircle4;
    private swing.MyTextField txtStudentSearch;
    private javax.swing.JTextField txt_authorName;
    private javax.swing.JTextField txt_bookName;
    private javax.swing.JTextField txt_category;
    private javax.swing.JTextField txt_quantity;
    // End of variables declaration//GEN-END:variables
}
