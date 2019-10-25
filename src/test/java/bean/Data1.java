package bean;

import java.util.List;

/**
 * 一个泛型参数测试
 * @Description:
 * @Date: 2019/9/27 16:17
 */
public class Data1<T> {
    private Integer businessId;

    private List<Integer> businessList;

    private T params1;

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
