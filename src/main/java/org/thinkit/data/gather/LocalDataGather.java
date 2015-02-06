package org.thinkit.data.gather;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.thinkit.data.vo.FileAbstract;

public class LocalDataGather implements GatherInteface {
	
	
	public static List getFileList(String aduioPath,String suffix){
		  //    在此目录中找文件      
      String baseDIR = aduioPath;      
      //    找扩展名为txt的文件      
      String fileName = "*."+suffix;      
      List resultList = new ArrayList();
      String[] s = null;
      findFiles(baseDIR, fileName,resultList);      
      if (resultList.size() == 0) {     
          System.out.println("No File Fount.");     
      } else {     
          for (int i = 0; i < resultList.size(); i++) {     
              System.out.println(resultList.get(i));//显示查找结果。
          }     
      }     
      return resultList;
      
	}
	
	  /**   
   * 递归查找文件   
   * @param baseDirName  查找的文件夹路径   
   * @param targetFileName  需要查找的文件名   
   * @param fileList  查找到的文件集合   
   */    
	 public static void findFiles(String baseDirName, String targetFileName, List fileList) {     
	        
	        File baseDir = new File(baseDirName);       // 创建一个File对象   
	        if (!baseDir.exists() || !baseDir.isDirectory()) {  // 判断目录是否存在   
	            System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");  
	        }  
	        String tempName = null;     
	        //判断目录是否存在      
	        File tempFile;  
	        File[] files = baseDir.listFiles();  
	        for (int i = 0; i < files.length; i++) {  
	            tempFile = files[i];  
	            if(tempFile.isDirectory()){  
	                findFiles(tempFile.getAbsolutePath(), targetFileName, fileList);  
	            }else if(tempFile.isFile()){  
	                tempName = tempFile.getName();  
	                if(wildcardMatch(targetFileName, tempName)){  
	                    // 匹配成功，将文件名添加到结果集   
	                    fileList.add(tempFile.getAbsoluteFile());  
	                }  
	            }  
	        }  
	    }   
	  /**   
	     * 通配符匹配   
	     * @param pattern    通配符模式   
	     * @param str    待匹配的字符串   
	     * @return    匹配成功则返回true，否则返回false   
	     */    
	 private static boolean wildcardMatch(String pattern, String str) {     
	        int patternLength = pattern.length();     
	        int strLength = str.length();     
	        int strIndex = 0;     
	        char ch;     
	        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {     
	            ch = pattern.charAt(patternIndex);     
	            if (ch == '*') {     
	                //通配符星号*表示可以匹配任意多个字符      
	                while (strIndex < strLength) {     
	                    if (wildcardMatch(pattern.substring(patternIndex + 1),     
	                            str.substring(strIndex))) {     
	                        return true;     
	                    }     
	                    strIndex++;     
	                }     
	            } else if (ch == '?') {     
	                //通配符问号?表示匹配任意一个字符      
	                strIndex++;     
	                if (strIndex > strLength) {     
	                    //表示str中已经没有字符匹配?了。      
	                    return false;     
	                }     
	            } else {     
	                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {     
	                    return false;     
	                }     
	                strIndex++;     
	            }     
	        }     
	        return (strIndex == strLength);     
	    }   
		
	public static void main(String args[]){
		List s  = getFileList("D://thinkit","V3");
		System.out.println("扫描数据 进行输入……");
		
		for(int i=0;i<s.size();i++){
			System.out.println(s.get(i));
		}
		
		System.out.println("开始进行数据导入……");
	}
	
	

	public List<FileAbstract> getFileAbstractlist(URI u) {
		// TODO Auto-generated method stub
		return null;
	}

}
