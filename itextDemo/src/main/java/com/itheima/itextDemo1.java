package com.itheima;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class itextDemo1 {
    public static void main(String[] args) {


        try {
            Document document = new Document();
            PdfWriter.getInstance(document,new FileOutputStream("D:\\test.pdf"));
            document.open();
            document.add(new Paragraph("hello,kukkld"));
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
