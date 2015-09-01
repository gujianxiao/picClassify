package picClassify;
import picClassify.databean;
import java.awt.Container;  
import java.awt.FlowLayout;
import java.awt.List;
import java.awt.TextField;

import java.awt.GridLayout;  
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;  
import javax.swing.JPanel;
import javax.swing.JScrollPane;  
import javax.swing.JTextArea;  

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.iptc.IptcReader;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;



public class picClassify {

    public static void FileCopy(String inputPath,String outputPath) throws IOException{
    	File inputFile;  
    	File outputFile;  
    	InputStream inputStream;  
    	OutputStream outputStream;  
        inputFile=new File(inputPath);  
        outputFile=new File(outputPath);  
        inputStream=new FileInputStream(inputFile);  
        outputStream=new FileOutputStream(outputFile);  

        byte b[]=new byte[(int)inputFile.length()];  
        inputStream.read(b);       //一次性读入  
        outputStream.write(b);   //一次性写入  
//      inputStream.close();  
//      outputStream.close();  
    }  
    //边读边写  
    public void copy2() throws IOException{  
        int temp=0;  
        File inputFile;  
        File outputFile;  
        InputStream inputStream = null;  
        OutputStream outputStream = null;  
        while((temp=inputStream.read())!=-1){  
            outputStream.write(temp);  
        }  
        inputStream.close();  
        outputStream.close();  
    }  

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	    final JFileChooser fileChooser = new JFileChooser(".");
		JFrame frame=new JFrame("PicClassify");
		final JTextArea picDir=new JTextArea();
		JButton button=new JButton("选择文件夹");
 	    final Container content = frame.getContentPane(); 
 	    MouseListener ml=new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(e.getClickCount()==2){
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            	 fileChooser.setDialogTitle("打开文件夹");
	            	 int ret = fileChooser.showOpenDialog(null);
	            	 if (ret == JFileChooser.APPROVE_OPTION) {
	            		//文件夹路径
	            		 String absolutepath=fileChooser.getSelectedFile().getAbsolutePath();
	            		System.out.println(absolutepath);
	            		picDir.setText(absolutepath);
	            		}
	                String dir= picDir.getText();
	                File d=new File(dir);
	        		File list[] = d.listFiles();//file list
                   databean choose=new databean();
                   Connection conn = null;
                   ResultSet rs=null;
                   int count=0;
                   int traincount=0;
                   choose.setDB("train");//set database name
                   conn=choose.getConn();//connect to database
                   String location=fileChooser.getSelectedFile().getName();
                   String sql="select  * from trainandtest where location='"+location+"' and time<'2015:07:30 24:00:00'  and type!=100 order by time";
                   rs=choose.executeSQL(sql);
                   try {
					rs.last();
					count=rs.getRow();
					traincount=(count*5)/6;
					rs.beforeFirst();
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
                   
                   int i=0;
                   int k=0;
                   File trainf = new File("e:\\pic\\train\\type.txt");
                   File testf = new File("e:\\pic\\test\\type.txt");
                   try{
                   trainf.createNewFile();
                   testf.createNewFile();
                	   while(i<traincount){
                		   rs.next();
                		   String oldFilePath=dir;//get dir
                		   String picName=rs.getString(3);//get pic name
                		   oldFilePath+="\\";               		   
                		   oldFilePath+=picName;  // get pic full dir
                		   String newFilePath="e:\\pic\\train\\";
                		   int type=rs.getInt(11);               		   
                		       newFilePath+=type+"\\"+picName;//build new pic dir
                			   FileCopy(oldFilePath,newFilePath);//copy file
                			   BufferedReader input1 = new BufferedReader(new FileReader(trainf));//read f

                				String n;
                		        String sortd = null;
                		        int j=0;
                				while((n = input1.readLine())!=null){
                					if(j==0){
                						sortd=n+"\r\n";
                						j++;
                						continue;                						
                					}               					
                					sortd += n;//获取原字符串
                					sortd+="\r\n";
                				}
                				input1.close();	
                				BufferedWriter output = new BufferedWriter(new FileWriter(trainf));
                				if(j==0){
                					sortd=(newFilePath+" "+type+"\r\n");
                				}
                				else sortd+=(newFilePath+" "+type+"\r\n");//new info for write 
                				output.write(sortd);//write into f
                				output.close();
                				i++;
                		   }
                	   while(i<count){
                		   rs.next();
                		   String oldFilePath=dir;//get dir
                		   String picName=rs.getString(3);
                		   oldFilePath+="\\";               		   
                		   oldFilePath+=picName;  // get pic dir
                		   String newFilePath="e:\\pic\\test\\";
                		   int type=rs.getInt(11);
                		       newFilePath+=type+"\\"+picName;//build new pic dir
                			   FileCopy(oldFilePath,newFilePath);//copy file
                			   BufferedReader input1 = new BufferedReader(new FileReader(testf));//read f
                				String n;
                		        String sortd=null;
                		        int j=0;
                				while((n = input1.readLine())!=null){
                					if(j==0){
                						sortd=n+"\r\n";
                						j++;
                						continue;                						
                					}               					
                					sortd += n;//获取原字符串
                					sortd+="\r\n";
                				}
                				input1.close();	
                				BufferedWriter output = new BufferedWriter(new FileWriter(testf));
                				if(j==0){
                					sortd=(newFilePath+" "+type+"\r\n");
                				}
                				else sortd+=(newFilePath+" "+type+"\r\n");//new info for write 
                				output.write(sortd);//write into f
                				output.close();
                				i++;
                				k++;
                		   }
                   }
                   catch(Exception e1){
                	   e1.printStackTrace();
                   }
                   System.out.println("finish dealing with"+location);
                   System.out.println("the result set  count is:"+i+"the list of rs is:"+count+",the traincount is:"+traincount+",the test set is:"+k);
                   
				}
			}

					
					
					
				
			

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
 	    	
 	    };
 	   button.addMouseListener(ml);
	   content.add(button);
	   JPanel txt=new JPanel();
	   picDir.setColumns(15);
	   picDir.setRows(1);
       txt.add(picDir); 
	   content.add(txt);
	   content.setLayout(new FlowLayout(FlowLayout.LEFT)); 
	   frame.setSize(500,600);
	   frame.setVisible(true);
	   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
 	    
	}

}
