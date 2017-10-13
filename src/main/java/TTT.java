import com.saxonica.objectweb.asm.util.CheckMethodAdapter;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import net.sf.saxon.dom.DOMSender;
import net.sf.saxon.om.MutableNodeInfo;
import net.sf.saxon.om.TreeModel;
import net.sf.saxon.s9api.*;
import net.sf.saxon.tree.linked.DocumentImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.tidy5.Tidy;
import org.w3c.tidy5.premium.XQPCleaner;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.lang.invoke.MethodHandles;
import java.security.Key;
import java.security.Provider;
import java.security.Security;
import java.util.Iterator;

/**
 * Created by xujl-mac on 2017/9/11.
 */
public class TTT {
    private static final String PUBLIC_KEY = "308201B73082012C06072A8648CE3804013082011F02818100FD7F53811D75122952DF4A9C2EECE4E7F611B7523CEF4400C31E3F80B6512669455D402251FB593D8D58FABFC5F5BA30F6CB9B556CD7813B801D346FF26660B76B9950A5A49F9FE8047B1022C24FBBA9D7FEB7C61BF83B57E7C6A8A6150F04FB83F6D3C51EC3023554135A169132F675F3AE2B61D72AEFF22203199DD14801C70215009760508F15230BCCB292B982A2EB840BF0581CF502818100F7E1A085D69B3DDECBBCAB5C36B857B97994AFBBFA3AEA82F9574C0B3D0782675159578EBAD4594FE67107108180B449167123E84C281613B7CF09328CC8A6E13C167A8B547C8D28E0A3AE1E2BB3A675916EA37F0BFA213562F1FB627A01243BCCA4F1BEA8519089A883DFE15AE59F06928B665E807B552564014C3BFECF492A038184000281806CA067E469754A2F98D08965D35AB41252974F69FCD260152CFB58F94D8B956D6A1A91CC213A11107301DD37C85B383DDA409EC067D2A8C4BE6651020A69AFD533630172D6F96F928667D7705D46E56D364E8E967002A7D864BACB02225F52B271BA1F6522D98FF8299C6273B24BC41202C8857897E46B40FD7624A9CB0CC9E8";

    static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    static PrintWriter pw = new PrintWriter(new Writer() {

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            LOG.warn(cbuf.toString());

        }

        @Override
        public void flush() throws IOException {
            // TODO Auto-generated method stub

        }

        @Override
        public void close() throws IOException {
            // TODO Auto-generated method stub

        }
    });
    public static void mainSecret(String[] args) throws Exception {

        FileReader fr =new FileReader("/Users/xujl-mac/IdeaProjects/tt/src/main/resources/saxon-license.lic") ;
        BufferedReader br =  new BufferedReader(fr);
        StringWriter sw =new StringWriter();
        String l=null;
        Provider[] providers = Security.getProviders();
//        for (Provider provider:providers) {
//            System.out.println(provider.getName());
//            for (String key: provider.stringPropertyNames())
//                System.out.println("\t" + key + "\t" + provider.getProperty(key));
//        }
        while(( l= br.readLine()) != null) {
            sw.write(l+"\n");
        }
        PBEKeySpec dks = new PBEKeySpec(PUBLIC_KEY.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        Key secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        byte[] salt = {     (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
                (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03};
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 1);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey,paramSpec);

        byte[] bytes = cipher.doFinal(sw.toString().getBytes());
        BASE64Encoder be = new BASE64Encoder();
        String enc="uASlbcf/aCbmVLvm46jWzLgi7TjFxBTdnLHcs+qBSzuD8nhtyrkjWXqN2GSLOdnvDaQ+f45Ithx1\n" +
                "WDMElfob7F8UbUjh5sCPiGXnl9Mr/OZfaogoGQOqFrp+wTqgk1Pu9l+CX6rnVOYjzrljqvkN4Y5t\n" +
                "Pk1s5UgkFgTKpilV0GieLOa7jdD4NJe/vPMCUsssl1vCDpzs6zCMA0bmztTN3210BKtFVIfobaWu\n" +
                "1CYpWalmqQr1O9DhDm30YczhVlZiCUm9DLfJgQkQrjzKrYiRPXCwWc172l0y8rIQRE/pI6CIOido\n" +
                "eln2P8OCc8Xs7tzs";
        String encode = be.encode(bytes);
        System.out.println(encode);
        decode(enc);
    }
    private  static String decode(String b64) throws Exception {
        byte[] decode = Base64.decode(b64);
        PBEKeySpec dks = new PBEKeySpec(PUBLIC_KEY.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        Key secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        byte[] salt = {     (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
                (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03};
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 1);
        cipher.init(Cipher.DECRYPT_MODE, secretKey,paramSpec);

        byte[] bytes = cipher.doFinal(decode);
        String res = new String(bytes);
        return res;
    }
    public static void main(String[] s) {

        try {
            Processor PROCESSOR = new Processor(true);
//            PROCESSOR.getUnderlyingConfiguration().getParseOptions().addParserFeature("http://xml.org/sax/features/namespaces",false);
//            String html = Tidy.me(new FileReader("/Users/xujl-mac/IdeaProjects/tt/src/main/resources/simple.xml")).asString();
            Tidy me = Tidy.me();
            DOMSender.ignoreNoBindingNamespaces();
//            Document document = Tidy.me().parseDOM(new FileReader("/Users/xujl-mac/IdeaProjects/tt/src/main/resources/simple.xml"), null);
            DocumentBuilder builder = PROCESSOR.newDocumentBuilder();
            DOMSource domSource = Tidy.toDOMSource(new FileReader("/Users/xujl-mac/IdeaProjects/tt/src/main/resources/simple.xml"));
            builder.setTreeModel(TreeModel.LINKED_TREE);
            XdmNode xdmItems = builder.build(domSource);
            XQueryCompiler xQueryCompiler = PROCESSOR.newXQueryCompiler();
            xQueryCompiler.setUpdatingEnabled(true);
            XQueryExecutable queryExecutable = xQueryCompiler.compile("replace value of node  //body with \"Goodbye\" ");
            LOG.info(xdmItems.toString());
            XQueryEvaluator evaluator = queryExecutable.load();

//            evaluator.setDestination(new SAXDestination()Destination());
            evaluator.setContextItem(xdmItems);
            evaluator.run();
            LOG.info("========================");
            Iterator<XdmNode> updatedDocuments = evaluator.getUpdatedDocuments();
            for (Iterator<XdmNode> it = updatedDocuments; it.hasNext(); ) {
                XdmNode xn = it.next();


                LOG.info(xn.toString());

            }
//            String asString = Tidy.me().asString(document);
//            DOMSource domSource = Tidy.toDOMSource(new FileReader("/Users/xujl-mac/IdeaProjects/tt/src/main/resources/simple.xml"));
//			 System.out.println(html);
//            System.out.println(XQPCleaner.clearDomSource(domSource,  "&replace value of node  //body with \"Goodbye\" "));
//            System.out.println(XQPCleaner.clear(asString,  "&replace value of node  //body with \"Goodbye\" ","-//p","-//script"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void xpath() {
        try {


            Processor PROCESSOR = new Processor(true);

            XPathCompiler xPathCompiler = PROCESSOR.newXPathCompiler();
            XPathExecutable executable = xPathCompiler.compile("//* intersect //title");
            DocumentBuilder builder = PROCESSOR.newDocumentBuilder();
            builder.setTreeModel(TreeModel.LINKED_TREE);
            XdmNode xdmItems = builder.build(new File("/Users/xujl-mac/IdeaProjects/tt/src/main/resources/simple.xml"));
            System.out.println(xdmItems.toString());
            XPathSelector selector = executable.load();
            selector.setContextItem(xdmItems);

            for (XdmItem xdmItem : selector) {

                LOG.info(xdmItem.toString());
            }
            System.out.println(xdmItems.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void xquery() {
        try {
            DocumentImpl di = new DocumentImpl();
//            Maintenance

            Processor PROCESSOR = new Processor(true);
            XQueryCompiler xQueryCompiler = PROCESSOR.newXQueryCompiler();
            xQueryCompiler.setUpdatingEnabled(true);
//            XQueryExecutable queryExecutable = xQueryCompiler.compile("insert node <year>2005</year>\n" +
//                    "    after  //head");
//            XQueryExecutable queryExecutable = xQueryCompiler.compile("delete nodes //head");
            XQueryExecutable queryExecutable = xQueryCompiler.compile("replace value of node  //head with \"Goodbye\" ");
            DocumentBuilder builder = PROCESSOR.newDocumentBuilder();
            builder.setTreeModel(TreeModel.LINKED_TREE);
            XdmNode xdmItems = builder.build(new File("/Users/xujl-mac/IdeaProjects/tt/src/main/resources/simple.xml"));
            queryExecutable. explain(new XdmDestination());
            LOG.info(xdmItems.toString());
            XQueryEvaluator evaluator = queryExecutable.load();

//            evaluator.setDestination(new SAXDestination()Destination());
            evaluator.setContextItem(xdmItems);
            evaluator.run();
            LOG.info("========================");
            Iterator<XdmNode> updatedDocuments = evaluator.getUpdatedDocuments();
            for (Iterator<XdmNode> it = updatedDocuments; it.hasNext(); ) {
                XdmNode xn = it.next();


                LOG.info(xn.toString());

            }
//            LOG.info(xdmItems.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
