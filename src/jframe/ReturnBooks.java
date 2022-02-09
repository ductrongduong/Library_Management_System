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
 * @author PV
 */
public class ReturnBooks extends javax.swing.JPanel {

    /**
     * Creates new form ReturnBooks
     */
    java.util.Date dueDate;
    
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
    
    public ReturnBooks() {
        initComponents();
        this.returnDate.setDate(new java.util.Date(System.currentTimeMillis()));
        
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
    
    
    /**
     * to update status student
     * lated twice time -> bad
     * returned twice time -> normal
     */
    public void updateStatus() {
        int idStudent = Integer.parseInt(this.lbl_studentId.getText());
        try {
            Connection con = DBConnection.getConnection();
            String sql = "select * from borrow_book_detail where student_id = ? and (status = 'returned lated' or status = 'retunred') "
                    + "order by borrow_date asc";
            
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, idStudent);
            ResultSet rs = pst.executeQuery();
            
            String before = "";
            String lastest = "";
            
            while (rs.next()){
                
                before = lastest;
                lastest = rs.getString("status");
                
            }
            
            sql = "select * from student_detail where student_id = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, idStudent);
            rs = pst.executeQuery();
            String status = "";
            if (rs.next()) {
                status = rs.getString("status");
            }
            
            if (before.equals(lastest)) {
                String statusnow = "NORMAL";
                System.out.println(before + " " + lastest + " " + status);
                if (before.equals("returned lated") && status.equals("GOOD")){
                    statusnow = "NORMAL";
                }
                else if (before.equals("returned lated") && status.equals("NORMAL")) {
                    statusnow = "BAD";
                }
                else if (before.equals("returned") && status.equals("BAD")) {
                    statusnow = "NORMAL";
                }
                sql = "update student_detail set status = ? where student_id = ?";
                pst = con.prepareStatement(sql);
                pst.setString(1, statusnow);
                pst.setInt(2, idStudent);
                int rowCount = pst.executeUpdate();
                
            }
            
            
            
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
        
    
    
    public void getborrowBookDetails() {
        String[] nameid = txtStudentSearch.getText().split("-");
        String studentName = nameid[0].trim();
        int StudentId = Integer.parseInt(nameid[1].trim());
        
        String bookName = txtBookSearch.getText();
       // String studentName = txtStudentSearch.getText();
        String author = authorBox.getItemAt(authorBox.getSelectedIndex());
        try {
            Connection con = DBConnection.getConnection();
            String sql = "select * from borrow_book_detail where book_name = ? and student_name = ? and status = ? and author = ? and student_id = ?";
            
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, bookName);
            pst.setString(2, studentName);
            pst.setString(3, "pending");
            pst.setString(4, author);
            pst.setInt(5, StudentId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()){
                
                lbl_issueId.setText(rs.getString("id"));
                lbl_bookName.setText(rs.getString("book_name"));
                lbl_studentName.setText(rs.getString("student_name"));
                lbl_issueDate.setText(rs.getString("borrow_date"));
                lbl_dueDate.setText(rs.getString("due_date"));
                lbl_author.setText(rs.getString("author"));
                dueDate = rs.getDate("due_date");
                lbl_studentId.setText(rs.getString("student_id"));
                lbl_bookError.setText("");
                
            }
            else {
                lbl_bookError.setText("Không tìm thấy lần mượn");
                lbl_issueId.setText("");
                lbl_bookName.setText("");
                lbl_studentName.setText("");
                lbl_issueDate.setText("");
                lbl_dueDate.setText("");
                
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    
    }
    
    public boolean returnBook() {
        boolean isReturned = false;
        String bookName = txtBookSearch.getText();
        //String studentName = txtStudentSearch.getText();
        String[] nameid = txtStudentSearch.getText().split("-");
        String studentName = nameid[0].trim();
        int studentId = Integer.parseInt(nameid[1].trim());
        String status = "returned";
        java.util.Date uReturnDate = this.returnDate.getDate();
        
        Long l1 = uReturnDate.getTime();
        
        java.sql.Date sReturnDate = new java.sql.Date(l1);

        if(dueDate.before(returnDate.getDate())) status = "returned lated";
        
        try {
            Connection con = DBConnection.getConnection();
            String sql = "update borrow_book_detail set status = ?, return_date = ?, returnedLibrarian = ? where student_name = ? and book_name = ? and status = ? and student_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, status);
            pst.setDate(2, sReturnDate);
            pst.setString(3, LoginPage.user_name);
            pst.setString(4, studentName);
            pst.setString(5, bookName);
            pst.setString(6, "pending");
            pst.setInt(7, studentId);
            
            int rowCount = pst.executeUpdate();
            if (rowCount > 0) {
                isReturned = true;
            } else {
                isReturned = false;
            }
           
            
        }catch(Exception e) {
            e.printStackTrace();
        }
        return isReturned;
    }
    
    
    public void updateBookCount() {
        String bookName = txtBookSearch.getText();
        try {
            Connection con = DBConnection.getConnection();
            String sql = "update book_details set quantity  = quantity + 1 where book_name = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, bookName);
            
            int rowCount =  pst.executeUpdate();
            
            if(rowCount > 0) {
                
            }else {
                JOptionPane.showMessageDialog(this, "Không thể cập nhật");
            }
        } catch(Exception e) {
            e.printStackTrace();
    }
    }
    
    
    
    //*********************************************************************************************************//
    //// Methods for Search book
    
    
    /**
    * Lưu lại thông tin sách đã tìm kiếm vào search history để gợi ý các lần tìm kiếm tiếp theo
    * Chỉ lưu lại 6 lần tìm kiếm gần nhất
    * Tham số là tên sách đã click
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
    * Xóa sách khỏi search history
    * Tham số là tên sách được click
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
    * Thêm các sách tìm kiếm gần đây vào các mục gợi ý tìm kiếm
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
    * danh sách tất cả các sách vào listbook
    * Các sách được sắp theo thứ tự alphabet
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
    * Trả về các sách được gợi ý theo type release của người nhập
    * Số lượng tối đa sách gợi ý là 7
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
    * Clear danh sách các sách gợi ý
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
    * Kiểm tra sách có ở trong lịch sử tìm kiếm không
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

        panelMain = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lbl_bookError = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lbl_issueId = new javax.swing.JLabel();
        lbl_bookName = new javax.swing.JLabel();
        lbl_studentName = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lbl_issueDate = new javax.swing.JLabel();
        lbl_dueDate = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        lbl_author = new javax.swing.JLabel();
        lbl_studentId = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        rSMaterialButtonCircle3 = new rojerusan.RSMaterialButtonCircle();
        rSMaterialButtonCircle4 = new rojerusan.RSMaterialButtonCircle();
        txtBookSearch = new swing.MyTextField();
        txtStudentSearch = new swing.MyTextField();
        returnDate = new com.toedter.calendar.JDateChooser();
        jLabel15 = new javax.swing.JLabel();
        authorBox = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(102, 102, 102));
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        setMinimumSize(new java.awt.Dimension(1200, 770));
        setPreferredSize(new java.awt.Dimension(1308, 811));

        panelMain.setBackground(new java.awt.Color(51, 51, 51));
        panelMain.setForeground(new java.awt.Color(102, 102, 102));
        panelMain.setAlignmentX(0.0F);
        panelMain.setAlignmentY(0.0F);
        panelMain.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        panelMain.setMinimumSize(new java.awt.Dimension(1200, 770));
        panelMain.setPreferredSize(new java.awt.Dimension(1308, 811));
        panelMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI", 0, 20)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/library-2.png"))); // NOI18N
        panelMain.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 360, 520));

        jPanel1.setBackground(new java.awt.Color(0, 102, 153));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 25)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddNewBookIcons/icons8_Literature_100px_1.png"))); // NOI18N
        jLabel3.setText("Thông tin chi tiết");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 120, 320, 110));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 230, 330, 10));

        lbl_bookError.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        lbl_bookError.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(lbl_bookError, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 700, 290, 30));

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Tác giả :");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 450, 110, -1));

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Tên bạn đọc :");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 510, 140, -1));

        lbl_issueId.setFont(new java.awt.Font("Yu Gothic UI", 0, 20)); // NOI18N
        lbl_issueId.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(lbl_issueId, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 330, 190, 30));

        lbl_bookName.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lbl_bookName.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(lbl_bookName, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 390, 190, 30));

        lbl_studentName.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lbl_studentName.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(lbl_studentName, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 510, 190, 30));

        jLabel10.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Ngày mượn :");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 570, 150, -1));

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Hạn trả :");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 630, 110, -1));

        lbl_issueDate.setFont(new java.awt.Font("Yu Gothic UI", 0, 20)); // NOI18N
        lbl_issueDate.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(lbl_issueDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 570, 190, 30));

        lbl_dueDate.setFont(new java.awt.Font("Yu Gothic UI", 0, 20)); // NOI18N
        lbl_dueDate.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(lbl_dueDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 640, 190, 30));

        jLabel14.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Id Student :");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, 110, -1));

        jLabel17.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Tên sách :");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 390, 110, -1));

        lbl_author.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lbl_author.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(lbl_author, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 450, 190, 30));

        lbl_studentId.setFont(new java.awt.Font("Yu Gothic UI", 0, 20)); // NOI18N
        lbl_studentId.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(lbl_studentId, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 270, 190, 30));

        jLabel18.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Id :");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, 110, -1));

        panelMain.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 0, 430, 800));

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 25)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Trả sách");
        panelMain.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 160, 140, 30));

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

        panelMain.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 220, 320, 5));

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Tên sách :");
        panelMain.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 320, 180, 40));

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Tác giả :");
        panelMain.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 550, 130, 40));

        rSMaterialButtonCircle3.setBackground(new java.awt.Color(102, 102, 102));
        rSMaterialButtonCircle3.setText("Tìm kiếm");
        rSMaterialButtonCircle3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonCircle3ActionPerformed(evt);
            }
        });
        panelMain.add(rSMaterialButtonCircle3, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 610, 470, 60));

        rSMaterialButtonCircle4.setBackground(new java.awt.Color(102, 102, 102));
        rSMaterialButtonCircle4.setText("Trả sách");
        rSMaterialButtonCircle4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonCircle4ActionPerformed(evt);
            }
        });
        panelMain.add(rSMaterialButtonCircle4, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 680, 470, 60));

        txtBookSearch.setBackground(new java.awt.Color(51, 51, 51));
        txtBookSearch.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtBookSearch.setForeground(new java.awt.Color(153, 153, 153));
        txtBookSearch.setText("Nhập tên sách ...");
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
        panelMain.add(txtBookSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 310, 310, 40));

        txtStudentSearch.setBackground(new java.awt.Color(51, 51, 51));
        txtStudentSearch.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtStudentSearch.setForeground(new java.awt.Color(153, 153, 153));
        txtStudentSearch.setText("Nhập tên bạn đọc ...");
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
        panelMain.add(txtStudentSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 400, 310, 40));

        returnDate.setBackground(new java.awt.Color(51, 51, 51));
        returnDate.setDateFormatString("yyyy/MM/dd");
        returnDate.setFont(new java.awt.Font("Dialog", 0, 17)); // NOI18N
        panelMain.add(returnDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 480, 320, 40));

        jLabel15.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Tên bạn đọc :");
        panelMain.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 400, 130, 40));

        authorBox.setFont(new java.awt.Font("Dialog", 0, 17)); // NOI18N
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
        panelMain.add(authorBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 550, 320, 40));

        jLabel16.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Ngày trả :");
        panelMain.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 480, 130, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void rSMaterialButtonCircle3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonCircle3ActionPerformed
        this.getborrowBookDetails();

    }//GEN-LAST:event_rSMaterialButtonCircle3ActionPerformed

    private void rSMaterialButtonCircle4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonCircle4ActionPerformed
        if(returnBook() == true) {
            JOptionPane.showMessageDialog(this, "Trả sách thành công");
            this.updateBookCount();
            this.updateStatus();
        } else{
            JOptionPane.showMessageDialog(this, "Trả sách thất bại");
        }
    }//GEN-LAST:event_rSMaterialButtonCircle4ActionPerformed

    private void txtBookSearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBookSearchFocusGained
        if(txtBookSearch.getText().equals("Nhập tên sách ...")){
            txtBookSearch.setText("");
            txtBookSearch.setForeground(new Color(255,255,255));
        }
    }//GEN-LAST:event_txtBookSearchFocusGained

    private void txtBookSearchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBookSearchFocusLost
        if (txtBookSearch.getText().equals("Nhập tên sách ...")) {
            
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
            
            //this.getBookDetails();
        }
        else if(txtBookSearch.getText().equals("")){
            txtBookSearch.setText("Nhập tên sách ...");
            txtBookSearch.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_txtBookSearchFocusLost

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

    private void authorBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_authorBoxMouseClicked

    }//GEN-LAST:event_authorBoxMouseClicked

    private void authorBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_authorBoxActionPerformed
//        this.getBookDetails();
    }//GEN-LAST:event_authorBoxActionPerformed

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

    private void txtStudentSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStudentSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStudentSearchActionPerformed

    private void txtStudentSearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtStudentSearchMouseClicked
        if (searchStudent.getItemSize() > 0) {
            menuStudent.show(txtStudentSearch, 0, txtStudentSearch.getHeight());
        }
    }//GEN-LAST:event_txtStudentSearchMouseClicked

    private void txtStudentSearchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStudentSearchFocusLost
        if (txtStudentSearch.getText().equals("Nhập tên bạn đọc ...")) {

        }
        else if(!txtStudentSearch.getText().equals("")){
            //            this.getStudentDetails();
        }
        else if(txtStudentSearch.getText().equals("")){
            txtStudentSearch.setText("Nhập tên bạn đọc ...");
            txtStudentSearch.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_txtStudentSearchFocusLost

    private void txtStudentSearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStudentSearchFocusGained
        if(txtStudentSearch.getText().equals("Nhập tên bạn đọc ...")){
            txtStudentSearch.setText("");
            txtStudentSearch.setForeground(new Color(255,255,255));
        }
    }//GEN-LAST:event_txtStudentSearchFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> authorBox;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lbl_author;
    private javax.swing.JLabel lbl_bookError;
    private javax.swing.JLabel lbl_bookName;
    private javax.swing.JLabel lbl_dueDate;
    private javax.swing.JLabel lbl_issueDate;
    private javax.swing.JLabel lbl_issueId;
    private javax.swing.JLabel lbl_studentId;
    private javax.swing.JLabel lbl_studentName;
    private javax.swing.JPanel panelMain;
    private rojerusan.RSMaterialButtonCircle rSMaterialButtonCircle3;
    private rojerusan.RSMaterialButtonCircle rSMaterialButtonCircle4;
    private com.toedter.calendar.JDateChooser returnDate;
    private swing.MyTextField txtBookSearch;
    private swing.MyTextField txtStudentSearch;
    // End of variables declaration//GEN-END:variables
}
