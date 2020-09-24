package com.itheima.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.MemberService;
import com.itheima.service.ReportService;
import com.itheima.service.SetmealService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference
    private MemberService memberService;

    @Reference
    private SetmealService setmealService;

    @Reference
    private ReportService reportService;

    //会员数量折线图数据
    @RequestMapping("/getMemberReport")
    public Result getMemberReport() {
        //封装数据，返回页面
        HashMap<String, Object> map = new HashMap<>();
        //获得当前日期
        Calendar calendar = Calendar.getInstance();
        //计算过去12个月
        calendar.add(Calendar.MONTH, -12);//当前日期向前推进12个月
        List<String> months = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            calendar.add(Calendar.MONTH, 1);
            Date time = calendar.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM");
            String format = simpleDateFormat.format(time);
            months.add(format);
        }
        map.put("months", months);

        List<Integer> memberCount = memberService.findMemberCountByMonths(months);
        map.put("memberCount", memberCount);

        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS, map);
    }

    //套餐预约占比饼形图
    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport() {
        //使用模拟数据测试使用什么样的java对象转为饼形图所需的json数据格式
        HashMap<String, Object> data = new HashMap<>();

        try {
            List<Map<String, Object>> setmealCount = setmealService.findSetmealCount();
            data.put("setmealCount", setmealCount);

            List<String> setmealNames = new ArrayList<>();
            for (Map<String, Object> map : setmealCount) {
                String name = (String) map.get("name");//套餐名
                setmealNames.add(name);
            }
            data.put("setmealNames", setmealNames);

            return new Result(true, MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS, data);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL);

        }
    }

    //运营数据统计
    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData() {

        try {
            Map<String, Object> data = reportService.getBusinessReportData();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS, data);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    //导出运营数据
    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request,HttpServletResponse response) {

        try {
            Map<String, Object> result = reportService.getBusinessReportData();
            //取出返回结果数据，准备将报表数据写入到Excel文件中
            String reportDate = (String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");
            //获取模板文件在项目部署中的虚拟路径  File.separator 分隔符 根据系统来决定是/还是\。
            String filePath = request.getSession().getServletContext().getRealPath("template") + File.separator + "report_template.xlsx";
            //基于模板在内存中创建一个excel表格对象
            XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream(new File(filePath)));
            //读取第一个工作表
            XSSFSheet sheet = excel.getSheetAt(0);

            XSSFRow row = sheet.getRow(2);
            row.getCell(5).setCellValue(reportDate);//日期

            row = sheet.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);//新增会员数（本日）
            row.getCell(7).setCellValue(totalMember);//总会员数

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日到诊数

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周到诊数

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月到诊数

            int rowNum=12;
            for (Map map : hotSetmeal) {
                String name = (String) map.get("name");
                Long setmeal_count = (Long) map.get("setmeal_count");
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                row = sheet.getRow(rowNum++);
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(setmeal_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
            }
            //使用输出流进行表格下载，基于浏览器作为客户端下载
            OutputStream out = response.getOutputStream();
            //告诉浏览器是什么类型的文件 代表的是Excel文件类型
            response.setContentType("application/vnd.ms-excel");
            //设置响应头信息 指定以附件形式进行下载
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");
            excel.write(out);
            out.flush();
            out.close();
            excel.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }
    //导出运营数据到PDF文件并提供客户端下载
    @RequestMapping("/exportBusinessReport4PDF")
    public Result exportBusinessReport4PDF(HttpServletRequest request,HttpServletResponse response){
        try {
            //获得数据
            Map<String, Object> result = reportService.getBusinessReportData();
            //取出返回结果数据，准备将报表数据写入pdf
            List<Map>  hotSetmeal = (List<Map>) result.get("hotSetmeal");
            //动态获取pdf模板文件路径
            String jrxmlPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jrxml";
            String jasperPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jasper";

            //编译模板
          JasperCompileManager.compileReportToFile(jrxmlPath,jasperPath);

          //填充数据---使用JavaBean数据源方式填充
            JasperPrint jasperPrint = JasperFillManager.
                    fillReport(jasperPath, result,
                            new JRBeanCollectionDataSource(hotSetmeal));
            //创建输出流,写出到客户端
            OutputStream out = response.getOutputStream();
            response.setHeader("content-Disposition", "attachment;filename=report.pdf");
            response.setContentType("application/pdf");
            JasperExportManager.exportReportToPdfStream(jasperPrint,out);
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }

    }



}
