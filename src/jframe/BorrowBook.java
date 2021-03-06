/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jframe;


import java.sql.Connection;
import java.sql.*;
import java.awt.Color;
import java.awt.Component;
import java.util.*;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import swing.DataSearch;
import swing.EventClick;
import swing.PanelSearch;
/**
 *
 * Panel to borrow book
 */
public class BorrowBook extends javax.swing.JPanel {
    
    // param to search book
    private JPopupMenu menuBook;
    private PanelSearch searchBook;
    ArrayList<String> listBook = new ArrayList<String>();
    ArrayList<String> dataBookStory = new ArrayList<String>();
    
    // param to search student
    private JPopupMenu menuStudent;
    private PanelSearch searchStudent;
    ArrayList<String> listStudent = new ArrayList<String>();
    ArrayList<String> dataStudentStory = new ArrayList<String>();
    
    
    //Contructor
    public BorrowBook() {
        initComponents();
        this.date_borrowDate.setDate(new java.util.Date(System.currentTimeMillis()));
        setupBookSearch();
        setupStudentSearch();
    }
    
    
    /*
    * To setup GUI, menu popup to book search
    */
    public void setupBookSearch() {
        addDataBookStory();
        addListBook();
        menuBook = new JPopupMenu();
        searchBook = new PanelSearch();
        menuBook.setBorder(BorderFactory.createLineBorder(new Color(164, 164, 164)));
        menuBook.add(searchBook);
        menuBook.setFocusable(false);
        searchBook.addEventClick(new EventClick() {
            @Override
            public void itemClick(DataSearch data) {
                menuBook.setVisible(false);
                txtBookSearch.setText(data.getText());
                addBookStory(data.getText());
                dataBookStory.clear();
                addDataBookStory();
                System.out.println("Click Item : " + data.getText());
            }

            @Override
            public void itemRemove(Component com, DataSearch data) {
                searchBook.remove(com);
                removeBookHistory(data.getText());
                menuBook.setPopupSize(menuBook.getWidth(), (searchBook.getItemSize() * 35) + 2);
                if (searchBook.getItemSize() == 0) {
                    menuBook.setVisible(false);
                }
                System.out.println("Remove Book Item : " + data.getText());
            }
        });
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
    
    
    public void setBookName(String name) {
        txtBookSearch.setText(name);
    }
    
    /**
    * L???y th??ng tin s??ch t??? database, n???u kh??ng t??m th???y b??o l???i
    */
    public void getBookDetails(){
        String bookName = txtBookSearch.getText();
        String author = authorBox.getItemAt(authorBox.getSelectedIndex());
        
        try{
            this.lbl_bookError.setForeground(new Color(0,153,102));
            Connection con = DBConnection.getConnection();
            String sql = "select * from book_details where book_name = ? and author = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, bookName);
            pst.setString(2, author);
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()){
                this.lbl_bookId.setText(rs.getString("book_id"));
                this.lbl_bookName.setText(rs.getString("book_name"));
                this.lbl_author.setText(rs.getString("author"));
                this.lbl_quantity.setText(rs.getString("quantity"));
            }else{
//                this.lbl_bookError.setText("Kh??ng t??m th???y s??ch");
                  this.lbl_bookError.setForeground(new Color(255,255,0));
                  this.lbl_bookId.setText("");
                  this.lbl_bookName.setText("");
                  this.lbl_author.setText("");
                  this.lbl_quantity.setText("");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    /**
    * L???y th??ng tin b???n ?????c t??? database, n???u kh??ng t??m th???y b??o l???i
    */
    public void getStudentDetails(){
        String[] nameid = txtStudentSearch.getText().split("-");
        String studentName = nameid[0].trim();
        int StudentId = Integer.parseInt(nameid[1].trim());
        
        try{
            lbl_studentError1.setForeground(new Color(0,153,153));
            Connection con = DBConnection.getConnection();
            String sql = "select * from student_detail where student_name = ? and student_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, studentName);
            pst.setInt(2, StudentId);
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()){
                this.lbl_studentId.setText(rs.getString("student_id"));
                this.lbl_studentName.setText(rs.getString("student_name"));
                this.lbl_major.setText(rs.getString("major"));
                this.lbl_status.setText(rs.getString("status"));
            }else{
               // this.lbl_studentError1.setText("Kh??ng t??m th???y h???c sinh");
               lbl_studentError1.setForeground(new Color(255,255,0));
               this.lbl_studentId.setText("");
                this.lbl_studentName.setText("");
                this.lbl_major.setText("");
                this.lbl_status.setText("");
        }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
    * M?????n s??ch theo th??ng tin s??ch, b???n ?????c ???? t??m th???y
    * Th??m th??ng tin m?????n v??o database
    * N???u s??ch ???? h???t b??o l???i
    */
    public boolean borrowBook(){
        boolean borrow = false;
        int bookId = Integer.parseInt(lbl_bookId.getText());
        int studentId = Integer.parseInt(lbl_studentId.getText());
        String bookName = this.lbl_bookName.getText();
        String studentName = this.lbl_studentName.getText();
        String author = lbl_author.getText();
        
        java.util.Date uborrowDate = this.date_borrowDate.getDate();
        java.util.Date uDueDate = this.date_dueDate.getDate();
        
        Long l1 = uborrowDate.getTime();
        long l2 = uDueDate.getTime();
        
        java.sql.Date sborrowDate = new java.sql.Date(l1);
        java.sql.Date sDueDate = new java.sql.Date(l2);
        try{
            Connection con = DBConnection.getConnection();
            String sql = "insert into borrow_book_detail(book_id, book_name, student_id, student_name, borrowLibrarian,"
                    +" borrow_date, due_date, status, author) values(?,?,?,?,?,?,?,?,?) " ; 
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, bookId);
            pst.setString(2, bookName);
            pst.setInt(3, studentId);
            pst.setString(4, studentName);
            pst.setString(5, LoginPage.user_name);
            
            pst.setDate(6, sborrowDate);
            pst.setDate(7, sDueDate);
            pst.setString(8, "pending");
            pst.setString(9, author);
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0){
                borrow = true;
            }else{
                borrow = false;
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return borrow;
        
    }
    
    
    /**
    * Tr??? s??? l?????ng s??ch ??i 1 sau khi cho m?????n
    */
    public void updateBookCount() {
        int bookId = Integer.parseInt(lbl_bookId.getText());
        try {
            Connection con = DBConnection.getConnection();
            String sql = "update book_details set quantity  = quantity - 1 where book_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, bookId);
            
            int rowCount =  pst.executeUpdate();
            
            if(rowCount > 0) {
                
                int initialCount = Integer.parseInt(this.lbl_quantity.getText());
                this.lbl_quantity.setText(Integer.toString(initialCount - 1));
            }else {
                
            }
        } catch(Exception e) {
            e.printStackTrace();
    }
    }
    
    //checking whether book already allocated or not 
    public boolean isAlreadyborrowd(){
        boolean isAlreadyborrowd = false;
        int bookId = Integer.parseInt(lbl_bookId.getText());
        int studentId = Integer.parseInt(lbl_studentId.getText());
        
        try {
            Connection con = DBConnection .getConnection();
            String sql = "select * from borrow_book_detail where book_id = ? and student_id = ? and status = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, bookId);
            pst.setInt(2, studentId);
            pst.setString(3, "pending");
            
            ResultSet  rs = pst.executeQuery();
            
            if(rs.next()){
                isAlreadyborrowd = true;
            } else {
                isAlreadyborrowd = false;
            }
            
        }catch(Exception e) {
            e.printStackTrace();
        }
        return isAlreadyborrowd;
    }
    
    
    //*********************************************************************************************************//
    //// Methods for Search book
    
    
    /**
    * L??u l???i th??ng tin s??ch ???? t??m ki???m v??o search history ????? g???i ?? c??c l???n t??m ki???m ti???p theo
    * Ch??? l??u l???i 6 l???n t??m ki???m g???n nh???t
    * Tham s??? l?? t??n s??ch ???? click
    */
    public void addBookStory(String book_name) {
        try{
            Connection con = DBConnection.getConnection();
            String sql = "delete from book_data_story where book_name = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, book_name);
            pst.execute();
            
            sql = "insert into book_data_story (book_name) values(?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, book_name);
            pst.execute();
            
            sql = "DELETE FROM book_data_story where book_name = (SELECT book_name from book_data_story limit 1) \n" +
"	and 7 = (select count(id) FROM book_data_story)";

            pst = con.prepareStatement(sql);
            pst.execute();
            
            
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    /**
    * X??a s??ch kh???i search history
    * Tham s??? l?? t??n s??ch ???????c click
    */
    public void removeBookStory(String book_name) {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms", "root", "");
            String sql = "delete from book_data_story where book_name = ? ";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, book_name);
            int rowCount = pst.executeUpdate();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    /**
    * Th??m c??c s??ch t??m ki???m g???n ????y v??o c??c m???c g???i ?? t??m ki???m
    */
    public void addDataBookStory() {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms", "root", "");
            java.sql.Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select book_name from book_data_story");
            while(rs.next()){
                String bookName = rs.getString("book_name");
                dataBookStory.add(bookName);
                
            }
        }catch(Exception e){
         
        }
    }
    
    
    /**
    * danh s??ch t???t c??? c??c s??ch v??o listbook
    * C??c s??ch ???????c s???p theo th??? t??? alphabet
    */
    public void addListBook() {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms", "root", "");
            java.sql.Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select book_name from book_details where available = 'yes'");
            while(rs.next()){
                String bookName = rs.getString("book_name");
                if(!listBook.contains(bookName)){
                    listBook.add(bookName);
                }
                    
                Collections.sort(listBook);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
    * Tr??? v??? c??c s??ch ???????c g???i ?? theo type release c???a ng?????i nh???p
    * S??? l?????ng t???i ??a s??ch g???i ?? l?? 7
    */
    private List<DataSearch> searchBook(String search) {
        int limitData = 7;
        List<DataSearch> list = new ArrayList<>();

        for (String d : listBook) {
            if (d.toLowerCase().contains(search)) {
                boolean story = isBookStory(d);
                if (story) {
                    list.add(0, new DataSearch(d, story));
                    //  add or insert to first record
                } 
                if (list.size() == limitData) {
                    return list;
                }
            }
        }
        
        for (String d : listBook) {
            if (d.toLowerCase().contains(search)) {
                boolean story = isBookStory(d);
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
    * Clear danh s??ch c??c s??ch g???i ??
    */
    private void removeBookHistory(String text) {
        try{
            for (int i = 0; i < dataBookStory.size(); i++) {
            String d = dataBookStory.get(i);
            if (d.toLowerCase().equals(text.toLowerCase())) {
//                dataStory[i] = "";
                removeBookStory(dataBookStory.get(i));
                dataBookStory.clear();
                addDataBookStory();
                break;
                }
            }   
        } catch (Exception e) {
            
        }
        
    }
    
    /**
    * Ki???m tra s??ch c?? ??? trong l???ch s??? t??m ki???m kh??ng
    */
    private boolean isBookStory(String text) {
        for (String d : dataBookStory) {
            if (d.toLowerCase().equals(text.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    
    //**************************************************************************************************************
    //// Method for search Student
    
    /**
    * L??u l???i th??ng tin b???n ?????c ???? t??m ki???m v??o search history ????? g???i ?? c??c l???n t??m ki???m ti???p theo
    * Ch??? l??u l???i 6 l???n t??m ki???m g???n nh???t
    * Tham s??? l?? t??n b???n ?????c ???? click
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
    * X??a b???n ?????c kh???i search history
    * Tham s??? l?? t??n b???n ?????c ???????c click
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
    * Th??m c??c b???n ?????c t??m ki???m g???n ????y v??o c??c m???c g???i ?? t??m ki???m
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
    * danh b???n ?????c t???t c??? c??c b???n ?????c v??o listbook
    * C??c b???n ?????c ???????c s???p theo th??? t??? alphabet
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
    * Tr??? v??? c??c b???n ?????c ???????c g???i ?? theo type release c???a ng?????i nh???p
    * S??? l?????ng t???i ??a b???n ?????c g???i ?? l?? 7
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
    * Clear danh b???n ?????c c??c b???n ?????c g???i ??
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
    * Ki???m tra b???n ?????c c?? ??? trong l???ch s??? t??m ki???m kh??ng
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

        panel_main = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lbl_quantity = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lbl_bookId = new javax.swing.JLabel();
        lbl_bookName = new javax.swing.JLabel();
        lbl_author = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lbl_bookError = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        lbl_status = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lbl_studentId = new javax.swing.JLabel();
        lbl_studentName = new javax.swing.JLabel();
        lbl_major = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lbl_studentError1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        buttonToBorrow = new rojerusan.RSMaterialButtonCircle();
        jLabel1 = new javax.swing.JLabel();
        date_dueDate = new com.toedter.calendar.JDateChooser();
        date_borrowDate = new com.toedter.calendar.JDateChooser();
        txtBookSearch = new swing.MyTextField();
        txtStudentSearch = new swing.MyTextField();
        authorBox = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(102, 102, 102));
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        setMinimumSize(new java.awt.Dimension(1200, 770));
        setPreferredSize(new java.awt.Dimension(1308, 811));

        panel_main.setBackground(new java.awt.Color(51, 51, 51));
        panel_main.setForeground(new java.awt.Color(102, 102, 102));
        panel_main.setMinimumSize(new java.awt.Dimension(1200, 770));
        panel_main.setPreferredSize(new java.awt.Dimension(1308, 811));
        panel_main.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 153, 102));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 25)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddNewBookIcons/icons8_Literature_100px_1.png"))); // NOI18N
        jLabel3.setText("Th??ng tin s??ch");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 270, 110));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 130, -1, 10));

        lbl_quantity.setFont(new java.awt.Font("Yu Gothic UI", 0, 20)); // NOI18N
        lbl_quantity.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(lbl_quantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 380, 190, 30));

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("T??n s??ch :");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 110, -1));

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("T??c gi??? :");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 110, -1));

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Id s??ch :");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 110, -1));

        lbl_bookId.setFont(new java.awt.Font("Yu Gothic UI", 0, 20)); // NOI18N
        lbl_bookId.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(lbl_bookId, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 200, 190, 30));

        lbl_bookName.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lbl_bookName.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(lbl_bookName, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 260, 190, 30));

        lbl_author.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lbl_author.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(lbl_author, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 320, 190, 30));

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("S??? l?????ng :");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 380, 110, -1));

        lbl_bookError.setBackground(new java.awt.Color(255, 204, 0));
        lbl_bookError.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        lbl_bookError.setForeground(new java.awt.Color(0, 153, 102));
        lbl_bookError.setText("Kh??ng t??m th???y s??ch !!!");
        jPanel1.add(lbl_bookError, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 430, 290, 40));

        panel_main.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 280, 610, 530));

        jPanel3.setBackground(new java.awt.Color(0, 153, 153));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 350, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 120, 350, 10));

        jLabel14.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Tr???ng th??i :");
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 380, 110, -1));

        lbl_status.setFont(new java.awt.Font("Yu Gothic UI", 0, 20)); // NOI18N
        lbl_status.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(lbl_status, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 380, 220, 30));

        jLabel16.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("T??n b???n ?????c :");
        jPanel3.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 260, 140, -1));

        jLabel17.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("L???p :");
        jPanel3.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 320, 110, -1));

        jLabel18.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Id b???n ?????c :");
        jPanel3.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 200, 110, -1));

        lbl_studentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 20)); // NOI18N
        lbl_studentId.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(lbl_studentId, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 200, 240, 30));

        lbl_studentName.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lbl_studentName.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(lbl_studentName, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 260, 240, 30));

        lbl_major.setFont(new java.awt.Font("Yu Gothic UI", 0, 20)); // NOI18N
        lbl_major.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(lbl_major, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 320, 240, 30));

        jLabel15.setFont(new java.awt.Font("Dialog", 0, 25)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddNewBookIcons/icons8_Student_Registration_100px_2.png"))); // NOI18N
        jLabel15.setText("Th??ng tin b???n ?????c");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 340, 110));

        lbl_studentError1.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        lbl_studentError1.setForeground(new java.awt.Color(0, 153, 153));
        lbl_studentError1.setText("Kh??ng t??m th???y b???n ?????c !!!");
        jPanel3.add(lbl_studentError1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 460, 260, -1));

        panel_main.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 280, 700, 530));

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 25)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Cho m?????n s??ch");
        panel_main.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 10, 210, 30));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        panel_main.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 50, -1, 10));

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("T??n t??c gi??? :");
        panel_main.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, 120, 40));

        jLabel10.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Ng??y m?????n :");
        panel_main.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 80, 130, 40));

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("T??n b???n ?????c :");
        panel_main.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 220, 130, 40));

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("H???n tr??? :");
        panel_main.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 150, 130, 40));

        buttonToBorrow.setBackground(new java.awt.Color(102, 102, 102));
        buttonToBorrow.setText("Cho m?????n");
        buttonToBorrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonToBorrowActionPerformed(evt);
            }
        });
        panel_main.add(buttonToBorrow, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 210, 470, 60));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setText("X");
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        panel_main.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1416, 20, 20, 40));

        date_dueDate.setBackground(new java.awt.Color(102, 102, 102));
        date_dueDate.setDateFormatString("yyyy/MM/dd");
        date_dueDate.setFont(new java.awt.Font("Dialog", 0, 17)); // NOI18N
        panel_main.add(date_dueDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 154, 320, 30));

        date_borrowDate.setBackground(new java.awt.Color(51, 51, 51));
        date_borrowDate.setDateFormatString("yyyy/MM/dd");
        date_borrowDate.setFont(new java.awt.Font("Dialog", 0, 17)); // NOI18N
        panel_main.add(date_borrowDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 84, 320, 30));

        txtBookSearch.setBackground(new java.awt.Color(51, 51, 51));
        txtBookSearch.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtBookSearch.setForeground(new java.awt.Color(153, 153, 153));
        txtBookSearch.setText("Nh???p t??n s??ch ...");
        txtBookSearch.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        txtBookSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBookSearchFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBookSearchFocusLost(evt);
            }
        });
        txtBookSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtBookSearchMouseClicked(evt);
            }
        });
        txtBookSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBookSearchActionPerformed(evt);
            }
        });
        txtBookSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBookSearchKeyReleased(evt);
            }
        });
        panel_main.add(txtBookSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, 310, 40));

        txtStudentSearch.setBackground(new java.awt.Color(51, 51, 51));
        txtStudentSearch.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtStudentSearch.setForeground(new java.awt.Color(153, 153, 153));
        txtStudentSearch.setText("Nh???p t??n b???n ?????c ...");
        txtStudentSearch.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
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
        panel_main.add(txtStudentSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 220, 310, 40));

        authorBox.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        authorBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                authorBoxMouseClicked(evt);
            }
        });
        authorBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                authorBoxActionPerformed(evt);
            }
        });
        panel_main.add(authorBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 150, 310, 40));

        jLabel19.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("T??n s??ch :");
        panel_main.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 100, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel_main, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel_main, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonToBorrowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonToBorrowActionPerformed
        if (date_dueDate.getDate().after(date_borrowDate.getDate())){
            boolean status = true;
            try{
               
                Connection con = DBConnection.getConnection();
                String sql = "select hs.status as status, count(hs.status) as count"
                        + " from student_detail hs, borrow_book_detail muon "
                        + " where hs.student_id = muon.student_id "
                        + " and hs.student_id = " + lbl_studentId.getText()
                        + " and muon.status = 'pending' "
                        + " group by muon.status";

                PreparedStatement pst = con.prepareStatement(sql);

                ResultSet rs = pst.executeQuery();

                String statusStudent = "";
                int pending = 0;

                while (rs.next()){
                    statusStudent = rs.getString("status");
                    pending = rs.getInt("count");
                }
                
                System.out.println(statusStudent + " " + pending);
                
                if (statusStudent.equals("GOOD") && pending >= 3) status = false;
                if (statusStudent.equals("NORMAL") && pending >= 2) status = false;
                if (statusStudent.equals("BAD") && pending >= 1) status = false;
                
            } catch(Exception e) {
                e.printStackTrace();
            }
            
            if(status) {
                if(lbl_quantity.getText().equals("0")) {
                    JOptionPane.showMessageDialog(this, "Kh??ng t??m th???y s??ch");
                } else{
                    if(this.isAlreadyborrowd() == false){
                        if(borrowBook() == true){
                            JOptionPane.showMessageDialog(this, "Cho m?????n th??nh c??ng !!");
                            updateBookCount();
                        }else{
                            JOptionPane.showMessageDialog(this, "Cho m?????n th???t b???i !!");
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "B???n ?????c ???? m?????n cu???n s??ch n??y");
                    }
                }
            }
            else {
                JOptionPane.showMessageDialog(this, "B???n ?????c ???? m?????n v?????t qu?? s??? l???n");
            }
            
        } else {
            JOptionPane.showMessageDialog(this, "C???n nh???p ng??y tr??? sau ng??y m?????n");
        }
        
        

    }//GEN-LAST:event_buttonToBorrowActionPerformed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel1MouseClicked

    private void txtBookSearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtBookSearchMouseClicked
        if (searchBook.getItemSize() > 0) {
            menuBook.show(txtBookSearch, 0, txtBookSearch.getHeight());
        }
    }//GEN-LAST:event_txtBookSearchMouseClicked

    private void txtBookSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBookSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBookSearchActionPerformed

    private void txtBookSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBookSearchKeyReleased
        String text = txtBookSearch.getText().trim().toLowerCase();
        searchBook.setData(searchBook(text));
        if (searchBook.getItemSize() > 0) {
            //  * 2 top and bot border
            menuBook.show(txtBookSearch, 0, txtBookSearch.getHeight());
            menuBook.setPopupSize(menuBook.getWidth(), (searchBook.getItemSize() * 35) + 2);
        } else {
            menuBook.setVisible(false);
        }
    }//GEN-LAST:event_txtBookSearchKeyReleased

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

    private void txtBookSearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBookSearchFocusGained
        if(txtBookSearch.getText().equals("Nh???p t??n s??ch ...")){
            txtBookSearch.setText("");
            txtBookSearch.setForeground(new Color(255,255,255));
        }
    }//GEN-LAST:event_txtBookSearchFocusGained

    private void txtBookSearchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBookSearchFocusLost

        if (txtBookSearch.getText().equals("Nh???p t??n s??ch ...")) {
            
        }
        else if(!txtBookSearch.getText().equals("")){
            
            authorBox.removeAllItems();
            try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms", "root", "");
            java.sql.Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select author from book_details where available = 'yes' and book_name = '"
                                                + this.txtBookSearch.getText() + "'");
            while(rs.next()){
                String author = rs.getString("author");
                authorBox.addItem(author);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
            
            this.getBookDetails();
        }
        else if(txtBookSearch.getText().equals("")){
            txtBookSearch.setText("Nh???p t??n s??ch ...");
            txtBookSearch.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_txtBookSearchFocusLost

    private void txtStudentSearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStudentSearchFocusGained
        if(txtStudentSearch.getText().equals("Nh???p t??n b???n ?????c ...")){
            txtStudentSearch.setText("");
            txtStudentSearch.setForeground(new Color(255,255,255));
        }
    }//GEN-LAST:event_txtStudentSearchFocusGained

    private void txtStudentSearchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStudentSearchFocusLost
        if (txtStudentSearch.getText().equals("Nh???p t??n b???n ?????c ...")) {
            
        }
        else if(!txtStudentSearch.getText().equals("")){
            this.getStudentDetails();
        }
        else if(txtStudentSearch.getText().equals("")){
            txtStudentSearch.setText("Nh???p t??n b???n ?????c ...");
            txtStudentSearch.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_txtStudentSearchFocusLost

    private void authorBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_authorBoxMouseClicked
        
    }//GEN-LAST:event_authorBoxMouseClicked

    private void authorBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_authorBoxActionPerformed
        this.getBookDetails();
    }//GEN-LAST:event_authorBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> authorBox;
    private rojerusan.RSMaterialButtonCircle buttonToBorrow;
    private com.toedter.calendar.JDateChooser date_borrowDate;
    private com.toedter.calendar.JDateChooser date_dueDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lbl_author;
    private javax.swing.JLabel lbl_bookError;
    private javax.swing.JLabel lbl_bookId;
    private javax.swing.JLabel lbl_bookName;
    private javax.swing.JLabel lbl_major;
    private javax.swing.JLabel lbl_quantity;
    private javax.swing.JLabel lbl_status;
    private javax.swing.JLabel lbl_studentError1;
    private javax.swing.JLabel lbl_studentId;
    private javax.swing.JLabel lbl_studentName;
    private javax.swing.JPanel panel_main;
    private swing.MyTextField txtBookSearch;
    private swing.MyTextField txtStudentSearch;
    // End of variables declaration//GEN-END:variables
}
