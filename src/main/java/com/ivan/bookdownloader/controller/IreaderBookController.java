package com.ivan.bookdownloader.controller;

import com.ivan.bookdownloader.utils.IreaderTool;
import com.ivan.bookdownloader.service.IreaderBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@RestController
public class IreaderBookController {

    @Autowired
    private IreaderBookService bookService;

    @RequestMapping("/generatorPDF")
    public String test(String url, @RequestParam(required = false,defaultValue = "") String cookie) throws Exception {

        String uuid = UUID.randomUUID().toString();

        String rootPath = IreaderBookController.class.getClassLoader().getResource("").getPath();
        String pdfFilePath = rootPath+uuid+".pdf";

        List<String> resource = bookService.getResource(url,cookie);
        IreaderTool.html2pdf(resource, pdfFilePath);

        return uuid;
    }

    @RequestMapping("/download")
    public void download(String uuid, HttpServletResponse response) {
        String rootPath = IreaderBookController.class.getClassLoader().getResource("").getPath();
        String pdfFilePath = rootPath+uuid+".pdf";
        IreaderTool.download(pdfFilePath,response);
    }

}
