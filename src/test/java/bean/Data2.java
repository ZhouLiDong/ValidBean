package bean;

import java.util.List;

/**
 * 两个泛型参数设置
 * @Description:
 * @Date: 2019/9/27 16:17
 */
public class Data2<T,T2> {
    private Integer businessId;

    private List<Integer> businessList;

    private T params1;

    private T2 params2;



    public T2 getParams2() {
        return params2;
    }

    public void setParams2(T2 params2) {
        this.params2 = params2;
    }

    public Integer getBusinessId() {

        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public List<Integer> getBusinessList() {
        return businessList;
    }

    public void setBusinessList(List<Integer> businessList) {
        this.businessList = businessList;
    }

    public T getParams1() {
        return params1;
    }

    public void setParams1(T params1) {
        this.params1 = params1;
    }
}
