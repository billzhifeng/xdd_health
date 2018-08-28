package com.xueduoduo.health.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.java.common.base.BaseResp;
import com.github.java.common.utils.JavaAssert;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.GenderType;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.enums.UserRoleType;
import com.xueduoduo.health.domain.user.User;
import com.xueduoduo.health.domain.user.UserRepository;

/**
 * @author wangzhifeng
 * @date 2018年8月21日 下午5:30:25
 */
@RestController
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Value("${file.upload.path}")
    private String              fileUploadPath;
    @Value("${file.upload.bakpath}")
    private String              bakpathUploadPath;
    @Autowired
    private UserRepository      userRepository;

    @Value("${spring.profiles.active}")
    private String              springProfilesActive;

    @RequestMapping(value = "/file/upload", method = RequestMethod.POST)
    public BaseResp upload(@RequestParam(value = "file") MultipartFile file) {

        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        if (!file.isEmpty()) {
            try {
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                String fileName = file.getOriginalFilename();
                logger.info("上传文件名：{}", fileName);
                // 获取文件的后缀名
                String suffixName = fileName.substring(fileName.lastIndexOf("."));
                //
                String filePath = fileUploadPath + uuid + suffixName;
                String returnUrl = uuid + suffixName;
                File dest = new File(filePath);
                // 检测是否存在目录
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }
                file.transferTo(dest);
                if ("dev".equals(springProfilesActive)) {
                    resp.setData("/files/" + returnUrl);
                } else {
                    resp.setData("/data/heartTest/files/" + returnUrl);
                }
            } catch (FileNotFoundException e) {
                logger.error("文件上传失败", e);
                resp = BaseResp.buildFailResp("上传失败", BaseResp.class);
            } catch (IOException e) {
                logger.error("文件上传失败", e);
                resp = BaseResp.buildFailResp("上传失败", BaseResp.class);
            }
        }
        return resp;
    }

    @RequestMapping(value = "/admin/account/uploadTeachers", method = RequestMethod.POST)
    public BaseResp uploadTeacher(@RequestParam(value = "file") MultipartFile file) {

        logger.info("EXCEL上传教师列表");
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);

        try {
            JavaAssert.isTrue(null != file, ReturnCode.PARAM_ILLEGLE, "教师EXCEL文件不存在", HealthException.class);
            XSSFWorkbook xwb = new XSSFWorkbook(file.getInputStream());
            //循环工作表sheet
            int numberOfSheets = xwb.getNumberOfSheets();
            //CHECK
            for (int numSheet = 0; numSheet < numberOfSheets; numSheet++) {
                XSSFSheet xSheet = xwb.getSheetAt(numSheet);
                JavaAssert.isTrue(null != xSheet, ReturnCode.PARAM_ILLEGLE, "教师EXCEL文件中空白", HealthException.class);

                //循环行row
                int totalRowNums = xSheet.getLastRowNum();
                for (int numRow = 0; numRow <= totalRowNums; numRow++) {
                    if (numRow == 0) {
                        continue;
                    }
                    XSSFRow xRow = xSheet.getRow(numRow);
                    if (xRow == null) {
                        logger.error("教师文件中存在空白行,文件名:{}", file.getOriginalFilename());
                        throw new HealthException(ReturnCode.PARAM_ILLEGLE,
                                "教师问文件存在空白行,文件名：" + file.getOriginalFilename());
                    }
                    //循环列cell
                    int totalCellNums = xRow.getLastCellNum();

                    for (int numCell = 0; numCell < totalCellNums; numCell++) {
                        XSSFCell cell = xRow.getCell(numCell);
                        JavaAssert.isTrue(null != cell, ReturnCode.PARAM_ILLEGLE, "教师文件存在空白列", HealthException.class);
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_NUMERIC:
                                double d = cell.getNumericCellValue();
                                logger.info("文件, 第{}行,第{}列，值：{}", numRow + 1, numCell + 1, d);
                                break;
                            case Cell.CELL_TYPE_STRING:
                                String value = cell.getStringCellValue();
                                logger.info("文件, 第{}行,第{}列，值：{}", numRow + 1, numCell + 1, value);
                                break;
                            default:
                                break;
                        }

                    }
                }
            }

            //save
            List<User> teachers = new ArrayList<User>();
            for (int numSheet = 0; numSheet < numberOfSheets; numSheet++) {
                XSSFSheet xSheet = xwb.getSheetAt(numSheet);
                JavaAssert.isTrue(null != xSheet, ReturnCode.PARAM_ILLEGLE, "教师EXCEL文件中空白", HealthException.class);

                //循环行row
                int totalRowNums = xSheet.getLastRowNum();
                for (int numRow = 0; numRow <= totalRowNums; numRow++) {
                    if (numRow == 0) {
                        continue;
                    }
                    XSSFRow xRow = xSheet.getRow(numRow);
                    if (xRow == null) {
                        logger.error("教师文件中存在空白行,文件名:{}", file.getOriginalFilename());
                        throw new HealthException(ReturnCode.PARAM_ILLEGLE,
                                "教师文件存在空白行,文件名：" + file.getOriginalFilename());
                    }
                    //循环列cell
                    int totalCellNums = xRow.getLastCellNum();

                    User t = new User();
                    for (int numCell = 0; numCell < totalCellNums; numCell++) {
                        XSSFCell cell = xRow.getCell(numCell);
                        JavaAssert.isTrue(null != cell, ReturnCode.PARAM_ILLEGLE, "教师文件存在空白列", HealthException.class);
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_NUMERIC:
                                Double d = cell.getNumericCellValue();
                                DecimalFormat df = new DecimalFormat("0");
                                String str = df.format(d);
                                if (3 == numCell) {
                                    t.setPhone(str);
                                }
                                if (5 == numCell) {
                                    t.setPassword(str);
                                }
                                break;
                            case Cell.CELL_TYPE_STRING:
                                String value = cell.getStringCellValue();
                                if (0 == numCell) {
                                    t.setAccountNo(value);
                                }
                                if (1 == numCell) {
                                    t.setUserName(value);
                                }
                                if (2 == numCell) {
                                    if ("班主任".equals(value)) {
                                        t.setPosition(UserRoleType.CLASS_HEADER.name());
                                        t.setRole(UserRoleType.CLASS_HEADER.name());
                                    } else {
                                        t.setPosition(UserRoleType.TEACHER.name());
                                        t.setRole(UserRoleType.TEACHER.name());
                                    }

                                }
                                if (4 == numCell) {
                                    if ("男".equals(value)) {
                                        t.setGender(GenderType.MALE.name());
                                    } else {
                                        t.setGender(GenderType.FEMALE.name());
                                    }
                                }
                                if (5 == numCell) {
                                    t.setPassword(value);
                                }
                                break;
                            default:
                                break;
                        }

                    }

                    userRepository.saveUser(t);
                    teachers.add(t);
                }
            }
            logger.info("成功上传完成 teachers:{}", teachers);
            resp.setData(teachers.size());
        } catch (HealthException e) {
            logger.error("教师问卷上传异常", e);
            resp = BaseResp.buildFailResp("教师文件上传异常." + e.getReturnMsg(), BaseResp.class);

        } catch (Exception e) {
            logger.warn("EXCEL上传教师列表,文件格式不对或出错:{}", file);
            resp = BaseResp.buildFailResp(e, "EXCEL上传教师列表,文件格式不对或出错", BaseResp.class);
        }

        return resp;
    }

    /**
     * 学生
     * 
     * @param file
     * @return
     */
    @RequestMapping(value = "/admin/account/uploadStudents", method = RequestMethod.POST)
    public BaseResp uploadStudents(@RequestParam(value = "file") MultipartFile file) {

        logger.info("EXCEL上传学生列表");
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);

        try {
            JavaAssert.isTrue(null != file, ReturnCode.PARAM_ILLEGLE, "学生EXCEL文件不存在", HealthException.class);
            XSSFWorkbook xwb = new XSSFWorkbook(file.getInputStream());
            //循环工作表sheet
            int numberOfSheets = xwb.getNumberOfSheets();
            //CHECK
            for (int numSheet = 0; numSheet < numberOfSheets; numSheet++) {
                XSSFSheet xSheet = xwb.getSheetAt(numSheet);
                JavaAssert.isTrue(null != xSheet, ReturnCode.PARAM_ILLEGLE, "学生EXCEL文件中空白", HealthException.class);

                //循环行row
                int totalRowNums = xSheet.getLastRowNum();
                for (int numRow = 0; numRow <= totalRowNums; numRow++) {
                    if (numRow == 0) {
                        continue;
                    }
                    XSSFRow xRow = xSheet.getRow(numRow);
                    if (xRow == null) {
                        logger.error("学生文件中存在空白行,文件名:{}", file.getOriginalFilename());
                        throw new HealthException(ReturnCode.PARAM_ILLEGLE,
                                "学生文件存在空白行,文件名：" + file.getOriginalFilename());
                    }
                    //循环列cell
                    int totalCellNums = xRow.getLastCellNum();

                    for (int numCell = 0; numCell < totalCellNums; numCell++) {
                        XSSFCell cell = xRow.getCell(numCell);
                        JavaAssert.isTrue(null != cell, ReturnCode.PARAM_ILLEGLE, "学生文件存在空白列", HealthException.class);
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_NUMERIC:
                                double d = cell.getNumericCellValue();
                                logger.info("文件, 第{}行,第{}列，值：{}", numRow + 1, numCell + 1, d);
                                break;
                            case Cell.CELL_TYPE_STRING:
                                String value = cell.getStringCellValue();
                                logger.info("文件, 第{}行,第{}列，值：{}", numRow + 1, numCell + 1, value);
                                break;
                            default:
                                break;
                        }

                    }
                }
            }

            //save
            List<User> students = new ArrayList<User>();
            for (int numSheet = 0; numSheet < numberOfSheets; numSheet++) {
                XSSFSheet xSheet = xwb.getSheetAt(numSheet);
                JavaAssert.isTrue(null != xSheet, ReturnCode.PARAM_ILLEGLE, "学生EXCEL文件中空白", HealthException.class);

                //循环行row
                int totalRowNums = xSheet.getLastRowNum();
                for (int numRow = 0; numRow <= totalRowNums; numRow++) {
                    if (numRow == 0) {
                        continue;
                    }
                    XSSFRow xRow = xSheet.getRow(numRow);
                    if (xRow == null) {
                        logger.error("学生文件中存在空白行,文件名:{}", file.getOriginalFilename());
                        throw new HealthException(ReturnCode.PARAM_ILLEGLE,
                                "学生问文件存在空白行,文件名：" + file.getOriginalFilename());
                    }
                    //循环列cell
                    int totalCellNums = xRow.getLastCellNum();

                    User t = new User();
                    for (int numCell = 0; numCell < totalCellNums; numCell++) {
                        XSSFCell cell = xRow.getCell(numCell);
                        JavaAssert.isTrue(null != cell, ReturnCode.PARAM_ILLEGLE, "学生文件存在空白列", HealthException.class);
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_NUMERIC:
                                Double d = cell.getNumericCellValue();
                                DecimalFormat df = new DecimalFormat("0");
                                String str = df.format(d);
                                if (2 == numCell) {
                                    t.setStudentNo(str);
                                    break;
                                }
                                if (3 == numCell) {
                                    t.setGradeNo(Integer.parseInt(str));
                                    break;
                                }
                                if (4 == numCell) {
                                    t.setClassNo(Integer.parseInt(str));
                                    break;
                                }
                                if (6 == numCell) {
                                    t.setPassword(str);
                                    break;
                                }
                                break;
                            case Cell.CELL_TYPE_STRING:
                                String value = cell.getStringCellValue();
                                if (0 == numCell) {
                                    t.setAccountNo(value);
                                    break;
                                }
                                if (1 == numCell) {
                                    t.setUserName(value);
                                }
                                if (2 == numCell) {
                                    t.setStudentNo(value);
                                    break;
                                }
                                if (5 == numCell) {
                                    if ("男".equals(value)) {
                                        t.setGender(GenderType.MALE.name());
                                    } else {
                                        t.setGender(GenderType.FEMALE.name());
                                    }
                                    break;
                                }
                                if (6 == numCell) {
                                    t.setPassword(value);
                                    break;
                                }
                                break;
                            default:
                                break;
                        }

                    }

                    t.setRole(UserRoleType.STUDENT.name());
                    userRepository.saveUser(t);
                    students.add(t);
                }
            }
            logger.info("成功上传完成 students:{}", students);
            resp.setData(students.size());
        } catch (HealthException e) {
            logger.error("学生问卷上传异常", e);
            resp = BaseResp.buildFailResp("学生问卷上传异常." + e.getReturnMsg(), BaseResp.class);

        } catch (Exception e) {
            logger.warn("EXCEL上传学生列表,文件格式异常:{}", file);
            resp = BaseResp.buildFailResp(e, "EXCEL上传学生列表,文件格式异常", BaseResp.class);
        }

        return resp;
    }
}
