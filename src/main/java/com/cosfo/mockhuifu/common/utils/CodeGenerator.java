package com.cosfo.mockhuifu.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.util.Collections;
import java.util.Scanner;

/**
 * 代码生成器
 *
 * @author: Cathy
 */
public class CodeGenerator {

    private final static String templatePath = "/templates/mapper.xml.vm";
    private final static String hostIpAddress = "159.27.10.66";

    public static void main(String[] args) {
        //核心代码
//        FastAutoGenerator.create(new DataSourceConfig.Builder("jdbc:mysql://159.27.10.66:3307/cosfodb?useUnicode=true&useSSL=false&characterEncoding=utf8", "test", "xianmu619"))
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl("jdbc:mysql://mysql-8.summerfarm.net:3307/cosfodb?useUnicode=true&characterEncoding=utf-8");
        mysqlDataSource.setUser("test");
        mysqlDataSource.setPassword("xianmu619");
        FastAutoGenerator.create(new DataSourceConfig.Builder(mysqlDataSource))
                .globalConfig(builder -> {
                    builder.author("George") // 设置作者
                            .outputDir(System.getProperty("user.dir")+"/src/main/java")// 指定输出目录
                            .fileOverride()// 覆盖已生成文件
                            .commentDate("yyyy-MM-dd")//日期格式
                            .disableOpenDir()
                            .fileOverride();//覆盖之前文件
                })
                .packageConfig(builder -> {
                    builder.parent("com.cosfo") // 设置父包名
                            .moduleName("mockhuifu") // 设置父包模块名
                            //设置包的命名
                            .entity("model.po")
                            .service("repository")
                            .serviceImpl("repository.impl")
                            .mapper("mapper")
                            .xml("mapper")
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml,System.getProperty("user.dir")  + "/src/main/resources/mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("hui_fu_mock_transaction_summary") // 设置需要生成的表名
                            .addTablePrefix("m_") // 设置过滤表前缀
                            .serviceBuilder()// service策略配置
                            .formatServiceFileName("%sRepository")//   格式化 service 接口文件名称
                            .formatServiceImplFileName("%sRepositoryImpl")//   格式化 serviceImpl 接口文件名称
                            .entityBuilder()//  实体策略配置
                            .enableLombok()//   开启 lombok 模型
                            .enableTableFieldAnnotation()// 	开启生成实体时生成字段注解
                            .controllerBuilder()//  controller   策略配置
                            .enableHyphenStyle()//  开启驼峰命名转字符
//                            .enableRestStyle()//    开启生成@RestController 控制器
                            .mapperBuilder()//  mapper 策略配置
                            .superClass(BaseMapper.class)// 设置父类
                            .formatMapperFileName("%sMapper")// 格式化 mapper 文件名称
                            .enableMapperAnnotation()// 	开启 @Mapper 注解
                            .formatXmlFileName("%sMapper");//   格式化 xml 实现类文件名称
                })
                // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
