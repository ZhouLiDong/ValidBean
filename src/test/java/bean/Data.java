package bean;

import java.util.List;

/**
 *
 * @Description:
 * @Date: 2019/9/27 16:17
 */
public class Data {
    private Integer businessId;

    private List<Integer> businessList;

    private Integer activityId;

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
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

}
