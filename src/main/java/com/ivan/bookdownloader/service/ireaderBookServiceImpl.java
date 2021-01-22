package com.ivan.bookdownloader.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ivan.bookdownloader.utils.IreaderTool;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ireaderBookServiceImpl implements IreaderBookService {
    @Override
    public List<String> getResource(String url, String cookie) throws IOException {

        //获取链接中的bid参数
        String bid = IreaderTool.getParamFromUrl(url, "bid");
        List<String> htmls = new ArrayList<>();
        //获取章节信息
        Connection.Response data = Jsoup
                .connect("http://www.ireader.com/index.php?ca=Chapter.List&ajax=1&bid="+bid+"&page=1&pageSize=10000")
                .ignoreContentType(true)
                .execute();
        String s = IreaderTool.decodeUnicode(data.body());
        JSONObject object = JSON.parseObject(s);
        int size = object.getJSONArray("list").size();
        for (int i = 1; i < size; i++) {
            Document doc = Jsoup.connect("http://www.ireader.com/index.php?ca=Chapter.Content&bid="+bid+"&cid=" + i).header("Cookie", cookie).get();
            doc.head().select("link").remove();
            String fileContent = IreaderTool.getFileContent();
            doc.head().append("<style>"+fileContent+"</style>");
            doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            htmls.add(doc.toString());
            System.out.println("正在请求第" + i + "章内容。。。。");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return htmls;
    }
}
