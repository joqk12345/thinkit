package org.thinkit.decoder;
	import java.io.IOException;
	import java.io.StringReader;
	import java.util.List;
	import org.jdom2.Document;
	import org.jdom2.Element;
	import org.jdom2.JDOMException;
	import org.jdom2.input.SAXBuilder;
	import org.xml.sax.InputSource;
public class AnalysisXML {
	    public List xmlElements(String xmlDoc) {
	        //创建一个新的字符串
	        StringReader read = new StringReader(xmlDoc);
	        //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
	        InputSource source = new InputSource(read);
	        //创建一个新的SAXBuilder
	        SAXBuilder sb = new SAXBuilder();
	        try {
	            //通过输入源构造一个Document
	            Document doc = sb.build(source);
	            //取的根元素
	            Element root = doc.getRootElement();
//	            System.out.println(root.getName());//输出根元素的名称（测试）
	            
	            Xmlchild(root);
	            //得到根元素所有子元素的集合
//	            List jiedian = null; 
//	            List fjiedian = root.getChildren();	            
//	            List zjiedian = null;
//	            List aa;
	            //获得XML中的命名空间（XML中未定义可不写）
//	            Namespace ns = root.getNamespace();
//	            Element et = null;
//	            Element et1 = null;
//	            for (int m=0;m<fjiedian.size();m++){
//	            	et1 = (Element) fjiedian.get(m);
//	            	jiedian =et1.getChildren();
//	            for(int i=0;i<jiedian.size();i++){
//	                et = (Element) jiedian.get(i);//循环依次得到子元素
//	                zjiedian = et.getChildren();
//	                for(int j=0;j<zjiedian.size();j++){
//	                    Element xet = (Element) zjiedian.get(j);
//	                    System.out.println(xet.getName()+":"+xet.getText()+ " ");
//	                    aa = xet.getChildren();
//	                    if (aa.size() == 0)
//	                    {System.out.println("yes" );}
//	                }
////	                System.out.println(et.getChild("users_id",ns).getText());
////	                System.out.println(et.getChild("users_address",ns).getText());
//	            }
//	            System.out.println(fjiedian.size()+" "+jiedian.size()+ " " +zjiedian.size());
//	            }
	           

	        } catch (JDOMException e) {
	            // TODO 自动生成 catch 块
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO 自动生成 catch 块
	            e.printStackTrace();
	        }
	        return null;
	    }
	    
	    public void Xmlchild (Element father){
	    	List jiedian = father.getChildren();
	    	if(jiedian.size() == 0){
	    		System.out.println(father.getName()+":"+father.getText()+ " ");
	    	}
	    	else{
	    		System.out.println(father.getName());
	    		for(int i=0;i<jiedian.size();i++)
	    		{
	    			Element et = (Element) jiedian.get(i);
	    			Xmlchild(et);
	    			
	    		}
	    		System.out.println();
	    	}
	    }

	    public static void main(String[] args){
	    	AnalysisXML doc = new AnalysisXML();
	    	String xml1 = "<?xml version=\"1.0\" encoding=\"gb2312\"?><results><code>0</code><description> Successful speech recognition !</description><result><no>0</no><name>啊</name><confidence>0</confidence><phoneme>啊</phoneme><time>11.18 11.26</time></result><keywords></keywords></results>	";
	    	String xml = "<?xml version=\"1.0\" encoding=\"gb2312\"?><results><keywords><kw><no>1</no><keyword>可以</keyword><confidence>-0.026</confidence><phoneme>可以</phoneme><time>0.320 0.540</time></kw><kw><no>2</no><keyword>一下</keyword><confidence>-0.105</confidence><phoneme>一下</phoneme><time>0.720 0.920</time></kw></keywords></results>";
	        doc.xmlElements(xml);
	    }
}

