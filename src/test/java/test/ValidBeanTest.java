package test;

import bean.Data;
import bean.Data1;
import bean.Data2;
import bean.GenericData1;
import bean.GenericData2;
import com.bean.valid.util.TypeReference;
import com.bean.valid.util.ValidBean;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @Author 周利东
 * @Date: 2019/10/24 19:36
 */
public class ValidBeanTest {
    public static void main(String[] args) {
        tt();
    }

    public static void tt() {
//        Data<GenericData1> data = new Data<>();
//        data.setBusinessId(12);
//        data.setBusinessList(Arrays.asList(123, 222));
//
//        GenericData1 paramBO = new GenericData1();
//        paramBO.setStartDate("2019-10-01");
//        paramBO.setEndDate("2019-10-01");
//        data.setParams1(paramBO);
//
//
//        ValidBean.of(data)
//                .lazy()  //懒惰执行，即等到调用complete方法时再执行全部校验逻辑。。不调用该方法时，默认是立即执行校验逻辑，即调用了notEmpty就会立即校验
//                .notEmpty(Data::getBusinessId, "2") //校验data.getActivityId的返回值是否非空，如果非空，则不能通过校验，会抛异常，异常的msg为第二个参数指定值
//                .notEmpty(Data::getBusinessId) //不传第二个参数，使用默认的msg="属性不能为空"
//                .notEmpty(Data::getBusinessList, "3")  //支持String、Collection、Map的空校验
//                .map(Data::getParams1)//转换为校验getParams1方法返回的对象，类似于Optional的map方法，这里的getParams返回的对象类型是GenericData类型，是前面指定泛型类型
//                .notEmpty(GenericData1::getEndDate, "5")//校验上面转换后的属性实例的指定方法返回值
//                .notEmpty(GenericData1::getStartDate, "6")
//                .parent(new TypeReference<Data<GenericData1>>())//当前是校验data.getParams返回的对象，此时如果要再返回去校验data对象，则使用该parent方法。。。
//                //注意，返回之后会丢失泛型类型(泛型会变成Object类型，实际上还是GenericData类型，如果又要用到该泛型，那么此时就需要使用下面的cast方法强制转换一下类型)
//                .map(Data::getParams1) //此时有要用到该泛型。。。但由于上面的parent方法丢失了泛型信息，所以需要再调用cast方法转换为指定类型
//                .notEmpty(GenericData1::getStartDate, "7") //强制转换后就又可以使用方法引用了
//                .notEmpty(GenericData1::getEndDate, "8")
//                .complete(); //在一开始调用了lazy方法，所以最后只有在调用complete方法后才会之前前面的校验逻辑。如果没有开启lazy，则该方法无用。为了习惯，不论有没有开启lazy，最好在最后都调一下这个方法

        //下面方法可以自己测试不同功能
        simpleTest(); //1
        mapTest();//2
        parentTest();//3
        customValidRuleTest();//4
        lazyTest();//5
    }

    /**
     * 1、简单使用例子
     */
    public static void simpleTest() {
        Data data = new Data();
        data.setBusinessId(12);  //可以试着注释掉这一行再来执行看看
        data.setBusinessList(Arrays.asList(123, 222));//可以试着注释掉这一行再来执行看看

        ValidBean.of(data)
                .notEmpty(Data::getBusinessId, "商户id不能为空") //data.getBusinessId的返回值必须非空，如果为空，则不能通过校验，会抛异常，异常的msg为第二个参数
                .notEmpty(Data::getBusinessId) //不传第二个参数，使用默认的msg="属性不能为空"
                .notEmpty(Data::getBusinessList, "商户列表不能为空")  //支持String、Collection、Map的空校验
                .empty(Data::getActivityId, "活动id必须为空") //data.getBusinessId的返回值必须为空，如果不为空则报错
                .complete(); //未开启懒惰模式lazy()时，可以不需要complete。为了习惯，不论有没有开启lazy，最好在最后都调一下这个方法
    }

    /**
     * 2、校验子属性为pojo的例子
     */
    public static void mapTest() {
        Data1<GenericData1> data = new Data1<>();
        data.setBusinessId(12);
        data.setBusinessList(Arrays.asList(123, 222));

        //添加POJO 子属性
        GenericData1 paramBO = new GenericData1();
        paramBO.setStartDate("2019-10-01");
        paramBO.setEndDate("2019-10-01");
        data.setParams1(paramBO);


        ValidBean.of(data)
                .notEmpty(Data1::getBusinessId, "商户id不能为空") //校验data.getActivityId的返回值是否非空，如果非空，则不能通过校验，会抛异常，异常的msg为第二个参数指定值
                .notEmpty(Data1::getBusinessList, "商户列表不能为空")  //支持String、Collection、Map的空校验
                .map(Data1::getParams1)//转换为基于getParams1方法返回的对象的校验对象，类似于Optional的map方法(如果熟悉Optional，相信会很容易理解的)。
                // 在这之后就是基于getParams1返回值进行校验了
                .notEmpty(GenericData1::getEndDate, "开始时间不能为空")//校验是data对象的getParams1返回的对象的getEndDate方法的返回值
                .notEmpty(GenericData1::getStartDate, "结束时间不能为空")
                .complete();

    }


    /**
     * 3、当POJO有多个子属性，并且这多个子属性都是POJO，怎么办？
     * <p>
     * 比如在2的例子中，data的校验通过调用map(Data::getParams1)方法之后变成了对getParams1方法返回值的校验。
     * 但getParams1返回的对象校验完之后，又想校验data的getParams2的返回值怎么办？
     * 有办法，就是先回到data，再用map(Data::getParams2)就可以了。请看这个例子
     */
    public static void parentTest() {
        Data2<GenericData1, GenericData2> data = new Data2<>();
        data.setBusinessId(12);
        data.setBusinessList(Arrays.asList(123, 222));

        //添加第一个 POJO 子属性
        GenericData1 param1 = new GenericData1();
        param1.setStartDate("2019-10-01");
        param1.setEndDate("2019-10-01");
        data.setParams1(param1);

        //添加第二个 POJO 子属性
        GenericData2 param2 = new GenericData2();
        param2.setName("zld");
        param2.setSex("男");
        data.setParams2(param2);


        ValidBean.of(data)
                .notEmpty(Data2::getBusinessId, "商户id不能为空") //校验data.getActivityId的返回值是否非空，如果非空，则不能通过校验，会抛异常，异常的msg为第二个参数指定值
                .notEmpty(Data2::getBusinessList, "商户列表不能为空")  //支持String、Collection、Map的空校验
                .map(Data2::getParams1)//转换为基于getParams1方法返回的对象的校验对象，类似于Optional的map方法(如果熟悉Optional，相信会很容易理解的)。
                // 在这之后就是基于getParams1返回值进行校验了
                .notEmpty(GenericData1::getEndDate, "第一个参数：开始时间不能为空")//校验是data对象的getParams1返回的对象的getEndDate方法的返回值
                .notEmpty(GenericData1::getStartDate, "第一个参数：结束时间不能为空")
                //此时对getParams1返回值的校验已完成，然后又想回到对data校验怎么办？使用parent方法即可
                //调用parent方法回到上层的校验时，由于ValidSubBean和ValidBean之间泛型转换会导致类型信息丢失，所以需要重新传入类型泛型信息
                //其中Data<GenericData1, GenericData2>就是data的类型
                .parent(new TypeReference<Data2<GenericData1, GenericData2>>())
                //此时又可以对data进行校验了，一样
                .notEmpty(Data2::getBusinessId, "商户id不能为空")
                //再次使用map，此时校验的是getParams2的返回值
                .map(Data2::getParams2)
                .notEmpty(GenericData2::getName, "第二个参数：名字不能为空")
                .notEmpty(GenericData2::getSex, "第二个参数：性别不能为空")
                .complete();
    }


    /**
     * 4、在前面的例子中，都只能用notEmpty和empty进行非空与空校验，但如果想要自定义校验规则呢？也有相关支持
     */
    public static void customValidRuleTest() {
        Data data = new Data();
        data.setPhone("12345678911");//假设是十一位数的手机号码

        ValidBean.of(data)
                //第一个参数就是调用data.getPhone方法获取返回值
                //第二个参数就是把返回值作为参数传给lambda。lambda表达式返回true则表示通过校验，不会报错。。
                // 如果lambda返回false，则报错，，错误信息为第三个参数
                .valid(Data::getPhone, StringUtils::isNumeric, "手机号码存在非数字字符")
                .valid(Data::getPhone, phone -> phone.length() == 11, "手机号码的长度必须是11位")
                .complete();
    }


    /**
     * 5、懒惰执行
     * 在前面的例子中，调用方法会立即执行校验逻辑。。
     * 比如调用notEmpty，那么就会立即获取指定方法的返回值，并且立即进行非空校验。。
     * 但，可能你不想这样，想要等最后再执行校验逻辑，出现错误时再全部一起报错。。
     * 使用很简单(实现废了老大功夫了)
     */
    public static void lazyTest() {
        Data2<GenericData1, GenericData2> data = new Data2<>();
        data.setBusinessId(12);
        data.setBusinessList(Arrays.asList(123, 222));

        //添加第一个 POJO 子属性
        GenericData1 param1 = new GenericData1();
        param1.setStartDate("2019-10-01");
        param1.setEndDate("2019-10-01");
        data.setParams1(param1);

        //添加第二个 POJO 子属性
        GenericData2 param2 = new GenericData2();
        param2.setName("zld");
        param2.setSex("男");
        data.setParams2(param2);


        ValidBean.of(data)
                .lazy()  //----------就在这，只要加了lazy方法就表示启用懒惰执行了。。下面的一堆校验逻辑都不会立即执行，而是等到调用complete方法时才会执行
                .notEmpty(Data2::getBusinessId, "商户id不能为空") //校验data.getActivityId的返回值是否非空，如果非空，则不能通过校验，会抛异常，异常的msg为第二个参数指定值
                .notEmpty(Data2::getBusinessList, "商户列表不能为空")  //支持String、Collection、Map的空校验
                .map(Data2::getParams1)//转换为基于getParams1方法返回的对象的校验对象，类似于Optional的map方法(如果熟悉Optional，相信会很容易理解的)。
                // 在这之后就是基于getParams1返回值进行校验了
                .notEmpty(GenericData1::getEndDate, "第一个参数：开始时间不能为空")//校验是data对象的getParams1返回的对象的getEndDate方法的返回值
                .notEmpty(GenericData1::getStartDate, "第一个参数：结束时间不能为空")
                //此时对getParams1返回值的校验已完成，然后又想回到对data校验怎么办？使用parent方法即可
                //调用parent方法回到上层的校验时，由于ValidSubBean和ValidBean之间泛型转换会导致类型信息丢失，所以需要重新传入类型泛型信息
                //其中Data<GenericData1, GenericData2>就是data的类型
                .parent(new TypeReference<Data2<GenericData1, GenericData2>>())
                //此时又可以对data进行校验了，一样
                .notEmpty(Data2::getBusinessId, "商户id不能为空")
                //再次使用map，此时校验的是getParams2的返回值
                .map(Data2::getParams2)
                .notEmpty(GenericData2::getName, "第二个参数：名字不能为空")
                .notEmpty(GenericData2::getSex, "第二个参数：性别不能为空")
                .complete();  //懒惰执行时，一口气执行上面的所有逻辑
    }


}
