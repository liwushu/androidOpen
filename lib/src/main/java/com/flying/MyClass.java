package com.example;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

public class MyClass {
    public static void main(String args[]) throws Exception {
        // 1: 数据库版本号
        // com.xxx.bean:自动生成的Bean对象会放到/java-gen/com/xxx/bean中
        Schema schema = new Schema(1, "com.flying.test");
        // DaoMaster.java、DaoSession.java、BeanDao.java会放到/java-gen/com/xxx/dao中
        schema.setDefaultJavaPackageDao("com.flying.test");
        initUserBean(schema);//初始化bean
        // // 最后我们将使用 DAOGenerator 类的 generateAll() 方法自动生成代码
        new DaoGenerator().generateAll(schema,"D:\\document\\demo\\androidOpen\\test\\src\\main\\java-gen");
        //在下面的args参数处写上输出目录，可以在builder.gfade中配置页可以直接写，我这里直接写的绝对目录
        //new DaoGenerator().generateAll(schema, args[0]);//自动创建
    }

    private static void initUserBean(Schema schema) {
        // 一个实体（类）就关联到数据库中的一张表，此处表名为「Note」（既类名）
        Entity note = schema.addEntity("Note");
        // 你也可以重新给表命名
        // note.setTableName("NODE");
        // greenDAO 会自动根据实体类的属性值来创建表字段，并赋予默认值
        // 接下来你便可以设置表中的字段，还可以为字段设置主键，是否为空
        note.addIdProperty();
        note.addStringProperty("text").notNull();
        // 与在 Java 中使用驼峰命名法不同，默认数据库中的命名是使用大写和下划线来分割单词的。
        // For example, a property called “creationDate” will become a database column “CREATION_DATE”.
        note.addStringProperty("comment");
        note.addDateProperty("date");
    }
}
