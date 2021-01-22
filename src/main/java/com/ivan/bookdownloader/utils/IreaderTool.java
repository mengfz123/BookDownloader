package com.ivan.bookdownloader.utils;

import com.itextpdf.text.pdf.BaseFont;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.List;

/**
 * 文件格式转换工具类
 *
 * @author lbj
 * <p>
 * 2015-10-8 上午10:52:22
 */
public class IreaderTool {


    /**
     * 将HTML转成PD格式的文件。html文件的格式比较严格
     *
     * @param pdfFilePath
     * @throws Exception
     */
    public static void html2pdf(List<String> htmls, String pdfFilePath) throws Exception {
        File file = new File(pdfFilePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStream os = new FileOutputStream(pdfFilePath);
        ITextRenderer renderer = new ITextRenderer();
        ITextFontResolver fontResolver = renderer.getFontResolver();
        fontResolver.addFont("c:/Windows/Fonts/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        renderer.setDocumentFromString(htmls.get(0));
        renderer.layout();
        renderer.createPDF(os, false);
        for (int i = 1; i < htmls.size(); i++) {
            renderer.setDocumentFromString(htmls.get(i));
            renderer.layout();
            renderer.writeNextDocument();
        }
        renderer.finishPDF();
        os.close();
        System.out.println("create pdf done!!");

    }

    public static String getFileContent()  {
        ClassLoader classLoader = IreaderTool.class.getClassLoader();
        InputStream resource = classLoader.getResourceAsStream("static/style.css");
        InputStreamReader reader = new InputStreamReader(resource);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String tmp="";
        StringBuffer content = new StringBuffer();
        while (true) {
            try {
                if (!((tmp = bufferedReader.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            content.append(tmp);
        }
        return content.toString();
    }

    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    /**
     * 获取url中的参数值
     * @param url
     * @param keyWord
     * @return
     */
    public static String getParamFromUrl(String url,String keyWord) {
        String retValue = "";
        try {
            final String charset = "utf-8";
            url = URLDecoder.decode(url, charset);
            if (url.indexOf('?') != -1) {
                final String contents = url.substring(url.indexOf('?') + 1);
                String[] keyValues = contents.split("&");
                for (int i = 0; i < keyValues.length; i++) {
                    String key = keyValues[i].substring(0, keyValues[i].indexOf("="));
                    String value = keyValues[i].substring(keyValues[i].indexOf("=") + 1);
                    if (key.equals(keyWord)) {
                        if (value != null || !"".equals(value.trim())) {
                            retValue = value;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retValue;
    }

    public static HttpServletResponse download(String path, HttpServletResponse response) {
        try {
            // path是指欲下载的文件的路径。
            File file = new File(path);
            // 取得文件名。
            String filename = file.getName();
            // 取得文件的后缀名。
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }

}
