package fnd;

import com.alibaba.fastjson.JSON;

/**
 * Created by mnipa on 2019/1/21.
 */
public class UserDto {
    private String id;
    private String uuid;
    private String code;
    private String version;
    private String name;
    private String sexcode;
    private String idcard;
    private String typecode;
    private String orgcode;
    private String deptcode;
    private String account;
    private String state;
    private String positioncode;
    private String positiontypecode;
    private String workplace;
    private String contact1;
    private String contact2;
    private String email;
    private String weight;
    private String orgpkvalue;
    private String deptpkvalue;
    private String postpkvalue;
    private String psnpkvalue;
    private String positionname;
    private String create_time;
    private Boolean isAdmin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSexcode() {
        return sexcode;
    }

    public void setSexcode(String sexcode) {
        this.sexcode = sexcode;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getTypecode() {
        return typecode;
    }

    public void setTypecode(String typecode) {
        this.typecode = typecode;
    }

    public String getOrgcode() {
        return orgcode;
    }

    public void setOrgcode(String orgcode) {
        this.orgcode = orgcode;
    }

    public String getDeptcode() {
        return deptcode;
    }

    public void setDeptcode(String deptcode) {
        this.deptcode = deptcode;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPositioncode() {
        return positioncode;
    }

    public void setPositioncode(String positioncode) {
        this.positioncode = positioncode;
    }

    public String getPositiontypecode() {
        return positiontypecode;
    }

    public void setPositiontypecode(String positiontypecode) {
        this.positiontypecode = positiontypecode;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getContact1() {
        return contact1;
    }

    public void setContact1(String contact1) {
        this.contact1 = contact1;
    }

    public String getContact2() {
        return contact2;
    }

    public void setContact2(String contact2) {
        this.contact2 = contact2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getOrgpkvalue() {
        return orgpkvalue;
    }

    public void setOrgpkvalue(String orgpkvalue) {
        this.orgpkvalue = orgpkvalue;
    }

    public String getDeptpkvalue() {
        return deptpkvalue;
    }

    public void setDeptpkvalue(String deptpkvalue) {
        this.deptpkvalue = deptpkvalue;
    }

    public String getPostpkvalue() {
        return postpkvalue;
    }

    public void setPostpkvalue(String postpkvalue) {
        this.postpkvalue = postpkvalue;
    }

    public String getPsnpkvalue() {
        return psnpkvalue;
    }

    public void setPsnpkvalue(String psnpkvalue) {
        this.psnpkvalue = psnpkvalue;
    }

    public String getPositionname() {
        return positionname;
    }

    public void setPositionname(String positionname) {
        this.positionname = positionname;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
