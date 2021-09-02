package com.yexin997.vaccines;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yexin997.vaccines.constant.VaccinesConstant;
import com.yexin997.vaccines.helper.HttpHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 *
 * @author zhou.dalu
 * @date 2021/9/2
 */
public class VaccinesCrush {

    private static Map<String, String> headers = new HashMap<>();

    private static Map<String, Object> loginBody = new HashMap<>();

    static {
        headers.put("Content-Type", "application/json");
        loginBody.put("rid", "patient");
        loginBody.put("forAccessToken", true);
        loginBody.put("tenantId", "hcn.taicang");
        loginBody.put("loginName", VaccinesConstant.LOGIN_NAME);
        loginBody.put("pwd", DigestUtils.md5Hex(VaccinesConstant.LOGIN_PWD));
    }

    public static void main(String[] args) {
        //登录获取token
        JSONObject loginData = HttpHelper.httpPost(VaccinesConstant.LOGIN_URL, null, loginBody, headers);
        String token = loginData.getJSONObject("properties").getString("accessToken");
        //获取个人信息
        Map<String, String> personalInformationHeaders = new HashMap<>(headers);
        personalInformationHeaders.put("X-Service-Method", "getAppInfoByDevice");
        personalInformationHeaders.put("X-Access-Token", token);
        personalInformationHeaders.put("X-Service-Id", "hcn.device");
        JSONObject personalInformationData = HttpHelper.httpPost(VaccinesConstant.PERSONAL_INFORMATION_URL, null, new JSONArray(), personalInformationHeaders);
        String userName = personalInformationData.getJSONObject("body").getString("userName");
        String mpiId = personalInformationData.getJSONObject("body").getJSONObject("user").getString("mpiId");
        String phoneNo = personalInformationData.getJSONObject("body").getJSONObject("user").getString("phoneNo");
        String certificateNo = personalInformationData.getJSONObject("body").getJSONObject("user").getJSONObject("certificate").getString("certificateNo");
        //获取疫苗列表
        Map<String, Object> vaccineListBody = new HashMap<>();
        vaccineListBody.put("mpiId", mpiId);
        JSONObject vaccineListData = HttpHelper.httpPost(VaccinesConstant.VACCINE_LIST_URL, null, vaccineListBody, headers);
        JSONObject vaccineData = vaccineListData.getJSONObject("data").getJSONArray("task_list").getJSONObject(0);
        String taskName = vaccineData.getString("taskName");
        String taskId = vaccineData.getString("taskId");
        //秒杀
        Map<String, Object> submitBody = new HashMap<>();
        submitBody.put("taskId", taskId);
        submitBody.put("name", userName);
        submitBody.put("regMpiId", mpiId);
        submitBody.put("sourceMpiId", mpiId);
        submitBody.put("idCard", certificateNo);
        submitBody.put("taskName", taskName);
        submitBody.put("phone", phoneNo);
        System.out.println("脚本加载完成，可以开始暴力递归秒杀，键入1开始秒杀，否则退出脚本。");
        Scanner input = new Scanner(System.in);
        String str = input.next();
        if (!StringUtils.equals(str, "1")) {
            return;
        }
        String submitCode = "300";
        int times = 0;
        long startTime = System.currentTimeMillis();
        while (StringUtils.equals(submitCode, "300")) {
            JSONObject submitData = HttpHelper.httpPost(VaccinesConstant.SUBMIT_URL, null, submitBody, headers);
            submitCode = submitData.getString("code");
            times++;
            System.out.println("脚本执行" + times + "次，耗时" + (System.currentTimeMillis() - startTime) + "ms...");
        }

    }
}
